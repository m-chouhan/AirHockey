package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.dyn4j.dynamics.Body;

/**
 * Represents whole game
 * basically our Game is F of (class Game)
 */
public class GameState implements NetworkInterface {
    public enum State {WAITING, RUNNING, PAUSE, FINISHED};

    public Player player1, player2;
    public Puck puck;

    public GameState(Player p1, Player p2, Puck puck) {
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
        SFSObject puck = new SFSObject();
        puck.putFloat("x", (float) this.puck.getTransform().getTranslationX());
        puck.putFloat("y", (float) this.puck.getTransform().getTranslationY());
        out.putSFSObject("puck", puck);
        return out;
    }

    @Override
    public String toString() {
        return "P1 : " + player1.toString() + ", P2 : " + player2.toString() + ", Puck : ("
                + puck.getTransform().getTranslationX() + ", " + puck.getTransform().getTranslationY() + ")";
    }

    public void updateState(int id, float x, float y) {
        Player player = player1.id == id ? player1 : player2;
        player.setPosition(x, y);
    }
}
