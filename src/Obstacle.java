import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Obstacle {
    protected final Point position;

    public Obstacle(double startX, double startY) {
        this.position = new Point(startX, startY);
    }

    protected Rectangle getBoundingBox(Image obstacle){
        return new Rectangle(position, obstacle.getWidth(), obstacle.getHeight());
    }
}
