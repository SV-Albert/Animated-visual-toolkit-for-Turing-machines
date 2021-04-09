package execution;

import turing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Executor {

    private final TuringMachine turingMachine;
    private final Tape tape;
    private ExecutorHandler handler;
    private final List<String> stateCodes = new ArrayList<>();
    private final ExecutionPath executionPath;

    public Executor(TuringMachine turingMachine, Tape tape){
        this.turingMachine = turingMachine;
        this.tape = tape;
        executionPath = new ExecutionPath();
        setup();
    }

    public Executor(TuringMachine turingMachine, Tape tape, ExecutorHandler handler){
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.handler = handler;
        executionPath = new ExecutionPath();
        setup();
    }

    public Executor(TuringMachine turingMachine, Tape tape, ExecutionPath executionPath,
                    TuringTransition nextTuringTransition, ExecutorHandler handler) {
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.executionPath = executionPath;
        this.handler = handler;
        setup();
        step(nextTuringTransition);
    }

    private void setup(){
        stateCodes.add("Accept");
        stateCodes.add("Reject");
        stateCodes.add("Halt");
        stateCodes.add("Stuck");
        stateCodes.add("Run");
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
            char read = tape.read();
            List<TuringTransition> turingTransitions = findTransitions(read);
            if(turingTransitions.size() > 1){
                for (int i = 1; i < turingTransitions.size(); i++) {
                    TuringMachine tmCopy = turingMachine.getCopy();
                    ExecutionPath pathCopy = executionPath.getCopy();
                    Tape tapeCopy = tape.getCopy();
                    Executor executor = new Executor(tmCopy, tapeCopy, pathCopy, turingTransitions.get(i), handler);
                    handler.addExecutor(tmCopy, executor);
                }
            }
            else if (turingTransitions.size() == 0){
                return stateCodes.get(3);
            }

            TuringTransition turingTransition = turingTransitions.get(0);
            step(turingTransition);
            return stateCodes.get(4);
        }
    }

    public void step(TuringTransition turingTransition){
        State from = turingTransition.getFromState();
        State to = turingTransition.getToState();
        char read = turingTransition.getTransitionRule().getReadSymbol();
        char write = turingTransition.getTransitionRule().getWriteSymbol();
        char direction = turingTransition.getTransitionRule().getDirection();
        executionPath.addStep(new ExecutionStep(from, to, read, write, direction));
        try{
            tape.write(write);
            tape.move(direction);
            turingMachine.setCurrentState(to);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stepBack(){
        int numberOfSteps = executionPath.getAllSteps().size();
        if(numberOfSteps > 0){
            ExecutionStep lastStep = executionPath.getAllSteps().get(numberOfSteps - 1);
            char direction = lastStep.getDirection();
            if(direction == 'R'){
                tape.move('L');
            }
            else if(direction == 'L'){
                tape.move('R');
            }
            tape.write(lastStep.getRead());
            executionPath.getAllSteps().remove(lastStep);
            turingMachine.setCurrentState(lastStep.getFrom());
        }
    }

    public List<TuringTransition> findTransitions(char read){
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
