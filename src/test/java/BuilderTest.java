import canvasNodes.InitialStateNode;
import io.SaveManager;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.GuiTest;

import org.testfx.framework.junit5.ApplicationTest;
import turing.TransitionRule;
import turing.TuringMachine;
import turing.TuringTransition;
import ui.BuilderController;
import ui.ControllerLoader;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class BuilderTest extends ApplicationTest {

    private BuilderController controller;

    @Override
    public void start (Stage primaryStage) throws Exception {
        controller = ControllerLoader.openBuilderWindow(primaryStage);
        primaryStage.show();
    }

    @BeforeEach
    public void beforeEach () throws Exception {
        controller.initialize();
    }


    @Test
    public void testAddingStateByDragging(){
        Button runButton = GuiTest.find("#runButton");
        Group stateNode = GuiTest.find("#stateNode");
        assertTrue(runButton.isDisabled());
        assertTrue(stateNode.isDisabled());
        drag("#initialStateNode");
        dropTo("#canvas");
        clickOn("#nameTextField");
        write("s0");
        clickOn("#confirmButton");
        assertFalse(runButton.isDisabled());
        assertFalse(stateNode.isDisabled());
        TuringMachine tm = controller.getTM();
        assertEquals("s0", tm.getInitialState().getName());
    }

    @Test
    public void testAddingTransitionAndClearingCanvas(){
        drag("#initialStateNode");
        dropTo("#canvas");
        clickOn("#nameTextField");
        write("s0");
        clickOn("#confirmButton");
        press(KeyCode.CONTROL);
        clickOn("s0");
        clickOn("s0");
        release(KeyCode.CONTROL);
        clickOn("#readField");
        write("0");
        clickOn("#writeField");
        write("1");
        clickOn("#directionChoiceBox");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#confirmButton");
        TuringMachine tm = controller.getTM();
        //Transition function should only contain one element at this point
        Set<TuringTransition> transitionFunction = tm.getTransitionFunction();
        assertEquals(1, transitionFunction.size());
        for (TuringTransition transition: transitionFunction) {
            TransitionRule transitionRule = transition.getTransitionRule();
            assertEquals("s0", transition.getFromState().getName());
            assertEquals("s0", transition.getToState().getName());
            assertEquals('0', transitionRule.getReadSymbol());
            assertEquals('1', transitionRule.getWriteSymbol());
            assertEquals('R', transitionRule.getDirection());
        }
        clickOn("#clearButton");
        Pane canvas = GuiTest.find("#canvas");
        assertTrue(canvas.getChildren().isEmpty());
    }

    @Test
    public void testEncodingThroughTransitionRules(){
        clickOn("#addTransitionsButton");
        clickOn("#fromStateTextField0");
        write("s0");
        clickOn("#toStateTextField0");
        write("s1");
        doubleClickOn("#readTextField0");
        write("0");
        doubleClickOn("#writeTextField0");
        write("1");
        clickOn("#fromStateTextField1");
        write("s1");
        clickOn("#toStateTextField1");
        write("s2");
        doubleClickOn("#readTextField1");
        write("1");
        doubleClickOn("#writeTextField1");
        write("0");
        clickOn("#initialStateTextField");
        write("s0");
        clickOn("#acceptingStatesTextField0");
        write("s2");
        clickOn("#confirmButton");
        Pane canvas = GuiTest.find("#canvas");
        assertEquals(5, canvas.getChildren().size());
        TuringMachine tm = controller.getTM();
        Set<TuringTransition> transitionFunction = tm.getTransitionFunction();
        assertEquals(2, transitionFunction.size());
        for (TuringTransition transition: transitionFunction) {
            TransitionRule transitionRule = transition.getTransitionRule();
            if(transition.getFromState().getName().equals("s0")){
                assertTrue(transition.getFromState().isInitial());
                assertEquals("s1", transition.getToState().getName());
                assertEquals('0', transitionRule.getReadSymbol());
                assertEquals('1', transitionRule.getWriteSymbol());
                assertEquals('N', transitionRule.getDirection());

            }
            else{
                assertTrue(transition.getToState().isAccepting());
                assertEquals("s1", transition.getFromState().getName());
                assertEquals("s2", transition.getToState().getName());
                assertEquals('1', transitionRule.getReadSymbol());
                assertEquals('0', transitionRule.getWriteSymbol());
                assertEquals('N', transitionRule.getDirection());
            }
        }
    }
}
