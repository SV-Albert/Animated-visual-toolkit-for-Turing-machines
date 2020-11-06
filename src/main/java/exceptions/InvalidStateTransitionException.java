package main.java.exceptions;

public class InvalidStateTransitionException extends Exception{
    public InvalidStateTransitionException(String message){
        super(message);
    }
}
