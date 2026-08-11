package org.example.enemy;

import org.example.player.Player;

import org.example.occupant.Position;

public class Trap extends Enemy {
    private int ticksCount;
    private boolean visible;
    private final char visibleTile;
    private final int visibilityTime;
    private final int invisibilityTime;

    public Trap(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int visibilityTime, int invisibilityTime) {
        super(tile, position, name, healthPool, attackPoints, defensePoints, experienceValue);
        this.visible = true;
        this.ticksCount = 0;
        this.visibleTile = tile;
        this.visibilityTime = visibilityTime;
        this.invisibilityTime = invisibilityTime;
    }

    @Override
    public void onEnemyTurn() {
        this.visible = this.ticksCount < this.visibilityTime;
        this.tile = this.visible ? this.visibleTile : '.';

        if (this.ticksCount == this.visibilityTime + this.invisibilityTime) {
            this.ticksCount = 0;
        } else {
            this.ticksCount++;
        }

        Player player = gameBoard.getPlayerInRange(this.position, 2);
        if (player != null) {
            this.attack(player);
        }
    }

    @Override
    public String description() {
        return String.format("%s\t\tState: %s\t\tTicks: %d/%d", super.description(), visible ? "VISIBLE" : "INVISIBLE", ticksCount, (visible ? visibilityTime : invisibilityTime));
    }
}