package execution;

import turing.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExecutionStep {
    private final State from;
    private final State to;
    private final char read;
    private final char write;
    private final char direction;
    private final List<Character> tapeArray;

    public ExecutionStep(State from, State to, char read, char write, char direction, List<Character> tapeArray){
        this.from = from;
        this.to = to;
        this.read = read;
        this.write = write;
        this.direction = direction;
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

    public List<Character> getTapeArray(){
        return tapeArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionStep that = (ExecutionStep) o;
        if(read == that.read && write == that.write && direction == that.direction && from.equals(that.from) && to.equals(that.to)){
            if(tapeArray.size() != that.tapeArray.size()){
                return false;
            }
            else{
                for (int i = 0; i < tapeArray.size(); i++) {
                    if(tapeArray.get(i) != that.tapeArray.get(i)){
                        return false;
                    }
                }
                return true;
            }
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash =  Objects.hash(from, to, read, write, direction);
        for (char ch: tapeArray) {
            hash += Objects.hashCode(ch);
        }
        return hash;
    }
}
