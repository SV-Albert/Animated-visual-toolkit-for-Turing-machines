package turing;

import execution.ExecutionNode;


public class TuringMachineHandler {
    private final Executor rootExecutor;


    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        ExecutionNode rootNode = new ExecutionNode(true, null, tape.toString(), null);
        rootExecutor = new Executor(turingMachine, tape, rootNode, null);
    }

    public void run() {
        rootExecutor.run();
    }


}
