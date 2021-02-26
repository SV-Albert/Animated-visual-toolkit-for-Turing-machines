package execution;

import turing.Transition;

import java.util.ArrayList;
import java.util.List;

public class ExecutionNode {
    private final Transition transition;
    private final String tapeState;
    private final ExecutionNode parent;
    private List<ExecutionNode> children;
    private final boolean isRoot;

    public ExecutionNode(boolean isRoot, Transition transition, String tapeState, ExecutionNode parent){
        this.isRoot = isRoot;
        this.transition = transition;
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
            sb.append("From state:" ).append(transition.getFromState().getName()).append('\n');
            sb.append("To state:" ).append(transition.getToState().getName()).append('\n');
            sb.append("Read:" ).append(transition.getReadSymbol()).append('\n');
            sb.append("Write:" ).append(transition.getWriteSymbol()).append('\n');
            sb.append("Move:" ).append(transition.getDirection()).append('\n');
        }
        return sb.toString();
    }

}
