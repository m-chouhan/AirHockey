package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Represents whole game
 * basically our Game is F of (class Game)
 */
public class GameState implements SFSInterface {
    Player player1, player2;
    Puck puck;

    public GameState(Player p1, Player p2, Puck puck) {
        this.player1 = p1;
        this.player2 = p2;
        this.puck = puck;
    }

    @Override
    public void fromSfs(SFSObject sfsObject) {}

    @Override
    public SFSObject toSfs() {
        SFSObject out = new SFSObject();
        out.putSFSObject(String.valueOf(player1.id), player1.toSfs());
        out.putSFSObject(String.valueOf(player2.id), player2.toSfs());
        out.putSFSObject("puck", puck.toSfs());
        return out;
    }
}
