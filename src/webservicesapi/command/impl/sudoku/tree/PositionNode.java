package webservicesapi.command.impl.sudoku.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PositionNode {

    private Position pos;
    private Set<Position> children;
    private PositionNode parent;
    private int value;

    public PositionNode() {
        children = new HashSet<Position>();
    }

    public void setParent(PositionNode parent) {
        this.parent = parent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public PositionNode getParent() {
        return parent;
    }


    public void addAll(Collection<Position> newPositions) {
        children.addAll(newPositions);
    }

    public Set<Position> getAvailablePositions() {
        return children;
    }

    public boolean removePosition(Position position) {
        return children.remove(position);
    }

    public void setCurrentPosition(Position pos) {
        this.pos = pos;
    }

    public Position getCurrentPosition() {
        return pos;
    }
}
