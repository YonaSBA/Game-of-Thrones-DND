# Game of Thrones 2D Dungeon Crawler 🐺⚔️

## Overview
A tile-based, 2D RPG dungeon crawler inspired by the Game of Thrones universe, developed entirely in Java. This project features diverse character classes, strategic combat mechanics, and dynamic resource management. It was built with a strong emphasis on clean object-oriented design and scalable architecture.

## ✨ Core Features
*   **Diverse Character Classes:** Choose from unique classes, each with distinct abilities, strengths, and playstyles tailored to the lore.
*   **Dynamic Resource Management:** Strategic gameplay requiring players to carefully manage their Health, Mana, and Energy pools during exploration and combat.
*   **Smart Enemy:** Enemies utilize intelligent pathfinding algorithms based on Euclidean distance for precise target tracking and movement across the grid.
*   **Tile-Based Combat:** Tactical, turn-based (or tick-based) combat mechanics resolved on a 2D grid.

## 🏗️ Architecture & Design Patterns
The game engine is engineered with a scalable architecture, heavily utilizing industry-standard design patterns to maintain clean code and separation of concerns:
*   **Visitor Pattern:** Implemented for robust combat and movement resolution. This allows the game to cleanly determine interactions between different board entities (e.g., Player vs. Enemy, Player vs. Wall) without relying on messy instanceof checks.
*   **Observer Pattern:** Utilized to achieve seamless decoupling between the User Interface (UI) and the underlying business logic. The UI acts as an observer that automatically reacts and updates whenever the game state changes.