package org.example.occupant;

import org.example.enemy.Enemy;

import org.example.player.Player;

public interface OccupantVisitor {
    void visit(Enemy enemy);
    void visit(Player player);
}
