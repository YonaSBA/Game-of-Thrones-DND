package org.example.occupant;

import org.example.board.Wall;
import org.example.board.Floor;
import org.example.board.GameBoard;

import org.example.enemy.Enemy;

import org.example.player.Player;

public abstract class Unit extends Occupant implements CellVisitor, OccupantVisitor {
    protected String name;

    protected int healthPool;
    protected int healthAmount;
    protected int attackPoints;
    protected int defensePoints;

    protected Position position;
    protected Position destination;

    protected GameBoard gameBoard;

    protected MessageCallback messageCallback;


    public Unit(char tile, Position position, String name, int healthPool, int attackPoints, int defensePoints) {
        super(tile);
        this.name = name;
        this.position = position;
        this.healthPool = healthPool;
        this.healthAmount = healthPool;
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
    }

    public String getName() { return name; }
    public int getHealthPool() {
        return healthPool;
    }
    public Position getPosition() { return position; }
    public int getHealthAmount() {
        return healthAmount;
    }
    public int getAttackPoints() {
        return attackPoints;
    }
    public int getDefensePoints() {
        return defensePoints;
    }

    public void setPosition(Position position) { this.position = position; }
    public void setGameBoard(GameBoard gameBoard) { this.gameBoard = gameBoard; }

    public void attack(Unit defender) {
        log(String.format("%s attacked %s.", name, defender.name));
        int attackRoll = (int)(Math.random() * (attackPoints + 1));
        int defenseRoll = (int)(Math.random() * (defender.defensePoints + 1));

        int damage = attackRoll - defenseRoll;
        if (damage > 0) {
            defender.defend(damage);
            log(String.format("%s rolled %d attack points, %s rolled %d defense points, dealing %d damage.", name, attackRoll, defender.name, defenseRoll, damage));
        } else {
            log(String.format("%s wasn't harmed.", defender.name));
        }

        log(defender.description());
    }

    public void attack(Unit defender, String specialAbilityName, int specialAbilityPower) {
        log(String.format("%s cast %s on %s.", name, specialAbilityName, defender.name));
        int defenseRoll = (int)(Math.random() * (defender.defensePoints + 1));
        int damage = specialAbilityPower - defenseRoll;

        if (damage > 0) {
            defender.defend(damage);
            log(String.format("%s activated %d attack points, %s rolled %d defense point, dealing %d damage.", name, specialAbilityPower, defender.name, defenseRoll, damage));
        } else {
            log(String.format("%s wasn't harmed.", defender.name));
        }

        log(defender.description());
    }

    public void defend(int damage) {
        healthAmount = Math.max(healthAmount - damage, 0);
    }

    public boolean isDead() {
        return healthAmount == 0;
    }

    public void visit(Wall wall) {
        log(String.format("%s ran into a wall and remained in his original position.", name));
    }

    public void visit(Floor floor) {
        if (floor.getOccupant() == null) {
            Position oldPosition = position;
            position = destination;
            gameBoard.setOccupant(position, this);
            gameBoard.setOccupant(oldPosition, null);
        } else {
            floor.getOccupant().accept(this);
        }
    }

    public abstract void visit(Enemy enemy);
    public abstract void visit(Player player);

    public String description() {
        return String.format("%s\t\tHealth: %d/%d\t\tAttack: %d\t\tDefense: %d",
                getName(), healthAmount, healthPool, attackPoints, defensePoints);
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    protected void log(String message) {
        if (messageCallback != null) {
            messageCallback.send(message);
        }
    }
}
