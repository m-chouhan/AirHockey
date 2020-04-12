package com.airhockey.core;

import com.airhockey.entities.Boundary;
import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Core game logic
 */

public class Core implements Runnable {

    private final GameState state;
    final World myWorld;

    //game constants
    final float TimeStep = 0.1f;
    final int VelocityIterations = 10, PositionIterations = 8;
    final SFSExtension EXT;

    public Core(SFSExtension ext, User user1, User user2) {
        ext.trace("core game init!!");
        Vec2 gravity = new Vec2(0,0);
        myWorld = new Engine(gravity, 1.5f,0.6f);
        Player player1 = new Player(myWorld, user1.getId(), 0.2f, -1, 0,  0);
        Player player2 = new Player(myWorld, 100, 0.2f,1, 0, 0);
        Circle puck = new Circle(myWorld,0.2f,-0.5f,0);
        //Boundary boundary = new Boundary(myWorld, 0, -1, 10, 1);
        puck.setVelocity(1f, 0.2f);
        state = new GameState(player1, player2, puck);
        EXT = ext;
    }

    @Override
    public void run() {
        myWorld.step(TimeStep, VelocityIterations, PositionIterations);
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
