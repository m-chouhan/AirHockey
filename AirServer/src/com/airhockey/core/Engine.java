package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.entities.SFSInterface;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class Engine extends World {

    final float width,height;
    final ArrayList<Circle> objList = new ArrayList<>();

    final SFSExtension EXT;
    public Engine(SFSExtension extension, float width, float height) {
        super(new Vec2(0,0));

//        // Setup this
//        float timeStep = 1.0f/60.0f;
//        int velocityIterations = 6;
//        int positionIterations = 2;
//        // Run loop
//        for (int i = 0; i < 120; ++i) {
//            this.step(timeStep, velocityIterations, positionIterations);
//            Vec2 position = body.getPosition();
//            float angle = body.getAngle();
//            EXT.trace(position.toString() +", " + angle);
//        }

        EXT = extension;
        this.width = width;
        this.height = height;
    }

    Engine addObject(Circle obj) {
        objList.add(obj);
        return this;
    }

    @Override
    public void step(float dt, int velocityIterations, int positionIterations) {
        super.step(dt, velocityIterations, positionIterations);
        /*
        for(Circle circle : objList)
        {
            move(circle, dt);
            for(Circle other : objList)
                if(other != circle)
                    resolveCollision(circle, other, dt);
        }
        */
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
        if(cA.getBodyType() == BodyType.STATIC) {
            cB.setVelocity(vB.mul(1.15f));
        }
        else if(cB.getBodyType() == BodyType.STATIC) {
            cA.setVelocity(vA.mul(1.15f));
        }
        else {
            cA.setVelocity(vA);
            cB.setVelocity(vB);
        }
        separate(cA, cB);
        //move(cA, dt*1.5f);
        //move(cB, dt*1.5f);
    }

    private void separate(Circle cA, Circle cB) {
        Vec2 diffAB = cA.getPosition().sub(cB.getPosition());
        float currentDist = diffAB.length();
        float expectedDist = (cA.getRadius() + cB.getRadius());
        float expMag = (expectedDist - currentDist)*1.15f;
        diffAB.normalize();

        /*
        EXT.trace(diffAB);
        EXT.trace("current dist " + currentDist + ", expectedDist " + expectedDist + ", ratio " + ratio + " normalize " + diffAB.normalize());
        EXT.trace(diffAB.normalize());
        */

        if(cA.getBodyType() == BodyType.STATIC)
            cB.setPosition(cB.getPosition().add(diffAB.mul(-expMag)));
        if(cB.getBodyType() == BodyType.STATIC)
            cA.setPosition(cA.getPosition().add(diffAB.mul(expMag)));
    }
}
