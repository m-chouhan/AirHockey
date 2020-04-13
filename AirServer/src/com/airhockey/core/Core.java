package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

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
    final int Width = 4, Height = 2;

    public Core(SFSExtension ext, User user1, User user2) {
        ext.trace("core game init!!");
        engine = new Engine(ext , Width, Height);
        Player player1 = new Player(engine, user1.getId(), 0.1f, -2, 0,  0);
        Player player2 = new Player(engine, 100, 0.1f,2, 0, 0);
        Circle puck = new Circle(engine, 0.01f,0,0, 1);
        puck.setVelocity(2,5);
        creatBoundry(engine, -(Width+1), 0, 1, Height*2);
        creatBoundry(engine, Width+1, 0, 1, Height*2);
        creatBoundry(engine, 0, -(Height+1), Width*2, 1);
        creatBoundry(engine, 0, Height+1, Width*2, 1);

        //puck.setVelocity(1f, 0.05f);

        state = new GameState(player1, player2, puck);
        EXT = ext;
        engine
            .addObject(player1)
            .addObject(player2)
            .addObject(puck);
    }

    public void creatBoundry(World world, float x, float y, float width, float height) {
        // Static Body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(x, y);
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width/2, height/2);
        FixtureDef groundfixture = new FixtureDef();
        groundfixture.shape = groundBox;
        groundfixture.density = 10;
        groundfixture.friction = 0.2f;
        groundfixture.restitution = 0.9f;
        groundBody.createFixture(groundfixture);
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
        //player.setPosition(isfsObject.getFloat("x"), isfsObject.getFloat("y"));
    }
}
