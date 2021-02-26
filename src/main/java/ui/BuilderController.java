package ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import canvasNodes.*;
import turing.State;
import turing.TuringMachine;

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
    @FXML private Button saveButton;
    @FXML private Button loadButton;
    @FXML private Button addTransitionsButton;
    @FXML private Button runButton;
    @FXML private Button backButton;
    @FXML private Pane canvas;
    @FXML private ScrollPane canvasScroll;

    private TuringMachine tm;
    private Group nodeDragged;
    private boolean nodeMoved;
    private boolean initialStateExists;
    private Group fromTransitionNode;
    private List<Group> stateNodes;
    private Map<Group, StateNode> groupStateNodeMap;
    private Map<Group, State> groupStateMap;
    private Map<StateNode, List<TransitionArrow>> transitionLinesFromState;
    private Map<StateNode, List<StateNode>> transitions;

    @FXML
    public void initialize(){
        initialStateExists = false;
        nodeMoved = false;
        stateNodes = new ArrayList<>();
        groupStateNodeMap = new HashMap<>();
        groupStateMap = new HashMap<>();
        transitionLinesFromState = new HashMap<>();
        transitions = new HashMap<>();
        stateNodes.add(initialStateNode);
        stateNodes.add(stateNode);
        stateNodes.add(finalAcceptingStateNode);
        stateNodes.add(finalRejectingStateNode);
        stateNodes.add(haltingStateNode);
        configureToolbar();
    }

    private void configureToolbar(){
        for(Group nodeGroup: stateNodes){
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

    private void addClickListener(Group nodeGroup){
        nodeGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isControlDown()){
                    if(fromTransitionNode == null){
                        fromTransitionNode = nodeGroup;
                    }
                    else {
                        addTransition(groupStateNodeMap.get(fromTransitionNode), groupStateNodeMap.get(nodeGroup));
                        fromTransitionNode = null;
                    }
                }
            }
        });
    }

    private void addStateNode(Group groupNode, double xPos, double yPos){
        if(tm == null && groupNode != initialStateNode){
            System.out.println("Add initial state first"); //TODO Add exception
        }
        else if(initialStateExists && groupNode == initialStateNode){
            System.out.println("Initial state already exists"); //TODO Add exception
        }
        else{
            StateNode stateNode;
            State newState = new State();
            newState.setPosition(xPos, yPos);
            if(groupNode == initialStateNode){
                newState.setInitial(true);
                stateNode = new InitialStateNode(xPos, yPos);
                initialStateExists = true;
                tm = new TuringMachine(new HashMap<>(), newState);
            }
            else if(groupNode == finalAcceptingStateNode){
                newState.setAccepting(true);
                stateNode = new AcceptingStateNode(xPos, yPos);

            }
            else if(groupNode == finalRejectingStateNode){
                newState.setRejecting(true);
                stateNode = new RejectingStateNode(xPos, yPos);

            }
            else if(groupNode == haltingStateNode){
                newState.setHalting(true);
                stateNode = new HaltingStateNode(xPos, yPos);

            }
            else{
                stateNode = new StateNode(xPos, yPos);
            }
            Group newNodeGroup = stateNode.getNodeGroup();
            addDragging(newNodeGroup);
            addClickListener(newNodeGroup);
            groupStateNodeMap.put(newNodeGroup, stateNode);
            groupStateMap.put(newNodeGroup, newState);
            canvas.getChildren().add(newNodeGroup);

            enterStateNamePopUp(stateNode, newState);
        }
    }

    private void moveStateNode(Group nodeGroup, double xPos, double yPos){
        StateNode stateNode = groupStateNodeMap.get(nodeGroup);
        stateNode.move(xPos, yPos);
        groupStateMap.get(nodeGroup).setPosition(xPos, yPos);
        //Redraw transition lines
        if(transitionLinesFromState.containsKey(stateNode)){
            for (TransitionArrow transitionArrow: transitionLinesFromState.get(stateNode)){
                transitionArrow.repositionLine();
            }
        }
    }

    private void enterStateNamePopUp(StateNode stateNode, State state){
        Stage popup = new Stage();
        VBox root = new VBox();
        root.getStylesheets().add("stylesheet.css");
        root.getStyleClass().add("pop-up");
        root.setAlignment(Pos.CENTER);
        root.setPrefHeight(100);
        root.setPrefWidth(260);
        Text text = new Text("Enter the state name");
        text.getStyleClass().add("pop-up-text");
        TextField textField = new TextField();
        textField.getStyleClass().add("pop-up-textfield");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            stateNode.setName(textField.getText());
            state.setName(textField.getText());
            popup.close();
        });
        confirmButton.getStyleClass().add("simulator-buttons");
        root.getChildren().addAll(text, textField, confirmButton);
        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.show();
    }

    private void addTransition(StateNode from, StateNode to){
        if(transitions.get(from) != to || transitions.get(to) != from){
            //TODO add new TM transition
        }
        TransitionArrow transitionArrow = new TransitionArrow(from, to);
        transitionArrow.addRules('1', '2', 'R');

        if(transitionLinesFromState.containsKey(from)){
            transitionLinesFromState.get(from).add(transitionArrow);
        }
        else{
            List<TransitionArrow> connectedLines = new ArrayList<>();
            connectedLines.add(transitionArrow);
            transitionLinesFromState.put(from, connectedLines);
        }
        if(transitionLinesFromState.containsKey(to)){
            transitionLinesFromState.get(to).add(transitionArrow);
        }
        else{
            List<TransitionArrow> connectedLines = new ArrayList<>();
            connectedLines.add(transitionArrow);
            transitionLinesFromState.put(to, connectedLines);
        }
        if(transitions.containsKey(from)){
            transitions.get(from).add(to);
        }
        else{
            List<StateNode> transitionsFromState = new ArrayList<>();
            transitionsFromState.add(to);
            transitions.put(from, transitionsFromState);
        }
        if(transitions.containsKey(to)){
            transitions.get(to).add(from);
        }
        else{
            List<StateNode> transitionsFromState = new ArrayList<>();
            transitionsFromState.add(from);
            transitions.put(to, transitionsFromState);
        }

        canvas.getChildren().add(transitionArrow.getLineGroup());
    }

    private void increaseCanvas(boolean increaseWidth){
        if(increaseWidth){
            canvas.setPrefWidth(canvas.getPrefWidth() + 100);
        }
        else{
            canvas.setPrefHeight(canvas.getPrefHeight() + 100);
        }
    }
}
