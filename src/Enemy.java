import bagel.*;
import bagel.util.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Enemy {
    private Random rand = new Random();
    private final static int Y_HEALTH_OFFSET = 6;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    protected Point position;
    protected Point firePosition;
    protected boolean orientation;
    protected String direction;
    private final static DrawOptions ROTATE = new DrawOptions();

    public Enemy(int startX, int startY) {
        this.position = new Point(startX, startY);
        this.direction = randDirection();
        this.firePosition = new Point(startX, startY);
        this.orientation = true;
    }

    /**
     * Method that moves Enemy given the direction
     */
    protected void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that randomizes Enemy movement
     */
    protected String randDirection() {
        Set<String> directions = new HashSet<String>();

        directions.add("left");
        directions.add("right");
        directions.add("up");
        directions.add("down");

        String[] arrayDirection = directions.toArray(new String[directions.size()]);
        int randNum = rand.nextInt(directions.size());
        return arrayDirection[randNum];
    }

    /**
     * Method that moves Enemy to the opposite the direction
     */
    protected void moveOpposite() {
        if (direction == "up") {
            direction = "down";
        } else if (direction == "down") {
            direction = "up";
        } else if (direction == "left") {
            direction = "right";
        } else if (direction == "right") {
            direction = "left";
        }
    }

    /**
     * Method that shots fire to a direction according to player's position
     */
    protected void shotFire(Player player, Image fire, Image enemy) {
        if (player.getPosition().x <= position.x) {
            if (player.getPosition().y <= position.y) {
                fireOffset(-fire.getWidth(), -fire.getHeight());
                fire.drawFromTopLeft(firePosition.x, firePosition.y);
                orientation = true;
            } else if (player.getPosition().y > position.y) {
                fireOffset(-fire.getHeight(), enemy.getHeight());
                fire.drawFromTopLeft(firePosition.x, firePosition.y, ROTATE.setRotation(270 * Math.PI / 180));
                orientation = false;
            }
        } else if (player.getPosition().x > position.x) {
            if (player.getPosition().y <= position.y) {
                fireOffset(enemy.getWidth(), -fire.getWidth());
                fire.drawFromTopLeft(firePosition.x, firePosition.y, ROTATE.setRotation(90 * Math.PI / 180));
                orientation = false;
            } else if (player.getPosition().y > position.y) {
                fireOffset(enemy.getWidth(), enemy.getHeight());
                fire.drawFromTopLeft(firePosition.x, firePosition.y, ROTATE.setRotation(180 * Math.PI / 180));
                orientation = true;

            }
        }
    }

    /**
     * Method that gives fire position
     */
    protected void fireOffset(double fireX, double fireY) {
        double newFireX = position.x + fireX;
        double newFireY = position.y + fireY;
        this.firePosition = new Point(newFireX, newFireY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    protected void renderHealthPoints(int healthPoints, int MAX_HEALTH_POINTS){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", position.x, position.y - Y_HEALTH_OFFSET, COLOUR);
    }
}
