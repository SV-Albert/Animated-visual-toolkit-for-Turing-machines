package execution;

import turing.State;

import java.util.ArrayList;
import java.util.List;

public class ExecutionStep {
    private final State from;
    private final State to;
    private final char read;
    private final char write;
    private final char direction;
    private final boolean isSplitStep;
    private final List<Character> tapeArray;

    public ExecutionStep(State from, State to, char read, char write, char direction,
                         boolean isSplitStep, List<Character> tapeArray){
        this.from = from;
        this.to = to;
        this.read = read;
        this.write = write;
        this.direction = direction;
        this.isSplitStep = isSplitStep;
        this.tapeArray = new ArrayList<>(tapeArray);
    }
    public State getFrom() {
        return from;
    }

    public State getTo() {
        return to;
    }

    public char getRead() {
        return read;
    }

    public char getWrite() {
        return write;
    }

    public char getDirection() {
        return direction;
    }

    public boolean isSplitStep(){
        return isSplitStep;
    }

    public List<Character> getTapeArray(){
        return tapeArray;
    }
}
