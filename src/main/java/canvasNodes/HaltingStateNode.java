package canvasNodes;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class HaltingStateNode extends StateNode{

    private Circle innerCircle;

    public HaltingStateNode(double xPos, double yPos) {
        super(xPos, yPos);
        double innerRadius = 22;
        innerCircle = new Circle(xPos, yPos, innerRadius);
        innerCircle.getStyleClass().add("state");
        Text name = (Text) super.getNodeGroup().getChildren().get(1);
        super.getNodeGroup().getChildren().remove(1);
        super.getNodeGroup().getChildren().addAll(innerCircle, name);
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
        innerCircle.getStyleClass().add("state-active");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        innerCircle.getStyleClass().clear();
        innerCircle.getStyleClass().add("state-halting");
    }
}
