package com.airhockey;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Core game logic
 */

public class CoreGame implements Runnable {

    private GameState state;

    final World myWorld;
    //game constants
    final float TimeStep = 1/60f;
    final int VelocityIterations = 10, PositionIterations = 8;
    final SFSExtension EXT;

    public CoreGame(SFSExtension ext, User user1, User user2) {
        ext.trace("core game init!!");
        Vec2 gravity = new Vec2(0.1f,0);
        myWorld = new World(gravity);
        Player player1 = new Player(myWorld, user1.getId(), 0.1f, -1, 0,  0);
        Player player2 = new Player(myWorld, 100, 0.1f,1, 0, 0);
        Circle puck = new Circle(myWorld,0.1f,0,0);
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
}
