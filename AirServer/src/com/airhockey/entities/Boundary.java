package com.airhockey.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Boundary {
    // A boundary is a simple rectangle with x,y,width,and height
    float x;
    float y;
    float w;
    float h;
    // But we also have to make a body for box2d to know about it
    Body b;

    public Boundary(World world, float x_, float y_, float w_, float h_) {
        x = x_;
        y = y_;
        w = w_;
        h = h_;
        // Define the polygon
        PolygonShape sd = new PolygonShape();
        // Figure out the box2d coordinates
        float box2dW = w/2;
        float box2dH = h/2;
        // We're just a box
        sd.setAsBox(box2dW, box2dH);

        // Create the body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(x,y);
        b = world.createBody(bd);
        // Attached the shape to the body using a Fixture
        b.createFixture(sd,1);
    }
}
