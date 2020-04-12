package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

/**
 * Core game logic
 */

public class Core implements Runnable {

    private final GameState state;
    final Engine engine;

    //game constants
    final float TimeStep = 0.02f;
    final int VelocityIterations = 10, PositionIterations = 8;
    final SFSExtension EXT;

    public Core(SFSExtension ext, User user1, User user2) {
        ext.trace("core game init!!");
        Player player1 = new Player(user1.getId(), 0.12f, -1, 0,  0);
        Player player2 = new Player( 100, 0.12f,1, 0, 0);
        Circle puck = new Circle(0.15f,-0.5f,0, 1);
        puck.setVelocity(1f, 0.05f);
        state = new GameState(player1, player2, puck);
        EXT = ext;
        engine = new Engine(ext ,  1.5f,0.6f);
        engine
            .addObject(player1)
            .addObject(player2)
            .addObject(puck);
    }

    @Override
    public void run() {
        engine.step(TimeStep, VelocityIterations, PositionIterations);
        EXT.trace(state.toString());
        EXT.send("move", state.toSfs(), EXT.getParentRoom().getUserList());
    }

    public GameState getState() {
        return state;
    }

    public void updatePlayer(int id, ISFSObject isfsObject) {
        Player player = state.player1.getId() == id ? state.player1 : state.player2;
        player.setPosition(isfsObject.getFloat("x"), isfsObject.getFloat("y"));
    }
}
