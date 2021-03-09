package turing;

import javafx.util.Pair;

public class State {
    private String name;
    private boolean isInitial;
    private boolean isAccepting;
    private boolean isRejecting;
    private boolean isHalting;
    private Pair<Double, Double> position;


    public State(){
        isInitial = false;
        isAccepting = false;
        isRejecting = false;
        isHalting = false;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPosition(double xPos, double yPos){
        position = new Pair<>(xPos, yPos);
    }

    public void setInitial(boolean isInitial){
        this.isInitial = isInitial;
    }

    public void setAccepting(boolean isAccepting){
        this.isAccepting = isAccepting;
    }

    public void setRejecting(boolean isRejecting){
        this.isRejecting = isRejecting;
    }

    public void setHalting(boolean isHalting){
        this.isHalting = isHalting;
    }

    public String getName(){
        return name;
    }

    public Pair<Double, Double> getPosition(){
        return position;
    }

    public boolean isInitial(){
        return isInitial;
    }

    public boolean isAccepting(){
        return isAccepting;
    }

    public boolean isRejecting(){
        return isRejecting;
    }

    public boolean isHalting(){
        return isHalting;
    }
}
