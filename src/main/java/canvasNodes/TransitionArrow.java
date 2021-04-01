package canvasNodes;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import turing.TransitionRule;

import java.util.ArrayList;
import java.util.List;

public class TransitionArrow {
    private final StateNode from;
    private final StateNode to;
    private final QuadCurve line;
    private final QuadCurve loop;
    private final Group lineGroup;
    private final List<TransitionRule> transitionRules;
    private final Label rulesLabel;
    private final Line headTop;
    private final Line headBottom;

    public TransitionArrow(StateNode from, StateNode to){
        this.from = from;
        this.to = to;
        line = new QuadCurve();
        line.getStyleClass().add("transition-loop");
        loop = new QuadCurve();
        loop.getStyleClass().add("transition-loop");
        headTop = new Line();
        headTop.getStyleClass().add("transition-arrow");
        headBottom = new Line();
        headBottom.getStyleClass().add("transition-arrow");
        rulesLabel = new Label();
        rulesLabel.getStyleClass().add("transition-label");
        lineGroup = new Group();
        transitionRules = new ArrayList<>();
        if(from != null && to != null){
            repositionLine();
        }
    }

    public void repositionLine(){
        double radius = from.getRadius()+1;
        if(from == to){
            double x = from.getX();
            double y = from.getY();
            loop.setStartX(x);
            loop.setStartY(y - radius);
            loop.setEndX(x + radius);
            loop.setEndY(y);
            loop.setControlX(x + radius*2);
            loop.setControlY(y - radius*2);
            double rulesLabelYOffset = (transitionRules.size() + 1) * 16 + radius;
            rulesLabel.setTranslateX(x);
            rulesLabel.setTranslateY(Math.max(y - rulesLabelYOffset, 0));
            lineGroup.getChildren().clear();
            lineGroup.getChildren().add(loop);
            lineGroup.getChildren().add(rulesLabel);
        }
        else{
            double angle = Math.atan(Math.abs(from.getY() - to.getY())/Math.abs(from.getX() - to.getX()));
            double toX;
            double toY;
            double fromX;
            double fromY;
            double controlX;
            double controlY;
            if(from.getY() <= to.getY()) {
                fromY = from.getY() + Math.sin(angle)*radius;
                toY = to.getY() - Math.sin(angle)*radius;
            }
            else {
                fromY = from.getY() - Math.sin(angle)*radius;
                toY = to.getY() + Math.sin(angle)*radius;
            }
            if(from.getX() <= to.getX()){
                fromX = from.getX() + Math.cos(angle)*radius;
                toX = to.getX() - Math.cos(angle)*radius;
            }
            else {
                fromX = from.getX() - Math.cos(angle)*radius;
                toX = to.getX() + Math.cos(angle)*radius;
            }

            headTop.setStartX(toX);
            headTop.setStartY(toY);
            headBottom.setStartX(toX);
            headBottom.setStartY(toY);

            //https://github.com/citiususc/jflap-lib/blob/master/jflaplib-core/src/main/java/edu/duke/cs/jflap/gui/viewer/CurvedArrow.java
            //---------
            double distance = Math.sqrt(Math.pow(Math.abs(fromX - toX), 2) + Math.pow(Math.abs(fromY - toY), 2));
            controlX = (fromX + toX) / 2 + 25 * (toY - fromY) / distance;
            controlY = (fromY + toY) / 2 - 25 * (toX - fromX) / distance;

            double arrowAngle = Math.atan2((fromX - toX), (fromY - toY));
            headTop.setEndX((Math.sin(arrowAngle - 0.8) * 10) + toX);
            headTop.setEndY((Math.cos(arrowAngle - 0.8) * 10) + toY);
            headBottom.setEndX((Math.sin(arrowAngle + 0.4) * 10) + toX);
            headBottom.setEndY((Math.cos(arrowAngle + 0.4) * 10) + toY);
            //---------

            line.setStartX(fromX);
            line.setStartY(fromY);
            line.setEndX(toX);
            line.setEndY(toY);
            line.setControlX(controlX);
            line.setControlY(controlY);
            rulesLabel.setTranslateX(controlX);
            rulesLabel.setTranslateY(controlY);


            lineGroup.getChildren().clear();
            lineGroup.getChildren().addAll(line, rulesLabel, headTop, headBottom);
        }
    }

    public void addRules(TransitionRule rule){
        transitionRules.add(rule);
        if(!rule.isEmpty()) {
            refreshRulesLabel();
        }
    }

    public void refreshRulesLabel(){
        rulesLabel.setText("");
        for (int i = 0; i < transitionRules.size() - 1; i++) {
            TransitionRule rule = transitionRules.get(i);
            rulesLabel.setText(rulesLabel.getText() + rule.getReadSymbol() + "|" + rule.getWriteSymbol() + "|" + rule.getDirection() + '\n');
        }
        if(transitionRules.size() > 0){
            TransitionRule rule = transitionRules.get(transitionRules.size() - 1);
            rulesLabel.setText(rulesLabel.getText() + rule.getReadSymbol() + "|" + rule.getWriteSymbol() + "|" + rule.getDirection());
        }
        repositionLine();
    }

    public void removeTransitionRule(TransitionRule rule){
        transitionRules.remove(rule);
        refreshRulesLabel();
    }

    public boolean containsTransitionRule(String rule){
        for (TransitionRule transitionRule: transitionRules) {
            if(transitionRule.toString().equals(rule.toString())){
                return true;
            }
        }
        return false;
    }

    public List<TransitionRule> getTransitionRules() {
        return transitionRules;
    }

    public Group getLineGroup(){
        return lineGroup;
    }

    public StateNode getFrom(){
        return from;
    }

    public StateNode getTo(){
        return to;
    }
}
