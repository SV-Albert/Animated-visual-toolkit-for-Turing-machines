package canvasNodes;

import javafx.scene.shape.Circle;

public class AcceptingStateNode extends StateNode{
    private Circle innerCircle;
    public AcceptingStateNode(double xPos, double yPos) {
        super(xPos, yPos);
        double innerRadius = 22;
        innerCircle = new Circle(xPos, yPos, innerRadius);
        innerCircle.getStyleClass().add("state-accepting");
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
        innerCircle.getStyleClass().add("state-accepting-active");
    }

    @Override
    public void deactivate(){
        super.deactivate();
        innerCircle.getStyleClass().clear();
        innerCircle.getStyleClass().add("state-accepting");
    }
}