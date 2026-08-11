package org.example;

import org.example.game.Game;

public class Main {
    void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide levels directory path.");
        } else {
            try {
                Game game = new Game(args[0]);
                game.run();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
