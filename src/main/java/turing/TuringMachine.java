package java.turing;

import main.java.exceptions.InvalidStateTransitionException;
import main.java.exceptions.InvalidSymbolException;

import java.util.HashMap;
import java.util.HashSet;


public class TuringMachine {
    private String name;
    private HashSet<Character> alphabet;
    private HashMap<String, State> states;
    private State initialState;
    private State currentState;
    private TransitionFunction transitionFunction;

    public TuringMachine(String name, HashSet<Character> alphabet, HashMap<String, State> states, State initialState, TransitionFunction transitionFunction){
        this.name = name;
        this.alphabet = alphabet;
        this.states = states;
        this.initialState = initialState;
        currentState = initialState;
        this.transitionFunction = transitionFunction;
    }

    public void nextState(char tapeSymbol) throws InvalidStateTransitionException, InvalidSymbolException{
        if (alphabet.contains(tapeSymbol) || tapeSymbol == '~'){
            currentState = transitionFunction.getNextState(currentState, tapeSymbol);
        }
        else{
            throw new InvalidSymbolException("Symbol '" + tapeSymbol + "' is not part of the Turing Machine's alphabet");
        }
    }

    public State getCurrentState(){
        return currentState;
    }

    public String getName(){
        return name;
    }

    public HashSet<Character> getAlphabet(){
        return alphabet;
    }

    public HashMap<String, State> getStates(){
        return states;
    }

    public State getInitialState(){
        return initialState;
    }

    public TransitionFunction getTransitionFunction(){
        return transitionFunction;
    }

}
