package ui;

import canvasNodes.*;
import execution.*;
import io.SaveManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import turing.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class SimulatorController {
    @FXML private HBox executionHistorySection;
    @FXML private HBox tapeSection;
    @FXML private ListView<ExecutionPath> executionPathListView;
    @FXML private Button backButton;
    @FXML private Button loadButton;
    @FXML private Button loadExampleButton;
    @FXML private Button editButton;
    @FXML private Button tapeInputButton;
    @FXML private Button runOrNextButton;
    @FXML private Button pauseOrBackButton;
    @FXML private Button resetButton;
    @FXML private ToggleButton isAutoToggle;
    @FXML private Slider speedSlider;
    @FXML private Pane canvas;
    @FXML private Label inputStatusLabel;
    @FXML private Label tmNameLabel;
    @FXML private ScrollPane historyScroll;

    private Tape tape;
    private ArrayList<Character> originalTapeInput;
    private TuringMachine initialTM;
    private TuringMachine currentTM;
    private File currentSaveFile;
    private boolean isRunning;
    private ExecutorHandler executorHandler;
    private ExecutionTimer timer;
    private Thread timerThread;
    private Executor manualExecutor;
    private Stage primaryStage;
    private StateNode currentState;
    private Map<State, StateNode> stateStateNodeMap;
    private Map<StateNode, List<TransitionArrow>> stateNodeArrowMap;
    private boolean exampleLoaded;
    private boolean popupOpened;

    @FXML
    public void initialize(){
        stateStateNodeMap = new HashMap<>();
        stateNodeArrowMap = new HashMap<>();
        timer = new ExecutionTimer(this, speedSlider.getValue());
        timerThread = new Thread(timer);
        isRunning = false;
        runOrNextButton.setDisable(true);
        pauseOrBackButton.setDisable(true);
        popupOpened = false;
        configureUI();
    }

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    private void configureUI(){
        setUIDisable(true);
        backButton.setOnAction(event -> ControllerLoader.openMainMenu(primaryStage));
        loadButton.setOnAction(event -> loadFromFile());
        loadExampleButton.setOnAction(event -> loadExamplePopup());
        tapeInputButton.setOnAction(event -> addTapeInput());
        editButton.setOnAction(event -> openBuilderWindow());
        resetButton.setOnAction(event -> reset());
        isAutoToggle.setOnAction(event -> switchExecutionMode());
        isAutoToggle.setSelected(true);
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            timer.changeSpeed((Double) newValue);
        });
        runOrNextButton.setOnAction(event -> {
            if(isAutoToggle.isSelected()){
                run();
            }
            else{
                requestManualStep();
            }
        });
        pauseOrBackButton.setOnAction(event -> {
            if(isAutoToggle.isSelected()){
                pause();
            }
            else{
                stepBack();
            }
        });
    }

    private void setUIDisable(boolean isDisabled){
        editButton.setDisable(isDisabled);
        tapeInputButton.setDisable(isDisabled);
        resetButton.setDisable(isDisabled);
        speedSlider.setDisable(isDisabled);
        isAutoToggle.setDisable(isDisabled);
    }

    private void run(){
        if(!isRunning){
            try {
                pauseOrBackButton.setDisable(false);
                runOrNextButton.setDisable(true);
                timerThread.start();
                isRunning = true;
            }
            catch (IllegalThreadStateException e){
                NotificationManager.errorNotification("Cannot run the simulation", "Machine is in the final state", primaryStage);
            }
        }
    }

    private void pause(){
        if(isRunning){
            timer.stop();
            timerThread.interrupt();
            isRunning = false;
            timerThread = new Thread(timer);
            pauseOrBackButton.setDisable(true);
            runOrNextButton.setDisable(false);
        }
    }

    public void nextAutoStep(){
        String stateCode = "";
        if(executorHandler != null && isRunning){
            stateCode = executorHandler.step();
            if(!stateCode.equals("Run")){
                isRunning = false;
                runOrNextButton.setDisable(true);
                pauseOrBackButton.setDisable(true);;
            }
        }
        timer.stop();
        timerThread.interrupt();

        changeCurrentState();
        String finalStateCode = stateCode;
        if(executionHistorySection.getChildren().size() >= 1000){
            isRunning = false;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setInputStatus(finalStateCode);
                populateTape();
                updateUniquePathsSelector();
                if(finalStateCode.equals("Run")){
                    int stepIndex = executionHistorySection.getChildren().size();
                    if(stepIndex <= 1000){
                        recordStep(stepIndex);
                    }
                    else {
                        NotificationManager.confirmationNotification("Terminating simulation", "The maximum execution length of 1000 steps has been reached", primaryStage);
                        setInputStatus("Terminated");
                    }
                }
                else if(finalStateCode.equals("Accept")){
                    switchTM(executorHandler.getAcceptingExecution());
                }
                if(executorHandler != null && executorHandler.isExecutorStopped(currentTM)){
                    switchTM(executorHandler.getARunningTM());
                }
            }
        });
        if(isRunning){
            timerThread = new Thread(timer);
            timerThread.start();
        }
    }

    private void requestManualStep(){
        if(!popupOpened){
            if(manualExecutor == null){
                manualExecutor = new Executor(currentTM, tape);
            }

            char read = tape.read();
            List<TuringTransition> transitionList = manualExecutor.findTransitions(read);
            if(transitionList.size() == 0){
                runOrNextButton.setDisable(true);
                setInputStatus("Stuck");
            }
            else if(transitionList.size() > 1){
                selectNextStepPopup(transitionList);
            }
            else{
                manualStep(transitionList.get(0));
            }
        }
    }

    private void manualStep(TuringTransition turingTransition){
        manualExecutor.step(turingTransition);
        changeCurrentState();
        String stateCode;
        if(currentTM.getCurrentState().isAccepting()){
            stateCode = "Accept";
            runOrNextButton.setDisable(true);
        }
        else if(currentTM.getCurrentState().isRejecting()){
            stateCode = "Reject";
            runOrNextButton.setDisable(true);
        }
        else if(currentTM.getCurrentState().isHalting()){
            stateCode = "Halt";
            runOrNextButton.setDisable(true);
        }
        else{
            stateCode = "Run";
        }
        setInputStatus(stateCode);
        switchHistorySection();
        populateTape();
        pauseOrBackButton.setDisable(false);
    }

    private void stepBack(){
        if(!popupOpened){
            if(manualExecutor.getExecutionPath().getAllSteps().size() > 0){
                manualExecutor.stepBack();
                changeCurrentState();
                switchHistorySection();
                populateTape();
                runOrNextButton.setDisable(false);
                if(manualExecutor.getExecutionPath().getAllSteps().size() == 0){
                    pauseOrBackButton.setDisable(true);
                    tape.resetHead();
                    tape.setTapeArray(originalTapeInput);
                    setInputStatus("None");
                }
                else{
                    setInputStatus("Run");
                }
            }
        }
    }

    private void changeCurrentState(){
        if (currentTM != null) {
            StateNode newCurrentState = stateStateNodeMap.get(currentTM.getCurrentState());
            if(newCurrentState != null && currentState != newCurrentState){
                currentState.deactivate();
                currentState = newCurrentState;
                currentState.activate();
            }
        }
    }

    private void createTape(List<Character> tapeArray, VBox root, TextField textField) {
        originalTapeInput = new ArrayList<>();
        if(textField.getText().isEmpty()){
            textField.setText("~");
        }
        for (char ch:textField.getText().toCharArray()) {
            tapeArray.add(ch);
            originalTapeInput.add(ch);
        }
        tape = new Tape(tapeArray, 0);
        populateTape();
        canvas.getChildren().remove(root);
        popupOpened = false;
        executorHandler = new ExecutorHandler(initialTM, tape);
        runOrNextButton.setDisable(false);
        pauseOrBackButton.setDisable(true);
        reset();

    }

    public void populateTape(){
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
            int previousWriteIndex = tape.getPreviousHeadPos();
            if(tape.getHead() <= headCellIndex){
                tapeIndex = 0;
                leftSideIndex = headCellIndex - tape.getHead();
                rightSideIndex = Math.min(tapeArray.size() + leftSideIndex, numberOfCells);
            }
            else{
                tapeIndex = tape.getHead() - headCellIndex;
                leftSideIndex = 0;
                rightSideIndex = Math.min((tapeArray.size() - tapeIndex), numberOfCells);
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
                cellText.getStyleClass().clear();
                if(tapeIndex == previousWriteIndex){
                    cellText.getStyleClass().add("tape-text-previous");
                }
                else{
                    cellText.getStyleClass().add("tape-text");
                }
                tapeIndex++;
            }
        }
    }

    private void switchTM(TuringMachine tm){
        if(currentTM != tm && tm != null){
            currentTM = tm;
            changeCurrentState();
            switchHistorySection();
            tape = executorHandler.getTape(tm);
            populateTape();
            tmNameLabel.setText("TM: " + currentTM.getName());
            Tooltip tmNameTooltip = new Tooltip(tmNameLabel.getText());
            tmNameLabel.setTooltip(tmNameTooltip);
        }
    }

    private void recordStep(int stepIndex){

        VBox stepLabelContainer = new VBox();
        stepLabelContainer.setAlignment(Pos.CENTER);
        ExecutionStep executionStep = null;
        if(isAutoToggle.isSelected() && stepIndex <= executorHandler.getAllSteps(currentTM).size()){
            executionStep = executorHandler.getStep(currentTM, stepIndex - 1);
        }
        else if(!isAutoToggle.isSelected()){
            executionStep = manualExecutor.getExecutionPath().getAllSteps().get(stepIndex - 1);
        }
        if(executionStep != null){
            Label step = new Label(String.valueOf(stepIndex));
            Label from = new Label(executionStep.getFrom().getName());
            Label to = new Label(executionStep.getTo().getName());
            Label read = new Label(String.valueOf(executionStep.getRead()));
            Label write = new Label(String.valueOf(executionStep.getWrite()));
            Label move = new Label(String.valueOf(executionStep.getDirection()));
            stepLabelContainer.getChildren().addAll(step, from, to, read, write, move);
            for (Node label: stepLabelContainer.getChildren()) {
                label.getStyleClass().add("execution-history-label");
                ((Label) label).setAlignment(Pos.CENTER);
            }
            executionHistorySection.getChildren().add(stepLabelContainer);
            if(executionHistorySection.getWidth() > historyScroll.getHvalue()){
                historyScroll.setHvalue(executionHistorySection.getWidth() - historyScroll.getHvalue());
            }
        }
    }

    private void switchHistorySection(){
        resetHistorySection();
        List<ExecutionStep> steps;
        if(isAutoToggle.isSelected()){
            steps = executorHandler.getAllSteps(currentTM);
        }
        else{
            steps = manualExecutor.getExecutionPath().getAllSteps();
        }

        for (int i = 1; i <= steps.size(); i++) {
            recordStep(i);
        }
    }

    private void updateUniquePathsSelector(){
        if(isAutoToggle.isSelected()){
            executionPathListView.getItems().clear();
            Map<ExecutionPath, TuringMachine> executorMap = executorHandler.getUniqueExecutionPaths();
            for (ExecutionPath uniquePath: executorMap.keySet()) {
                if(!executionPathListView.getItems().contains(uniquePath)){
                    executionPathListView.getItems().add(uniquePath);
                }
            }
            executionPathListView.setOnMouseClicked(event -> {
                TuringMachine selectedTM = executorMap.get(executionPathListView.getSelectionModel().getSelectedItem());
                switchTM(selectedTM);
            });
        }
    }

    private void resetHistorySection(){
        VBox labels = (VBox) executionHistorySection.getChildren().get(0);
        executionHistorySection.getChildren().clear();
        executionHistorySection.getChildren().add(labels);
    }

    private void setInputStatus(String stateCode){
        switch(stateCode){
            case "Accept":
                inputStatusLabel.setText("Input status: Accepted");
                break;
            case "Reject":
                inputStatusLabel.setText("Input status: Rejected");
                break;
            case "Halt":
                inputStatusLabel.setText("Input status: Halted");
                break;
            case "Stuck":
                inputStatusLabel.setText("Input status: Stuck");
                break;
            case "Run":
                inputStatusLabel.setText("Input status: Running");
                break;
            case "None":
                inputStatusLabel.setText("Input status: None");
                break;
        }
    }

    private void loadStateNode(State state) {
        double xPos = state.getPosition().getKey();
        double yPos = state.getPosition().getValue();
        StateNode stateNode;
        if (state.isInitial()) {
            stateNode = new InitialStateNode(xPos, yPos);
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

        if(stateNode instanceof InitialStateNode){
            currentState = stateNode;
            stateNode.activate();
        }
        Group newNodeGroup = stateNode.getNodeGroup();
        stateStateNodeMap.put(state, stateNode);
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
                    break;
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
        transitionRule.setArrow(transitionArrow);
    }

    private void addTapeInput(){
        if(!popupOpened){
            List<Character> tapeArray = new ArrayList<>();
            VBox root = new VBox();
            root.getStylesheets().add("ui/stylesheets/stylesheet.css");
            root.getStyleClass().add("pop-up");
            root.setAlignment(Pos.CENTER);
            root.setPrefHeight(120);
            root.setPrefWidth(260);

            root.setLayoutX(0);
            root.setLayoutY(250);

            Text text = new Text("Enter the tape input"  + '\n' +"(~ represents an empty cell)");
            text.setTextAlignment(TextAlignment.CENTER);
            text.getStyleClass().add("pop-up-text");
            TextField textField = new TextField();
            if(originalTapeInput != null){
                StringBuilder existingInput = new StringBuilder();
                for (char ch: originalTapeInput) {
                    existingInput.append(ch);
                }
                textField.setText(existingInput.toString());
            }
            textField.getStyleClass().add("pop-up-textfield");
            textField.setId("InputField");
            textField.setOnKeyPressed(event -> {
                if(event.getCode().equals(KeyCode.ENTER)){
                    createTape(tapeArray, root, textField);
                }
            });
            Button confirmButton = new Button("Confirm");
            confirmButton.setId("Confirm");
            confirmButton.getStyleClass().add("buttons");
            confirmButton.setOnAction(event -> {
                createTape(tapeArray, root, textField);
            });
            root.getChildren().addAll(text, textField, confirmButton);
            popupOpened = true;
            canvas.getChildren().add(root);
        }
    }

    private void openBuilderWindow(){
        BuilderController controller = ControllerLoader.openBuilderWindow(primaryStage);
        controller.loadTuringMachine(initialTM);
        if(!exampleLoaded){
            controller.setSaveFile(currentSaveFile);
        }
    }

    private void switchExecutionMode(){
        if(!popupOpened){
            reset();
            if(isAutoToggle.isSelected()){
                runOrNextButton.setText("Run");
                pauseOrBackButton.setText("Pause");
                isAutoToggle.setText("Auto Simulation");
                speedSlider.setDisable(false);
                executionPathListView.setDisable(false);
            }
            else{
                runOrNextButton.setText("Next");
                pauseOrBackButton.setText("Back");
                isAutoToggle.setText("Manual Simulation");
                speedSlider.setDisable(true);
                executionPathListView.setDisable(true);
            }
        }
    }

    private void selectNextStepPopup(List<TuringTransition> transitionList){
        if(!popupOpened){
            VBox root = new VBox();
            root.getStylesheets().add("ui/stylesheets/stylesheet.css");
            root.getStyleClass().add("pop-up");
            root.setPrefHeight(180);
            root.setPrefWidth(150);
            root.setLayoutX(0);
            root.setLayoutY(250);
            Label label = new Label("Select transition:");

            label.getStyleClass().add("pop-up-label");
            ListView<TuringTransition> listView = new ListView<>();

            Collections.sort(transitionList);
            for (TuringTransition transition: transitionList) {
                listView.getItems().add(transition);
            }

            listView.setOnMouseClicked(event -> {
                TuringTransition selected = listView.getSelectionModel().getSelectedItem();
                if(selected != null){
                    manualStep(selected);
                    canvas.getChildren().remove(root);
                    popupOpened = false;
                }
            });
            root.getChildren().addAll(label, listView);
            canvas.getChildren().add(root);
            popupOpened = true;
        }
    }

    private void loadExamplePopup(){
        if(!popupOpened){
            VBox root = new VBox();
            root.getStylesheets().add("ui/stylesheets/stylesheet.css");
            root.getStyleClass().add("pop-up");
            root.setAlignment(Pos.CENTER);
            root.setPrefHeight(260);
            root.setPrefWidth(150);
            root.setLayoutX(0);
            root.setLayoutY(200);
            ListView<String> listView = new ListView<>();
            listView.getItems().add("BitFlipper");
            listView.getItems().add("IsPalindrome");
            listView.getItems().add("IsDivisibleBy3");
            listView.getItems().add("Is_y_in_x(NDTM)");
            listView.getItems().add("BinaryAddition");
            listView.getItems().add("Copier");
            listView.getItems().add("Infinite1Fill");
            listView.getItems().add("InfiniteNDTM");
            listView.setId("Examples");
            listView.setOnMouseClicked(event -> {
                loadExample(listView.getSelectionModel().getSelectedItem());
                canvas.getChildren().remove(root);
                popupOpened = false;
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("buttons");
            cancelButton.setPrefWidth(140);
            cancelButton.setOnAction(event -> {
                canvas.getChildren().remove(root);
                popupOpened = false;
            });

            root.getChildren().addAll(listView, cancelButton);
            canvas.getChildren().add(root);
            popupOpened = true;
        }
    }

    public void loadExample(String fileName){
        if(fileName != null && !fileName.isEmpty()){
            try {
                InputStream stream = SimulatorController.class.getResourceAsStream("examples/" + fileName + ".xml");
                TuringMachine exampleTM = SaveManager.loadTuringMachineFromStream(stream);
                loadTuringMachine(exampleTM);
                exampleLoaded = true;
            } catch (Exception e) {
                NotificationManager.errorNotification("Error", "Could not load the example file", primaryStage);
            }
        }
    }

    private void loadFromFile(){
        try{
            FileChooser directoryChooser = new FileChooser();
            directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
            currentSaveFile = directoryChooser.showOpenDialog(primaryStage);
            loadTuringMachine(SaveManager.loadTuringMachine(currentSaveFile));

        }
        catch (Exception e){
            NotificationManager.errorNotification("Loading error", "Failed to load the Turing machine encoding from file", primaryStage);
        }
    }

    public void loadTuringMachine(TuringMachine tm){
        if(tm != null){
            clearCanvas();
            tape = null;
            originalTapeInput = new ArrayList<>();
            reset();
            initialTM = tm;
            currentTM = initialTM;
            tmNameLabel.setText("TM: " + currentTM.getName());
            Tooltip tmNameTooltip = new Tooltip(tmNameLabel.getText());
            tmNameLabel.setTooltip(tmNameTooltip);
            double highestX = 0;
            double highestY = 0;
            for(State state: currentTM.getStates().values()){
                Pair<Double, Double> stateNodePosition = state.getPosition();
                if(stateNodePosition.getKey() > highestX){
                    highestX = stateNodePosition.getKey();
                }
                if(stateNodePosition.getValue() > highestY){
                    highestY = stateNodePosition.getValue();
                }
                loadStateNode(state);
            }
            for(TuringTransition turingTransition : currentTM.getTransitionFunction()){
                TransitionRule transitionRule = turingTransition.getTransitionRule();
                State fromState = turingTransition.getFromState();
                State toState = turingTransition.getToState();
                addTransitionArrow(stateStateNodeMap.get(fromState), stateStateNodeMap.get(toState), transitionRule);
            }
            setUIDisable(false);
            while(highestX > canvas.getMinWidth()){
                increaseCanvas(true);
            }
            while (highestY > canvas.getMinHeight()){
                increaseCanvas(false);
            }
        }
    }

    public void setSaveFile(File saveFile){
        currentSaveFile = saveFile;
    }

    private void increaseCanvas(boolean increaseWidth){
        if(increaseWidth){
            canvas.setMinWidth(canvas.getMinWidth() + 150);
        }
        else{
            canvas.setMinHeight(canvas.getMinHeight() + 150);
        }
    }

    private void clearCanvas(){
        canvas.getChildren().clear();
        stateNodeArrowMap.clear();
        stateStateNodeMap.clear();
        exampleLoaded = false;
        popupOpened = false;
        tmNameLabel.setText("TM: Not loaded");
    }

    private void reset(){
        if(tape != null){
            tape.setTapeArray(originalTapeInput);
            tape.resetHead();
            runOrNextButton.setDisable(false);
        }
        else{
            runOrNextButton.setDisable(true);
        }
        populateTape();
        pauseOrBackButton.setDisable(true);
        resetHistorySection();
        executionPathListView.getItems().clear();
        if(initialTM != null){
            initialTM.reset();
            currentTM = initialTM;
            changeCurrentState();
            timer.stop();
            isRunning = false;
        }
        timerThread = new Thread(timer);
        if(isAutoToggle.isSelected()){
            executorHandler = new ExecutorHandler(currentTM, tape);
            manualExecutor = null;
        }
        else{
            executorHandler = null;
            manualExecutor = new Executor(currentTM, tape);
        }
        inputStatusLabel.setText("Input status: None");
    }

    public Tape getTape(){
        return tape;
    }

    public TuringMachine getCurrentTM(){
        return currentTM;
    }
}
