package turing;

public class TuringTransition implements Comparable<TuringTransition>{

    private final State fromState;
    private final State toState;
    private final TransitionRule transitionRule;


    public TuringTransition(State fromState, State toState, TransitionRule transitionRule){
        this.fromState = fromState;
        this.toState = toState;
        this.transitionRule = transitionRule;
    }

    public State getFromState(){
        return fromState;
    }

    public State getToState(){
        return toState;
    }

    public TransitionRule getTransitionRule(){
        return transitionRule;
    }

    public String toString(){
        return fromState.getName() + "|" + toState.getName() + "|" + transitionRule.toString();
    }

    @Override
    public int compareTo(TuringTransition o) {
        return this.toString().compareTo(o.toString());
    }
}
