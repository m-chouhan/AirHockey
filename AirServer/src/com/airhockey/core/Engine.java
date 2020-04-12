package com.airhockey.core;

import com.airhockey.entities.Circle;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.entities.SFSInterface;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import sfs2x.extensions.games.spacewar.core.Game;

import java.util.List;

public class Engine extends World {

    final float width,height;
    final Circle pA, pB, puck;
    final SFSExtension EXT;
    public Engine(SFSExtension extension, Core game, float width, float height) {
        super(new Vec2());
        EXT = extension;
        puck = game.getState().puck;
        pA = game.getState().player1;
        pB = game.getState().player2;
        this.width = width;
        this.height = height;
    }

    @Override
    public Body createBody(BodyDef def) {
        return super.createBody(def);
    }

    @Override
    public void step(float dt, int velocityIterations, int positionIterations) {
        move(puck, dt);
        move(pA, dt);
        move(pB, dt);
        resolveCollision(pA, puck, dt);
        resolveCollision(pB, puck, dt);
    }

    private void move(Circle puck, float dt) {
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
        puck.setPosition(nextPosition);
    }

    private void resolveCollision(Circle cA, Circle cB, float dt) {
        Vec2 diffAB = cA.getPosition().sub(cB.getPosition());
        float distSq = diffAB.x*diffAB.x + diffAB.y*diffAB.y;
        float radiusSq = (cA.getRadius() + cB.getRadius())*(cA.getRadius() + cB.getRadius());
        if(distSq >= radiusSq) return;

        /*
        float cx1 = cA.getPosition().x, cy1 = cA.getPosition().y;
        float cx2 = cB.getPosition().x, cy2 = cB.getPosition().y;
        float m1 = cA.getMass(), m2 = cB.getMass();
        float vx1 = cA.getVelocity().x, vy1 = cA.getVelocity().y;
        float vx2 = cB.getVelocity().x, vy2 = cB.getVelocity().y;
        double d = Math.sqrt(Math.pow(cx1 - cx2, 2) + Math.pow(cy1 - cy2, 2));
        double nx = (cx2 - cx1) / d;
        double ny = (cy2 - cy1) / d;
        double p = 2 * (vx1 * nx + vy1 * ny - vx2 * nx - vy2 * ny) / (m1 + m2);
        float ux1 = (float) (vx1 - p * m1 * nx);
        float uy1 = (float) (vy1 - p * m1 * ny);
        float ux2 = (float) (vx2 + p * m2 * nx);
        float uy2 = (float) (vy2 + p * m2 * ny);
        cA.setVelocity(ux1, uy1);
        cB.setVelocity(ux2, uy2);
        EXT.trace("d="+ d + ", nx = " + nx + ", ny = " + ny + ", p = " + p + ", m1 + m2 = " + (m1 + m2));

        /**/
        /*
        float ux1 = (vx1 * (m1 - m2) + (2 * m2 * vx2)) / (m1 + m2);
        float uy1 = (vy1 * (m1 - m2) + (2 * m2 * vy2)) / (m1 + m2);
        float ux2 = (vx2 * (m2 - m1) + (2 * m1 * vx1)) / (m1 + m2);
        float uy2 = (vy2 * (m2 - m1) + (2 * m1 * vy1)) / (m1 + m2);
        */

        float dist = (float) Math.sqrt(distSq);
        Vec2 normalizedVec = diffAB.mul(1/dist);
        float pValue = 2* (Vec2.dot(cA.getVelocity(), normalizedVec) - Vec2.dot(cB.getVelocity(), normalizedVec)) /
                            (cA.getMass() + cB.getMass());

        Vec2 vA = cA.getVelocity().sub(normalizedVec.mul(pValue*  cB.getMass()));
        Vec2 vB = cB.getVelocity().add(normalizedVec.mul(pValue * cA.getMass()));
        cA.setVelocity(vA);cB.setVelocity(vB);
        /*
        */
        move(cA, dt*2);
        move(cB, dt*2);
    }
}
