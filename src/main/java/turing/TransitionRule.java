package turing;

public class TransitionRule {
    private char readSymbol;
    private char writeSymbol;
    private char direction;

    public TransitionRule(){}

    public TransitionRule(char readSymbol, char writeSymbol, char direction){
        this.readSymbol = readSymbol;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }

    public void setReadSymbol(char readSymbol){
        this.readSymbol = readSymbol;
    }

    public char getReadSymbol() {
        return readSymbol;
    }

    public void setWriteSymbol(char writeSymbol){
        this.writeSymbol = writeSymbol;
    }

    public char getWriteSymbol() {
        return writeSymbol;
    }

    public void setDirection(char direction){
        this.direction = direction;
    }

    public char getDirection(){
        return direction;
    }

    public String toString(){
        return readSymbol + "|" + writeSymbol + "|" + direction;
    }
}
