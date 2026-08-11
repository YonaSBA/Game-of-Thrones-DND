package org.example.ui;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private final Scanner scanner;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    public int chooseCharacter(List<String> characters) {
        System.out.println("\nCharacters:");
        for  (int i = 0; i < characters.size(); i++) {
            System.out.printf("\t%d. %s\n", i + 1, characters.get(i));
        }

        System.out.print("Please choose your character: ");
        return scanner.nextInt();
    }

    public char chooseAction() {
        System.out.println("\n================= Action ================");
        System.out.print("Please choose an action (w/a/s/d = Move, e = Ability, q = Rest): ");
        return scanner.next().charAt(0);
    }

    public void displayGameState(String gameBoard, String playerStatistics) {
        System.out.println("\n================ GAME BOARD ================");
        System.out.println(gameBoard);
        System.out.println("================ STATISTICS ================");
        System.out.println(playerStatistics);
    }

    public void printLevel(int level) {
        System.out.printf("\n================= Level %d ================%n", level);
    }

    public void printInvalidChoiceMessage() {
        System.out.println("Invalid choice! Please try again.");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
