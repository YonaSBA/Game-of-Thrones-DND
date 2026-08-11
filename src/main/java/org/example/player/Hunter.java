package org.example.player;

import org.example.enemy.Enemy;

import org.example.occupant.Position;

public class Hunter extends Player {
    private final int range;
    private int arrowsCount;
    private int ticksCount;

    public Hunter(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int range) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.range = range;
        this.arrowsCount = 10;
        this.ticksCount = 0;
    }

    @Override
    public void levelUp() {
        int oldLevel = level;
        super.levelUp();

        arrowsCount += 10 * oldLevel;
        attackPoints += 2 * oldLevel;
        defensePoints += oldLevel;

        log(String.format("Hunter Bonus: +%d Arrows, +%d ATK, +%d DEF!", 10 * oldLevel, 2 * oldLevel, oldLevel));
    }

    @Override
    public void onGameTick() {
        if (ticksCount == 10) {
            arrowsCount += level;
            ticksCount = 0;
        } else {
            ticksCount++;
        }
    }

    @Override
    public void castSpecialAbility() {
        if (arrowsCount == 0) {
            log("Out of arrows to Shoot!");
        } else {
            Enemy enemy = gameBoard.getClosestEnemyInRange(position, range);
            if (enemy == null) {
                log("No enemies in range to Shoot!");
            } else {
                arrowsCount--;
                attack(enemy, "Shoot", this.attackPoints);

                if (enemy.isDead()) {
                    onEnemyKilled(enemy);
                }
            }
        }
    }

    @Override
    public String description() {
        return String.format("%s\t\tArrows: %d\t\tArrows Ticks: %d/10",
                super.description(), arrowsCount, ticksCount);
    }
}