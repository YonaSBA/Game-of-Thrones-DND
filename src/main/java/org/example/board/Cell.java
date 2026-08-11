package org.example.board;

import org.example.occupant.Occupant;
import org.example.occupant.CellVisitor;

public abstract class Cell {
    protected char tile;

    public Cell(char tile) {
        this.tile = tile;
    }

    public Occupant getOccupant() { return null; }
    public void setOccupant(Occupant occupant) { }
    public abstract void accept(CellVisitor visitor);

    @Override
    public String toString() {
        return String.valueOf(this.tile);
    }
}