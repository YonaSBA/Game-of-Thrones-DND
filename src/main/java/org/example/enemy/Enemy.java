package org.example.enemy;

import org.example.player.Player;

import org.example.occupant.Unit;
import org.example.occupant.Position;
import org.example.occupant.OccupantVisitor;

public abstract class Enemy extends Unit implements OccupantVisitor {
    protected final int experienceValue;

    public Enemy(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.experienceValue = experienceValue;
    }

    public int getExperienceValue() { return experienceValue; }
    public abstract void onEnemyTurn();

    @Override public void accept(OccupantVisitor visitor) { visitor.visit(this); }
    @Override public void visit(Enemy enemy) {}

    @Override
    public void visit(Player player) {
        this.attack(player);
    }

    @Override
    public String description() {
        return String.format("%s\t\tKill XP: %d", super.description(), experienceValue);
    }
}
