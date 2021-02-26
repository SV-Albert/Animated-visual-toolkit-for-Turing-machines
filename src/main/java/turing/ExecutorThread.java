package turing;

import execution.ExecutionNode;

import java.util.ArrayList;
import java.util.List;

public class ExecutorThread implements Runnable{

    private final TuringMachine turingMachine;
    private final Tape tape;
    private ExecutionNode currentNode;
    private final TuringMachineHandler handler;

    public ExecutorThread(TuringMachine turingMachine, Tape tape, ExecutionNode currentNode, Transition nextTransition, TuringMachineHandler handler) {
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.currentNode = currentNode;
        this.handler = handler;
        if (nextTransition != null){
            step(nextTransition);
        }
    }

    @Override
    public void run() {
        boolean finished = false;
        while(!finished){
            System.out.println(currentNode.toString());
            finished = nextState();
        }
    }

    private boolean nextState(){
        if(turingMachine.getCurrentState().isAccepting()){
            return true;
        }
        else{
            char read = tape.read();
            List<Transition> transitions = findTransitions(read);
            if(transitions.size() > 1){
                for (int i = 1; i < transitions.size(); i++) {
                    ExecutorThread executorThread = new ExecutorThread(turingMachine.getCopy(), tape.getCopy(), currentNode, transitions.get(i), handler);
                    handler.addThread(executorThread);
                    executorThread.run();
                }
            }
            if (transitions.size() == 0){
                return true;
            }
            else {
                Transition transition = transitions.get(0);
                step(transition);
                return false;
            }
        }
    }

    private void step(Transition transition){
        recordStep(transition);
        try{
            tape.write(transition.getWriteSymbol());
            tape.move(transition.getDirection());
            turingMachine.setCurrentState(transition.getToState());
        }
        catch (Exception e){
            System.out.println("DO SOMETHING ABOUT EXCEPTION HANDLING");
        }
    }

    private void recordStep(Transition transition){
        ExecutionNode newNode = new ExecutionNode(false, transition, tape.toString(), currentNode);
        currentNode.addChild(newNode);
        currentNode = newNode;
    }

    private List<Transition> findTransitions(char read){
        List<Transition> foundTransitions = new ArrayList<>();
        for (Transition transition:turingMachine.getTransitionsFromCurrentState()) {
            if(transition.getReadSymbol() == read){
                foundTransitions.add(transition);
            }
        }
//        if(foundTransitions.size() > 0){
            return foundTransitions;
//        }
//        else{
//            throw new InvalidSymbolException("State " + turingMachine.getCurrentState().getName() + " has no operations for input '" + read + "'");
//        }
    }
}
