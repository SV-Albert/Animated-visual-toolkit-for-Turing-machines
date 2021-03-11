package turing;

public class TuringTransition {

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
}
