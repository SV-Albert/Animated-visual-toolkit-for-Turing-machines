package turing;

import canvasNodes.TransitionArrow;

public class TransitionRule {
    private char readSymbol;
    private char writeSymbol;
    private char direction;
    private TransitionArrow arrow;

    public TransitionRule(){}

    public TransitionRule(char readSymbol, char writeSymbol, char direction){
        this.readSymbol = readSymbol;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }

    public void setReadSymbol(char readSymbol){
        this.readSymbol = readSymbol;
        updateArrow();
    }

    public char getReadSymbol() {
        return readSymbol;
    }

    public void setWriteSymbol(char writeSymbol){
        this.writeSymbol = writeSymbol;
        updateArrow();
    }

    public char getWriteSymbol() {
        return writeSymbol;
    }

    public void setDirection(char direction){
        this.direction = direction;
        updateArrow();
    }

    public char getDirection(){
        return direction;
    }

    public void setArrow(TransitionArrow arrow){
        this.arrow = arrow;
    }

    private void updateArrow(){
        if(arrow != null){
            arrow.refreshRulesLabel();
        }
    }

    public boolean isEmpty(){
        return readSymbol == '\u0000' && writeSymbol == '\u0000' && direction == '\u0000';
    }

    public String toString(){
        return readSymbol + "|" + writeSymbol + "|" + direction;
    }
}
