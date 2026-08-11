package org.example.occupant;

import org.example.board.Wall;
import org.example.board.Floor;

public interface CellVisitor {
    void visit(Wall wall);
    void visit(Floor floor);
}
