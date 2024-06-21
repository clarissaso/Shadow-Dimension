import bagel.*;
import bagel.util.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

public class Demon extends Enemy {
    private final static String DEMON_LEFT = "res/demon/demonLeft.png";
    private final static String DEMON_RIGHT = "res/demon/demonRight.png";
    private final static String DEMON_INV_LEFT = "res/demon/demonInvincibleLeft.png";
    private final static String DEMON_INV_RIGHT = "res/demon/demonInvincibleRight.png";
    private final static int DEMON_INV_TIME = 3000;
    private final static int ATTACK_RANGE = 150;
    private final static String DEMON_FIRE = "res/demon/demonFire.png";
    private final static int DAMAGE_POINTS = 10;
    private final static int MAX_HEALTH_POINTS = 40;
    private final static double MIN_SPEED = 0.2;
    private final static double MAX_SPEED = 0.7;
    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;
    private String demonType;
    private double demonSpeed;
    private int healthPoints;
    private boolean invincibleState;
    private boolean isActive;
    private boolean speedUp;
    private boolean speedDown;
    private boolean facingRight;
    private boolean gotAttacked;
    private boolean startTimer;
    private boolean playerInRange;
    private boolean changeState;
    private double ms;
    private int counter;
    private int timescale;
    private int prevTimescale;
    private int count;
    private Image currentImage;
    private Image demonFire;
    private Level1 level1;
    private Level0 level0;
    private Player player;
    private Random rand = new Random();

    public Demon(int startX, int startY){
        super(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.isActive = true;
        this.facingRight = true;
        this.invincibleState = false;
        this.gotAttacked = false;
        this.currentImage = new Image(DEMON_RIGHT);
        this.demonFire = new Image(DEMON_FIRE);
        this.demonType = randDemonType();
        this.demonSpeed = ThreadLocalRandom.current().nextDouble(MIN_SPEED, MAX_SPEED);
        this.startTimer = false;
        this.playerInRange = false;
        this.speedUp = true;
        this.speedDown = true;
        this.timescale = 0;
        this.changeState = false;
    }

    /**
     * Method that performs state update
     */
    public void update(Player player, Input input) {
        if (isActive){
            currentImage.drawFromTopLeft(this.position.x, this.position.y);
        }

        if (demonType == "aggressive") {
            timescaleControl(input);

            if (direction == "up") {
                super.move(0, -demonSpeed);
            } else if (direction == "down") {
                super.move(0, demonSpeed);
            } else if (direction == "left") {
                super.move(-demonSpeed, 0);
                if (facingRight && !invincibleState) {
                    this.currentImage = new Image(DEMON_LEFT);
                    facingRight = !facingRight;
                }
            } else if (direction == "right") {
                super.move(demonSpeed, 0);
                if (!facingRight && !invincibleState) {
                    this.currentImage = new Image(DEMON_RIGHT);
                    facingRight = !facingRight;
                }
            }

        }
        if (gotAttacked) {
            changeState = true;
        }
        if (changeState) {
            goInvincible(startTimer());
        }
        if (isActive) {
            super.renderHealthPoints(this.healthPoints, MAX_HEALTH_POINTS);
        }
        if (playerInRange) {
            super.shotFire(player, demonFire, currentImage);
        }
    }

    /**
     * Method that returns timer in millisecond
     */
    private double startTimer() {
        ms = counter / 0.06;
        counter++;
        return ms;
    }

    /**
     * Method that performs invincible state on Demon
     */
    private void goInvincible(double startTimer) {
        invincibleState = true;
        if (ms >= 3000) {
            invincibleState = false;
            changeState = false;
            stopTimer();
        }
        updateImage();
    }

    /**
     * Method that updates Demon's image
     */
    private void updateImage() {
        if (invincibleState) {
            if (facingRight) {
                this.currentImage = new Image(DEMON_INV_RIGHT);
            } else {
                this.currentImage = new Image(DEMON_INV_LEFT);
            }
        } else {
            if (facingRight) {
                this.currentImage = new Image(DEMON_RIGHT);
            } else {
                this.currentImage = new Image(DEMON_LEFT);
            }
        }
    }

    /**
     * Method that controls the timescale
     */
    private void timescaleControl(Input input) {
        if (input.isDown(Keys.L)) {
            startTimer = true;
            speedUp = true;
        } else if (input.isDown(Keys.K)) {
            startTimer = true;
            speedDown = true;
        } else {
            speedUp = false;
            speedDown = false;
            startTimer = false;
            stopTimer();
        }

        if (startTimer && speedUp) {
            if (oneFrame() && timescale < MAX_TIMESCALE) {
                timescale += 1;
                demonSpeed = demonSpeed * (1.5);
            }
        } else if (startTimer && speedDown) {
            if (oneFrame() && timescale > MIN_TIMESCALE) {
                timescale -= 1;
                demonSpeed = demonSpeed * (0.5);
            }
        }
    }

    private void setPrevTimescale(){

        this.prevTimescale = timescale;
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

    private void stopTimer() {
        counter = 0;
    }

    /**
     * Method that randomizes Demon type
     */
    public String randDemonType() {
        Set<String> demonTypes = new HashSet<String>();

        demonTypes.add("passive");
        demonTypes.add("aggressive");

        String[] arrayDemonType = demonTypes.toArray(new String[demonTypes.size()]);
        int randNum = rand.nextInt(demonTypes.size());
        return arrayDemonType[randNum];
    }

    public int getDamagePoints(){
        return DAMAGE_POINTS;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public void setGotAttacked(boolean attacked) {
        gotAttacked = attacked;
    }

    public Point getPosition() {
        return position;
    }

    public Point getFirePosition() {
        return firePosition;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public Image getFireImage() {
        return demonFire;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public boolean getInvincibleState() {
        return invincibleState;
    }

    public void setInvincibleState(boolean state) {
        invincibleState = state;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public static int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    public static int getAttackRange() {
        return ATTACK_RANGE;
    }

    public boolean getGotAttacked() {
        return gotAttacked;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public void setPlayerInRange(boolean inRange) {
        this.playerInRange = inRange;
    }

    public boolean getPlayerInRange() {
        return playerInRange;
    }
}
