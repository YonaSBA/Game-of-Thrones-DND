package org.example.player;

import java.util.List;

import org.example.enemy.Enemy;

import org.example.occupant.Position;

public class Rogue extends Player {
    private final int cost;
    private int currentEnergy;

    public Rogue(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int cost) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.cost = cost;
        this.currentEnergy = 100;
    }

    @Override
    public void levelUp() {
        int oldLevel = level;
        super.levelUp();

        currentEnergy = 100;
        attackPoints += 3 * level;

        log(String.format("Rogue Bonus: +%d ATK! Energy fully restored to 100!", 3 * oldLevel));
    }

    @Override
    public void onGameTick() {
        currentEnergy = Math.min(currentEnergy + 10, 100);
    }

    @Override
    public void castSpecialAbility() {
        if (currentEnergy < cost) {
            log("There isn't enough energy to cast Fan of Knives!");
        } else {
            currentEnergy -= cost;

            List<Enemy> enemies = gameBoard.getEnemiesInRange(position, 2);
            if (enemies.isEmpty()) {
                log("No enemies in range to Shoot!");
            } else {
                for (Enemy enemy : enemies) {
                    attack(enemy, "Fan of Knives", attackPoints);
                    if (enemy.isDead()) {
                        onEnemyKilled(enemy);
                    }
                }
            }
        }
    }

    @Override
    public String description() {
        return String.format("%s\t\tEnergy: %d/100", super.description(), currentEnergy);
    }
}