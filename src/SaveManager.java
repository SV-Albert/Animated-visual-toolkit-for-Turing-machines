import javafx.util.Pair;
import turing.State;
import turing.Tape;
import turing.TransitionFunction;
import turing.TuringMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class SaveManager {

    public SaveManager(){}

    public String getTuringXML(TuringMachine tm){
        String name = tm.getName();
        HashSet<Character> alphabet = tm.getAlphabet();
        HashSet<State> states = tm.getStates();
        State initialState = tm.getInitialState();
        TransitionFunction tFunc = tm.getTransitionFunction();

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n');
        builder.append("<TuringMachineSave>" + '\n');
        builder.append('\t' + "<name>").append(name).append("</name>" + '\n');
        builder.append('\t' + "<alphabet>" + '\n');
        for(char ch: alphabet){
            builder.append(tab(2)).append("<symbol>").append(ch).append("</symbol>" + '\n');
        }
        builder.append('\t' + "</alphabet>" + '\n');
        builder.append('\t' + "<states>" + '\n');
        for(State state: states){
            HashMap<Character, Pair<Character, Character>> stateData = state.getData();
            builder.append(tab(2)).append("<state>").append('\n');
            builder.append(tab(3)).append("<name>").append(state.getName()).append("</name>" + '\n');
            builder.append(tab(3)).append("<actions>").append('\n');
            for(char tapeSymbol: stateData.keySet()){
                builder.append(tab(4)).append("<action>").append('\n');
                builder.append(tab(5)).append("<tapeSymbol>").append(tapeSymbol).append("</tapeSymbol>"  + '\n');
                builder.append(tab(5)).append("<writeSymbol>").append(stateData.get(tapeSymbol).getKey()).append("</writeSymbol>"  + '\n');
                builder.append(tab(5)).append("<direction>").append(stateData.get(tapeSymbol).getValue()).append("</direction>"  + '\n');
                builder.append(tab(4)).append("</action>").append('\n');
            }
            builder.append(tab(3)).append("</actions>").append('\n');
            builder.append(tab(2)).append("</state>").append('\n');
        }
        builder.append('\t' + "</states>" + '\n');
        builder.append('\t' + "<initialState>").append(initialState.getName()).append("</initialState>" + '\n');
        HashMap<Pair<State, Character>, State> transitionTable = tFunc.getData();
        builder.append('\t' + "<transitions>"  + '\n');
        for (Pair<State, Character> keyPair: transitionTable.keySet()){
            builder.append(tab(2)).append("<transition>").append('\n');
            builder.append(tab(3)).append("<fromState>").append(keyPair.getKey().getName()).append("</fromState>"  + '\n');
            builder.append(tab(3)).append("<tapeSymbol>").append(keyPair.getValue()).append("</tapeSymbol>"  + '\n');
            builder.append(tab(3)).append("<toState>").append(transitionTable.get(keyPair).getName()).append("</toState>"  + '\n');
            builder.append(tab(2)).append("<transition>").append('\n');
        }
        builder.append('\t' + "</transitions>"  + '\n');
        builder.append("</TuringMachineSave>" + '\n');

        return builder.toString();
    }

    public String getTapeXML(Tape tape){
        String name = tape.getName();
        ArrayList<Character> tapeArray = tape.getTapeArray();
        int head = tape.getInitialHeadPosition();

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n');
        builder.append("<TuringTapeSave>" + '\n');
        builder.append('\t' + "<name>").append(name).append("</name>" + '\n');
        builder.append('\t' + "<headPosition>").append(head).append("</headPosition>" + '\n');
        builder.append('\t' + "<tape>" + '\n');
        for(char ch: tapeArray){
            if (ch == '\n'){
                ch = '~';
            }
            builder.append(tab(2)).append("<value> ").append(ch).append(" </value>" + '\n');
        }
        builder.append('\t' + "</tape>" + '\n');
        builder.append("</TuringTapeSave>" + '\n');

        return builder.toString();
    }

    private String tab(int times){
        StringBuilder builder = new StringBuilder();
        for (int i = times; i > 0; i--) {
            builder.append('\t');
        }
        return builder.toString();
    }
}
