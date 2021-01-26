//package turingTest;
//
//import exceptions.InvalidSymbolException;
//import turing.State;
//import java.util.HashMap;
//import javafx.util.Pair;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class StateTest {
//
//    private State testState;
//    private State emptyAcceptingState;
//
//    @BeforeEach
//    public void beforeEach(){
//        testState = new State("TestState", false);
//        testState.addAction('0', '1', 'R');
//        testState.addAction('1', '0', 'L');
//        testState.addAction('~', '1', 'N');
//
//        emptyAcceptingState = new State("EmptyAcceptingState", true);
//    }
//
//    @Test
//    public void testGetDirection() throws InvalidSymbolException {
//        char direction = testState.getDirection('~');
//        assertEquals('N', direction);
//    }
//
//    @Test
//    public void testEmptyGetDirection() throws InvalidSymbolException {
//        assertThrows(InvalidSymbolException.class, () ->{
//            emptyAcceptingState.getDirection('1');
//        });
//    }
//
//    @Test
//    public void testGetWriteSymbol() throws InvalidSymbolException {
//        char symbol = testState.getWriteSymbol('~');
//        assertEquals('1', symbol);
//    }
//
//    @Test
//    public void testEmptyGetWriteSymbol() throws InvalidSymbolException {
//        assertThrows(InvalidSymbolException.class, () ->{
//            emptyAcceptingState.getWriteSymbol('1');
//        });
//    }
//
//    @Test
//    public void testGetData(){
//        HashMap<Character, Pair<Character, Character>> expectedData = new HashMap<>();
//        expectedData.put('0', new Pair<>('1', 'R'));
//        expectedData.put('1', new Pair<>('0', 'L'));
//        expectedData.put('~', new Pair<>('1', 'N'));
//
//        assertEquals(expectedData, testState.getData());
//    }
//
//    @Test
//    public void testEmptyGetData(){
//        assertTrue(emptyAcceptingState.getData().isEmpty());
//    }
//
//    @Test
//    public void testAddAction(){
//        char expectedKey = '2';
//        Pair<Character, Character> expectedValue = new Pair<>('2', 'N');
//        testState.addAction('2', '2', 'N');
//        HashMap<Character, Pair<Character, Character>> stateData = testState.getData();
//
//        assertTrue(stateData.containsKey(expectedKey));
//        assertTrue(stateData.containsValue(expectedValue));
//    }
//
//    @Test
//    public void testGetName(){
//        assertEquals("TestState", testState.getName());
//    }
//
//    @Test
//    public void testisAccepting(){
//        assertTrue(emptyAcceptingState.isAccepting());
//        assertFalse(testState.isAccepting());
//    }
//}
