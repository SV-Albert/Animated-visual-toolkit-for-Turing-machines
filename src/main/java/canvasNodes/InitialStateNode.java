package canvasNodes;

import javafx.scene.image.ImageView;

public class InitialStateNode extends StateNode{

    private ImageView arrowImage;
    private final int xOffset = 60;
    private final int yOffset = 17;

    public InitialStateNode(double xPos, double yPos) {
        super(xPos, yPos);
        super.activate();
        try {
            arrowImage = new ImageView("arrow_right.png");
            arrowImage.setX(xPos - xOffset);
            arrowImage.setY(yPos - yOffset);
            super.getNodeGroup().getChildren().add(arrowImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void move(double xPos, double yPos){
        super.move(xPos, yPos);
        arrowImage.setX(xPos - xOffset);
        arrowImage.setY(yPos - yOffset);
    }
}
