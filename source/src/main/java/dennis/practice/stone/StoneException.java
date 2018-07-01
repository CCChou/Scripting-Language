package dennis.practice.stone;

import dennis.practice.stone.ast.ASTree;

public class StoneException extends RuntimeException {
    private static final long serialVersionUID = 1441804312495770631L;

    public StoneException(String message) {
        super(message);
    }
    
    public StoneException(String message, ASTree tree) {
        super(message + tree.location());
    }
}
