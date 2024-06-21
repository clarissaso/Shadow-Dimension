import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import java.util.*;

//code by Tharun Dharmawickrema from Project 1 solution
public class Player {
    private final int REFRESH_RATE = 60;
    private final static String FAE_LEFT = "res/fae/faeLeft.png";
    private final static String FAE_RIGHT = "res/fae/faeRight.png";
    private final static String FAE_ATTACK_LEFT = "res/fae/faeAttackLeft.png";
    private final static String FAE_ATTACK_RIGHT = "res/fae/faeAttackRight.png";
    private final static int MAX_HEALTH_POINTS = 100;
    private final static int DAMAGE_POINTS = 20;
    private final static double MOVE_SIZE = 2;
    private final static int WIN_X = 950;
    private final static int WIN_Y = 670;

    private final static int HEALTH_X = 20;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private Demon demon;
    private Navec navec;
    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private boolean facingRight;
    private boolean attacked;
    private boolean gotAttacked;
    private boolean attackState;
    private boolean idleState;
    private boolean invincibleState;
    private static int counter;
    private boolean startTimer;
    private boolean attackAttempt;
    private boolean damaged;
    private Timer timer;
    private static double ms;


    public Player(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(FAE_RIGHT);
        this.facingRight = true;
        this.attackState = false;
        this.idleState = true;
        this.invincibleState = false;
        this.attackAttempt = false;
        this.attacked = false;
        this.gotAttacked = false;
        this.damaged = false;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(Input input){
        if (input.isDown(Keys.UP)){
            setPrevPosition();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)){
            setPrevPosition();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)){
            setPrevPosition();
            move(-MOVE_SIZE,0);
            if (facingRight) {
                if (attackState && !idleState) {
                    this.currentImage = new Image(FAE_ATTACK_LEFT);
                } else if (!attackState && idleState){
                    this.currentImage = new Image(FAE_LEFT);
                }
                facingRight = !facingRight;
            }
        } else if (input.isDown(Keys.RIGHT)) {
            setPrevPosition();
            move(MOVE_SIZE, 0);
            if (!facingRight) {
                if (attackState && !idleState) {
                    this.currentImage = new Image(FAE_ATTACK_RIGHT);
                } else if (!attackState && idleState) {
                    this.currentImage = new Image(FAE_RIGHT);
                }
                facingRight = !facingRight;
            }
        }
        // attack state
        if (input.isDown(Keys.A)) {
            startTimer = true;
            attackAttempt = true;
        }
        if (startTimer && attackAttempt) {
            ms = counter / 0.06;
            counter++;
            if (ms <= 1000) {
                attackState = true;
                idleState = false;
                if (facingRight) {
                    this.currentImage = new Image(FAE_ATTACK_RIGHT);
                } else {
                    this.currentImage = new Image(FAE_ATTACK_LEFT);
                }
            } else if (ms < 3000) {
                idleState = true;
                attackState = false;
            } else {
                attackAttempt = false;
                startTimer = false;
                stopTimer();
            }
        }

        if (idleState) {
            if (facingRight) {
                this.currentImage = new Image(FAE_RIGHT);
            } else {
                this.currentImage = new Image(FAE_LEFT);
            }
        }
        if (gotAttacked) {
            startTimer = true;
            invincibleState = true;
        }
        if (startTimer && gotAttacked) {
            ms = counter / 0.06;
            counter++;
            if (ms <= 1000/60) {
                damaged = true;
            } else {
                damaged = false;
            }
            if (ms >= 3000) {
                invincibleState = false;
                startTimer = false;
                gotAttacked = false;
                stopTimer();
            }
        }

        this.currentImage.drawFromTopLeft(position.x, position.y);
        renderHealthPoints();
    }

    private double startTimer() {
        counter++;
        ms = counter / 0.06;
        return ms;
    }

    private void stopTimer() {
        counter = 0;
    }

    /**
     * Method that stores Fae's previous position
     */
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves Fae back to previous position
     */
    public void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) this.healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if Fae's health has depleted
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Method that checks if Fae has found the gate
     */
    public boolean reachedGate(){
        return (this.position.x >= WIN_X) && (this.position.y >= WIN_Y);
    }

    public Point getPosition() {
        return position;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public boolean getAttackAttempt() {
        return attackAttempt;
    }

    public boolean getAttacked() {
        return attacked;
    }

    public boolean getAttackState() {
        return attackState;
    }

    public void setGotAttacked(boolean gotAttacked) {
        this.gotAttacked = gotAttacked;
    }

    public boolean getDamaged() {
        return damaged;
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

    public int getDamagePoints(){
        return DAMAGE_POINTS;
    }

}

