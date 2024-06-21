import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;

/**
 * SWEN20003 Project 1, Semester 2, 2022
 *
 * @author Tharun Dharmawickrema
 */
public class Level1 extends Level{
    private final static String WORLD_FILE = "res/level1.csv";
    private final Image BACKGROUND_IMAGE = new Image("res/background1.png");
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int INS_X = 350;
    private final static int INS_Y = 350;
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    private final static String INSTRUCTION_MESSAGE = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    private final static String END_MESSAGE = "GAME OVER!";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static int TREE_ARRAY_SIZE = 15;
    private final static int DEMON_ARRAY_SIZE = 5;
    private final static int S_HOLE_ARRAY_SIZE = 5;
    private final static Tree[] trees = new Tree[TREE_ARRAY_SIZE];
    private final static Demon[] demons = new Demon[DEMON_ARRAY_SIZE];
    private final static Sinkhole[] sinkholes = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private Point demonCentre;
    private Point firePosition;
    private Point navecCentre;
    private Player player;
    private Level0 level0;
    private Navec navec;
    private boolean hasStarted;
    private boolean gameOver;
    private boolean fireBoxDemon;
    private boolean fireBoxNavec;
    private boolean playerWin;
    private static int counter;
    private boolean startTimer;
    private boolean fireWidth;
    private boolean attackSuccess;

    private Timer timer;
    private static double ms;

    public Level1(){
        readCSV();
        this.hasStarted = false;
        this.gameOver = false;
        this.playerWin = false;
        this.attackSuccess = false;
        this.firePosition = new Point(0,0);
        this.fireWidth = true;
        this.fireBoxDemon = false;
        this.fireBoxNavec = false;
    }

    /**
     * Method used to read file and create objects
     */
    public void readCSV(){
        try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE))){

            String line;
            int currentTreeCount = 0;
            int currentDemonCount = 0;
            int currentSinkholeCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        player = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Tree":
                        trees[currentTreeCount] = new Tree(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentTreeCount++;
                        break;
                    case "Sinkhole":
                        sinkholes[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
                        break;
                    case "Demon":
                        demons[currentDemonCount] = new Demon(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentDemonCount++;
                        break;
                    case "Navec":
                        navec = new Navec(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "TopLeft":
                        topLeft = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    public void update(Input input) {
        if (!startTimer) {
            counter = 0;
        }
        if (!hasStarted){
            super.drawStartScreen(INSTRUCTION_MESSAGE, INS_X, INS_Y);
            if (input.wasPressed(Keys.SPACE)){
                hasStarted = true;
            }
        }

        if (gameOver){
            super.drawMessage(INSTRUCTION_FONT, END_MESSAGE, TITLE_FONT_SIZE);
        } else if (playerWin) {
            super.drawMessage(INSTRUCTION_FONT, WIN_MESSAGE, TITLE_FONT_SIZE);
        }

        // game is running
        if (hasStarted && !gameOver && !playerWin){
            super.drawBackground(BACKGROUND_IMAGE);

            for(Tree current: trees){
                current.update();
            }

            for(Demon current: demons){
                current.update(player, input);
            }

            for(Sinkhole current: sinkholes){
                current.update();
            }
            player.update(input);
            navec.update(player, input);

            if (player.isDead()){
                gameOver = true;
            }
            if (!navec.isActive()){
                playerWin = true;
            }
        }

        checkInAttackRange(player);
        checkCollisions(player);
        checkOutOfBounds(player);
    }

    /**
     * Method that checks for collisions between all entities, and performs
     * corresponding actions.
     */
    public void checkCollisions(Player player){
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        Rectangle navecBox = new Rectangle(navec.getPosition(), navec.getCurrentImage().getWidth(),
                navec.getCurrentImage().getHeight());

        // Collisions between demon and other entities
        for (Demon demon : demons) {
            Rectangle demonBox = new Rectangle(demon.getPosition(), demon.getCurrentImage().getWidth(),
                    demon.getCurrentImage().getHeight());
            if (demon.isActive() && faeBox.intersects(demonBox)) {
                if (player.getAttackState()) {
                    startTimer = true;
                    if (oneFrame() && !demon.getGotAttacked()) {
                        demon.setHealthPoints(Math.max(demon.getHealthPoints() - player.getDamagePoints(), 0));
                        System.out.println("Fae inflicts " + player.getDamagePoints() + " damage points on Demon. " +
                                "Demon's current health: " + demon.getHealthPoints() + "/" + demon.getMaxHealthPoints());
                    }
                    demon.setGotAttacked(true);
                } else {
                    startTimer = false;
                    demon.setGotAttacked(false);
                }

                if (demon.getHealthPoints() == 0) {
                    demon.setActive(false);
                }

            }

            if (demon.getPlayerInRange()) {
                Rectangle demonFireBox;
                if (demon.getOrientation()) {
                    demonFireBox = new Rectangle(demon.getFirePosition(), demon.getFireImage().getWidth(),
                            demon.getFireImage().getHeight());
                } else {
                    demonFireBox = new Rectangle(demon.getFirePosition(), demon.getFireImage().getHeight(),
                            demon.getFireImage().getWidth());
                }
                if (faeBox.intersects(demonFireBox)) {
                    player.setGotAttacked(true);
                }
                if (player.getDamaged()) {
                    player.setHealthPoints(Math.max(player.getHealthPoints() - demon.getDamagePoints(), 0));
                    System.out.println("Demon inflicts " + demon.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
                }

            }

            for (Tree tree : trees){
                Rectangle treeBox = tree.getBoundingBox();
                if (demon.isActive() && demonBox.intersects(treeBox)){
                    demon.moveOpposite();
                }
            }

            for (Sinkhole hole : sinkholes) {
                Rectangle holeBox = hole.getBoundingBox();
                if (demon.isActive() && hole.isActive() && demonBox.intersects(holeBox)) {
                    demon.moveOpposite();
                }
            }
        }

        // Collisions between tree and other entities
        for (Tree current : trees){
            Rectangle treeBox = current.getBoundingBox();
            if (faeBox.intersects(treeBox)){
                player.moveBack();
            }
            if (navecBox.intersects(treeBox)){
                navec.moveOpposite();
            }
        }

        // Collisions between sinkhole and other entities
        for (Sinkhole hole : sinkholes){
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && faeBox.intersects(holeBox)){
                player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                player.moveBack();
                hole.setActive(false);
                System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                        "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
            }
            if (hole.isActive() && navec.isActive() && navecBox.intersects(holeBox)) {
                navec.moveOpposite();
            }
        }

        // Collisions between navec and other entities
        if (navec.isActive() && faeBox.intersects(navecBox)) {
            if (player.getAttackState()) {
                startTimer = true;
                if (oneFrame() && !navec.getGotAttacked()) {
                    navec.setHealthPoints(Math.max(navec.getHealthPoints() - player.getDamagePoints(), 0));
                    System.out.println("Fae inflicts " + player.getDamagePoints() + " damage points on Demon. " +
                            "Demon's current health: " + navec.getHealthPoints() + "/" + navec.getMaxHealthPoints());
                }
                navec.setGotAttacked(true);
            } else {
                navec.setGotAttacked(false);
                startTimer = false;
            }

            if (navec.getHealthPoints() == 0) {
                navec.setActive(false);
            }

        }
        if (navec.getPlayerInRange()) {
            Rectangle navecFireBox;
            if (navec.getOrientation()) {
                navecFireBox = new Rectangle(navec.getFirePosition(), navec.getFireImage().getWidth(),
                        navec.getFireImage().getHeight());
            } else {
                navecFireBox = new Rectangle(navec.getFirePosition(), navec.getFireImage().getHeight(),
                        navec.getFireImage().getWidth());
            }
            if (faeBox.intersects(navecFireBox)) {
                player.setGotAttacked(true);
            }
            if (player.getDamaged()) {
                player.setHealthPoints(Math.max(player.getHealthPoints() - navec.getDamagePoints(), 0));
                System.out.println("Navec inflicts " + navec.getDamagePoints() + " damage points on Fae. " +
                        "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
            }

        }

    }

    public boolean oneFrame() {
        ms = counter / 0.06;
        counter++;
        if (ms <= 1000/60) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method that checks if Fae, demon, or Navec has gone out-of-bounds and performs corresponding action
     */
    public void checkOutOfBounds(Player player){
        super.checkOutOfBounds(player);
        for (Demon current : demons) {
            Point demonPosition = current.getPosition();
            if ((demonPosition.y > bottomRight.y) || (demonPosition.y < topLeft.y) || (demonPosition.x < topLeft.x)
                    || (demonPosition.x > bottomRight.x)){
                current.moveOpposite();
            }
        }
        Point navecPosition = navec.getPosition();
        if ((navecPosition.y > bottomRight.y) || (navecPosition.y < topLeft.y) || (navecPosition.x < topLeft.x)
                || (navecPosition.x > bottomRight.x)){
            navec.moveOpposite();
        }

    }

    /**
     * Method that checks if Fae is in enemy's attack range
     */
    public void checkInAttackRange(Player player) {
        for (Demon demon : demons) {
            demonCentre = new Point(demon.getPosition().x + demon.getCurrentImage().getWidth()/2,
                    demon.getPosition().y + demon.getCurrentImage().getHeight()/2);
            if (demon.isActive() && demonCentre.distanceTo(player.getPosition()) <= demon.getAttackRange()) {
                demon.setPlayerInRange(true);
            } else {
                demon.setPlayerInRange(false);
            }
        }

        navecCentre = new Point(navec.getPosition().x + navec.getCurrentImage().getWidth()/2,
                navec.getPosition().y + navec.getCurrentImage().getHeight()/2);
        if (navec.isActive() && navecCentre.distanceTo(player.getPosition()) <= navec.getAttackRange()) {
            navec.setPlayerInRange(true);
        } else {
            navec.setPlayerInRange(false);
        }
    }

    public boolean isHasStarted() {
        return hasStarted;
    }
}

