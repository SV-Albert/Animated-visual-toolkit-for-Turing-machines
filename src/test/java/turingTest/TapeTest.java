//package turingTest;
//
//import turing.Tape;
//import java.util.ArrayList;
//import exceptions.InvalidSymbolException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TapeTest {
//
//    private Tape testTape;
//    private char emptyChar;
//
//    @BeforeEach
//    public void beforeEach(){
//        ArrayList<Character> tapeArray = new ArrayList<>();
//        tapeArray.add(emptyChar);
//        tapeArray.add('0');
//        tapeArray.add('0');
//        tapeArray.add('1');
//        tapeArray.add(emptyChar);
//        testTape = new Tape("Test Tape", tapeArray, 0);
//        emptyChar = testTape.getEmptyChar();
//    }
//
//    @Test
//    public void testRead() {
//        assertEquals(emptyChar, testTape.read());
//    }
//
////    @Test
////    public void testMove() throws InvalidSymbolException {
////        testTape.move('L');
////        assertEquals('0', testTape.read());
////        testTape.move('N');
////        assertEquals('0', testTape.read());
////        testTape.move('R');
////        testTape.move('R'); //move head to the left edge
////        assertEquals(emptyChar, testTape.read());
////        for (int i = 0; i < 7; i++) { //move head to the right edge
////            testTape.move('L');
////        }
////        assertEquals(emptyChar, testTape.read());
////    }
////
////    @Test
////    public void testWrite() throws InvalidSymbolException {
////        System.out.println(testTape.getHead());
////        testTape.write('2');
////        assertEquals('2', testTape.read());
////        testTape.move('R'); //move head to the left edge
////        testTape.write('3');
////        assertEquals('3', testTape.read());
////        System.out.println(testTape.getHead());
////        for (int i = 0; i < 7; i++) { //move head to the right edge
////            testTape.move('L');
////
////        }
////        testTape.write('4');
////        assertEquals('4', testTape.read());
////    }
//
//
//}
