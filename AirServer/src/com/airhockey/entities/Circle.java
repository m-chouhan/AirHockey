package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import com.sun.istack.internal.Nullable;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.Optional;

public class Circle implements SFSInterface {

    Vec2 position = new Vec2();
    Vec2 velocity = new Vec2();
    float radius, mass;
    BodyType bodyType;

    public Circle(float radius, float x, float y, float mass, BodyType bodyType) {
        this.radius = radius;
        position.set(x,y);
        this.bodyType = bodyType;
        this.mass = mass;
    }

    public Circle(float radius, float x, float y, float mass) {
        this(radius, x, y, mass, BodyType.DYNAMIC);
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

    public Vec2 getPosition() { return position; }

    public void setPosition(Vec2 pos) { position.set(pos); }

    public void setPosition(float x, float y) { getPosition().set(x, y); }

    public float getRadius() { return radius; }

    @Override
    public String toString() { return getPosition().toString(); }

    public void setVelocity(float x, float y) { velocity.set(x, y); }

    public Vec2 getVelocity() { return velocity; }

    public BodyType getBodyType() { return bodyType; }

    public float getMass() { return mass; }

    public void setVelocity(Vec2 vB) {
        velocity.set(vB);
    }
}
