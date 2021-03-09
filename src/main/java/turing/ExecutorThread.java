package turing;

import execution.ExecutionNode;

import java.util.ArrayList;
import java.util.List;

public class ExecutorThread implements Runnable{

    private final TuringMachine turingMachine;
    private final Tape tape;
    private ExecutionNode currentNode;
    private final TuringMachineHandler handler;

    public ExecutorThread(TuringMachine turingMachine, Tape tape, ExecutionNode currentNode, TuringTransition nextTuringTransition, TuringMachineHandler handler) {
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.currentNode = currentNode;
        this.handler = handler;
        if (nextTuringTransition != null){
            step(nextTuringTransition);
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
            List<TuringTransition> turingTransitions = findTransitions(read);
            if(turingTransitions.size() > 1){
                for (int i = 1; i < turingTransitions.size(); i++) {
                    ExecutorThread executorThread = new ExecutorThread(turingMachine.getCopy(), tape.getCopy(), currentNode, turingTransitions.get(i), handler);
                    handler.addThread(executorThread);
                    executorThread.run();
                }
            }
            if (turingTransitions.size() == 0){
                return true;
            }
            else {
                TuringTransition turingTransition = turingTransitions.get(0);
                step(turingTransition);
                return false;
            }
        }
    }

    private void step(TuringTransition turingTransition){
        recordStep(turingTransition);
        try{
            tape.write(turingTransition.getTransitionRule().getWriteSymbol());
            tape.move(turingTransition.getTransitionRule().getDirection());
            turingMachine.setCurrentState(turingTransition.getToState());
        }
        catch (Exception e){
            System.out.println("DO SOMETHING ABOUT EXCEPTION HANDLING");
        }
    }

    private void recordStep(TuringTransition turingTransition){
        ExecutionNode newNode = new ExecutionNode(false, turingTransition, tape.toString(), currentNode);
        currentNode.addChild(newNode);
        currentNode = newNode;
    }

    private List<TuringTransition> findTransitions(char read){
        List<TuringTransition> foundTuringTransitions = new ArrayList<>();
        for (TuringTransition turingTransition :turingMachine.getTransitionsFromCurrentState()) {
            if(turingTransition.getTransitionRule().getReadSymbol() == read){
                foundTuringTransitions.add(turingTransition);
            }
        }
//        if(foundTuringTransitions.size() > 0){
            return foundTuringTransitions;
//        }
//        else{
//            throw new InvalidSymbolException("State " + turingMachine.getCurrentState().getName() + " has no operations for input '" + read + "'");
//        }
    }
}
