package org.example.player;

import org.example.enemy.Enemy;

import org.example.occupant.Position;

public class Warrior extends Player {
    private int remainingCooldown;
    private final int abilityCooldown;

    public Warrior(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int abilityCooldown) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }

    @Override
    public void levelUp() {
        int oldLevel = level;
        super.levelUp();

        remainingCooldown = 0;
        defensePoints += oldLevel;
        healthPool += 5 * oldLevel;
        attackPoints += 2 * oldLevel;

        log(String.format("Warrior Bonus: +%d HP, +%d ATK, +%d DEF! Avenger's Shield Cooldown refreshed!", 5 * oldLevel, 2 * oldLevel, oldLevel));
    }

    @Override
    public void onGameTick() {
        if (remainingCooldown > 0) {
            remainingCooldown--;
        }
    }

    @Override
    public void castSpecialAbility() {
        if (remainingCooldown > 0) {
            log("Avenger’s Shield is on cooldown!");
        } else {
            remainingCooldown = abilityCooldown;
            healthAmount = Math.min(healthAmount + (10 * defensePoints), healthPool);

            Enemy enemy = gameBoard.getRandomEnemyInRange(position, 3);
            if (enemy == null) {
                log("No enemies in range to Shoot!");
            } else {
                attack(enemy, "Avenger’s Shield", (int)(healthPool * 0.1));
                if (enemy.isDead()) {
                    onEnemyKilled(enemy);
                }
            }
        }
    }

    @Override
    public String description() {
        return String.format("%s\t\tCooldown: %d/%d", super.description(), remainingCooldown, abilityCooldown);
    }
}