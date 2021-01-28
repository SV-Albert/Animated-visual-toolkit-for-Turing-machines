import turing.*;

import java.io.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;

public class Simulation {
    public Simulation(){}

    public static void main(String[] args) {
        ArrayList<Character> tapeArray = new ArrayList<>();
        tapeArray.add('~');
        tapeArray.add('0');
        tapeArray.add('0');
        tapeArray.add('1');
        tapeArray.add('~');
        Tape tape = new Tape("Ones and zeros", tapeArray, 0);

        HashSet<Character> alphabet = new HashSet<>();
        alphabet.add('0');
        alphabet.add('1');

        State s0 = new State("s0", false);
        State s1 = new State("s1", false);
        State s2 = new State("s2", true);
        HashMap<String, State> states = new HashMap<>();
        states.put("s0", s0);
        states.put("s1", s1);
        states.put("s2", s2);

        TuringMachine tm = new TuringMachine("0 and 1 flip", states, s0);
        tm.addTransition(new Transition(s0, s1, '~', '~', 'L'));
//        tm.addTransition(new Transition(s0, s1, '~', '1', 'L'));
        tm.addTransition(new Transition(s0, s0, '0', '1', 'R'));
        tm.addTransition(new Transition(s0, s0, '1', '0', 'R'));
        tm.addTransition(new Transition(s1, s2, '~', '~', 'R'));
        tm.addTransition(new Transition(s1, s1, '0', '1', 'L'));
        tm.addTransition(new Transition(s1, s1, '1', '0', 'L'));

        try{
            SaveManager.saveTuringMachine(tm);
            SaveManager.saveTape(tape);
            System.out.println("Save completed");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Loading from save...");

        try{
            tm = SaveManager.loadTuringMachine();
            tape = SaveManager.loadTape();
            System.out.println("Loading complete. Executing...");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        TuringMachineHandler handler = new TuringMachineHandler(tm, tape);
        handler.run();
    }
}
