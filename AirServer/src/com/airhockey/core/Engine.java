package com.airhockey.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import java.util.List;

public class Engine extends World {

    final float width,height;

    public Engine(Vec2 gravity, float width, float height) {
        super(gravity);
        this.width = width;
        this.height = height;
    }

    @Override
    public Body createBody(BodyDef def) {
        return super.createBody(def);
    }

    @Override
    public void step(float dt, int velocityIterations, int positionIterations) {
        Body body = getBodyList();
        while(body != null) {
            Vec2 position = body.getPosition();
            Vec2 velocity = body.getLinearVelocity();
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
            body.getPosition().set(nextPosition);
            body = body.m_next;
        }
    }
}
