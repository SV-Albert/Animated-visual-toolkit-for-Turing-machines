package turing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TuringMachine {
    private String name;
    private final Map<String, State> states;
    private final State initialState;
    private State currentState;
    private final Set<TuringTransition> transitionFunction;
    private final Map<State, Set<TuringTransition>> transitionMap;

    public TuringMachine(State initialState){
        this.initialState = initialState;
        currentState = initialState;
        states = new HashMap<>();
        states.put(initialState.getName(), initialState);
        transitionMap = new HashMap<>();
        transitionFunction = new HashSet<>();
    }

    public TuringMachine(String name, Map<String, State> states, State initialState,
                         Set<TuringTransition> turingTransitionFunction, Map<State, Set<TuringTransition>> transitionMap){
        this.name = name;
        this.states = states;
        this.initialState = initialState;
        currentState = initialState;
        this.transitionMap = transitionMap;
        this.transitionFunction = turingTransitionFunction;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addState(State state){
        states.put(state.getName(), state);
    }

    public void addTransition(TuringTransition turingTransition){
        State fromState = turingTransition.getFromState();
        State toState = turingTransition.getToState();
        transitionFunction.add(turingTransition);
        if(transitionMap.containsKey(fromState)){
            transitionMap.get(fromState).add(turingTransition);
        }
        else{
            Set<TuringTransition> stateTuringTransitions = new HashSet<>();
            stateTuringTransitions.add(turingTransition);
            transitionMap.put(fromState, stateTuringTransitions);
        }
        if(!states.containsValue(fromState)){
            states.put(fromState.getName(), fromState);
        }
        if(!states.containsValue(toState)){
            states.put(toState.getName(), toState);
        }
    }

    public void removeState(State state){
        states.remove(state.getName());
        if(transitionMap.containsKey(state)){
            for(TuringTransition turingTransition : transitionMap.get(state)){
                transitionFunction.remove(turingTransition);
            }
        }
        transitionMap.remove(state);
    }

    public void removeTransition(TransitionRule transitionRule){
        TuringTransition transitionToRemove = null;
        for(TuringTransition turingTransition : transitionFunction){
            if (turingTransition.getTransitionRule() == transitionRule){
                transitionToRemove = turingTransition;
            }
        }
        if(transitionToRemove != null){
            transitionFunction.remove(transitionToRemove);
            transitionMap.get(transitionToRemove.getFromState()).remove(transitionToRemove);
        }
    }

    public void setCurrentState(State state) {
        currentState = state;
    }

    public void reset(){
        currentState = initialState;
    }

    public State getCurrentState(){
        return currentState;
    }

    public String getName(){
        return name;
    }

    public Map<String, State> getStates(){
        return states;
    }

    public State getInitialState(){
        return initialState;
    }

    public Set<TuringTransition> getTransitionFunction(){
        return transitionFunction;
    }

    public Set<TuringTransition> getTransitionsFromCurrentState(){
        return transitionMap.get(currentState);
    }

    public TuringMachine getCopy(){
        return new TuringMachine(name, states, initialState, transitionFunction, transitionMap);
    }
}
