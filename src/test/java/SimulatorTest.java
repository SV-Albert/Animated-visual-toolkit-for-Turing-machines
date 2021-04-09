import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit5.ApplicationTest;
import turing.TuringMachine;
import ui.ControllerLoader;
import ui.SimulatorController;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SimulatorTest extends ApplicationTest {

    private SimulatorController controller;

    @Override
    public void start(Stage primaryStage){
        controller = ControllerLoader.openSimulatorWindow(primaryStage);
        primaryStage.show();
    }

    @BeforeEach
    public void beforeEach(){
        controller.initialize();
    }

    @Test
    public void testManualSimulationOnBinaryAddition(){
        clickOn("#loadExampleButton");
        clickOn("#Examples");
        clickOn("#tapeInputButton");
        clickOn("#InputField");
        write("1001~101");
        clickOn("#Confirm");
        clickOn("#isAutoToggle");
        Button next = GuiTest.find("#runOrNextButton");
        while (!next.isDisabled()){
            clickOn("#runOrNextButton");
        }
        ArrayList<Character> expected = new ArrayList<>();
        expected.add('1');
        expected.add('1');
        expected.add('1');
        expected.add('0');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        ArrayList<Character> tapeArray = new ArrayList<>(controller.getTape().getTapeArray());

        assertEquals(expected, tapeArray);

        HBox historySection = GuiTest.find("#executionHistorySection");
        assertEquals(48, historySection.getChildren().size());

        Label inputStatus = GuiTest.find("#inputStatusLabel");
        assertEquals("Input status: Halted", inputStatus.getText());

        TuringMachine tm = controller.getCurrentTM();
        assertEquals("halt", tm.getCurrentState().getName());

        clickOn("#pauseOrBackButton");
        assertFalse(next.isDisabled());
        clickOn("#pauseOrBackButton");
        clickOn("#pauseOrBackButton");
        clickOn("#pauseOrBackButton");
        clickOn("#pauseOrBackButton");

        assertEquals(43, historySection.getChildren().size());
        assertEquals("Input status: Running", inputStatus.getText());
        assertEquals("s8", tm.getCurrentState().getName());

        clickOn("#runOrNextButton");

        assertEquals(44, historySection.getChildren().size());
        assertEquals("Input status: Running", inputStatus.getText());
        assertEquals("s9", tm.getCurrentState().getName());

        Button back = GuiTest.find("#pauseOrBackButton");
        while (!back.isDisabled()){
            clickOn("#pauseOrBackButton");
        }

        assertEquals(1, historySection.getChildren().size());
        assertEquals("Input status: None", inputStatus.getText());
        assertEquals(tm.getInitialState(), tm.getCurrentState());
    }

    @Test
    public void testAutoSimulationOnBinaryAddition(){
        clickOn("#loadExampleButton");
        clickOn("#Examples");
        clickOn("#tapeInputButton");
        clickOn("#InputField");
        write("1001~101");
        clickOn("#Confirm");
        clickOn("#runOrNextButton");

        Button pause = GuiTest.find("#pauseOrBackButton");
        while (!pause.isDisabled()){
            int i = 0;
        }

        ArrayList<Character> expected = new ArrayList<>();
        expected.add('1');
        expected.add('1');
        expected.add('1');
        expected.add('0');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        expected.add('~');
        ArrayList<Character> tapeArray = new ArrayList<>(controller.getTape().getTapeArray());

        assertEquals(expected, tapeArray);

        HBox historySection = GuiTest.find("#executionHistorySection");
        assertEquals(48, historySection.getChildren().size());

        Label inputStatus = GuiTest.find("#inputStatusLabel");
        assertEquals("Input status: Halted", inputStatus.getText());

        TuringMachine tm = controller.getCurrentTM();
        assertEquals("halt", tm.getCurrentState().getName());

        clickOn("#resetButton");

        assertEquals(1, historySection.getChildren().size());
        assertEquals("Input status: None", inputStatus.getText());
        assertEquals(tm.getInitialState(), tm.getCurrentState());
    }
}
