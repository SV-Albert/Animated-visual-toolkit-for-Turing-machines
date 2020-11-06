package main.java;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import main.java.turing.State;
import main.java.turing.Tape;
import main.java.turing.TransitionFunction;
import main.java.turing.TuringMachine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class SaveManager {

    private final Path TuringSavePath = Paths.get("C:\\Users\\Blackout\\Downloads\\TMSaveTest.xml");
    private final Path TapeSavePath = Paths.get("C:\\Users\\Blackout\\Downloads\\TapeSaveTest.xml");

    public SaveManager(){}

    public void saveTuringMachine(TuringMachine tm) throws IOException {
        String xml = getTuringXML(tm);
        if(!Files.exists(TuringSavePath)){
            Files.createFile(TuringSavePath);
        }
        OutputStream outputStream = new FileOutputStream(TuringSavePath.toString());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.print(xml);
        writer.close();
    }

    public void saveTape(Tape tape) throws IOException {
        String xml = getTapeXML(tape);
        if(!Files.exists(TapeSavePath)){
            Files.createFile(TapeSavePath);
        }
        OutputStream outputStream = new FileOutputStream(TapeSavePath.toString());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.print(xml);
        writer.close();
    }

    public TuringMachine loadTuringMachine() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        FileInputStream stream = new FileInputStream(new File(TuringSavePath.toString()));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xml = builder.parse(stream);
        XPath xpath = XPathFactory.newInstance().newXPath();

        Element tmNode = (Element) xpath.compile("/TuringMachineSave").evaluate(xml, XPathConstants.NODE);

        String tmName = tmNode.getAttribute("name");

        Element alphabetNode = (Element) tmNode.getElementsByTagName("alphabet").item(0);
        NodeList values = alphabetNode.getElementsByTagName("symbol");
        HashSet<Character> alphabet = new HashSet<>();
        for (int i = 0; i < values.getLength(); i++) {
            alphabet.add(values.item(i).getTextContent().charAt(0));
        }

        HashMap<String, State> states = new HashMap<>();
        Element globalStatesNode = (Element) tmNode.getElementsByTagName("states").item(0);
        String initialStateName = globalStatesNode.getAttribute("initial");
        NodeList stateNodeList = globalStatesNode.getElementsByTagName("state");
        for (int i = 0; i < stateNodeList.getLength(); i++) {
            Element stateNode = (Element) stateNodeList.item(i);
            String stateName = stateNode.getAttribute("name");
            boolean isAccepting = Boolean.parseBoolean(stateNode.getElementsByTagName("isAccepting").item(0).getTextContent());
            State state = new State(stateName, isAccepting);
            states.put(stateName, state);
            NodeList actions = stateNode.getElementsByTagName("action");
            for (int j = 0; j < actions.getLength(); j++) {
                Element actionEntry = (Element) actions.item(j);
                char tapeSymbol = actionEntry.getElementsByTagName("tapeSymbol").item(0).getTextContent().charAt(0);
                char writeSymbol = actionEntry.getElementsByTagName("writeSymbol").item(0).getTextContent().charAt(0);
                char direction = actionEntry.getElementsByTagName("direction").item(0).getTextContent().charAt(0);
                state.addAction(tapeSymbol, writeSymbol, direction);
            }
        }

        State initialState = states.get(initialStateName);

        TransitionFunction tFunc = new TransitionFunction();
        NodeList transitionsNodeList = tmNode.getElementsByTagName("transition");
        for (int i = 0; i < transitionsNodeList.getLength(); i++) {
            Element transitionNode = (Element) transitionsNodeList.item(i);
            State fromState = states.get(transitionNode.getElementsByTagName("fromState").item(0).getTextContent());
            char tapeSymbol = transitionNode.getElementsByTagName("tapeSymbol").item(0).getTextContent().charAt(0);
            State toState = states.get(transitionNode.getElementsByTagName("toState").item(0).getTextContent());
            tFunc.addTransition(fromState, tapeSymbol, toState);
        }

        return new TuringMachine(tmName, alphabet, states, initialState, tFunc);
    }

    public Tape loadTape() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        FileInputStream stream = new FileInputStream(new File(TapeSavePath.toString()));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xml = builder.parse(stream);
        XPath xpath = XPathFactory.newInstance().newXPath();

        Element tapeNode = (Element) xpath.compile("/TuringTapeSave").evaluate(xml, XPathConstants.NODE);

        String tapeName = tapeNode.getAttribute("name");

        int headPosition = Integer.parseInt(tapeNode.getElementsByTagName("headPosition").item(0).getTextContent());

        Element valuesNode = (Element) tapeNode.getElementsByTagName("tape").item(0);
        NodeList values = valuesNode.getElementsByTagName("value");
        ArrayList<Character> tapeList = new ArrayList<>();
        for (int i = 0; i < values.getLength(); i++) {
            tapeList.add(values.item(i).getTextContent().charAt(0));
        }

        return new Tape(tapeName, tapeList, headPosition);
    }

    private String getTuringXML(TuringMachine tm){
        String name = tm.getName();
        HashSet<Character> alphabet = tm.getAlphabet();
        HashMap<String, State> states = tm.getStates();
        State initialState = tm.getInitialState();
        TransitionFunction tFunc = tm.getTransitionFunction();

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n');
        builder.append("<TuringMachineSave name = \"").append(tm.getName()).append("\">" + '\n');
        builder.append('\t' + "<alphabet>" + '\n');
        for(char ch: alphabet){
            builder.append(tab(2)).append("<symbol>").append(ch).append("</symbol>" + '\n');
        }
        builder.append('\t' + "</alphabet>" + '\n');
        builder.append('\t' + "<states initial = \"").append(initialState.getName()).append("\">" + '\n');
        for(State state: states.values()){
            HashMap<Character, Pair<Character, Character>> stateData = state.getData();
            builder.append(tab(2)).append("<state name = \"").append(state.getName()).append("\">" + '\n');
            builder.append(tab(3)).append("<isAccepting>").append(state.isAccepting()).append("</isAccepting>" + '\n');
            if(!state.getData().isEmpty()){
                builder.append(tab(3)).append("<actions>").append('\n');
                for(char tapeSymbol: stateData.keySet()){
                    builder.append(tab(4)).append("<action>").append('\n');
                    builder.append(tab(5)).append("<tapeSymbol>").append(tapeSymbol).append("</tapeSymbol>"  + '\n');
                    builder.append(tab(5)).append("<writeSymbol>").append(stateData.get(tapeSymbol).getKey()).append("</writeSymbol>"  + '\n');
                    builder.append(tab(5)).append("<direction>").append(stateData.get(tapeSymbol).getValue()).append("</direction>"  + '\n');
                    builder.append(tab(4)).append("</action>").append('\n');
                }
                builder.append(tab(3)).append("</actions>").append('\n');
            }
            builder.append(tab(2)).append("</state>").append('\n');
        }
        builder.append('\t' + "</states>" + '\n');
        HashMap<Pair<State, Character>, State> transitionTable = tFunc.getData();
        builder.append('\t' + "<transitions>"  + '\n');
        for (Pair<State, Character> keyPair: transitionTable.keySet()){
            builder.append(tab(2)).append("<transition>").append('\n');
            builder.append(tab(3)).append("<fromState>").append(keyPair.getKey().getName()).append("</fromState>"  + '\n');
            builder.append(tab(3)).append("<tapeSymbol>").append(keyPair.getValue()).append("</tapeSymbol>"  + '\n');
            builder.append(tab(3)).append("<toState>").append(transitionTable.get(keyPair).getName()).append("</toState>"  + '\n');
            builder.append(tab(2)).append("</transition>").append('\n');
        }
        builder.append('\t' + "</transitions>"  + '\n');
        builder.append("</TuringMachineSave>" + '\n');

        return builder.toString();
    }

    private String getTapeXML(Tape tape){
        String name = tape.getName();
        ArrayList<Character> tapeArray = tape.getTapeArray();
        int head = tape.getInitialHeadPosition();

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n');
        builder.append("<TuringTapeSave name = \"").append(tape.getName()).append("\">" + '\n');
        builder.append('\t' + "<headPosition>").append(head).append("</headPosition>" + '\n');
        builder.append('\t' + "<tape>" + '\n');
        for(char ch: tapeArray){
            if (ch == '\n'){
                ch = '~';
            }
            builder.append(tab(2)).append("<value>").append(ch).append("</value>" + '\n');
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