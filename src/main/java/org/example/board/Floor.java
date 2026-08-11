package org.example.board;

import org.example.occupant.Occupant;
import org.example.occupant.CellVisitor;

public class Floor extends Cell {
    private Occupant occupant;

    public Floor() {
        super('.');
        this.occupant = null;
    }
    public Floor(Occupant occupant) {
        super('.');
        this.occupant = occupant;
    }

    @Override
    public Occupant getOccupant() {
        return this.occupant;
    }

    @Override
    public void setOccupant(Occupant occupant) {
        this.occupant = occupant;
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        if (occupant != null) {
            return occupant.toString();
        }
        return super.toString();
    }
}