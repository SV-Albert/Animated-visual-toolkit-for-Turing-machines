package turing;

public class Transition {

    private final State fromState;
    private final State toState;
    private final char readSymbol;
    private final char writeSymbol;
    private final char direction;


    public Transition(State fromState, State toState, char readSymbol, char writeSymbol, char direction){
        this.fromState = fromState;
        this.toState = toState;
        this.readSymbol = readSymbol;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }

    public State getFromState(){
        return fromState;
    }

    public State getToState(){
        return toState;
    }

    public char getReadSymbol() {
        return readSymbol;
    }

    public char getWriteSymbol() {
        return writeSymbol;
    }

    public char getDirection(){
        return direction;
    }
}
