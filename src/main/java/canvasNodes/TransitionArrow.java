package canvasNodes;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;
import turing.State;
import turing.TransitionRule;

import java.util.ArrayList;
import java.util.List;

public class TransitionArrow {
    private final StateNode from;
    private final StateNode to;
    private final Line line;
    private final QuadCurve loop;
    private final Group lineGroup;
    private final List<TransitionRule> transitionRules;
    private final Label rulesLabel;

    public TransitionArrow(StateNode from, StateNode to){
        this.from = from;
        this.to = to;
        line = new Line();
        line.getStyleClass().add("transition-arrow");
        loop = new QuadCurve();
        loop.getStyleClass().add("transition-loop");
        rulesLabel = new Label();
        rulesLabel.getStyleClass().add("transition-label");
        lineGroup = new Group();
        transitionRules = new ArrayList<>();
        repositionLine();
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
            rulesLabel.setTranslateX((x1 + x2)/2);
            rulesLabel.setTranslateY((y1 + y2)/2);
            lineGroup.getChildren().clear();
            lineGroup.getChildren().add(line);
            lineGroup.getChildren().add(rulesLabel);
        }
    }

    public void addRules(TransitionRule rule){
        transitionRules.add(rule);
        addRuleToLabel(rule);
    }

    private void addRuleToLabel(TransitionRule rule){
        if(!rulesLabel.getText().isEmpty()){
            rulesLabel.setText(rulesLabel.getText()  + '\n');
        }
        rulesLabel.setText(rulesLabel.getText() + rule.getReadSymbol() + "|" + rule.getWriteSymbol() + "|" + rule.getDirection());
        repositionLine();
    }

    public void removeTransitionRule(TransitionRule rule){
        transitionRules.remove(rule);
        rulesLabel.setText("");
        for (TransitionRule transitionRule:transitionRules) {
            addRuleToLabel(transitionRule);
        }
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
