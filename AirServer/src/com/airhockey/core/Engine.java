package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.entities.SFSInterface;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import sfs2x.extensions.games.spacewar.core.Game;

import java.util.ArrayList;
import java.util.List;

public class Engine extends World {

    final float width,height;
    final ArrayList<Circle> objList = new ArrayList<>();

    final SFSExtension EXT;
    public Engine(SFSExtension extension, float width, float height) {
        super(new Vec2());
        EXT = extension;
        this.width = width;
        this.height = height;
    }

    Engine addObject(Circle obj) {
        objList.add(obj);
        return this;
    }

    @Override
    public Body createBody(BodyDef def) {
        return super.createBody(def);
    }

    @Override
    public void step(float dt, int velocityIterations, int positionIterations) {
        for(Circle circle : objList)
        {
            move(circle, dt);
            for(Circle other : objList)
                if(other != circle)
                    resolveCollision(circle, other, dt);
        }
    }

    private void move(Circle circle, float dt) {
        //we will never move static objects
        if(circle.getBodyType() == BodyType.STATIC) return;

        Vec2 position = circle.getPosition();
        Vec2 velocity = circle.getVelocity();
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
        circle.setPosition(nextPosition);
    }

    private void resolveCollision(Circle cA, Circle cB, float dt) {
        Vec2 diffAB = cA.getPosition().sub(cB.getPosition());
        float distSq = diffAB.x*diffAB.x + diffAB.y*diffAB.y;
        float radiusSq = (cA.getRadius() + cB.getRadius())*(cA.getRadius() + cB.getRadius());
        if(distSq >= radiusSq) return;

        float dist = (float) Math.sqrt(distSq);
        Vec2 normalizedVec = diffAB.mul(1/dist);
        float pValue = 2* (Vec2.dot(cA.getVelocity(), normalizedVec) - Vec2.dot(cB.getVelocity(), normalizedVec)) /
                            (cA.getMass() + cB.getMass());

        Vec2 vA = cA.getVelocity().sub(normalizedVec.mul(pValue*  cB.getMass()));
        Vec2 vB = cB.getVelocity().add(normalizedVec.mul(pValue * cA.getMass()));
        if(cA.getBodyType() != BodyType.STATIC)
            cA.setVelocity(vA);
        if(cB.getBodyType() != BodyType.STATIC)
            cB.setVelocity(vB);
        move(cA, dt*1.5f);
        move(cB, dt*1.5f);
    }
}
