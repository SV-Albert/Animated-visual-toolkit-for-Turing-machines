package turing;

import execution.ExecutionStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuringMachineHandler {
    private final Map<TuringMachine, ExecutorThread> executorsMap;
    private final Map<String, TuringMachine> uniqueExecutionPaths;

    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        ExecutorThread rootExecutorThread = new ExecutorThread(turingMachine, tape, this);
        executorsMap = new HashMap<>();
        executorsMap.put(turingMachine, rootExecutorThread);
        uniqueExecutionPaths = new HashMap<>();
    }

    synchronized public String step(){
        if(!executorsMap.isEmpty()){
            uniqueExecutionPaths.clear();
            for (ExecutorThread executorThread: executorsMap.values()) {
                String stateCode = executorThread.nextState();
                String path = executorThread.getExecutionPath();
                uniqueExecutionPaths.putIfAbsent(path, executorThread.getTM());
                System.out.println(uniqueExecutionPaths.size());
                if(!stateCode.equals("Run")){
                    return stateCode;
                }
            }
            return "Run";
        }
        return "Stuck";
    }

    public Map<String, TuringMachine> getUniqueExecutionPaths(){
        return uniqueExecutionPaths;
    }

    public Tape getTape(TuringMachine tm){
        return executorsMap.get(tm).getTape();
    }

    public ExecutionStep getStep(TuringMachine tm, int index){
        return executorsMap.get(tm).getStepHistory().get(index);
    }

    public List<ExecutionStep> getAllSteps(TuringMachine tm){
        return executorsMap.get(tm).getStepHistory();
    }

    public void addThread(TuringMachine tm, ExecutorThread executorThread){
        executorsMap.put(tm, executorThread);
    }
}
