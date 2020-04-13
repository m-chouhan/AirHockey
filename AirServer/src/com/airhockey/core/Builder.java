package com.airhockey.core;

import com.airhockey.entities.Player;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

public class Builder {

    public static Body createObstacle(float x, float y, float width, float height) {
        Body wall = new Body();
        wall.addFixture(Geometry.createRectangle(width, height), 1, 0.2, 0.9);
        wall.setAngularDamping(1);
        wall.setMass(MassType.INFINITE);
        wall.translate(x, y);
        return wall;
    }
    /**
     * Setup boundries, gravity etc
     * @param Width
     * @param Height
     * @return new instance with given properties
     */
    public static World setupWorld(int Width, int Height) {
        // no gravity please
        World world = new World();
        world.setGravity(World.ZERO_GRAVITY);

        // obstacles
        world.addBody(
                createObstacle(-Width, 0, 1, 2*Height)
        );
        world.addBody(
                createObstacle(Width, 0, 1, 2*Height)
        );
        world.addBody(
                createObstacle(0, -Height, 2*Width, 1)
        );
        world.addBody(
                createObstacle(0, Height, 2*Width, 1)
        );

//        Body wall1 = new Body();
//        wall1.addFixture(Geometry.createRectangle(1, 10), 1, 0.2, 0.9);
//        wall1.setAngularDamping(1);
//        wall1.setMass(MassType.INFINITE);
//        wall1.translate(4, 0);
//        world.addBody(wall1);
//
//        Body wall2 = new Body();
//        wall2.addFixture(Geometry.createRectangle(1, 10), 1, 0.2, 0.9);
//        wall2.setAngularDamping(1);
//        wall2.setMass(MassType.INFINITE);
//        wall2.translate(-4, 0);
//        world.addBody(wall2);
//
//        Body wall3 = new Body();
//        wall3.addFixture(Geometry.createRectangle(10, 1), 1, 0.2, 0.9);
//        wall3.setMass(MassType.INFINITE);
//        wall3.setAngularDamping(1);
//        wall3.translate(0, 4);
//        world.addBody(wall3);
//
//        Body wall4 = new Body();
//        wall4.addFixture(Geometry.createRectangle(10, 1), 1, 0.2, 0.9);
//        wall4.setMass(MassType.INFINITE);
//        wall4.setAngularDamping(1);
//        wall4.translate(0, -4);
//        world.addBody(wall4);
        return world;
    }

    public static void incarnate(Player player, int x, int y,  World world) {

        // player control setup
        Body master = new Body();
        Circle fixture = Geometry.createCircle(0.1);
        master.addFixture(fixture);
        master.setAngularDamping(1);
        master.setMass(MassType.INFINITE);
        master.setAutoSleepingEnabled(false);
        world.addBody(master);

        Body slave = new Body();
        slave.addFixture(Geometry.createCircle(0.5));
        slave.setMass(MassType.NORMAL);
        slave.setAngularDamping(1);
        slave.setAutoSleepingEnabled(false);
        world.addBody(slave);

        MotorJoint motorJoint = new MotorJoint(slave, master);
        motorJoint.setCollisionAllowed(false);
        motorJoint.setMaximumForce(1000.0);
        motorJoint.setMaximumTorque(0.0);
        world.addJoint(motorJoint);

        master.translate(x, y);
        player.master = master;
        player.slave = slave;
    }

    public static Body createPuck(World world, int x, int y) {

        Body circle = new Body();
        circle.addFixture(Geometry.createCircle(0.5));
        circle.setMass(MassType.NORMAL);
        circle.setAngularDamping(1);
        circle.setAutoSleepingEnabled(false);
        circle.translate(x,y);
        world.addBody(circle);
        return circle;
    }
}
