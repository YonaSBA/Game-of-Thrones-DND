package org.example.board;

import org.example.occupant.CellVisitor;

public class Wall extends Cell {
    public Wall() {
        super('#');
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }
}
