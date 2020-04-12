package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Circle implements SFSInterface {

    Body myBody;

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

    @Override
    public void fromSfs(SFSObject sfsObject) {
        Vec2 position = new Vec2(sfsObject.getFloat("x"), sfsObject.getFloat("y"));
        myBody.getTransform().set(position, 0);
    }

    @Override
    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        Vec2 position = myBody.getTransform().p;
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }

    public Vec2 getPosition() { return myBody.getPosition(); }

    public void setPosition(float x, float y) {
        getPosition().set(x, y);
    }

    @Override
    public String toString() {
        return getPosition().toString();
    }

    public void setVelocity(float x, float y) {
        myBody.m_linearVelocity.set(x, y);
    }
}
