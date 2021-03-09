import io.SaveManager;
import turing.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.HashMap;

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

//        HashSet<Character> alphabet = new HashSet<>();
//        alphabet.add('0');
//        alphabet.add('1');


        State s0 = new State();
        s0.setName("s0");
        TuringMachine tm = new TuringMachine(s0);
        State s1 = new State();
        s1.setName("s1");
        State s2 = new State();
        s2.setName("s2");
        s2.setAccepting(true);

        tm.addState(s1);
        tm.addState(s2);

        tm.addTransition(new TuringTransition(s0, s1, new TransitionRule('~', '~', 'L')));
//        tm.addTransition(new TuringTransition(s0, s1, '~', '1', 'L'));
        tm.addTransition(new TuringTransition(s0, s0, new TransitionRule('0', '1', 'R')));
        tm.addTransition(new TuringTransition(s0, s0, new TransitionRule('1', '0', 'R')));
        tm.addTransition(new TuringTransition(s1, s2, new TransitionRule('~', '~', 'R')));
        tm.addTransition(new TuringTransition(s1, s1, new TransitionRule('0', '1', 'L')));
        tm.addTransition(new TuringTransition(s1, s1, new TransitionRule('1', '0', 'L')));

        try{
            SaveManager.saveTuringMachine(tm, Paths.get("C:\\Users\\Blackout\\Documents\\IDEA\\Animated-visual-toolkit-for-Turing-machines\\src\\main\\resources\\TMSaveTest.xml"));
            SaveManager.saveTape(tape);
            System.out.println("Save completed");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Loading from save...");

//        try{
//            tm = SaveManager.loadTuringMachine();
//            tape = SaveManager.loadTape();
//            System.out.println("Loading complete. Executing...");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }

        TuringMachineHandler handler = new TuringMachineHandler(tm, tape);
        handler.run();
    }
}
