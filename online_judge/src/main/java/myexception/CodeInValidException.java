package myexception;

public class CodeInValidException extends RuntimeException {
    public CodeInValidException(String message) {
        super(message);
    }
}
