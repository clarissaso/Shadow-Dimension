import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

//code by Tharun Dharmawickrema from Project 1 solution
public class Sinkhole extends Obstacle{
    private final Image SINKHOLE = new Image("res/sinkhole.png");
    private final static int DAMAGE_POINTS = 30;
    private boolean isActive;

    public Sinkhole(int startX, int startY){
        super(startX, startY);
        this.isActive = true;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        if (isActive){
            SINKHOLE.drawFromTopLeft(this.position.x, this.position.y);
        }
    }

    public Rectangle getBoundingBox(){
        return super.getBoundingBox(SINKHOLE);
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
}
