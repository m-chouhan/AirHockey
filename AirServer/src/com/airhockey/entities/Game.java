package com.airhockey.entities;

/**
 * Represents whole game
 * basically our Game is F of (class Game)
 */
public class Game {
    Player player1, player2;
    Puck puck;

    public Game(Player p1, Player p2, Puck puck) {
        this.player1 = p1;
        this.player2 = p2;
        this.puck = puck;
    }

}
