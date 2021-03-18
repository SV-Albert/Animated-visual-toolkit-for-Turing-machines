package turing;

import execution.ExecutionStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExecutorThread{

    private final TuringMachine turingMachine;
    private final Tape tape;
    private final TuringMachineHandler handler;
    private final List<String> stateCodes = new ArrayList<>();
    private final List<ExecutionStep> stepHistory;

    public ExecutorThread(TuringMachine turingMachine, Tape tape, TuringMachineHandler handler){
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.handler = handler;
        stepHistory = new ArrayList<>();
        stateCodes.add("Accept");
        stateCodes.add("Reject");
        stateCodes.add("Halt");
        stateCodes.add("Stuck");
        stateCodes.add("Run");
    }

    public ExecutorThread(TuringMachine turingMachine, Tape tape, List<ExecutionStep> stepHistory,
                          TuringTransition nextTuringTransition, TuringMachineHandler handler) {
        this.turingMachine = turingMachine;
        this.tape = tape;
        this.stepHistory = stepHistory;
        this.handler = handler;
        stateCodes.add("Accept");
        stateCodes.add("Reject");
        stateCodes.add("Halt");
        stateCodes.add("Stuck");
        stateCodes.add("Run");
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
            char read = tape.read();
            List<TuringTransition> turingTransitions = findTransitions(read);
            if(turingTransitions.size() > 1){
                for (int i = 1; i < turingTransitions.size(); i++) {
                    TuringMachine tmCopy = turingMachine.getCopy();
                    List<ExecutionStep> historyCopy = new ArrayList<>(stepHistory);
                    ExecutorThread executorThread = new ExecutorThread(tmCopy, tape.getCopy(), historyCopy, turingTransitions.get(i), handler);
                    handler.addThread(tmCopy, executorThread);
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

    private void step(TuringTransition turingTransition){
        State from = turingTransition.getFromState();
        State to = turingTransition.getToState();
        char read = turingTransition.getTransitionRule().getReadSymbol();
        char write = turingTransition.getTransitionRule().getWriteSymbol();
        char direction = turingTransition.getTransitionRule().getDirection();
        stepHistory.add(new ExecutionStep(from, to, read, write, direction));
        try{
            tape.write(write);
            tape.move(direction);
            turingMachine.setCurrentState(to);
        }
        catch (Exception e){
//            TODO DO SOMETHING ABOUT EXCEPTION HANDLING
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
            return foundTuringTransitions;
        }
        else{
            return new ArrayList<>();
        }
    }

    public List<ExecutionStep> getStepHistory(){
        return stepHistory;
    }

    public String getExecutionPath(){
        StringBuilder builder = new StringBuilder();
        for (ExecutionStep step: stepHistory) {
            builder.append(step.getFrom().getName());
            builder.append(" | ");
        }
        return builder.toString();
    }

    public TuringMachine getTM(){
        return turingMachine;
    }

    public Tape getTape(){
        return tape;
    }

}
