package canvasNodes;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
//    private final ImageView arrowHead;
//    private final double arrowHeadLength = 25;
//    private final double arrowHeadWidth = 15;

    public TransitionArrow(StateNode from, StateNode to){
        this.from = from;
        this.to = to;
        line = new QuadCurve();
        line.getStyleClass().add("transition-loop");
//        Image arrowImage = new Image(getClass().getResourceAsStream("../arrow_head.png"));
//        arrowHead = new ImageView(arrowImage);
//        arrowHead.setFitHeight(arrowHeadLength);
//        arrowHead.setFitWidth(arrowHeadWidth);
        loop = new QuadCurve();
        loop.getStyleClass().add("transition-loop");
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
            rulesLabel.setTranslateX(x + radius*2);
            rulesLabel.setTranslateY(y - radius*2);
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
            double headX;
            double headY;
            if(from.getY() <= to.getY()) {
                fromY = from.getY() + Math.sin(angle)*radius;
                toY = to.getY() - Math.sin(angle)*radius;
//                headY = toY - Math.sin(angle) * arrowHeadWidth/2;
            }
            else {
                fromY = from.getY() - Math.sin(angle)*radius;
                toY = to.getY() + Math.sin(angle)*radius;
//                headY = toY + Math.sin(angle) * arrowHeadWidth/2;
            }
            if(from.getX() <= to.getX()){
                fromX = from.getX() + Math.cos(angle)*radius;
                toX = to.getX() - Math.cos(angle)*radius;
                controlY = Math.max(fromY, toY) - Math.min(Math.abs(fromX - toX), 50);
//                rulesLabel.setTranslateY(((fromY + toY)/2)+20);
//                headX = toX - Math.cos(angle)*arrowHeadLength/2;
            }
            else {
                fromX = from.getX() - Math.cos(angle)*radius;
                toX = to.getX() + Math.cos(angle)*radius;
                controlY = Math.min(fromY, toY) + Math.min(Math.abs(fromX - toX), 50);
//                rulesLabel.setTranslateY(((fromY + toY)/2)-20);
//                headX = toX + Math.cos(angle)*arrowHeadLength/2;
            }
            controlX = Math.min(fromX, toX) + (Math.abs(fromX - toX)/2);
            line.setStartX(fromX);
            line.setStartY(fromY);
            line.setEndX(toX);
            line.setEndY(toY);
            line.setControlX(controlX);
            line.setControlY(controlY);
//            arrowHead.setLayoutX(headX);
//            arrowHead.setLayoutY(headY);
//            arrowHead.setRotate(90 - Math.toDegrees(angle));
//            rulesLabel.setTranslateX((fromX + toX)/2);
            rulesLabel.setTranslateX(controlX);
            rulesLabel.setTranslateY(controlY);
            lineGroup.getChildren().clear();
//            lineGroup.getChildren().addAll(line, arrowHead, rulesLabel);
            lineGroup.getChildren().addAll(line, rulesLabel);
        }
    }

    public void addRules(TransitionRule rule){
        transitionRules.add(rule);
        if(!rule.isEmpty()) {
            refreshRulesLabel();
        }
    }

    private void addRuleToLabel(TransitionRule rule){
        if(!rulesLabel.getText().isEmpty()){
            rulesLabel.setText(rulesLabel.getText()  + '\n');
        }
        rulesLabel.setText(rulesLabel.getText() + rule.getReadSymbol() + "|" + rule.getWriteSymbol() + "|" + rule.getDirection());
        repositionLine();
    }

    public void refreshRulesLabel(){
        rulesLabel.setText("");
        for(TransitionRule rule: transitionRules){
            rulesLabel.setText(rulesLabel.getText() + rule.getReadSymbol() + "|" + rule.getWriteSymbol() + "|" + rule.getDirection() + '\n');
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
