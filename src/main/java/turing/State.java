package java.turing;

import main.java.exceptions.InvalidSymbolException;
import javafx.util.Pair;
import java.util.HashMap;

public class State {
    private final String name;
    private final boolean isAccepting;
    private HashMap<Character, Pair<Character, Character>> actionsTable; //Tape symbol, write symbol, direction

    public State(String name, boolean isAccepting){
        this.name = name;
        this.isAccepting = isAccepting;
        actionsTable = new HashMap<>();
    }

    public char getDirection(char tapeSymbol) throws InvalidSymbolException {
        if (actionsTable.containsKey(tapeSymbol)){
            return actionsTable.get(tapeSymbol).getValue();
        }
       else{
            throw new InvalidSymbolException("State " + name + " has no operations for input '" + tapeSymbol + "'");
        }
    }

    public char getWriteSymbol(char tapeSymbol) throws InvalidSymbolException {
        if (actionsTable.containsKey(tapeSymbol)){
            return actionsTable.get(tapeSymbol).getKey();
        }
        else{
            throw new InvalidSymbolException("State " + name + " has no operations for input '" + tapeSymbol + "'");
        }
    }


    public void addAction(char tapeSymbol, char writeSymbol, char direction){
        Pair<Character, Character> action = new Pair<>(writeSymbol, direction);
        actionsTable.put(tapeSymbol, action);
    }

    public String getName(){
        return name;
    }

    public boolean isAccepting(){
        return isAccepting;
    }

    public HashMap<Character, Pair<Character, Character>> getData(){
        return actionsTable;
    }
}
