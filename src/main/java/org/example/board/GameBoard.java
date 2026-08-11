package org.example.board;

import java.util.List;
import java.util.ArrayList;

import org.example.enemy.Enemy;

import org.example.player.Player;

import org.example.occupant.Occupant;
import org.example.occupant.Position;

public class GameBoard {
    private final Cell[][] board;

    private final Player player;
    private final List<Enemy> enemies;

    public GameBoard(Cell[][] board, Player player, List<Enemy> enemies) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;
    }

    public GameBoard(Player player, List<Enemy> enemies) {
        board = new Cell[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = new Floor();
            }
        }
        this.player = player;
        this.enemies = enemies;
    }

    public Enemy getRandomEnemyInRange(Position position, int maxRange) {
        List<Enemy> enemiesInRange = getEnemiesInRange(position, maxRange);
        return enemiesInRange.isEmpty() ? null : enemiesInRange.get((int)(Math.random() * enemiesInRange.size()));
    }

    public Enemy getClosestEnemyInRange(Position position, int maxRange) {
        Enemy closestEnemy = null;
        List<Enemy> enemiesInRange = getEnemiesInRange(position, maxRange);
        if (!enemiesInRange.isEmpty()) {
            double minimumDistance = Double.MAX_VALUE;
            for (Enemy enemy : enemies) {
                double currentDistance = position.range(enemy.getPosition());
                if (currentDistance < minimumDistance) {
                    minimumDistance = currentDistance;
                    closestEnemy = enemy;
                }
            }
        }
        return closestEnemy;
    }

    public List<Enemy> getEnemiesInRange(Position position, int maxRange) {
        List<Enemy> enemiesInRange = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (position.range(enemy.getPosition()) < maxRange) {
                enemiesInRange.add(enemy);
            }
        }
        return enemiesInRange;
    }

    public Player getPlayerInRange(Position position, int maxRange) {
        return position.range(player.getPosition()) < maxRange ? player : null;
    }

    public void killEnemy(Enemy enemy) {
        enemies.remove(enemy);
        setOccupant(enemy.getPosition(), null);
    }

    public Cell getCell(Position position) {
        return board[position.getY()][position.getX()];
    }
    public void setCell(Position position, Cell cell) {
        board[position.getY()][position.getX()] = cell;
    }

    public Occupant getOccupant(Position position) {
        return board[position.getY()][position.getX()].getOccupant();
    }
    public void setOccupant(Position position, Occupant occupant) {
        board[position.getY()][position.getX()].setOccupant(occupant);
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (Cell[] line : board) {
            for (Cell cell : line) {
                boardString.append(cell.toString());
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }
}