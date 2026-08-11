package org.example.occupant;

public abstract class Occupant {
    protected char tile;

    public Occupant(char tile) {
        this.tile = tile;
    }

    public String toString() {
        return String.valueOf(this.tile);
    }

    public abstract void accept(OccupantVisitor visitor);
}