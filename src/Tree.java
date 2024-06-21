import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Tree extends Obstacle {
    private final Image TREE = new Image("res/tree.png");

    public Tree(int startX, int startY){
        super(startX, startY);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        TREE.drawFromTopLeft(this.position.x, this.position.y);
    }

    public Rectangle getBoundingBox(){
        return super.getBoundingBox(TREE);
    }


}
