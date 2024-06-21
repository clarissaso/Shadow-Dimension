import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

//code by Tharun Dharmawickrema from Project 1 solution
public class Wall extends Obstacle{
    private final Image WALL = new Image("res/wall.png");

    public Wall(int startX, int startY){
        super(startX, startY);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        WALL.drawFromTopLeft(this.position.x, this.position.y);
    }

    public Rectangle getBoundingBox(){

        return super.getBoundingBox(WALL);
    }
}
