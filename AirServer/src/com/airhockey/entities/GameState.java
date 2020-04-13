package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.dyn4j.dynamics.Body;

/**
 * Represents whole game
 * basically our Game is F of (class Game)
 */
public class GameState implements NetworkInterface {
    public Player player1, player2;
    public Body puck;

    public GameState(Player p1, Player p2, Body puck) {
        this.player1 = p1;
        this.player2 = p2;
        this.puck = puck;
    }

    @Override
    public void fromNetworkObj(SFSObject sfsObject) {}

    @Override
    public SFSObject toNetworkObj() {
        SFSObject out = new SFSObject();
        out.putSFSObject(String.valueOf(player1.id), player1.toNetworkObj());
        out.putSFSObject(String.valueOf(player2.id), player2.toNetworkObj());
        SFSObject p = new SFSObject();
        p.putFloat("x", (float) puck.getTransform().getTranslationX());
        p.putFloat("y", (float) puck.getTransform().getTranslationY());
        out.putSFSObject("puck", p);
        return out;
    }

    @Override
    public String toString() {
        return "P1 : " + player1.toString() + ", P2 : " + player2.toString() + ", Puck : " + puck.getTransform().toString();
    }

    public void updateState(int id, float x, float y) {
        Player player = player1.id == id ? player1 : player2;
        player.setPosition(x,y);
    }
}
