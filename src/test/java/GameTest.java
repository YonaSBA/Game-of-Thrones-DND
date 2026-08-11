import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import org.example.occupant.Position;
import org.example.occupant.MessageCallback;

import org.example.enemy.*;
import org.example.player.*;
import org.example.board.Wall;
import org.example.board.GameBoard;

public class GameTest {

    private Player mage;
    private Player hunter;
    private Player warrior;

    private Monster monster;

    private Boss boss;
    private GameBoard gameBoard;
    private List<String> gameLogs;

    @BeforeEach
    public void setUp() {
        gameLogs = new ArrayList<>();
        MessageCallback testMockCallback = message -> gameLogs.add(message);

        warrior = new Warrior('@', new Position(1, 1), "Jon Snow", 300, 30, 4, 3);
        mage = new Mage('@', new Position(1, 1), "Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        hunter = new Hunter('@', new Position(1, 1), "Ygritte", 220, 30, 2, 6);
        monster = new Monster('s', new Position(2, 1), "Gold Cloak", 80, 8, 3, 25, 3);
        boss = new Boss('M', new Position(3, 1), "The Mountain", 1000, 60, 25, 500, 6, 5);

        warrior.setMessageCallback(testMockCallback);
        mage.setMessageCallback(testMockCallback);
        hunter.setMessageCallback(testMockCallback);
        monster.setMessageCallback(testMockCallback);
        boss.setMessageCallback(testMockCallback);
    }

    @Test
    public void testMoveToEmptyFloor() {
        gameBoard = new GameBoard(warrior, new ArrayList<>());
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        warrior.setGameBoard(gameBoard);

        Position currentPosition = warrior.getPosition();

        Position targetPosition = new Position(1, 0);
        warrior.move(targetPosition);

        assertEquals(targetPosition, warrior.getPosition());
        assertEquals(warrior, gameBoard.getOccupant(targetPosition));
        assertNull(gameBoard.getOccupant(currentPosition));
    }

    @Test
    public void testCollisionWithWall() {
        gameBoard = new GameBoard(warrior, new ArrayList<>());
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        warrior.setGameBoard(gameBoard);

        Position currentPosition = warrior.getPosition();

        Position wallPosition = new Position(1, 0);
        gameBoard.setCell(wallPosition, new Wall());

        warrior.move(wallPosition);

        assertEquals(currentPosition, warrior.getPosition());
        assertTrue(gameLogs.stream().anyMatch(log -> log.contains("wall")));
    }

    @Test
    public void testCollisionBetweenSameUnitTypes() {
        gameBoard = new GameBoard(null, List.of(monster, boss));
        gameBoard.setOccupant(monster.getPosition(), monster);
        gameBoard.setOccupant(boss.getPosition(), boss);
        monster.setGameBoard(gameBoard);
        boss.setGameBoard(gameBoard);

        Position currentPosition = monster.getPosition();

        monster.visit((Enemy)gameBoard.getOccupant(boss.getPosition()));

        assertEquals(currentPosition, monster.getPosition());
        assertEquals(boss, gameBoard.getOccupant(boss.getPosition()));
    }

    @Test
    public void testPlayerAttacksEnemyCalculatesDamage() {
        gameBoard = new GameBoard(warrior, List.of(monster));
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        gameBoard.setOccupant(monster.getPosition(), monster);
        warrior.setGameBoard(gameBoard);
        monster.setGameBoard(gameBoard);

        int initialHealthAmount = monster.getHealthAmount();

        warrior.move(monster.getPosition());

        assertTrue(monster.getHealthAmount() < initialHealthAmount || monster.isDead());

        assertFalse(gameLogs.isEmpty());
        assertTrue(gameLogs.stream().anyMatch(log -> log.contains("attacked")));
    }

    @Test
    public void testEnemyDeathAndCleanup() {
        gameBoard = new GameBoard(warrior, new ArrayList<>(List.of(monster)));
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        gameBoard.setOccupant(monster.getPosition(), monster);
        warrior.setGameBoard(gameBoard);
        monster.setGameBoard(gameBoard);

        monster.defend(80);
        warrior.move(monster.getPosition());
        assertTrue(monster.isDead());

        gameBoard.killEnemy(monster);

        assertNull(gameBoard.getOccupant(monster.getPosition()));
        assertTrue(warrior.getExperience() > 0);
    }

    @Test
    public void testPlayerDeathState() {
        gameBoard = new GameBoard(warrior, new ArrayList<>());
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        warrior.setGameBoard(gameBoard);

        warrior.defend(300);

        assertTrue(warrior.isDead());
        warrior.dead(monster);

        assertTrue(gameBoard.toString().contains("X"));
    }

    @Test
    public void testStandardPlayerLevelUp() {
        gameBoard = new GameBoard(mage, new ArrayList<>());
        gameBoard.setOccupant(mage.getPosition(), mage);
        mage.setGameBoard(gameBoard);

        int initialHealthPool = mage.getHealthPool();
        int initialAttackPoints = mage.getAttackPoints();
        int initialDefensePoints = mage.getDefensePoints();

        mage.addExperience(60);

        assertEquals(2, mage.getLevel());
        assertEquals(mage.getHealthPool(), mage.getHealthAmount());
        assertTrue(mage.getHealthPool() > initialHealthPool);
        assertTrue(mage.getAttackPoints() > initialAttackPoints);
        assertTrue(mage.getDefensePoints() > initialDefensePoints);
    }

    @Test
    public void testWarriorLevelUpHealthFormula() {
        gameBoard = new GameBoard(warrior, new ArrayList<>());
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        warrior.setGameBoard(gameBoard);

        warrior.defend(100);

        warrior.addExperience(60);

        assertEquals(315, warrior.getHealthPool());
        assertEquals(310, warrior.getHealthAmount());
    }

    @Test
    public void testResourceManagementAndBlocking() {
        gameBoard = new GameBoard(mage, new ArrayList<>());
        gameBoard.setOccupant(mage.getPosition(), mage);
        mage.setGameBoard(gameBoard);

        mage.defend(10);
        for (int i = 0; i < 5; i++) {
            mage.castSpecialAbility();
        }

        gameLogs.clear();
        mage.castSpecialAbility();

        assertTrue(gameLogs.stream().anyMatch(log -> log.contains("isn't enough")));
    }

    @Test
    public void testBossCastsSpecialAbilityWhenTicksReachFrequency() {
        gameBoard = new GameBoard(warrior, List.of(boss));
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        gameBoard.setOccupant(boss.getPosition(), boss);
        warrior.setGameBoard(gameBoard);
        boss.setGameBoard(gameBoard);

        warrior.setPosition(new Position(3, 2));
        gameBoard.setOccupant(warrior.getPosition(), warrior);

        for (int i = 0; i < 6; i++) {
            boss.onEnemyTurn();
        }

        assertTrue(gameLogs.stream().anyMatch(log -> log.contains("Shoebodybop")));
    }

    @Test
    public void testBossTicksResetWhenPlayerLeavesRange() {
        gameBoard = new GameBoard(warrior, List.of(boss));
        gameBoard.setOccupant(warrior.getPosition(), warrior);
        gameBoard.setOccupant(boss.getPosition(), boss);
        warrior.setGameBoard(gameBoard);
        boss.setGameBoard(gameBoard);

        boss.onEnemyTurn();

        warrior.setPosition(new Position(9, 9));
        gameBoard.setOccupant(warrior.getPosition(), warrior);

        boss.onEnemyTurn();

        assertTrue(boss.description().contains("Ticks: 0/5"));
    }

    @Test
    public void testHunterArrowsRefreshOnGameTick() {
        gameBoard = new GameBoard(hunter, new ArrayList<>());
        gameBoard.setOccupant(hunter.getPosition(), hunter);
        hunter.setGameBoard(gameBoard);

        for (int i = 0; i < 10; i++) {
            hunter.castSpecialAbility();
        }

        for (int i = 0; i < 10; i++) {
            hunter.onGameTick();
        }

        assertTrue(hunter.description().contains("Arrows: 1"));
    }
}