package org.example.enemy;

import org.example.player.Player;

import org.example.occupant.Position;
import org.example.occupant.HeroicUnit;

public class Boss extends Enemy implements HeroicUnit {
    private int combatTicks;
    private final int visionRange;
    private final int abilityFrequency;

    private Player player;

    public Boss(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int visionRange, int abilityFrequency) {
        super(tile, position, name, healthPool, attackPoints, defensePoints, experienceValue);
        this.combatTicks = 0;
        this.visionRange = visionRange;
        this.abilityFrequency = abilityFrequency;
    }

    @Override
    public void onEnemyTurn() {
        player = gameBoard.getPlayerInRange(position, visionRange);
        if (player != null) {
            if (combatTicks == abilityFrequency) {
                combatTicks = 0;
                castSpecialAbility();
            } else {
                combatTicks++;

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
        } else {
            combatTicks = 0;
        }
    }

    private void move(int xOffset, int yOffset) {
        destination = new Position(position.getX() + xOffset, position.getY() + yOffset);
        gameBoard.getCell(destination).accept(this);
    }

    @Override
    public void castSpecialAbility() {
        attack(player, "Shoebodybop", attackPoints);
    }

    @Override
    public String description() {
        return String.format("%s\t\tVision Range: %d\t\tCombat Ticks: %d/%d", super.description(), visionRange, combatTicks, abilityFrequency);
    }
}