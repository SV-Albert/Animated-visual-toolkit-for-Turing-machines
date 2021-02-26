package turing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TuringMachine {
    private String name;
    private final Set<Character> alphabet;
    private final Map<String, State> states;
    private final State initialState;
    private State currentState;
    private final Set<Transition> transitionFunction;
    private final Map<State, Set<Transition>> transitionMap;

    public TuringMachine(Map<String, State> states, State initialState){
        this.states = states;
        this.initialState = initialState;
        currentState = initialState;
        alphabet = new HashSet<>();
        transitionMap = new HashMap<>();
        transitionFunction = new HashSet<>();
    }

    public TuringMachine(String name, Map<String, State> states, State initialState,
                         Set<Character> alphabet, Set<Transition> transitionFunction, Map<State, Set<Transition>> transitionMap){
        this.name = name;
        this.states = states;
        this.initialState = initialState;
        currentState = initialState;
        this.alphabet = alphabet;
        this.transitionMap = transitionMap;
        this.transitionFunction = transitionFunction;
    }

    public void setName(String name){
        this.name = name;
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
        alphabet.add(readSymbol);
        alphabet.add(writeSymbol);
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

    public TuringMachine getCopy(){
        return new TuringMachine(name, states, initialState, alphabet, transitionFunction, transitionMap);
    }
//    public boolean isCharLegal(char read){
//        return alphabet.contains(read);
//    }

}
