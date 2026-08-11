package org.example.game;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;

import org.example.board.Cell;
import org.example.board.Wall;
import org.example.board.Floor;
import org.example.board.GameBoard;

import org.example.enemy.Trap;
import org.example.enemy.Boss;
import org.example.enemy.Enemy;
import org.example.enemy.Monster;

import org.example.occupant.Position;

import org.example.player.Mage;
import org.example.player.Rogue;
import org.example.player.Hunter;
import org.example.player.Player;
import org.example.player.Warrior;

import org.example.ui.CLI;

public class Game {
    private final CLI cli;
    private final List<File> levels;

    private final Player player;
    private final List<Enemy> currentLevelEnemies;

    public Game(String levelsDirectoryPath) {
        cli = new CLI();
        currentLevelEnemies = new ArrayList<>();
        levels = readLevels(levelsDirectoryPath);
        player = chooseCharacter();
    }

    private List<File> readLevels(String levelsDirectoryPath) {
        File folder = new File(levelsDirectoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Levels directory not found: " + levelsDirectoryPath);
        }

        File[] files = folder.listFiles((_, name) -> name.startsWith("level") && name.endsWith(".txt"));
        if (files == null) {
            throw new IllegalArgumentException("No level files found.");
        }

        Arrays.sort(files, Comparator.comparingInt(file -> Integer.parseInt(file.getName().replaceAll("[^0-9]", ""))));
        return Arrays.asList(files);
    }

    private Player chooseCharacter() {
        while (true) {
            switch (cli.chooseCharacter(List.of("Jon Snow (Warrior)\t\tHealth: 300/300\t\tAttack: 30\t\tDefense: 4\t\tLevel: 1\t\tExperience: 0/50\t\tCooldown: 0/3",
                                                "The Hound (Warrior)\t\tHealth: 400/400\t\tAttack: 20\t\tDefense: 6\t\tLevel: 1\t\tExperience: 0/50\t\tCooldown: 0/5",
                                                "Melisandre (Mage)\t\tHealth: 100/100\t\tAttack: 5\t\tDefense: 1\t\tLevel: 1\t\tExperience: 0/50\t\tMana: 75/300\t\tSpell Power: 15",
                                                "Thoros of Myr (Mage)\t\tHealth: 250/250\t\tAttack: 25\t\tDefense: 4\t\tLevel: 1\t\tExperience: 0/50\t\tMana: 37/150\t\tSpell Power: 20",
                                                "Arya Stark (Rogue)\t\tHealth: 150/150\t\tAttack: 40\t\tDefense: 2\t\tLevel: 1\t\tExperience: 0/50\t\tEnergy: 100/100",
                                                "Bronn (Rogue)\t\t\tHealth: 250/250\t\tAttack: 35\t\tDefense: 3\t\tLevel: 1\t\tExperience: 0/50\t\tEnergy: 100/100",
                                                "Ygritte (Hunter)\t\t\tHealth: 220/220\t\tAttack: 30\t\tDefense: 2\t\tLevel: 1\t\tExperience: 0/50\t\tArrows: 10\t\t\tRange: 6"))) {
                case 1:
                    return new Warrior('@', new Position(0,0), "Jon Snow", 300, 30, 4, 3);
                case 2:
                    return new Warrior('@', new Position(0,0), "The Hound", 400, 20, 6, 5);
                case 3:
                    return new Mage('@', new Position(0,0), "Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
                case 4:
                    return new Mage('@', new Position(0,0), "Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4);
                case 5:
                    return new Rogue('@', new Position(0,0), "Arya Stark", 150, 40, 2, 20);
                case 6:
                    return new Rogue('@', new Position(0,0), "Bronn", 250, 35, 3, 50);
                case 7:
                    return new Hunter('@', new Position(0,0), "Ygritte", 220, 30, 2, 6);
                default:
                    cli.printInvalidChoiceMessage();
            }
        }
    }

    public void run() {
        for (int i = 0; i < levels.size(); i++) {
            GameBoard gameBoard = new GameBoard(loadLevel(readLevel(levels.get(i))), player, currentLevelEnemies);

            player.setGameBoard(gameBoard);
            player.setMessageCallback(cli::printMessage);
            for (Enemy enemy : currentLevelEnemies) {
                enemy.setGameBoard(gameBoard);
                enemy.setMessageCallback(cli::printMessage);
            }

            cli.printLevel(i + 1);

            while (!currentLevelEnemies.isEmpty() && !player.isDead()) {
                cli.displayGameState(gameBoard.toString(), player.description());

                handlePlayerAction();
                player.onGameTick();

                for (Enemy enemy : currentLevelEnemies) {
                    enemy.onEnemyTurn();

                    if (player.isDead()) {
                        player.dead(enemy);
                        break;
                    }
                }
            }

            if (player.isDead()) {
                cli.displayGameState(gameBoard.toString(), player.description());
                cli.printMessage("Game Over!");
                return;
            } else {
                cli.printMessage(String.format("Level %d Complete!", i + 1));
            }
        }
        
        cli.printMessage("Congratulations! You won the game!");
    }

    private void handlePlayerAction() {
        Position currentPosition = player.getPosition();

        while (true) {
            switch (cli.chooseAction()) {
                case 'w':
                    player.move(new Position(currentPosition.getX(), currentPosition.getY() - 1));
                    return;
                case 's':
                    player.move(new Position(currentPosition.getX(), currentPosition.getY() + 1));
                    return;
                case 'a':
                    player.move(new Position(currentPosition.getX() - 1, currentPosition.getY()));
                    return;
                case 'd':
                    player.move(new Position(currentPosition.getX() + 1, currentPosition.getY()));
                    return;
                case 'e':
                    player.castSpecialAbility();
                    return;
                case 'q':
                    return;
                default:
                    cli.printInvalidChoiceMessage();
                    break;
            }
        }
    }

    private Cell[][] loadLevel(List<String> lines) {
        int height = lines.size();
        int width = lines.getFirst().length();
        
        Cell[][] board = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            String currentLine = lines.get(y);
            for (int x = 0; x < width; x++) {
                char tile = currentLine.charAt(x);
                switch(tile)
                {
                    case '#':
                        board[y][x] = new Wall();
                        break;
                    case '.':
                        board[y][x] = new Floor(null);
                        break;
                    case '@':
                        player.setPosition(new Position(x, y));
                        board[y][x] = new Floor(player);
                        break;
                    default:
                        Enemy enemy = createEnemy(tile, new Position(x, y));
                        currentLevelEnemies.add(enemy);
                        board[y][x] = new Floor(enemy);
                }
            }
        }
        
        return board;
    }

    private List<String> readLevel(File level) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(level))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Level file invalid!");
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Level file invalid!");
        }

        return lines;
    }

    private Enemy createEnemy(char tile, Position position) {
        return switch (tile) {
            case 's' -> new Monster('s', position, "Gold Cloak", 80, 8, 3, 25, 3);
            case 'k' -> new Monster('k', position, "Knight", 200, 14, 8, 50, 4);
            case 'q' -> new Monster('q', position, "Queen's Guard", 400, 20, 15, 100, 5);
            case 'z' -> new Monster('z', position, "Wright", 600, 30, 15, 100, 3);
            case 'b' -> new Monster('b', position, "Bear", 1000, 75, 30, 250, 4);
            case 'g' -> new Monster('g', position, "Giant", 1500, 100, 40, 500, 5);
            case 'w' -> new Monster('w', position, "White Walker", 2000, 150, 50, 1000, 6);
            case 'M' -> new Boss('M', position, "The Mountain", 1000, 60, 25, 500, 6,5);
            case 'C' -> new Boss('C', position, "Queen Cersei", 100, 10, 10, 1000, 1,8);
            case 'K' -> new Boss('K', position, "Night's King", 5000, 300, 150, 5000, 8, 3);
            case 'B' -> new Trap('B', position, "Bonus Trap", 1, 1, 1, 250, 1, 5);
            case 'Q' -> new Trap('Q', position, "Queen's Trap", 250, 50, 10, 100, 3, 7);
            case 'D' -> new Trap('D', position, "Death Trap", 500, 100, 20, 250, 1, 10);
            default -> throw new IllegalArgumentException(String.format("Unknown enemy: %c", tile));
        };
    }
}