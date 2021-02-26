package canvasNodes;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


public class TransitionArrow {
    private final StateNode from;
    private final StateNode to;
    private final Line line;
    private final Group lineGroup;
//    private Line arrowHeadTop;
//    private Line arrowHeadBottom;
    private Text rules;

    public TransitionArrow(StateNode from, StateNode to){
        this.from = from;
        this.to = to;
        line = new Line();
        line.getStyleClass().add("transition-arrow");
//        arrowHeadTop = new Line();
//        arrowHeadTop.getStyleClass().add("transition-arrow");
//        arrowHeadBottom = new Line();
//        arrowHeadBottom.getStyleClass().add("transition-arrow");
        rules = new Text();
        rules.getStyleClass().add("state-label");
        lineGroup = new Group();
        lineGroup.getChildren().add(line);
        lineGroup.getChildren().add(rules);
        repositionLine();
    }

    public void repositionLine(){
        double radius = from.getRadius()+1;
        double angle = Math.atan(Math.abs(from.getY() - to.getY())/Math.abs(from.getX() - to.getX()));
        double x1;
        double y1;
        double x2;
        double y2;
        if(from.getX() <= to.getX()){
            x1 = from.getX() + Math.cos(angle)*radius;
            x2 = to.getX() - Math.cos(angle)*radius;
        }
        else {
            x1 = from.getX() - Math.cos(angle)*radius;
            x2 = to.getX() + Math.cos(angle)*radius;
        }
        if(from.getY() <= to.getY()) {
            y1 = from.getY() + Math.sin(angle)*radius;
            y2 = to.getY() - Math.sin(angle)*radius;
        }
        else {
            y1 = from.getY() - Math.sin(angle)*radius;
            y2 = to.getY() + Math.sin(angle)*radius;
        }
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
        rules.setX((x1 + x2)/2);
        rules.setY((y1 + y2)/2);
        rules.setRotate(angle);
    }

    public void addRules(char read, char write, char direction){
//        this.readSymbol = readSymbol;
//        this.writeSymbol = writeSymbol;
//        this.direction = direction;
        rules.setText(read + "|" + write + "|" + direction);
        repositionLine();
    }

    public Group getLineGroup(){
        return lineGroup;
    }


}
