package Exceptions;

public class InvalidPlayerNumException extends Exception{

    private boolean tooMany;

    public InvalidPlayerNumException(String message){
        super(message);
    }
}
