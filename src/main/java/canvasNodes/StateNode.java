package canvasNodes;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import turing.State;

public class StateNode {
    private final double radius = 28;
    private final Group nodeGroup;
    private final Text stateName;
    private final Circle nodeCircle;
    private double xPos;
    private double yPos;
    private State state;
    private boolean isActive;

    public StateNode(double xPos, double yPos){
        this.xPos = xPos;
        this.yPos = yPos;
        nodeCircle = new Circle(xPos, yPos, radius);
        nodeCircle.getStyleClass().add("state");
        stateName = new Text("");
        stateName.getStyleClass().add("state-label");
        stateName.setTranslateX(nodeCircle.getCenterX() - stateName.prefWidth(-1)/3);
        stateName.setTranslateY(nodeCircle.getCenterY() - radius - stateName.prefHeight(-1)/2);
        nodeGroup = new Group();
        nodeGroup.getChildren().addAll(nodeCircle, stateName);
        isActive = false;
    }

    public Group getNodeGroup(){
        return nodeGroup;
    }

    public void setState(State state){
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setName(String name){
        state.setName(name);
        stateName.setText(name);
        stateName.setTranslateX(xPos - stateName.prefWidth(-1)/1.7);
        stateName.setTranslateY(yPos - radius - stateName.prefHeight(-1)/2);
    }

    public void move(double xPos, double yPos){
        this.xPos = xPos;
        this.yPos = yPos;
        state.setPosition(xPos, yPos);
        nodeCircle.setCenterX(xPos);
        nodeCircle.setCenterY(yPos);
        stateName.setTranslateX(xPos - stateName.prefWidth(-1)/1.7);
        stateName.setTranslateY(yPos - radius - stateName.prefHeight(-1)/2);
    }

    public double getX(){
        return xPos;
    }

    public double getY(){
        return yPos;
    }

    public double getRadius(){
        return radius;
    }

    public String getName(){
        return stateName.getText();
    }

    public void activate(){
        isActive = true;
        nodeCircle.getStyleClass().clear();
        nodeCircle.getStyleClass().add("state-active");
    }

    public void deactivate(){
        isActive = false;
        nodeCircle.getStyleClass().clear();
        nodeCircle.getStyleClass().add("state");
    }

    public boolean isActive() {
        return isActive;
    }

}
