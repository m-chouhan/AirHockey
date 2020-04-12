package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import com.sun.istack.internal.Nullable;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.Optional;

public class Circle implements SFSInterface {

    Vec2 position = new Vec2();
    Vec2 velocity = new Vec2();
    float radius, mass;
    BodyType bodyType;

    Body body;

    public Circle(World world, float radius, float x, float y, float mass, BodyType bodyType) {
        this.radius = radius;
        position.set(x,y);
        this.bodyType = bodyType;
        this.mass = mass;

        // Dynamic Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        CircleShape cs = new CircleShape();
        cs.m_radius = radius;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = cs;
        fixtureDef.density = 1;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 1f;
        body.createFixture(fixtureDef);
    }

    public Circle(World world, float radius, float x, float y, float mass) {
        this(world, radius, x, y, mass, BodyType.DYNAMIC);
    }

    @Override
    public void fromSfs(SFSObject sfsObject) {
        Vec2 position = new Vec2(sfsObject.getFloat("x"), sfsObject.getFloat("y"));
        this.position.set(position);
    }

    @Override
    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        Vec2 position = getPosition();
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }

    public Vec2 getPosition() { return body.getPosition(); }

    public void setPosition(Vec2 pos) { getPosition().set(pos); }

    public void setPosition(float x, float y) { getPosition().set(x, y); }

    public float getRadius() { return radius; }

    @Override
    public String toString() { return getPosition().toString(); }

    public void setVelocity(float x, float y) { body.getLinearVelocity().set(x, y); }

    public Vec2 getVelocity() { return body.getLinearVelocity(); }

    public BodyType getBodyType() { return body.m_type; }

    public float getMass() { return body.getMass(); }

    public void setVelocity(Vec2 vB) { setVelocity(vB.x, vB.y); }
}
