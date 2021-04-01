package execution;

import turing.Tape;
import turing.TuringMachine;

import java.util.*;

public class TuringMachineHandler {
    private final Map<TuringMachine, Executor> executorsMap;
    private final Map<ExecutionPath, TuringMachine> uniqueExecutionPaths;
    private final Map<TuringMachine, Executor> newExecutors;

    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        Executor rootExecutor = new Executor(turingMachine, tape, this);
        executorsMap = new HashMap<>();
        executorsMap.put(turingMachine, rootExecutor);
        uniqueExecutionPaths = new TreeMap<>();
        newExecutors = new HashMap<>();
    }

    synchronized public String autoStep(){
        if(!executorsMap.isEmpty()){
            List<TuringMachine> turingMachinesToStop = new ArrayList<>();
            uniqueExecutionPaths.clear();
            String stateCode = "Run";
            for (Executor executor : executorsMap.values()) {
                String receivedStateCode = executor.nextState();
                ExecutionPath path = executor.getExecutionPath();
                if(uniqueExecutionPaths.containsKey(path)){
                    turingMachinesToStop.add(executor.getTM());
                }
                else{
                    uniqueExecutionPaths.putIfAbsent(path, executor.getTM());
                }
                if(!receivedStateCode.equals("Run")){
                    if(stateCode.equals("Run")){
                        stateCode = receivedStateCode;
                    }
                    else if(stateCode.equals("Accept") && receivedStateCode.equals("Reject") ||
                            stateCode.equals("Reject") && receivedStateCode.equals("Accept")){
                        stateCode = "Halt";
                    }
                    else {
                        stateCode = "Stuck";
                        break;
                    }

                }
            }
            for (TuringMachine tm: newExecutors.keySet()) {
                executorsMap.putIfAbsent(tm, newExecutors.get(tm));
            }
            for (TuringMachine tm: turingMachinesToStop){
                executorsMap.remove(tm);
            }
            newExecutors.clear();
            return stateCode;
        }
        return "Stuck";
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
}
