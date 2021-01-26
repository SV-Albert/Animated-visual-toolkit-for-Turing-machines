package turing;

import exceptions.InvalidSymbolException;
import java.util.ArrayList;

public class Tape {
    private String name;
    private ArrayList<Character> tape;
    private int head;
    private final int initialHeadPosition;
    private final char emptyChar = '~';

    public Tape(String name, ArrayList<Character> tape, int head){
        this.name = name;
        this.tape = tape;
        this.head = head;
        this.initialHeadPosition = head;
    }

    public char read(){
        return tape.get(head);
    }

    public void move(char direction) throws InvalidSymbolException{
        if (direction == 'L'){
            head++;
            if(head == tape.size()){
                tape.add(emptyChar);
            }
        }
        else if (direction == 'R'){
            if(head == 0){
                addSpaceAfter(-1);
            }
            else{
                head--;
            }
        }
        else if (direction != 'N'){
            throw new InvalidSymbolException("Direction '" + direction + "' " + "is invalid");
        }
    }

    public void write(char ch){
        if (head < tape.size()){
            tape.set(head, ch);
        }
        else{
            for (int i = tape.size(); i < head; i++){
                tape.add(head, emptyChar);
            }
        }
    }

    public void addSpaceAfter(int index){
        for (int i = tape.size() - 1; i > index; i--) {
            tape.add(i+1, tape.get(i));
        }
        tape.add(index + 1, emptyChar);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (char value: tape) {
            if (value == emptyChar){
                value = '_';
            }
            builder.append('[');
            if(index == head){
                builder.append('(');
                builder.append(value);
                builder.append(')');
            }
            else{
                builder.append(value);
            }
            builder.append(']');
            index++;
        }
        return builder.toString();
    }

    public String getName(){
        return name;
    }

    public int getSize(){
        return tape.size();
    }

    public int getHead(){
        return head;
    }

    public ArrayList<Character> getTapeArray(){
        return tape;
    }

    public int getInitialHeadPosition(){
        return initialHeadPosition;
    }

    public char getEmptyChar(){
        return emptyChar;
    }

}
