import org.xml.sax.SAXException;
import turing.State;
import turing.Tape;
import turing.TransitionFunction;
import turing.TuringMachine;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
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
        s0.addAction('~', '~', 'L');
        s0.addAction('0', '1', 'R');
        s0.addAction('1', '0', 'R');
        State s1 = new State("s1", false);
        s1.addAction('~', '~', 'R');
        s1.addAction('0', '1', 'L');
        s1.addAction('1', '0', 'L');
        State s2 = new State("s2", true);
        HashMap<String, State> states = new HashMap<>();
        states.put("s0", s0);
        states.put("s1", s1);
        states.put("s2", s2);

        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(s0, '~', s1);
        tf.addTransition(s0, '0', s0);
        tf.addTransition(s0, '1', s0);
        tf.addTransition(s1, '~', s2);
        tf.addTransition(s1, '0', s1);
        tf.addTransition(s1, '1', s1);

        TuringMachine tm = new TuringMachine("0 and 1 flip", alphabet, states, s0, tf);

        SaveManager sm = new SaveManager();
        try{
            sm.saveTuringMachine(tm);
            sm.saveTape(tape);
            System.out.println("Save completed");
        }
        catch (IOException e){
            e.printStackTrace();
        }


        System.out.println("Loading from save...");

        try{
            tm = sm.loadTuringMachine();
            System.out.println("Loading complete. Executing...");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        while(!tm.getCurrentState().isAccepting()){
            System.out.println(tape.toString() + " Current state: " + tm.getCurrentState().getName());
            try{
                char tapeSymbol = tape.read();
                State currentState = tm.getCurrentState();
                char writeSymbol = currentState.getWriteSymbol(tapeSymbol);
                char direction = currentState.getDirection(tapeSymbol);
                tape.write(writeSymbol);
                tape.move(direction);
                tm.nextState(tapeSymbol);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(tape.toString() + " Current state: " + tm.getCurrentState().getName());

        try {
            sm.loadTuringMachine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
