package turing;

import execution.ExecutionNode;

import java.util.ArrayList;

public class TuringMachineHandler {
    private final ExecutorThread rootExecutorThread;
    private final ExecutionNode rootNode;
    private final ArrayList<ExecutorThread> threads;

    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        rootNode = new ExecutionNode(true, null, tape.toString(), null);
        rootExecutorThread = new ExecutorThread(turingMachine, tape, rootNode, null, this);
        threads = new ArrayList<>();
        threads.add(rootExecutorThread);
    }

    public void run() {
        rootExecutorThread.run();
    }

    public ExecutionNode getRootNode(){
        return rootNode;
    }

    public void addThread(ExecutorThread thread){
        threads.add(thread);
    }
}
