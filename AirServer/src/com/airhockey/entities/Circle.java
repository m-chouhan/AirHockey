package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Circle implements SFSInterface {

    /*
    Body myBody;
    //TODO : remove this later
    public Circle(World world, float radius, float x, float y) {
        // Step 1. Define the body.
        BodyDef bd = new BodyDef();
        bd.position.set(x,y);
        bd.type = BodyType.DYNAMIC;

        // Step 2. Create the body.
        myBody = world.createBody(bd);

        // Step 3. Define the shape.
        CircleShape cs = new CircleShape();
        cs.m_radius = radius;
        // Step 4. Define the fixture.
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 1;
        fd.friction = 0.0f;
        fd.restitution = 1f;
        // Step 5. Attach the shape to the body with the Fixture.
        myBody.createFixture(fd);
    }
    */

    Vec2 position = new Vec2();
    Vec2 velocity = new Vec2();
    float radius;

    public Circle(float radius, float x, float y) {
        this.radius = radius;
        position.set(x,y);
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

    public void setPosition(float x, float y) { getPosition().set(x, y); }

    public float getRadius() { return radius; }

    @Override
    public String toString() { return getPosition().toString(); }

    public void setVelocity(float x, float y) { velocity.set(x, y); }

    public Vec2 getVelocity() { return velocity; }
}
