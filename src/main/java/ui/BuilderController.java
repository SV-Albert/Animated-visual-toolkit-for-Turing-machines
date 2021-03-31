package ui;

import io.SaveManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import canvasNodes.*;
import javafx.util.Pair;
import turing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuilderController {
    @FXML private Group initialStateNode;
    @FXML private Group stateNode;
    @FXML private Group finalAcceptingStateNode;
    @FXML private Group finalRejectingStateNode;
    @FXML private Group haltingStateNode;
    @FXML private Button saveToFileButton;
    @FXML private Button saveButton;
    @FXML private Button loadButton;
    @FXML private Button addTransitionsButton;
    @FXML private Button runButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;
    @FXML private Pane canvas;
    @FXML private TextField tmNameField;

    private Stage primaryStage;
    private TuringMachine tm;
    private Group nodeDragged;
    private boolean nodeMoved;
    private boolean initialStateExists;
    private Group fromTransitionNode;
    private File currentSaveFile;
    private List<Group> toolbarStateNodes;
    private List<String> usedNames;
    private Map<State, StateNode> stateStateNodeMap;
    private Map<Group, StateNode> groupStateNodeMap;
    private Map<StateNode, List<TransitionArrow>> stateNodeArrowMap;
    private Map<StateNode, List<StateNode>> transitions;
    private VBox transitionRulesInputWindow;

    //Setup methods
    @FXML
    public void initialize(){
        initialStateExists = false;
        nodeMoved = false;
        usedNames = new ArrayList<>();
        toolbarStateNodes = new ArrayList<>();
        stateStateNodeMap = new HashMap<>();
        groupStateNodeMap = new HashMap<>();
        stateNodeArrowMap = new HashMap<>();
        transitions = new HashMap<>();
        toolbarStateNodes.add(initialStateNode);
        toolbarStateNodes.add(stateNode);
        toolbarStateNodes.add(finalAcceptingStateNode);
        toolbarStateNodes.add(finalRejectingStateNode);
        toolbarStateNodes.add(haltingStateNode);
        configureUI();
    }

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    private void setupTransitionRulesInputWindow(){
        Map<Character, Integer> directionMap = new HashMap<>();
        directionMap.put('R', 0);
        directionMap.put('L', 1);
        directionMap.put('N', 2);
        transitionRulesInputWindow = new VBox();
        transitionRulesInputWindow.setAlignment(Pos.TOP_CENTER);
        transitionRulesInputWindow.setPrefWidth(330);
        transitionRulesInputWindow.setPrefHeight(200);
        if(tm != null && !tm.getTransitionFunction().isEmpty()){
            for (TuringTransition turingTransition: tm.getTransitionFunction()) {
                TransitionRule transitionRule = turingTransition.getTransitionRule();
                HBox inputFields = getTransitionRuleInputFields();
                ((TextField) inputFields.getChildren().get(0)).setText(turingTransition.getFromState().getName());
                ((TextField) inputFields.getChildren().get(1)).setText(turingTransition.getToState().getName());
                ((TextField) inputFields.getChildren().get(2)).setText(String.valueOf(transitionRule.getReadSymbol()));
                ((TextField) inputFields.getChildren().get(3)).setText(String.valueOf(transitionRule.getWriteSymbol()));
                ((ChoiceBox<?>) inputFields.getChildren().get(4)).getSelectionModel().select(directionMap.get(transitionRule.getDirection()));
                transitionRulesInputWindow.getChildren().add(inputFields);
            }
        }
        HBox inputFields = getTransitionRuleInputFields();
        transitionRulesInputWindow.getChildren().add(inputFields);
    }

    private void configureUI(){
        configureDragAndDrop();

        tmNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!tmNameField.getText().isEmpty()){
                tm.setName(tmNameField.getText());
            }
        });

        saveButton.setOnAction(e -> save());
        saveToFileButton.setOnAction(e -> saveToFile());
        loadButton.setOnAction(e -> load());
        clearButton.setOnAction(e -> clearCanvas());
        runButton.setOnAction(event -> {
            SimulatorController controller = ControllerLoader.openSimulatorWindow(primaryStage);
            controller.loadTuringMachine(tm);
            controller.setSaveFile(currentSaveFile);
        });
        addTransitionsButton.setOnAction(e -> addTransitionsThroughRulesPopup());
        backButton.setOnAction(event -> ControllerLoader.openMainMenu(primaryStage));
        setUIDisable(true);
        setupTransitionRulesInputWindow();
    }

    private void configureDragAndDrop(){
        for(Group nodeGroup: toolbarStateNodes){
            addDragging(nodeGroup);
        }

        canvas.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });

        canvas.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                double xPos = event.getX();
                double yPos = event.getY();
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
                if(xPos > canvas.getPrefWidth() - 50){
                    increaseCanvas(true);
                }
                else if(yPos> canvas.getPrefHeight() - 50){
                    increaseCanvas(false);
                }
                if(nodeMoved){
                    moveStateNode(nodeDragged, xPos, yPos);
                }
                else {
                    addStateNode(nodeDragged, xPos, yPos);
                }
                nodeMoved = false;
            }
        });
    }

    private void addDragging(Group nodeGroup){
        nodeGroup.setOnDragDetected(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                Dragboard dragboard;
                if(canvas.getChildren().contains(nodeGroup)){
                    dragboard = nodeGroup.startDragAndDrop(TransferMode.MOVE);
                    nodeMoved = true;
                }
                else {
                    dragboard = nodeGroup.startDragAndDrop(TransferMode.COPY);
                }
                nodeDragged = nodeGroup;
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                Image dragImage = nodeGroup.snapshot(parameters, null);
                ClipboardContent content = new ClipboardContent();
                content.putImage(dragImage);
                dragboard.setContent(content);
                event.consume();
            }
        });
    }

    //UI Methods

    private void setUIDisable(boolean isDisabled){
        stateNode.setDisable(isDisabled);
        stateNode.setOpacity(stateNode.isDisabled() ? 0.1 : 1);
        for(Node node: finalAcceptingStateNode.getChildren()){
            node.setDisable(isDisabled);
            node.setOpacity(node.isDisabled() ? 0.1 : 1);
        }
        for(Node node: finalRejectingStateNode.getChildren()){
            node.setDisable(isDisabled);
            node.setOpacity(node.isDisabled() ? 0.1 : 1);
        }
        for(Node node: haltingStateNode.getChildren()){
            node.setDisable(isDisabled);
            node.setOpacity(node.isDisabled() ? 0.1 : 1);
        }
        saveToFileButton.setDisable(isDisabled);
        saveButton.setDisable(isDisabled);
        runButton.setDisable(isDisabled);
        tmNameField.setDisable(isDisabled);
        clearButton.setDisable(isDisabled);
    }

    private void addContextMenu(Group nodeGroup){
        StateNode stateNode = groupStateNodeMap.get(nodeGroup);
        ContextMenu menu = new ContextMenu();
        MenuItem renameState = new MenuItem("Rename State");
        renameState.setOnAction(event -> {
            usedNames.remove(stateNode.getName());
            enterStateNamePopUp(stateNode, stateNode.getState());
        });
        MenuItem deleteState = new MenuItem("Delete State");
        deleteState.setOnAction(event -> deleteState(nodeGroup));
        MenuItem deleteTransition = new MenuItem("Delete TuringTransition...");
        deleteTransition.setOnAction(event -> {
            ContextMenu removeTransitionMenu = getDeleteTransitionArrowsContextMenu(nodeGroup);
            removeTransitionMenu.show(nodeGroup, Side.RIGHT, menu.getPrefWidth(), 0);
        });
        menu.getItems().addAll(renameState, deleteState, deleteTransition);

        nodeGroup.setOnContextMenuRequested(event -> menu.show(nodeGroup, event.getScreenX(), event.getScreenY()));
        if(stateNode.getState().isInitial()){
            menu.getItems().remove(deleteState);
        }
    }

    private void clearCanvas(){
        canvas.getChildren().clear();
        transitions.clear();
        stateNodeArrowMap.clear();
        stateStateNodeMap.clear();
        groupStateNodeMap.clear();
        usedNames.clear();
        tmNameField.clear();
        tm = null;
        initialStateExists = false;
        setUIDisable(true);
    }

    private void increaseCanvas(boolean increaseWidth){
        if(increaseWidth){
            canvas.setMinWidth(canvas.getMinWidth() + 100);
        }
        else{
            canvas.setMinHeight(canvas.getMinHeight() + 100);
        }
    }

    //Event handling methods

    private void addClickListener(Group nodeGroup){
        nodeGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isControlDown() || event.isMetaDown()){
                    if(fromTransitionNode == null){
                        fromTransitionNode = nodeGroup;
                    }
                    else {
                        addTransitionWithPopup(groupStateNodeMap.get(fromTransitionNode), groupStateNodeMap.get(nodeGroup));
                        fromTransitionNode = null;
                    }
                }
            }
        });
    }

    private void addLengthLimit(TextField textField){
        //https://stackoverflow.com/questions/15159988/javafx-2-2-textfield-maxlength

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int length = textField.getText().length();
                if(length > 1){
                    textField.setText(String.valueOf(textField.getText().charAt(length - 1)));
                }
            }
        });
    }

    private void onTransitionRuleInputChange(HBox inputs){
        boolean allNonEmpty = true;
        for (Node inputNode: inputs.getChildren()) {
            if((inputNode instanceof TextField && ((TextField) inputNode).getText().isEmpty()) ||
                    (inputNode instanceof ChoiceBox && ((ChoiceBox<?>) inputNode).getValue() == null)){
                allNonEmpty = false;
            }
        }
        if(allNonEmpty && !transitionRulesInputWindow.getChildren().isEmpty() && inputs == transitionRulesInputWindow.getChildren().get(transitionRulesInputWindow.getChildren().size() - 1)){
            transitionRulesInputWindow.getChildren().add(getTransitionRuleInputFields());
        }
    }

    private void onSpecialStatesInputChange(HBox inputs){
        boolean allNonEmpty = true;
        for (Node inputNode: inputs.getChildren()) {
            if((inputNode instanceof TextField && ((TextField) inputNode).getText().isEmpty())){
                allNonEmpty = false;
            }
        }
        if(allNonEmpty){
            TextField newTextField = getNewPopupTextField("");
            newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(inputs));
            inputs.getChildren().add(newTextField);
        }
    }

    //Popup methods

    private void enterStateNamePopUp(StateNode stateNode, State state){
        VBox root = new VBox();
        root.getStylesheets().add("stylesheet.css");
        root.getStyleClass().add("pop-up");
        root.setAlignment(Pos.CENTER);
        root.setPrefHeight(100);
        root.setPrefWidth(260);
        double xPos = Math.min(Math.max(stateNode.getX() - 130, 0), canvas.getWidth() - 260);
        double yPos = Math.min(Math.max(stateNode.getY() + 40, 0), canvas.getHeight() - 100);
        while(yPos > canvas.getHeight() + 150){
            increaseCanvas(false);
        }
        root.setLayoutX(xPos);
        root.setLayoutY(yPos);
        Text text = new Text("Enter the state name");
        text.getStyleClass().add("pop-up-text");
        TextField textField = new TextField();
        textField.getStyleClass().add("pop-up-textfield");
        textField.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                if(addStateName(textField, stateNode, state)){
                    canvas.getChildren().remove(root);
                }
            }
        });
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("buttons");
        confirmButton.setOnAction(event -> {
            if(addStateName(textField, stateNode, state)){
                canvas.getChildren().remove(root);
            }
        });
        root.getChildren().addAll(text, textField, confirmButton);
        canvas.getChildren().add(root);
    }

    private void addTransitionWithPopup(StateNode from, StateNode to){
        if(!(from instanceof AcceptingStateNode || from instanceof RejectingStateNode || from instanceof HaltingStateNode)){
            TransitionRule transitionRule = newTransitionPopup(from.getX(), from.getY());
            TuringTransition tmTuringTransition = new TuringTransition(from.getState(), to.getState(), transitionRule);
            tm.addTransition(tmTuringTransition);
            addTransitionArrow(from, to, transitionRule);
        }
        else{
            NotificationManager.errorNotification("Illegal transition", "Cannot add a transition from the final state " + from.getName(), primaryStage);
        }
    }

    private TransitionRule newTransitionPopup(double xPos, double yPos){

        VBox root = new VBox();
        root.getStylesheets().add("stylesheet.css");
        root.getStyleClass().add("pop-up");
        root.setAlignment(Pos.CENTER);
        root.setPrefHeight(100);
        root.setPrefWidth(250);
        while(yPos > canvas.getHeight() + 150){
            increaseCanvas(false);
        }

        double x = Math.min(Math.max(xPos, 0), canvas.getWidth() - 250);
        double y = Math.min(Math.max(yPos + 40, 0), canvas.getHeight() - 100);
        root.setLayoutX(x);
        root.setLayoutY(y);
        HBox fields = new HBox();
        fields.setAlignment(Pos.CENTER);

        VBox readSection = new VBox();
        readSection.setPrefWidth(70);
        readSection.setAlignment(Pos.CENTER);
        Text read = new Text("Read");
        read.getStyleClass().add("pop-up-text");
        TextField readField = new TextField();
        readField.setPrefWidth(70);
        addLengthLimit(readField);
        readField.getStyleClass().add("pop-up-textfield");
        readSection.getChildren().addAll(read, readField);

        VBox writeSection = new VBox();
        writeSection.setPrefWidth(70);
        writeSection.setAlignment(Pos.CENTER);
        Text write = new Text("Write");
        write.getStyleClass().add("pop-up-text");
        TextField writeField = new TextField();
        writeField.setPrefWidth(70);
        addLengthLimit(writeField);
        writeField.getStyleClass().add("pop-up-textfield");
        writeSection.getChildren().addAll(write, writeField);

        VBox directionSection = new VBox();
        directionSection.setPrefWidth(70);
        directionSection.setAlignment(Pos.CENTER);
        Text direction = new Text("Direction");
        direction.getStyleClass().add("pop-up-text");
        ComboBox<Character> directionChoices = new ComboBox<Character>();
        directionChoices.setPrefWidth(70);
        directionChoices.getItems().add('R');
        directionChoices.getItems().add('L');
        directionChoices.getItems().add('N');
        directionChoices.getStyleClass().add("pop-up-combobox");
        directionSection.getChildren().addAll(direction, directionChoices);

        fields.getChildren().addAll(readSection, writeSection, directionSection);

        TransitionRule transitionRule = new TransitionRule();
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("buttons");
        confirmButton.setOnAction(event -> {
            char readChar;
            if(!readField.getText().isEmpty()){
                readChar = readField.getText().charAt(0);
            }
            else {
                readChar = '~';
            }
            char writeChar;
            if(!writeField.getText().isEmpty()){
                writeChar = writeField.getText().charAt(0);
            }
            else{
                writeChar = '~';
            }
            char directionChar;
            if(directionChoices.getValue() != null){
                directionChar = directionChoices.getValue();
            }
            else{
                directionChar = 'N';
            }
            transitionRule.setReadSymbol(readChar);
            transitionRule.setWriteSymbol(writeChar);
            transitionRule.setDirection(directionChar);
            canvas.getChildren().remove(root);
        });
        confirmButton.getStyleClass().add("buttons");

        root.getChildren().addAll(fields, confirmButton);
        canvas.getChildren().add(root);
        return transitionRule;
    }

    private void addTransitionsThroughRulesPopup(){
        BorderPane root = new BorderPane();
        ScrollPane content = new ScrollPane();
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setupTransitionRulesInputWindow();
        content.setContent(transitionRulesInputWindow);
        root.getStylesheets().add("stylesheet.css");
        root.getStyleClass().add("pop-up");

        double xPos = 0;
        double yPos = 275;
        root.setLayoutX(xPos);
        root.setLayoutY(yPos);

        HBox labels = new HBox();
        labels.setAlignment(Pos.CENTER_LEFT);
        Label fromLabel = new Label("From");
        Label toLabel = new Label(" To ");
        Label readLabel = new Label("Read");
        Label writeLabel = new Label("Write");
        Label moveLabel = new Label("Move");
        labels.getChildren().addAll(fromLabel, toLabel, readLabel, writeLabel, moveLabel);
        for (Node label: labels.getChildren()) {
            label.getStyleClass().add("pop-up-label");
            ((Label)label).setPrefWidth(63);
        }

        VBox specialStateInputs = new VBox();

        int labelWidth = 95;
        HBox acceptingStates = new HBox();
        acceptingStates.setMaxWidth(300);
        Label acceptingStatesLabel = new Label("Accepting states");
        acceptingStatesLabel.setTextAlignment(TextAlignment.CENTER);
        acceptingStatesLabel.getStyleClass().add("pop-up-label");
        acceptingStatesLabel.setPrefWidth(labelWidth);
        ScrollPane acceptingStatesScrollPane = new ScrollPane();
        acceptingStatesScrollPane.setContent(getExistingAcceptingStates());
        acceptingStatesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        acceptingStates.getChildren().addAll(acceptingStatesLabel, acceptingStatesScrollPane);

        HBox rejectingStates = new HBox();
        rejectingStates.setMaxWidth(transitionRulesInputWindow.getPrefWidth() - labelWidth);
        Label rejectingStatesLabel = new Label("Rejecting states");
        rejectingStatesLabel.setTextAlignment(TextAlignment.CENTER);
        rejectingStatesLabel.getStyleClass().add("pop-up-label");
        rejectingStatesLabel.setPrefWidth(labelWidth);
        ScrollPane rejectingStatesScrollPane = new ScrollPane();
        rejectingStatesScrollPane.setContent(getExistingRejectingStates());
        rejectingStatesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rejectingStates.getChildren().addAll(rejectingStatesLabel, rejectingStatesScrollPane);

        HBox haltingStates = new HBox();
        haltingStates.setMaxWidth(transitionRulesInputWindow.getPrefWidth() - labelWidth);
        Label haltingStatesLabel = new Label("Halting states");
        haltingStatesLabel.setTextAlignment(TextAlignment.CENTER);
        haltingStatesLabel.getStyleClass().add("pop-up-label");
        haltingStatesLabel.setPrefWidth(labelWidth);
        ScrollPane haltingStatesScrollPane = new ScrollPane();
        haltingStatesScrollPane.setContent(getExistingHaltingStates());
        haltingStatesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        haltingStates.getChildren().addAll(haltingStatesLabel, haltingStatesScrollPane);

        HBox initialStateAndButtons = new HBox();
        initialStateAndButtons.setSpacing(3);
        initialStateAndButtons.setAlignment(Pos.CENTER_LEFT);
        Label initialStateLabel = new Label("Initial State");
        initialStateLabel.setTextAlignment(TextAlignment.CENTER);
        initialStateLabel.setPrefWidth(labelWidth);
        initialStateLabel.getStyleClass().add("pop-up-label");
        TextField initialStateField = new TextField();
        initialStateField.setPrefWidth(60);
        initialStateField.getStyleClass().add("pop-up-textfield");
        if(initialStateExists){
            initialStateField.setText(tm.getInitialState().getName());
        }
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("buttons");
        confirmButton.setPrefWidth(80);
        confirmButton.setOnAction(event -> {
            if(initialStateField.getText() != null){
                List<String> acceptingStateNamesList = new ArrayList<>();
                for(Node node: ((HBox)acceptingStatesScrollPane.getContent()).getChildren()){
                    if(node instanceof TextField && !((TextField) node).getText().isEmpty()){
                        acceptingStateNamesList.add(((TextField) node).getText());
                    }
                }
                List<String> rejectingStateNamesList = new ArrayList<>();
                for(Node node: ((HBox)rejectingStatesScrollPane.getContent()).getChildren()){
                    if(node instanceof TextField && !((TextField) node).getText().isEmpty()){
                        rejectingStateNamesList.add(((TextField) node).getText());
                    }
                }
                List<String> haltingStateNamesList = new ArrayList<>();
                for(Node node: ((HBox)haltingStatesScrollPane.getContent()).getChildren()){
                    if(node instanceof TextField && !((TextField) node).getText().isEmpty()){
                        haltingStateNamesList.add(((TextField) node).getText());
                    }
                }
                addTransitionThroughRules(initialStateField.getText(), acceptingStateNamesList, rejectingStateNamesList, haltingStateNamesList);
                canvas.getChildren().remove(root);
            }
            else{
                NotificationManager.errorNotification("Missing initial state", "Cannot create a turing machine without an initial state", primaryStage);
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("buttons");
        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> {
            setupTransitionRulesInputWindow();
            canvas.getChildren().remove(root);
        });
        initialStateAndButtons.getChildren().addAll(initialStateLabel, initialStateField, confirmButton, cancelButton);

        specialStateInputs.getChildren().addAll(acceptingStates, rejectingStates, haltingStates, initialStateAndButtons);

        root.setTop(labels);
        root.setCenter(content);
        root.setBottom(specialStateInputs);
        canvas.getChildren().add(root);
    }


    //State methods

    private void addStateNode(Group groupNode, double xPos, double yPos){
        if(tm == null && groupNode != initialStateNode){
            NotificationManager.errorNotification("Cannot add state", "Add initial state first", primaryStage);
        }
        else if(initialStateExists && groupNode == initialStateNode){
            NotificationManager.errorNotification("Cannot add state", "Initial state already exists", primaryStage);
        }
        else{
            State newState = new State();
            newState.setPosition(xPos, yPos);
            if(groupNode == initialStateNode){
                newState.setInitial(true);
                initialStateExists = true;
                tm = new TuringMachine(newState);
                setUIDisable(false);
            }
            else if(groupNode == finalAcceptingStateNode){
                newState.setAccepting(true);
            }
            else if(groupNode == finalRejectingStateNode){
                newState.setRejecting(true);
            }
            else if(groupNode == haltingStateNode){
                newState.setHalting(true);
            }
            StateNode stateNode = loadStateNode(newState);
            enterStateNamePopUp(stateNode, newState);
            registerNewStateNode(stateNode, newState);
        }
    }

    private void registerNewStateNode(StateNode stateNode, State state){
        Group newNodeGroup = stateNode.getNodeGroup();
        stateStateNodeMap.put(state, stateNode);
        groupStateNodeMap.put(newNodeGroup, stateNode);
        addDragging(newNodeGroup);
        addClickListener(newNodeGroup);
        addContextMenu(stateNode.getNodeGroup());
        canvas.getChildren().add(newNodeGroup);
    }

    private boolean addStateName(TextField textField, StateNode stateNode, State state){
        String textFieldString = textField.getText();
        if (textFieldString.isEmpty()){
            NotificationManager.errorNotification("Empty name error", "Cannot add a state with an empty name", primaryStage);
            return false;
        }
        else if(!usedNames.contains(textFieldString)){
            usedNames.add(textFieldString);
            stateNode.setName(textFieldString);
            state.setName(textFieldString);
            return true;
        }
        else{
            NotificationManager.errorNotification("Non-unique name error", "The name " + textFieldString + " is already used by another state", primaryStage);
            return false;
        }
    }

    private State createNewStateFromName(List<String> acceptingStates, List<String> rejectingStates, List<String> haltingStates, String name) {
        State newState = new State();
        if(!usedNames.contains(name)){
            if(acceptingStates.contains(name)){
                newState.setAccepting(true);
            }
            else if(rejectingStates.contains(name)){
                newState.setRejecting(true);
            }
            else if(haltingStates.contains(name)){
                newState.setRejecting(true);
            }
        }
        else {
            for (State state: stateStateNodeMap.keySet()) {
                if(state.getName().equals(name)){
                    newState = state;
                }
            }
        }
        return newState;
    }

    private StateNode loadStateNode(State state){
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
            initialStateExists = true;
            setUIDisable(false);
        }
        registerNewStateNode(stateNode, state);
        usedNames.add(state.getName());
        return stateNode;
    }

    private void moveStateNode(Group nodeGroup, double xPos, double yPos){
        StateNode stateNode = groupStateNodeMap.get(nodeGroup);
        stateNode.move(xPos, yPos);
        //Redraw transition lines
        if(stateNodeArrowMap.containsKey(stateNode)){
            for (TransitionArrow transitionArrow: stateNodeArrowMap.get(stateNode)){
                transitionArrow.repositionLine();
            }
        }
    }

    private void deleteState(Group stateNodeGroup){
        StateNode stateNode = groupStateNodeMap.get(stateNodeGroup);
        State state = stateNode.getState();
        canvas.getChildren().remove(stateNodeGroup);
        tm.removeState(state);
        usedNames.remove(state.getName());
        stateStateNodeMap.remove(state);
        groupStateNodeMap.remove(stateNodeGroup);
        if(stateNodeArrowMap.containsKey(stateNode)){
            List<TransitionArrow> toRemove = new ArrayList<>(stateNodeArrowMap.get(stateNode));
            for(TransitionArrow arrow: toRemove){
                deleteTransitionArrow(arrow);
            }
        }
    }


    //Transition methods

    private void addTransition(StateNode from, StateNode to, TransitionArrow transitionArrow) {
        if(stateNodeArrowMap.containsKey(from)){
            stateNodeArrowMap.get(from).add(transitionArrow);
        }
        else{
            List<TransitionArrow> arrowsFromState = new ArrayList<>();
            arrowsFromState.add(transitionArrow);
            stateNodeArrowMap.put(from, arrowsFromState);
        }
        if(transitions.containsKey(from)){
            transitions.get(from).add(to);
        }
        else{
            List<StateNode> transitionsFromState = new ArrayList<>();
            transitionsFromState.add(to);
            transitions.put(from, transitionsFromState);
        }
    }

    private void addTransitionArrow(StateNode from, StateNode to, TransitionRule transitionRule){
        TransitionArrow transitionArrow = new TransitionArrow(from, to);
        boolean arrowExists = false;
        if(transitions.getOrDefault(from, new ArrayList<>()).contains(to)){
            for(TransitionArrow arrowFromState: stateNodeArrowMap.get(from)){
                if(arrowFromState.getFrom() == from && arrowFromState.getTo() == to){
                    transitionArrow = arrowFromState;
                    arrowExists = true;
                    break;
                }
            }
        }
        if(!arrowExists) {
            addTransition(from, to, transitionArrow);
            if (from != to) {
                addTransition(to, from, transitionArrow);
            }

            canvas.getChildren().add(transitionArrow.getLineGroup());
        }
        transitionArrow.addRules(transitionRule);
        transitionRule.setArrow(transitionArrow);
    }

    private void addTransitionThroughRules(String initialStateName, List<String> acceptingStates, List<String> rejectingStates, List<String> haltingStates){
        for(StateNode stateNode: stateNodeArrowMap.keySet()){
            if(stateNodeArrowMap.get(stateNode) != null){
                for(TransitionArrow arrow: stateNodeArrowMap.get(stateNode)){
                    canvas.getChildren().remove(arrow.getLineGroup());
                }
                stateNodeArrowMap.get(stateNode).clear();
            }
        }
        List<List<String>> transitionStrings = new ArrayList<>();
        for (int i = 0; i < transitionRulesInputWindow.getChildren().size() - 1; i++) {
            Node inputFields = transitionRulesInputWindow.getChildren().get(i);
            if(inputFields instanceof HBox){
                String fromName = ((TextField) ((HBox) inputFields).getChildren().get(0)).getText();
                if(!acceptingStates.contains(fromName) && !rejectingStates.contains(fromName) && !haltingStates.contains(fromName)){
                    List<String> transition = new ArrayList<>();
                    for (Node inputField: ((HBox) inputFields).getChildren()) {
                        if(inputField instanceof TextField){
                            transition.add(((TextField) inputField).getText());
                        }
                        else if(inputField instanceof ChoiceBox){
                            transition.add(((ChoiceBox<?>) inputField).getValue().toString());
                        }
                    }
                    transitionStrings.add(transition);
                }
                else {
                    NotificationManager.errorNotification("Illegal transition", "Cannot add a transition from the final state " + fromName, primaryStage);
                }
            }
        }

        HashMap<Pair<State, State>, List<TransitionRule>> transitionRuleList = new HashMap<>();
        if(!initialStateExists){
            State newInitialState = new State();
            newInitialState.setName(initialStateName);
            newInitialState.setInitial(true);
            newInitialState.setPosition(50, 50);
            tm = new TuringMachine(newInitialState);
            initialStateExists = true;
            usedNames.add(initialStateName);
            loadStateNode(newInitialState);
        }

        double xPos = 100;
        double yPos = 100;
        double maxXPos = 800;
        for(List<String> strList: transitionStrings){
            State from = new State();
            State to = new State();
            int numberOfStates = strList.get(0).equals(strList.get(1)) ? 1 : 2;
            for (int i = 0; i < numberOfStates; i++) {
                String stateName = strList.get(i);
                State stateToAdd = createNewStateFromName(acceptingStates, rejectingStates, haltingStates, stateName);
                if(stateToAdd.getName() == null && !stateName.equals(initialStateName)){
                    stateToAdd.setName(stateName);
                    usedNames.add(stateName);
                    stateToAdd.setPosition(xPos, yPos);
                    xPos += 100;
                    if(xPos > maxXPos){
                        xPos = 50;
                        yPos += 100;
                    }
                    loadStateNode(stateToAdd);
                }
                if(i == 0){
                    from = stateToAdd;
                    if(numberOfStates == 1){
                        to = stateToAdd;
                    }
                }
                else{
                    to = stateToAdd;
                }
            }
            TransitionRule transitionRule = new TransitionRule(strList.get(2).charAt(0), strList.get(3).charAt(0), strList.get(4).charAt(0));
            Pair<State, State> statePair = new Pair<>(from, to);
            if(transitionRuleList.containsKey(statePair)){
                transitionRuleList.get(statePair).add(transitionRule);
            }
            else{
                List<TransitionRule> rulesList = new ArrayList<>();
                rulesList.add(transitionRule);
                transitionRuleList.put(statePair, rulesList);
            }
        }

        tm.clearTransitions();
        for(Pair<State, State> statePair: transitionRuleList.keySet()){
            StateNode fromStateNode = stateStateNodeMap.get(statePair.getKey());
            StateNode toStateNode = stateStateNodeMap.get(statePair.getValue());
            for (TransitionRule rule: transitionRuleList.get(statePair)) {
//                boolean arrowExists = false;
//                if(stateNodeArrowMap.containsKey(fromStateNode) && stateNodeArrowMap.containsKey(toStateNode)){
//                    if(!stateNodeArrowMap.get(fromStateNode).isEmpty() && !stateNodeArrowMap.get(toStateNode).isEmpty()){
//                        for (TransitionArrow arrow: stateNodeArrowMap.get(fromStateNode)) {
//                            if(arrow.getFrom() == fromStateNode && arrow.getTo() == toStateNode &&
//                                    arrow.containsTransitionRule(rule.toString())){
//                                arrowExists = true;
//                            }
//                        }
//                        for (TransitionArrow arrow: stateNodeArrowMap.get(toStateNode)) {
//                            if(arrow.getFrom() == fromStateNode && arrow.getTo() == toStateNode &&
//                                    arrow.containsTransitionRule(rule.toString())){
//                                arrowExists = true;
//                            }
//                        }
//                    }
//                }
//                if(!arrowExists){
//                }
                    tm.addTransition(new TuringTransition(statePair.getKey(), statePair.getValue(), rule));
                    addTransitionArrow(fromStateNode, toStateNode, rule);
            }
        }
    }

    private void deleteTransition(String fromName, String toName, TransitionRule ruleCopy){
        if(tm != null){
            StateNode fromState = stateStateNodeMap.get(tm.findStateByName(fromName));
            StateNode toState = stateStateNodeMap.get(tm.findStateByName(toName));
            TransitionArrow arrowToDelete = new TransitionArrow(fromState, toState);
            for (TransitionArrow arrow: stateNodeArrowMap.get(fromState)) {
                if(arrow.getTo() == toState){
                    arrowToDelete = arrow;
                }
            }
            TransitionRule ruleToDelete = ruleCopy;
            for (TransitionRule rule: arrowToDelete.getTransitionRules()) {
                if(rule.toString().equals(ruleToDelete.toString())){
                    ruleToDelete = rule;
                }
            }
            deleteTransition(arrowToDelete, ruleToDelete);
        }
    }

    private void deleteTransition(TransitionArrow transitionArrow, TransitionRule transitionRule){
        transitionArrow.removeTransitionRule(transitionRule);
        tm.removeTransition(transitionRule);
        if(transitionArrow.getTransitionRules().isEmpty()){
            deleteTransitionArrow(transitionArrow);
        }
    }

    private void deleteTransitionArrow(TransitionArrow transitionArrow){
        for(TransitionRule transitionRule: transitionArrow.getTransitionRules()){
            tm.removeTransition(transitionRule);
        }
        StateNode from = transitionArrow.getFrom();
        StateNode to = transitionArrow.getTo();
        transitions.remove(from);
        stateNodeArrowMap.get(from).remove(transitionArrow);
        if(transitions.containsKey(to)){
            transitions.get(to).remove(from);
        }
        if(stateNodeArrowMap.containsKey(to)){
            stateNodeArrowMap.get(to).remove(transitionArrow);
        }
        canvas.getChildren().remove(transitionArrow.getLineGroup());
    }


    //Getter methods

    private HBox getTransitionRuleInputFields(){
        HBox inputs = new HBox();
        inputs.setAlignment(Pos.CENTER);
        TextField fromState = getNewPopupTextField("");
        TextField toState = getNewPopupTextField("");
        TextField read = getNewPopupTextField("~");
        addLengthLimit(read);
        TextField write = getNewPopupTextField("~");
        addLengthLimit(write);
        ChoiceBox<Character> direction = new ChoiceBox<>();
        direction.getItems().addAll('R', 'L', 'N');
        direction.getSelectionModel().select(2);
        direction.setMinWidth(55);
        direction.getStyleClass().add("pop-up-combobox");
        Button deleteButton = new Button("x");
        deleteButton.getStyleClass().add("transition-rules-popup-delete-button");
        deleteButton.setMinWidth(20);
        deleteButton.setMinHeight(20);
        deleteButton.setOnAction(event -> {
            int numberOfFields = transitionRulesInputWindow.getChildren().size();
            if(transitionRulesInputWindow.getChildren().indexOf(inputs) < numberOfFields - 1){
                transitionRulesInputWindow.getChildren().remove(inputs);
                TransitionRule ruleToDelete = new TransitionRule();
                ruleToDelete.setReadSymbol(read.getText().charAt(0));
                ruleToDelete.setWriteSymbol(write.getText().charAt(0));
                ruleToDelete.setDirection(direction.getSelectionModel().getSelectedItem());
                deleteTransition(fromState.getText(), toState.getText(), ruleToDelete);
            }
        });
        inputs.getChildren().addAll(fromState, toState, read, write, direction, deleteButton);

        for (Node inputNode: inputs.getChildren()) {
            if (inputNode instanceof TextField){
                ((TextField) inputNode).textProperty().addListener((observable, oldValue, newValue) -> onTransitionRuleInputChange(inputs));
            }
            else if(inputNode instanceof ChoiceBox){
                ((ChoiceBox<?>) inputNode).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTransitionRuleInputChange(inputs));
            }
        }
        return inputs;
    }

    private ContextMenu getDeleteTransitionArrowsContextMenu(Group nodeGroup){
        ContextMenu deleteTransitionsMenu = new ContextMenu();
        StateNode stateNode = groupStateNodeMap.get(nodeGroup);
        if(stateNodeArrowMap.containsKey(stateNode) && !stateNodeArrowMap.get(stateNode).isEmpty()){
            for(TransitionArrow arrow: stateNodeArrowMap.get(stateNode)){
                for(TransitionRule rule: arrow.getTransitionRules()){
                    MenuItem menuItem = new MenuItem(arrow.getFrom().getName() + "|" + arrow.getTo().getName() + "|" + rule.toString());
                    menuItem.setOnAction(event -> deleteTransition(arrow, rule));
                    deleteTransitionsMenu.getItems().add(menuItem);
                }
            }
        }
        else{
            MenuItem disabledMenuItem = new MenuItem("State has no transitions");
            disabledMenuItem.setDisable(true);
            deleteTransitionsMenu.getItems().add(disabledMenuItem);
        }
        return deleteTransitionsMenu;
    }

    private TextField getNewPopupTextField(String name){
        TextField newTextField = new TextField();
        newTextField.setText(name);
        newTextField.setPrefWidth(60);
        newTextField.getStyleClass().add("pop-up-textfield");
        return newTextField;
    }

    private HBox getExistingAcceptingStates(){
        HBox acceptingStatesFields = new HBox();
        acceptingStatesFields.setAlignment(Pos.CENTER);
        for(State state: stateStateNodeMap.keySet()){
            if(state.isAccepting()){
                TextField newTextField = getNewPopupTextField(state.getName());
                newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(acceptingStatesFields));
                acceptingStatesFields.getChildren().add(newTextField);
            }
        }
        TextField newTextField = getNewPopupTextField("");
        newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(acceptingStatesFields));
        acceptingStatesFields.getChildren().add(newTextField);

        return acceptingStatesFields;
    }

    private HBox getExistingHaltingStates(){
        HBox haltingStatesFields = new HBox();
        haltingStatesFields.setAlignment(Pos.CENTER);
        for(State state: stateStateNodeMap.keySet()){
            if(state.isHalting()){
                TextField newTextField = getNewPopupTextField(state.getName());
                newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(haltingStatesFields));
                haltingStatesFields.getChildren().add(newTextField);
            }
        }
        TextField newTextField = getNewPopupTextField("");
        newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(haltingStatesFields));
        haltingStatesFields.getChildren().add(newTextField);

        return haltingStatesFields;
    }

    private HBox getExistingRejectingStates(){
        HBox rejectingStatesFields = new HBox();
        rejectingStatesFields.setAlignment(Pos.CENTER);
        for(State state: stateStateNodeMap.keySet()){
            if(state.isRejecting()){
                TextField newTextField = getNewPopupTextField(state.getName());
                newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(rejectingStatesFields));
                rejectingStatesFields.getChildren().add(newTextField);
            }
        }
        TextField newTextField = getNewPopupTextField("");
        newTextField.textProperty().addListener((observable, oldValue, newValue) -> onSpecialStatesInputChange(rejectingStatesFields));
        rejectingStatesFields.getChildren().add(newTextField);

        return rejectingStatesFields;
    }

    //IO Methods

    private void save(){
        if(tm != null){
            if(currentSaveFile == null){
                saveToFile();
            }
            else{
                try{
                    SaveManager.saveTuringMachine(tm, currentSaveFile.toPath());
                    NotificationManager.confirmationNotification("Saved", "Saved successfully", primaryStage);
                } catch (IOException e) {
                    NotificationManager.errorNotification("Error", "Failed to save", primaryStage);
                }
            }
        }
        else{
            NotificationManager.errorNotification("Error", "Cannot save an empty Turing Machine", primaryStage);
        }
    }

    private void saveToFile(){
        if(tm != null) {
            try {
                FileChooser directoryChooser = new FileChooser();
                directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
                currentSaveFile = directoryChooser.showSaveDialog(primaryStage);
                SaveManager.saveTuringMachine(tm, currentSaveFile.toPath());
                NotificationManager.confirmationNotification("Saved", "Saved successfully", primaryStage);
            } catch (IOException e) {
                NotificationManager.errorNotification("Error", "Failed to save", primaryStage);
            }
        }
        else{
            NotificationManager.errorNotification("Error", "Cannot save an empty TM", primaryStage);
        }
    }

    private void load(){
        try{
            clearCanvas();
            FileChooser directoryChooser = new FileChooser();
            directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
            currentSaveFile = directoryChooser.showOpenDialog(primaryStage);
            tm = SaveManager.loadTuringMachine(currentSaveFile);
            loadTuringMachine(tm);
        }
        catch (Exception e){
            NotificationManager.errorNotification("Error", "Failed to load from file", primaryStage);
        }
    }

    public void loadTuringMachine(TuringMachine tm){
        if(tm != null){
            this.tm = tm;
            double highestX = 0;
            double highestY = 0;
            for(State state: tm.getStates().values()){
                Pair<Double, Double> stateNodePosition = state.getPosition();
                if(stateNodePosition.getKey() > highestX){
                    highestX = stateNodePosition.getKey();
                }
                if(stateNodePosition.getValue() > highestY){
                    highestY = stateNodePosition.getValue();
                }
                loadStateNode(state);
            }
            for(TuringTransition turingTransition : tm.getTransitionFunction()){
                TransitionRule transitionRule = turingTransition.getTransitionRule();
                State fromState = turingTransition.getFromState();
                State toState = turingTransition.getToState();
                addTransitionArrow(stateStateNodeMap.get(fromState), stateStateNodeMap.get(toState), transitionRule);
            }
            tmNameField.setText(tm.getName());
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

}