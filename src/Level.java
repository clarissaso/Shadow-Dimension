import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Point;

public class Level {
    protected Point topLeft;
    protected Point bottomRight;
    protected final static int TITLE_FONT_SIZE = 75;
    protected final static int INSTRUCTION_FONT_SIZE = 40;
    protected final Font TITLE_FONT = new Font("res/frostbite.ttf", TITLE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    protected static String INSTRUCTION_MESSAGE;
    protected final static int INS_X_OFFSET = 90;
    protected final static int INS_Y_OFFSET = 190;

    public Level() {

    }

    /**
     * Method that checks if Fae has gone out-of-bounds and performs corresponding action
     */
    protected void checkOutOfBounds(Player player) {
        Point currentPosition = player.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)) {
            player.moveBack();
        }
    }

    /**
     * Method used to draw the start screen title and instructions
     */
    protected void drawStartScreen(String message, double pointX, double pointY){
        INSTRUCTION_FONT.drawString(message, pointX, pointY);
    }

    /**
     * Method used to draw the game background
     */
    protected void drawBackground(Image background) {
        background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
    }

    /**
     * Method used to draw end screen messages
     */
    protected void drawMessage(Font font, String message, int fontSize){
        font.drawString(message, (Window.getWidth()/2.0 - (font.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (fontSize/2.0)));
    }


}
