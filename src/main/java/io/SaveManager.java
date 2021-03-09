package io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import turing.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class SaveManager {

    private static final Path TapeSavePath = Paths.get("C:\\Users\\Blackout\\Documents\\IDEA\\Animated-visual-toolkit-for-Turing-machines\\src\\main\\resources\\TapeSaveTest.xml");

    public static void saveTuringMachine(TuringMachine tm, Path savePath) throws IOException {
        String xml = getTuringXML(tm);
        if(!Files.exists(savePath)){
            Files.createFile(savePath);
        }
        OutputStream outputStream = new FileOutputStream(savePath.toString());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.print(xml);
        writer.close();
    }

    public static void saveTape(Tape tape) throws IOException {
        String xml = getTapeXML(tape);
        if(!Files.exists(TapeSavePath)){
            Files.createFile(TapeSavePath);
        }
        OutputStream outputStream = new FileOutputStream(TapeSavePath.toString());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.print(xml);
        writer.close();
    }

    public static TuringMachine loadTuringMachine(File saveFile) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        FileInputStream stream = new FileInputStream(saveFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xml = builder.parse(stream);
        XPath xpath = XPathFactory.newInstance().newXPath();

        Element tmNode = (Element) xpath.compile("/TuringMachineSave").evaluate(xml, XPathConstants.NODE);

        String tmName = tmNode.getAttribute("name");

        HashMap<String, State> states = new HashMap<>();
        State initialState = new State();
        Element globalStatesNode = (Element) tmNode.getElementsByTagName("states").item(0);
        String initialStateName = globalStatesNode.getAttribute("initial");
        NodeList stateNodeList = globalStatesNode.getElementsByTagName("state");
        for (int i = 0; i < stateNodeList.getLength(); i++) {
            Element stateElement = (Element) stateNodeList.item(i);
            String stateName = stateElement.getAttribute("name");
            boolean isInitial = Boolean.parseBoolean(stateElement.getElementsByTagName("isInitial").item(0).getTextContent());
            boolean isAccepting = Boolean.parseBoolean(stateElement.getElementsByTagName("isAccepting").item(0).getTextContent());
            boolean isRejecting = Boolean.parseBoolean(stateElement.getElementsByTagName("isRejecting").item(0).getTextContent());
            boolean isHalting = Boolean.parseBoolean(stateElement.getElementsByTagName("isHalting").item(0).getTextContent());
            double xPos = Double.parseDouble(stateElement.getElementsByTagName("xPos").item(0).getTextContent());
            double yPos = Double.parseDouble(stateElement.getElementsByTagName("yPos").item(0).getTextContent());
            State state = new State();
            state.setName(stateName);
            state.setInitial(isInitial);
            state.setAccepting(isAccepting);
            state.setRejecting(isRejecting);
            state.setHalting(isHalting);
            state.setPosition(xPos, yPos);
            if(isInitial){
                initialState = state;
            }
            states.put(stateName, state);

        }
        TuringMachine tm = new TuringMachine(initialState);
        tm.setName(tmName);
        for (State state:states.values()) {
            if(!state.isInitial()){
                tm.addState(state);
            }
        }
        NodeList transitionsNodeList = tmNode.getElementsByTagName("transition");
        for (int i = 0; i < transitionsNodeList.getLength(); i++) {
            Element transitionElement = (Element) transitionsNodeList.item(i);
            State fromState = states.get(transitionElement.getElementsByTagName("fromState").item(0).getTextContent());
            State toState = states.get(transitionElement.getElementsByTagName("toState").item(0).getTextContent());
            char readSymbol = transitionElement.getElementsByTagName("readSymbol").item(0).getTextContent().charAt(0);
            char writeSymbol = transitionElement.getElementsByTagName("writeSymbol").item(0).getTextContent().charAt(0);
            char direction = transitionElement.getElementsByTagName("direction").item(0).getTextContent().charAt(0);
            tm.addTransition(new TuringTransition(fromState, toState, new TransitionRule(readSymbol, writeSymbol, direction)));
        }

        return tm;
    }

    public static Tape loadTape() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
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

    private static String getTuringXML(TuringMachine tm){
        Map<String, State> states = tm.getStates();
        State initialState = tm.getInitialState();
        Set<TuringTransition> turingTransitionFunction = tm.getTransitionFunction();

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n');
        builder.append("<TuringMachineSave name = \"").append(tm.getName()).append("\">" + '\n');
        builder.append('\t' + "<states initial = \"").append(initialState.getName()).append("\">" + '\n');
        for(State state: states.values()){
            builder.append(tab(2)).append("<state name = \"").append(state.getName()).append("\">" + '\n');
            builder.append(tab(3)).append("<isInitial>").append(state.isInitial()).append("</isInitial>" + '\n');
            builder.append(tab(3)).append("<isAccepting>").append(state.isAccepting()).append("</isAccepting>" + '\n');
            builder.append(tab(3)).append("<isRejecting>").append(state.isRejecting()).append("</isRejecting>" + '\n');
            builder.append(tab(3)).append("<isHalting>").append(state.isHalting()).append("</isHalting>" + '\n');
            builder.append(tab(3)).append("<xPos>").append(state.getPosition().getKey()).append("</xPos>" + '\n');
            builder.append(tab(3)).append("<yPos>").append(state.getPosition().getValue()).append("</yPos>" + '\n');
            builder.append(tab(2)).append("</state>").append('\n');
        }
        builder.append('\t' + "</states>" + '\n');
        builder.append('\t' + "<transitions>"  + '\n');
        for (TuringTransition turingTransition : turingTransitionFunction){
            TransitionRule rule = turingTransition.getTransitionRule();
            builder.append(tab(2)).append("<transition>").append('\n');
            builder.append(tab(3)).append("<fromState>").append(turingTransition.getFromState().getName()).append("</fromState>"  + '\n');
            builder.append(tab(3)).append("<toState>").append(turingTransition.getToState().getName()).append("</toState>"  + '\n');
            builder.append(tab(3)).append("<readSymbol>").append(rule.getReadSymbol()).append("</readSymbol>"  + '\n');
            builder.append(tab(3)).append("<writeSymbol>").append(rule.getWriteSymbol()).append("</writeSymbol>"  + '\n');
            builder.append(tab(3)).append("<direction>").append(rule.getDirection()).append("</direction>"  + '\n');
            builder.append(tab(2)).append("</transition>").append('\n');
        }
        builder.append('\t' + "</transitions>"  + '\n');
        builder.append("</TuringMachineSave>" + '\n');

        return builder.toString();
    }

    private static String getTapeXML(Tape tape){
        String name = tape.getName();
        List<Character> tapeArray = tape.getTapeArray();
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

    private static String tab(int times){
        StringBuilder builder = new StringBuilder();
        for (int i = times; i > 0; i--) {
            builder.append('\t');
        }
        return builder.toString();
    }
}
