package turing;

import exceptions.InvalidSymbolException;
import java.util.ArrayList;
import java.util.List;

public class Tape {
    private String name;
    private List<Character> tapeArray;
    private int head;
    private final int initialHeadPosition;
    private final char emptyChar = '~';

    public Tape(String name, List<Character> tapeArray, int head){
        this.name = name;
        this.tapeArray = tapeArray;
        this.head = head;
        this.initialHeadPosition = head;
    }

    public char read(){
        return tapeArray.get(head);
    }

    public void move(char direction) throws InvalidSymbolException{
        if (direction == 'L'){
            head++;
            if(head == tapeArray.size()){
                tapeArray.add(emptyChar);
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
        if (head < tapeArray.size()){
            tapeArray.set(head, ch);
        }
        else{
            for (int i = tapeArray.size(); i < head; i++){
                tapeArray.add(head, emptyChar);
            }
        }
    }

    public void addSpaceAfter(int index){
        for (int i = tapeArray.size() - 1; i > index; i--) {
            tapeArray.add(i+1, tapeArray.get(i));
        }
        tapeArray.add(index + 1, emptyChar);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (char value: tapeArray) {
//            if (value == emptyChar){
//                value = '~';
//            }
            if(index == head){
                builder.append('(');
                builder.append(value);
                builder.append(')');
            }
            else{
                builder.append('[');
                builder.append(value);
                builder.append(']');
            }
            index++;
        }
        return builder.toString();
    }

    public String getName(){
        return name;
    }

    public int getSize(){
        return tapeArray.size();
    }

    public int getHead(){
        return head;
    }

    public List<Character> getTapeArray(){
        return tapeArray;
    }

    public int getInitialHeadPosition(){
        return initialHeadPosition;
    }

    public char getEmptyChar(){
        return emptyChar;
    }

    public Tape getCopy(){
        List<Character> tapeCopy = new ArrayList<>(tapeArray);
        return new Tape(name, tapeCopy, head);
    }

}
