package execution;

import turing.State;

public class ExecutionStep {
    private final State from;
    private final State to;
    private final char read;
    private final char write;
    private final char direction;


    public ExecutionStep(State from, State to, char read, char write, char direction){
        this.from = from;
        this.to = to;
        this.read = read;
        this.write = write;
        this.direction = direction;
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
}
