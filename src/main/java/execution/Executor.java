package execution;

import turing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Executor {

    private final TuringMachine turingMachine;
    private final Tape tape;
    private final TuringMachineHandler handler;
    private final List<String> stateCodes = new ArrayList<>();
    private final ExecutionPath executionPath;
    private boolean isSplitStep;

    public Executor(TuringMachine turingMachine, Tape tape, TuringMachineHandler handler){
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.handler = handler;
        executionPath = new ExecutionPath();
        stateCodes.add("Accept");
        stateCodes.add("Reject");
        stateCodes.add("Halt");
        stateCodes.add("Stuck");
        stateCodes.add("Run");
        isSplitStep = true;
    }

    public Executor(TuringMachine turingMachine, Tape tape, ExecutionPath executionPath,
                    TuringTransition nextTuringTransition, TuringMachineHandler handler) {
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.executionPath = executionPath;
        this.handler = handler;
        stateCodes.add("Accept");
        stateCodes.add("Reject");
        stateCodes.add("Halt");
        stateCodes.add("Stuck");
        stateCodes.add("Run");
        isSplitStep = true;
        step(nextTuringTransition);
    }

    public String nextState(){

        if(turingMachine.getCurrentState().isAccepting()){
            return stateCodes.get(0);
        }
        else if(turingMachine.getCurrentState().isRejecting()){
            return stateCodes.get(1);
        }
        else if(turingMachine.getCurrentState().isHalting()){
            return stateCodes.get(2);
        }
        else{
            boolean splitExecution = false;
            char read = tape.read();
            List<TuringTransition> turingTransitions = findTransitions(read);
            if(turingTransitions.size() > 1){
                splitExecution = true;
                for (int i = 1; i < turingTransitions.size(); i++) {
                    TuringMachine tmCopy = turingMachine.getCopy();
                    ExecutionPath pathCopy = executionPath.copy();
                    Executor executor = new Executor(tmCopy, tape.getCopy(), pathCopy, turingTransitions.get(i), handler);
                    handler.addExecutor(tmCopy, executor);
                }
            }
            else if (turingTransitions.size() == 0){
                return stateCodes.get(3);
            }

            TuringTransition turingTransition = turingTransitions.get(0);
            step(turingTransition);
            isSplitStep = splitExecution;
            return stateCodes.get(4);
        }
    }

    private void step(TuringTransition turingTransition){
        State from = turingTransition.getFromState();
        State to = turingTransition.getToState();
        char read = turingTransition.getTransitionRule().getReadSymbol();
        char write = turingTransition.getTransitionRule().getWriteSymbol();
        char direction = turingTransition.getTransitionRule().getDirection();
        executionPath.addStep(new ExecutionStep(from, to, read, write, direction, isSplitStep));
        try{
            tape.write(write);
            tape.move(direction);
            turingMachine.setCurrentState(to);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<TuringTransition> findTransitions(char read){
        List<TuringTransition> foundTuringTransitions = new ArrayList<>();
        Set<TuringTransition> transitionsFromState = turingMachine.getTransitionsFromCurrentState();
        if(transitionsFromState != null){
            for (TuringTransition turingTransition: transitionsFromState) {
                if(turingTransition.getTransitionRule().getReadSymbol() == read){
                    foundTuringTransitions.add(turingTransition);
                }
            }
        }
        return foundTuringTransitions;
    }

    public ExecutionPath getExecutionPath(){
        return executionPath;
    }

    public TuringMachine getTM(){
        return turingMachine;
    }

    public Tape getTape(){
        return tape;
    }

}