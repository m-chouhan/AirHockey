package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import sfs2x.extensions.games.spacewar.core.Game;

import java.util.List;

public class Engine extends World {

    final float width,height;
    final Core game;

    public Engine(Core game, float width, float height) {
        super(new Vec2());
        this.game = game;
        this.width = width;
        this.height = height;
    }

    @Override
    public Body createBody(BodyDef def) {
        return super.createBody(def);
    }

    @Override
    public void step(float dt, int velocityIterations, int positionIterations) {
        Circle puck = game.getState().puck;
        Vec2 position = puck.getPosition();
        Vec2 velocity = puck.getVelocity();
        Vec2 nextPosition = position.add(velocity.mul(dt));
        if(nextPosition.x < -width) {
            nextPosition.x = -width;
            velocity.x = -velocity.x;
        }
        if(nextPosition.x > width) {
            nextPosition.x = width;
            velocity.x = -velocity.x;
        }
        if(nextPosition.y < -height) {
            nextPosition.y = -height;
            velocity.y = -velocity.y;
        }
        if(nextPosition.y > height) {
            nextPosition.y = height;
            velocity.y = -velocity.y;
        }
        position.set(nextPosition);
    }
}
