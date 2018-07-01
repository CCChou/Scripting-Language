package dennis.practice.stone;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static final String REGEX_PATTERN = "\\s*((//*) | (0-9)+ | (\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct})?";
    private Pattern pattern = Pattern.compile(REGEX_PATTERN);
    private List<Token> queue = new ArrayList<>();
    private boolean hasMore;
    private LineNumberReader reader;
    
    public Lexer(Reader reader) {
        hasMore = true;
        this.reader = new LineNumberReader(reader);
    }
    
    public Token read() {
        if(fillQueue(0)) {
            return queue.remove(0);
        }
        
        return Token.EOF;
    }
    
    private boolean fillQueue(int i) {
        while(i >= queue.size()) {
            if(hasMore) {
                readLine();
            } else {
                return false;
            }
        }
        
        return true;
    }
    
    private void readLine() {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new ParseException(e);
        }
        
        if(line == null) {
            hasMore = false;
            return;
        }
        
        int lineNo = reader.getLineNumber();
        Matcher matcher = pattern.matcher(line);
        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        
        int pos = 0;
        int endPos = line.length();
        while(pos < endPos) {
            matcher.region(pos, endPos);
            if(matcher.lookingAt()) {
                addToken(lineNo, matcher);
                pos = matcher.end();
            } else {
                throw ParseException("bad token at line " + lineNo);
            }
        }
        
        queue.add(new IdToken(lineNo, Token.EOL));
    }
    
    private void addToken(int lineNo, Matcher matcher) {
        String m = matcher.group(1);
        // if not space
        if(m != null) { 
            // it not comment
            if(matcher.group(2) == null) { 
                Token token;
                if (matcher.group(3) != null) {
                    token = new NumToken(lineNo, Integer.parseInt(m));
                } else if (matcher.group(4) != null) {
                    token = new StrToken(lineNo, toStringLiteral(m));
                } else {
                    token = new IdToken(lineNo, m);
                }
                
                queue.add(token);
            }
        }
    }
    
    private String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int length = s.length() - 1;
        // handle the escape character
        for(int i = 1; i < length; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < length) {
                int c2 = s.charAt(i + 1);
                if (c2 == '"' || c2 == '\\') {
                    c = s.charAt(++i);
                } else if (c2 == 'n') {
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    private static class NumToken extends Token {
        private int value;
        
        public NumToken(int lineNumebr, int value) {
            super(lineNumebr);
            this.value = value;
        }
        
        @Override
        public boolean isNumber() {
            return true;
        }
        
        @Override
        public String getText() {
            return Integer.toString(value);
        }
        
        @Override
        public int getNumber() {
            return value;
        }
    }
    
    private static class IdToken extends Token {
        private String text;
        
        public IdToken(int lineNumber, String text) {
            super(lineNumber);
            this.text = text;
        }
        
        @Override
        public boolean isIdentifier() {
            return true;
        }
        
        @Override
        public String getText() {
            return text;
        }
    }
    
    private static class StrToken extends Token {
        private String literal;
        public StrToken(int lineNumber, String literal) {
            super(lineNumber);
            this.literal = literal;
        }
        
        @Override
        public boolean isString() {
            return true;
        }
        
        @Override
        public String getText() {
            return literal;
        }
    }
}
