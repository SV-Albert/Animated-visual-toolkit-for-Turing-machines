package main.java.turing;

import main.java.exceptions.InvalidStateTransitionException;
import javafx.util.Pair;
import java.util.HashMap;

public class TransitionFunction {
    private HashMap<Pair<State, Character>, State> stateTransitions; //Current state, tape symbol, next state

    public TransitionFunction(){
        stateTransitions = new HashMap<>();
    }

    public State getNextState(State currentState, char tapeSymbol) throws InvalidStateTransitionException{
        Pair<State, Character> key = new Pair<>(currentState, tapeSymbol);
        for (Pair<State, Character> keyPair: stateTransitions.keySet()) {
            if (keyPair.equals(key)){
                return stateTransitions.get(key);
            }
        }
        throw new InvalidStateTransitionException("No transition from state " + currentState.getName() + " with input '" + tapeSymbol + "' was found");
    }

    public void addTransition(State initialState, char tapeSymbol, State nextState){
        Pair<State, Character> key = new Pair<>(initialState, tapeSymbol);
        stateTransitions.put(key, nextState);
    }

    public HashMap<Pair<State, Character>, State> getData(){
        return stateTransitions;
    }
}
