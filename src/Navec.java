import bagel.*;
import bagel.util.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Navec extends Enemy {
    private final static String NAVEC_LEFT = "res/navec/navecLeft.png";
    private final static String NAVEC_RIGHT = "res/navec/navecRight.png";
    private final static String NAVEC_INV_LEFT = "res/navec/navecInvincibleLeft.png";
    private final static String NAVEC_INV_RIGHT = "res/navec/navecInvincibleRight.png";
    private final static String NAVEC_FIRE = "res/navec/navecFire.png";
    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;
    private final static int ATTACK_RANGE = 300;
    private final static int DAMAGE_POINTS = 20;
    private final static int MAX_HEALTH_POINTS = 80;
    private final static double MIN_SPEED = 0.2;
    private final static double MAX_SPEED = 0.7;
    private int healthPoints;
    private boolean isActive;
    private boolean gotAttacked;
    private boolean startTimer;
    private boolean playerInRange;
    private double ms;
    private int counter;
    private boolean invincibleState;
    private boolean facingRight;
    private double navecSpeed;
    private Image currentImage;
    private Image navecFire;
    private Player player;
    private int timescale;
    private boolean speedUp;
    private boolean speedDown;
    private boolean changeState;
    private final static DrawOptions ROTATE = new DrawOptions();

    public Navec(int startX, int startY){
        super(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.isActive = true;
        this.facingRight = true;
        this.invincibleState = false;
        this.gotAttacked = false;
        this.startTimer = false;
        this.currentImage = new Image(NAVEC_RIGHT);
        this.navecFire = new Image(NAVEC_FIRE);
        this.navecSpeed = ThreadLocalRandom.current().nextDouble(MIN_SPEED, MAX_SPEED);
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
        timescaleControl(input);
        if (direction == "up") {
            super.move(0, -navecSpeed);
        } else if (direction == "down") {
            super.move(0, navecSpeed);
        } else if (direction == "left") {
            super.move(-navecSpeed, 0);
            if (facingRight) {
                this.currentImage = new Image(NAVEC_LEFT);
                facingRight = !facingRight;
            }
        } else if (direction == "right") {
            super.move(navecSpeed, 0);
            if (!facingRight) {
                this.currentImage = new Image(NAVEC_RIGHT);
                facingRight = !facingRight;
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
            super.shotFire(player, navecFire, currentImage);
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
     * Method that performs invincible state on Navec
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
     * Method that updates Navec's image
     */
    private void updateImage() {
        if (invincibleState) {
            if (facingRight) {
                this.currentImage = new Image(NAVEC_INV_RIGHT);
            } else {
                this.currentImage = new Image(NAVEC_INV_LEFT);
            }
        } else {
            if (facingRight) {
                this.currentImage = new Image(NAVEC_RIGHT);
            } else {
                this.currentImage = new Image(NAVEC_LEFT);
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
                navecSpeed = navecSpeed * (1.5);
                System.out.println("Sped up, Speed: " + timescale);
            }
        } else if (startTimer && speedDown) {
            if (oneFrame() && timescale > MIN_TIMESCALE) {
                timescale -= 1;
                navecSpeed = navecSpeed * (0.5);
                System.out.println("Slowed down, Speed: " + timescale);
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
    private void stopTimer() {
        counter = 0;
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

    public Point getPosition() {
        return position;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public Image getFireImage() {
        return navecFire;
    }

    public int getHealthPoints() {
        return healthPoints;
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

    public boolean getInvincibleState() {
        return invincibleState;
    }

    public void setInvincibleState(boolean state) {
        invincibleState = state;
    }

    public boolean getGotAttacked() {
        return gotAttacked;
    }

    public void setGotAttacked(boolean attacked) {
        gotAttacked = attacked;
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
    public Point getFirePosition() {
        return firePosition;
    }
}
