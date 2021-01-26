package turing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TuringMachine {
    private final String name;
    private final Set<Character> alphabet;
    private final Map<String, State> states;
    private final State initialState;
    private State currentState;
    private final Set<Transition> transitionFunction;
    private final Map<State, Set<Transition>> transitionMap;

    public TuringMachine(String name, HashMap<String, State> states, State initialState){
        this.name = name;
        this.states = states;
        this.initialState = initialState;
        currentState = initialState;
        alphabet = new HashSet<>();
        transitionMap = new HashMap<>();
        transitionFunction = new HashSet<>();
    }

    public void addTransition(Transition transition){
        State fromState = transition.getFromState();
        State toState = transition.getToState();
        char readSymbol = transition.getReadSymbol();
        char writeSymbol = transition.getWriteSymbol();
        transitionFunction.add(transition);
        if(transitionMap.containsKey(fromState)){
            transitionMap.get(fromState).add(transition);
        }
        else{
            Set<Transition> stateTransitions = new HashSet<>();
            stateTransitions.add(transition);
            transitionMap.put(fromState, stateTransitions);
        }
        if(!states.containsValue(fromState)){
            states.put(fromState.getName(), fromState);
        }
        if(!states.containsValue(toState)){
            states.put(toState.getName(), toState);
        }
        if(!alphabet.contains(readSymbol)){
            alphabet.add(readSymbol);
        }
        if(!alphabet.contains(writeSymbol)){
            alphabet.add(writeSymbol);
        }
    }

    public void setCurrentState(State state) {
        currentState = state;
    }

    public State getCurrentState(){
        return currentState;
    }

    public String getName(){
        return name;
    }

    public Set<Character> getAlphabet(){
        return alphabet;
    }

    public Map<String, State> getStates(){
        return states;
    }

    public State getInitialState(){
        return initialState;
    }

    public Set<Transition> getTransitionFunction(){
        return transitionFunction;
    }

    public Set<Transition> getTransitionsFromCurrentState(){
        return transitionMap.get(currentState);
    }

//    public boolean isCharLegal(char read){
//        return alphabet.contains(read);
//    }

}
