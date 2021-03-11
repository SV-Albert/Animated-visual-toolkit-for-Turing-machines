package turing;

import execution.ExecutionNode;

import java.util.ArrayList;
import java.util.List;

public class TuringMachineHandler {
    private final ExecutionNode rootNode;
//    private final List<Thread> threads;
    private final List<ExecutorThread> executorsList;

    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        rootNode = new ExecutionNode(true, null, tape.toString(), null);
        ExecutorThread rootExecutorThread = new ExecutorThread(turingMachine, tape, rootNode, null, this);
//        threads = new ArrayList<>();
//        threads.add(new Thread(rootExecutorThread));
        executorsList = new ArrayList<>();
        executorsList.add(rootExecutorThread);
    }

//    public void run() {
//        for (Thread thread:threads) {
//            thread.start();
//        }
//    }

//    public void stop(){
//        for (ExecutorThread executor: executorsList) {
//            executor.stop();
//        }
//    }
    synchronized public boolean step(){
        if(!executorsList.isEmpty()){
            List<ExecutorThread> finished = new ArrayList<>();
            for (ExecutorThread executorThread: executorsList) {
                boolean isFinished = executorThread.nextState();
                if(isFinished){
                    finished.add(executorThread);
                }
            }
            for (ExecutorThread executorThread: finished) {
                executorsList.remove(executorThread);
            }
            return true;
        }
        return false;
    }

    public ExecutionNode getRootNode(){
        return rootNode;
    }

    public void addThread(ExecutorThread executorThread){
//        threads.add(new Thread(executorThread));
        executorsList.add(executorThread);
    }
}
