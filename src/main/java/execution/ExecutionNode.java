package execution;

import turing.TuringTransition;

import java.util.ArrayList;
import java.util.List;

public class ExecutionNode {
    private final TuringTransition turingTransition;
    private final String tapeState;
    private final ExecutionNode parent;
    private List<ExecutionNode> children;
    private final boolean isRoot;

    public ExecutionNode(boolean isRoot, TuringTransition turingTransition, String tapeState, ExecutionNode parent){
        this.isRoot = isRoot;
        this.turingTransition = turingTransition;
        this.tapeState = tapeState;
        this.parent = parent;
        children = new ArrayList<>();
    }

    public void addChild(ExecutionNode child){
        children.add(child);
    }

    public ExecutionNode getParent(){
        return parent;
    }

    public List<ExecutionNode> getChildren(){
        return children;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(!isRoot){
            sb.append("Tape: ").append(tapeState).append('\n');
            sb.append("----------").append('\n');
            sb.append("From state:" ).append(turingTransition.getFromState().getName()).append('\n');
            sb.append("To state:" ).append(turingTransition.getToState().getName()).append('\n');
            sb.append("Read:" ).append(turingTransition.getTransitionRule().getReadSymbol()).append('\n');
            sb.append("Write:" ).append(turingTransition.getTransitionRule().getWriteSymbol()).append('\n');
            sb.append("Move:" ).append(turingTransition.getTransitionRule().getDirection()).append('\n');
        }
        return sb.toString();
    }

}
