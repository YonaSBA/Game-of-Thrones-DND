package org.example.player;

import org.example.enemy.Enemy;

import org.example.occupant.Position;

public class Mage extends Player {
    private int manaPool;
    private int spellPower;
    private int currentMana;
    private final int manaCost;
    private final int hitsCount;
    private final int abilityRange;

    public Mage(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int manaPool, int manaCost, int spellPower, int hitsCount, int abilityRange) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.manaCost = manaCost;
        this.manaPool = manaPool;
        this.hitsCount = hitsCount;
        this.spellPower = spellPower;
        this.currentMana = manaPool / 4;
        this.abilityRange = abilityRange;
    }

    @Override
    public void levelUp() {
        int oldLevel = level;
        super.levelUp();

        manaPool += 25 * oldLevel;
        spellPower += 10 * oldLevel;
        currentMana = Math.min(currentMana + (manaPool / 4), manaPool);

        log(String.format("Mage Bonus: +%d Mana Pool, +%d Spell Power! Restored %d Mana.", 25 * oldLevel, 10 * oldLevel, manaPool / 4));
    }

    @Override
    public void onGameTick() {
        currentMana = Math.min(manaPool, currentMana + level);
    }

    @Override
    public void castSpecialAbility() {
        if (currentMana < manaCost) {
            log("There isn't enough mana to cast Blizzard!");
        } else {
            currentMana -= manaCost;

            int hits = 0;
            while (hits < hitsCount) {
                Enemy enemy = gameBoard.getRandomEnemyInRange(position, abilityRange);
                if (enemy == null) {
                    log("No enemies in range to Shoot!");
                    break;
                }

                attack(enemy, "Blizzard", spellPower);
                if (enemy.isDead()) {
                    onEnemyKilled(enemy);
                }

                hits++;
            }
        }
    }

    @Override
    public String description() {
        return String.format("%s\t\tMana: %d/%d", super.description(), currentMana, manaPool);
    }
}