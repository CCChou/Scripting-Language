package dennis.practice.stone;
import java.io.IOException;

public class ParseException extends Exception {
    private static final long serialVersionUID = -5781168519210930501L;

    public ParseException(Token token) {
        this("", token);
    }

    public ParseException(String msg, Token token) {
        super("syntax error around " + location(token) + ". " + msg);
    }

    public ParseException(IOException e) {
        super(e);
    }
    
    public ParseException(String msg) {
        super(msg);
    }
    
    private static String location(Token token) {
        if (token == Token.EOF) {
            return "the last line";
        } else {
            return "\"" + token.getText() + "\" at line " + token.getLineNumber();
        }
    }
    
}
