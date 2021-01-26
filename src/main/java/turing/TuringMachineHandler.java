package turing;

import exceptions.InvalidSymbolException;

public class TuringMachineHandler {
    private TuringMachine turingMachine;
    private Tape tape;

    public TuringMachineHandler(TuringMachine turingMachine, Tape tape){
        this.turingMachine = turingMachine;
        this.tape = tape;
    }

    private Transition findTransition(char read) throws InvalidSymbolException{
        for (Transition transition:turingMachine.getTransitionsFromCurrentState()) {
            if(transition.getReadSymbol() == read){
                return transition;
            }
        }
        throw new InvalidSymbolException("State " + turingMachine.getCurrentState().getName() + " has no operations for input '" + read + "'");
    }

    public void next() throws InvalidSymbolException{
        char read = tape.read();
        Transition transition = findTransition(read);
        tape.write(transition.getWriteSymbol());
        tape.move(transition.getDirection());
        turingMachine.setCurrentState(transition.getToState());
    }

//    public char getWriteSymbol(char read) throws InvalidSymbolException{
//        return findTransition(read).getWriteSymbol();
//    }
//
//    public char getDirection(char read) throws InvalidSymbolException{
//        return findTransition(read).getDirection();
//    }
}
