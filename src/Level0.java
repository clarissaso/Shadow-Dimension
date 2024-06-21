import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//code by Tharun Dharmawickrema from Project 1 solution
public class Level0 extends Level {
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String WORLD_FILE = "res/level0.csv";
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final static String INSTRUCTION_MESSAGE = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String END_MESSAGE = "GAME OVER!";
    private final static String WIN_MESSAGE = "LEVEL COMPLETE!";
    private final static int WALL_ARRAY_SIZE = 52;
    private final static int S_HOLE_ARRAY_SIZE = 5;
    private final static Wall[] walls = new Wall[WALL_ARRAY_SIZE];
    private final static Sinkhole[] sinkholes = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private Player player;
    private Level1 level1;
    private boolean hasStarted;
    private boolean gameOver;
    private boolean playerWin;
    private boolean nextLevel;
    private double ms;
    private int counter;

    public Level0(){
        readCSV();
        this.hasStarted = false;
        this.gameOver = false;
        this.playerWin = false;
        this.nextLevel = false;
    }

    /**
     * Method used to read file and create objects
     */
    public void readCSV(){
        try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE))){

            String line;
            int currentWallCount = 0;
            int currentSinkholeCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        player = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Wall":
                        walls[currentWallCount] = new Wall(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentWallCount++;
                        break;
                    case "Sinkhole":
                        sinkholes[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
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
        if(!hasStarted && !playerWin){
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            super.drawStartScreen(INSTRUCTION_MESSAGE, TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);

            if (input.wasPressed(Keys.SPACE)){
                hasStarted = true;
            }
        }

        if (gameOver){
            super.drawMessage(TITLE_FONT, END_MESSAGE, TITLE_FONT_SIZE);
        } else if (playerWin && !nextLevel) {
            ms = counter / 0.06;
            counter++;
            super.drawMessage(TITLE_FONT, WIN_MESSAGE, TITLE_FONT_SIZE);
            if (ms > 3000) {
                nextLevel = true;
            }
        }

        // game is running
        if (hasStarted && !gameOver && !playerWin){
            super.drawBackground(BACKGROUND_IMAGE);

            for(Wall current: walls){
                current.update();
            }
            for(Sinkhole current: sinkholes){
                current.update();
            }
            player.update(input);

            if (player.isDead()){
                gameOver = true;
            }

            if (player.reachedGate()){
                playerWin = true;
            }
        }
        checkCollisions(player);
        checkOutOfBounds(player);
    }

    /**
     * Method that checks for collisions between Fae and the other entities, and performs
     * corresponding actions.
     */
    public void checkCollisions(Player player){
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());
        for (Wall current : walls){
            Rectangle wallBox = current.getBoundingBox();
            if (faeBox.intersects(wallBox)){
                player.moveBack();
            }
        }

        for (Sinkhole hole : sinkholes){
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && faeBox.intersects(holeBox)){
                player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                player.moveBack();
                hole.setActive(false);
                System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                        "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
            }
        }
    }

    /**
     * Method that checks if Fae has gone out-of-bounds and performs corresponding action
     */
    protected void checkOutOfBounds(Player player){
        super.checkOutOfBounds(player);
    }

    public boolean isNextLevel() {
        return nextLevel;
    }

}
