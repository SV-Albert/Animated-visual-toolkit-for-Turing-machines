package execution;

import turing.Tape;
import turing.TuringMachine;

import java.util.*;

public class ExecutorHandler {
    private final Map<TuringMachine, Executor> executorsMap;
    private final Map<ExecutionPath, TuringMachine> uniqueExecutionPaths;
    private final Map<TuringMachine, Executor> newExecutors;
    private final List<Executor> stoppedExecutors;
    private boolean rejectedStateReached;
    private TuringMachine acceptingExecution;

    public ExecutorHandler(TuringMachine turingMachine, Tape tape){
        Executor rootExecutor = new Executor(turingMachine, tape, this);
        executorsMap = new HashMap<>();
        executorsMap.put(turingMachine, rootExecutor);
        uniqueExecutionPaths = new TreeMap<>();
        newExecutors = new HashMap<>();
        rejectedStateReached = false;
        stoppedExecutors = new ArrayList<>();
    }

    synchronized public String step(){
        if(executorsMap.size() != stoppedExecutors.size()){
            String stateCode = "Run";
            for (Executor executor : executorsMap.values()) {
                if(!stoppedExecutors.contains(executor)){
                    String receivedStateCode = executor.nextState();
                    ExecutionPath path = executor.getExecutionPath();
                    if(uniqueExecutionPaths.containsKey(path) && uniqueExecutionPaths.get(path) != executor.getTM()){
                        stoppedExecutors.add(executor);
                    }
                    else{
                        uniqueExecutionPaths.put(path, executor.getTM());
                    }
                    if(receivedStateCode.equals("Accept")){
                        stateCode = "Accept";
                        acceptingExecution = executor.getTM();
                        break;
                    }
                    if(!receivedStateCode.equals("Run")){
                        if(executorsMap.size() - stoppedExecutors.size() > 1){
                            stoppedExecutors.add(executor);
                            if(receivedStateCode.equals("Reject")){
                                rejectedStateReached = true;
                            }
                        }
                        else {
                            if(receivedStateCode.equals("Reject") || rejectedStateReached){
                                stateCode = "Reject";
                            }
                            else{
                                stateCode = receivedStateCode;
                            }
                        }
                    }
                }
            }
            for (TuringMachine tm: newExecutors.keySet()) {
                executorsMap.putIfAbsent(tm, newExecutors.get(tm));
            }
            newExecutors.clear();
            return stateCode;
        }
        if(rejectedStateReached){
            return "Reject";
        }
        else {
            return "Stuck";
        }
    }

    public TuringMachine getAcceptingExecution(){
        return acceptingExecution;
    }

    public Map<ExecutionPath, TuringMachine> getUniqueExecutionPaths(){
        return uniqueExecutionPaths;
    }

    public Tape getTape(TuringMachine tm){
        return executorsMap.get(tm).getTape();
    }

    public ExecutionStep getStep(TuringMachine tm, int index){
        return executorsMap.get(tm).getExecutionPath().getAllSteps().get(index);
    }

    public List<ExecutionStep> getAllSteps(TuringMachine tm){
        return executorsMap.get(tm).getExecutionPath().getAllSteps();
    }

    public void addExecutor(TuringMachine tm, Executor executor){
        newExecutors.put(tm, executor);
    }

    public boolean isExecutorStopped(TuringMachine tm){
        return stoppedExecutors.contains(executorsMap.get(tm));
    }

    public TuringMachine getARunningTM(){
        for (TuringMachine tm: executorsMap.keySet()) {
            if(!isExecutorStopped(tm)){
                return tm;
            }
        }
        return null;
    }
}
