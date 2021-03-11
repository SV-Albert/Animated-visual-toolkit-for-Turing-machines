package ui;

import canvasNodes.*;
import execution.ExecutionTimer;
import io.SaveManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import turing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatorController {
    @FXML private HBox executionHistorySection;
    @FXML private HBox tapeSection;
    @FXML private Button backButton;
    @FXML private Button loadButton;
    @FXML private Button editButton;
    @FXML private Button tapeInputButton;
    @FXML private Button runPauseButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;
    @FXML private Pane canvas;

    private Tape tape;
    private ArrayList<Character> originalTapeInput;
    private TuringMachine initialTM;
    private TuringMachine currentTM;
    private File currentSaveFile;
    private boolean isRunning;
    private TuringMachineHandler tmHandler;
    private ExecutionTimer timer;
    private Thread timerThread;
    private Stage primaryStage;
    private StateNode currentState;
    private Map<State, StateNode> stateStateNodeMap;
    private Map<StateNode, List<TransitionArrow>> stateNodeArrowMap;

    @FXML
    public void initialize(){
        stateStateNodeMap = new HashMap<>();
        stateNodeArrowMap = new HashMap<>();
        timer = new ExecutionTimer(this, speedSlider.getValue());
        timerThread = new Thread(timer);
        isRunning = false;
        runPauseButton.setDisable(true);
        configureUI();
    }

    private void configureUI(){
        setUIDisable(true);
        backButton.setOnAction(event -> ControllerLoader.openMainMenu(primaryStage));
        loadButton.setOnAction(event -> loadFromFile());
        tapeInputButton.setOnAction(event -> addTapeInput());
        editButton.setOnAction(event -> openBuilderWindow());
        resetButton.setOnAction(event -> reset());
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            timer.changeSpeed((Double) newValue);
        });
        runPauseButton.setOnAction(event -> {
            if(isRunning){
//                tmHandler.stop();
                timer.stop();
                isRunning = false;
                timerThread = new Thread(timer);
            }
            else{
//                tmHandler.run();
                timerThread.start();
                isRunning = true;
            }
        });
    }
    public void nextStep(){
        if(tmHandler != null && isRunning){
            isRunning = tmHandler.step();
        }
        currentState.deactivate();
        currentState = stateStateNodeMap.get(currentTM.getCurrentState());
        currentState.activate();
        populateTape();
        if(!isRunning){
            timer.stop();
        }
    }

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    private void setUIDisable(boolean isDisabled){
        editButton.setDisable(isDisabled);
        tapeInputButton.setDisable(isDisabled);
        resetButton.setDisable(isDisabled);
        speedSlider.setDisable(isDisabled);
    }

    private void loadStateNode(State state) {
        double xPos = state.getPosition().getKey();
        double yPos = state.getPosition().getValue();
        StateNode stateNode;
        if (state.isInitial()) {
            stateNode = new InitialStateNode(xPos, yPos);
            currentState = stateNode;
            stateNode.activate();
        }
        else if (state.isAccepting()) {
            stateNode = new AcceptingStateNode(xPos, yPos);
        }
        else if (state.isRejecting()) {
            stateNode = new RejectingStateNode(xPos, yPos);
        }
        else if (state.isHalting()) {
            stateNode = new HaltingStateNode(xPos, yPos);
        }
        else {
            stateNode = new StateNode(xPos, yPos);
        }
        stateNode.setState(state);
        stateNode.setName(state.getName());
        stateStateNodeMap.put(state, stateNode);
        Group newNodeGroup = stateNode.getNodeGroup();
        canvas.getChildren().add(newNodeGroup);
    }

    private void addTransitionArrow(StateNode from, StateNode to, TransitionRule transitionRule){
        TransitionArrow transitionArrow = new TransitionArrow(from, to);
        boolean arrowExists = false;
        if(stateNodeArrowMap.containsKey(from) && !stateNodeArrowMap.get(from).isEmpty()){
            for(TransitionArrow arrowFromState: stateNodeArrowMap.get(from)){
                if(arrowFromState.getFrom() == from && arrowFromState.getTo() == to){
                    transitionArrow = arrowFromState;
                    arrowExists = true;
                }
            }
        }
        if(!arrowExists){
            if(stateNodeArrowMap.containsKey(from)){
                stateNodeArrowMap.get(from).add(transitionArrow);
            }
            else{
                List<TransitionArrow> newArrowList = new ArrayList<>();
                newArrowList.add(transitionArrow);
                stateNodeArrowMap.put(from, newArrowList);
            }
            if(to!= from){
                if(stateNodeArrowMap.containsKey(to)){
                    stateNodeArrowMap.get(to).add(transitionArrow);
                }
                else{
                    List<TransitionArrow> newArrowList = new ArrayList<>();
                    newArrowList.add(transitionArrow);
                    stateNodeArrowMap.put(to, newArrowList);
                }
            }
            canvas.getChildren().add(transitionArrow.getLineGroup());
        }
        transitionArrow.addRules(transitionRule);
    }

    private void openBuilderWindow(){
        BuilderController controller = ControllerLoader.openBuilderWindow(primaryStage);
        controller.loadTuringMachine(initialTM);
        controller.setSaveFile(currentSaveFile);
    }

    private void addTapeInput(){
        List<Character> tapeArray = new ArrayList<>();
        VBox root = new VBox();
        root.getStylesheets().add("stylesheet.css");
        root.getStyleClass().add("pop-up");
        root.setAlignment(Pos.CENTER);
        root.setPrefHeight(120);
        root.setPrefWidth(260);

        root.setLayoutX(0);
        root.setLayoutY(430);

        Text text = new Text("Enter the tape input"  + '\n' +"(~ represents an empty cell)");
        text.setTextAlignment(TextAlignment.CENTER);
        text.getStyleClass().add("pop-up-text");
        TextField textField = new TextField();
        if(tape != null){
            StringBuilder existingInput = new StringBuilder();
            for (char ch: tape.getTapeArray()) {
                existingInput.append(ch);
            }
            textField.setText(existingInput.toString());
        }
        textField.getStyleClass().add("pop-up-textfield");
        textField.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                createTape(tapeArray, root, textField);
            }
        });
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("simulator-buttons");
        confirmButton.setOnAction(event -> {
            createTape(tapeArray, root, textField);
        });
        root.getChildren().addAll(text, textField, confirmButton);
        canvas.getChildren().add(root);
    }

    private void createTape(List<Character> tapeArray, VBox root, TextField textField) {
        originalTapeInput = new ArrayList<>();
        if(!textField.getText().isEmpty()){
            for (char ch:textField.getText().toCharArray()) {
                tapeArray.add(ch);
                originalTapeInput.add(ch);
            }
            tape = new Tape(tapeArray, 0);
            populateTape();
            canvas.getChildren().remove(root);
            tmHandler = new TuringMachineHandler(initialTM, tape);
            runPauseButton.setDisable(false);
            reset();
        }
    }

    private void populateTape(){
        int numberOfCells = tapeSection.getChildren().size();
        int headCellIndex = numberOfCells/2;
        for (Node node: tapeSection.getChildren()) {
            ((Text)((StackPane)node).getChildren().get(1)).setText("");
        }
        if(tape != null){
            List<Character> tapeArray = tape.getTapeArray();
            int tapeIndex;
            int leftSideIndex;
            int rightSideIndex;
            if(tape.getHead() <= headCellIndex){
                tapeIndex = 0;
                leftSideIndex = headCellIndex - tape.getHead();
                rightSideIndex = Math.min(tapeArray.size() + leftSideIndex, numberOfCells);
            }
            else{
                tapeIndex = tape.getHead() - headCellIndex;
                //TODO ??????
                leftSideIndex = 0;
                rightSideIndex = Math.min((tapeArray.size() - tapeIndex), numberOfCells);
                System.out.println("Size: " + tapeArray.size());
                System.out.println("rightSideIndex: " + rightSideIndex);
            }

            for (int i = leftSideIndex; i < rightSideIndex; i++) {
                char tapeChar = tapeArray.get(tapeIndex);
                StackPane cell = (StackPane) tapeSection.getChildren().get(i);
                Text cellText = (Text) cell.getChildren().get(1);
                if(tapeChar == '~'){
                    cellText.setText("");
                }
                else{
                    cellText.setText(String.valueOf(tapeChar));
                }
                tapeIndex++;
            }
//
//            if(tapeLength <= numberOfCells){
//                int offset = (numberOfCells - tapeLength)/2;
//                int tapeIndex = 0;
//                for (int i = offset; i < offset + tapeLength; i++) {
//                    StackPane cell = (StackPane) tapeSection.getChildren().get(i);
//                    Text cellText = (Text) cell.getChildren().get(1);
//                    cellText.setText(String.valueOf(tapeArray.get(tapeIndex)));
//                    tapeIndex++;
//                }
//            }
//            else{
//                int tapeIndex = tape.getHead();
//                for (int i = 0; i < numberOfCells; i++) {
//                    StackPane cell = (StackPane) tapeSection.getChildren().get(i);
//                    Text cellText = (Text) cell.getChildren().get(1);
//                    if(tapeLength > tapeIndex){
//                        cellText.setText(String.valueOf(tapeArray.get(tapeIndex)));
//                    }
//                    tapeIndex++;
//                }
//            }
        }
    }

    public void setSaveFile(File saveFile){
        currentSaveFile = saveFile;
    }

    private void loadFromFile(){
        try{
            FileChooser directoryChooser = new FileChooser();
            directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
            currentSaveFile = directoryChooser.showOpenDialog(primaryStage);
            loadTuringMachine(SaveManager.loadTuringMachine(currentSaveFile));

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadTuringMachine(TuringMachine tm){
        initialTM = tm;
        currentTM = initialTM;
        clearCanvas();
        for(State state: tm.getStates().values()){
            loadStateNode(state);
        }
        for(TuringTransition turingTransition : tm.getTransitionFunction()){
            TransitionRule transitionRule = turingTransition.getTransitionRule();
            State fromState = turingTransition.getFromState();
            State toState = turingTransition.getToState();
            addTransitionArrow(stateStateNodeMap.get(fromState), stateStateNodeMap.get(toState), transitionRule);
        }
        setUIDisable(false);
    }

    private void clearCanvas(){
        canvas.getChildren().clear();
        stateNodeArrowMap.clear();
        stateStateNodeMap.clear();
    }

    private void reset(){
        tape.setTapeArray(originalTapeInput);
        tape.resetHead();
        populateTape();
        currentTM.reset();
        currentState.deactivate();
        currentState = stateStateNodeMap.get(currentTM.getInitialState());
        currentState.activate();
        timer.stop();
        timerThread = new Thread(timer);
        isRunning = false;
        tmHandler = new TuringMachineHandler(currentTM, tape);
    }
}
