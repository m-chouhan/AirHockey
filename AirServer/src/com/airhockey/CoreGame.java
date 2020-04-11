package com.airhockey;

import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.entities.Puck;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Core game logic
 */

public class CoreGame implements Runnable {

    private GameState state;

    World myWorld;
    private Body bodyd, bodys, bodyk;
    float timeStep = 1/60f;
    int velocityIterations = 10, positionIterations = 8;
    final SFSExtension EXT;

    public CoreGame(SFSExtension ext, User user1, User user2) {
        this.EXT = ext;
        setupBox2dWorld();
        Player player1 = new Player(user1.getId(), -1, 0, 0);
        Player player2 = new Player(100, 1, 0, 0);
        Puck puck = new Puck(0,0);
        state = new GameState(player1, player2, puck);

        ext.trace("core game init!!");
    }

    void setupBox2dWorld() {
        Vec2 gravity = new Vec2(0.1f,0);
        myWorld = new World(gravity);
        // Step 1. Define the body.
        BodyDef bd = new BodyDef();
        bd.position.set(0,0);
        bd.type = BodyType.DYNAMIC;
        // Step 2. Create the body.
        bodyk = myWorld.createBody(bd);

        // Step 3. Define the shape.
        CircleShape cs = new CircleShape();
        cs.m_radius = 2;
        // Step 4. Define the fixture.
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 1;
        fd.friction = 0.3f;
        fd.restitution = 0.5f;
        // Step 5. Attach the shape to the body with the Fixture.
        bodyk.createFixture(fd);
    }

    @Override
    public void run() {
        myWorld.step(timeStep, velocityIterations, positionIterations);
        EXT.trace("Position" + bodyk.getTransform().p);
    }

    public GameState getState() {
        return state;
    }
}
