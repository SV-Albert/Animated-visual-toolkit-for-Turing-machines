package turing;

public class State {
    private final String name;
    private final boolean isAccepting;


    public State(String name, boolean isAccepting){
        this.name = name;
        this.isAccepting = isAccepting;
    }

    public String getName(){
        return name;
    }

    public boolean isAccepting(){
        return isAccepting;
    }
}
