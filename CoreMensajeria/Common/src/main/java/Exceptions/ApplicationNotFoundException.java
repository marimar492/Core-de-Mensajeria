package Exceptions;

public class ApplicationNotFoundException extends PersonalizedException {
    public ApplicationNotFoundException(){}
    public ApplicationNotFoundException(Exception e){
        super(e);
    }
    public ApplicationNotFoundException(String msg){
        super(msg);
    }
    public ApplicationNotFoundException(String msg, Exception e){
        super(msg, e);
    }
}
