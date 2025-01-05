package myexception;

public class QuestionNotFound extends RuntimeException {
    public QuestionNotFound(String message) {
        super(message);
    }
}
