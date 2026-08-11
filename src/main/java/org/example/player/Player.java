package org.example.player;

import org.example.occupant.Unit;
import org.example.occupant.Position;
import org.example.occupant.HeroicUnit;
import org.example.occupant.OccupantVisitor;

import org.example.enemy.Enemy;

public abstract class Player extends Unit implements OccupantVisitor, HeroicUnit {
    protected int level;
    protected int experience;

    public Player(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints) {
        super(tile, position, name, healthPool, attackPoints, defensePoints);
        this.level = 1;
        this.experience = 0;
    }

    public int getLevel() {
        return level;
    }
    public int getExperience() {
        return experience;
    }

    public void addExperience(int amount) {
        experience += amount;
        while (experience >= 50 * level) {
            levelUp();
        }
    }

    public void levelUp() {
        int oldLevel = level;
        level++;

        defensePoints += oldLevel;
        healthPool += 10 * oldLevel;
        healthAmount = healthPool;
        attackPoints += 4 * oldLevel;
        experience -= (50 * oldLevel);

        log(String.format("LEVEL UP! %s reached Level %d!\nStats gained: +%d HP, +%d ATK, +%d DEF", name, level, 10 * oldLevel, 4 * oldLevel, oldLevel));
    }

    @Override public void accept(OccupantVisitor visitor) { visitor.visit(this); }
    @Override public void visit(Player player) {}

    @Override
    public void visit(Enemy enemy) {
        attack(enemy);

        if (enemy.isDead()) {
            onEnemyKilled(enemy);

            Position oldPosition = position;
            position = enemy.getPosition();

            gameBoard.setOccupant(position, this);
            gameBoard.setOccupant(oldPosition, null);
        }
    }

    public void dead(Enemy enemy) {
        log(String.format("%s killed by %s and lost.", name, enemy.getName()));
        this.tile = 'X';
    }

    public abstract void onGameTick();

    @Override
    public String description() {
        return String.format("%s\t\tLevel: %d\t\tExperience: %d/%d", super.description(), level, experience, 50 * level);
    }

    public void move(Position nextPosition) {
        destination = nextPosition;
        gameBoard.getCell(nextPosition).accept(this);
    }

    protected void onEnemyKilled(Enemy enemy)
    {
        log(String.format("%s killed %s and earned %d experience points.", name, enemy.getName(), enemy.getExperienceValue()));
        addExperience(enemy.getExperienceValue());

        gameBoard.killEnemy(enemy);
    }
}
