package canvasNodes;

import javafx.scene.shape.Circle;

public class RejectingStateNode extends StateNode{
    private Circle innerCircle;

    public RejectingStateNode(double xPos, double yPos) {
        super(xPos, yPos);
        double innerRadius = 22;
        innerCircle = new Circle(xPos, yPos, innerRadius);
        innerCircle.getStyleClass().add("state-rejecting");
        super.getNodeGroup().getChildren().add(innerCircle);
    }

    @Override
    public void move(double xPos, double yPos){
        super.move(xPos, yPos);
        innerCircle.setCenterX(xPos);
        innerCircle.setCenterY(yPos);
    }

    @Override
    public void activate(){
        super.activate();
        innerCircle.getStyleClass().clear();
        innerCircle.getStyleClass().add("state-rejecting-active");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        innerCircle.getStyleClass().clear();
        innerCircle.getStyleClass().add("state-rejecting");
    }

}
