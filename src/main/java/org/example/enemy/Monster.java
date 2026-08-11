package org.example.enemy;

import java.util.List;

import org.example.player.Player;

import org.example.occupant.Position;

public class Monster extends Enemy {
    private final int visionRange;

    public Monster(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int visionRange) {
        super(tile, position, name, healthPool, attackPoints, defensePoints, experienceValue);
        this.visionRange = visionRange;
    }

    @Override
    public void onEnemyTurn() {
        Player player = gameBoard.getPlayerInRange(position, visionRange);
        if (player == null) {
            List<Runnable> moves = List.of(
                    () -> move(0, -1),
                    () -> move(0, 1),
                    () -> move(-1, 0),
                    () -> move(1, 0)
            );
            moves.get((int)(Math.random() * moves.size())).run();
        } else {
            int dx = position.getX() - player.getPosition().getX();
            int dy = position.getY() - player.getPosition().getY();
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    move(-1, 0);
                } else {
                    move(1, 0);
                }
            } else {
                if (dy > 0) {
                    move(0, -1);
                } else {
                    move(0, 1);
                }
            }
        }
    }

    private void move(int xOffset, int yOffset) {
        destination = new Position(position.getX() + xOffset, position.getY() + yOffset);
        gameBoard.getCell(destination).accept(this);
    }

    @Override
    public String description() {
        return String.format("%s\t\tVision Range: %d", super.description(), visionRange);
    }
}