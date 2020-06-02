package com.airhockey.core;

import com.airhockey.entities.Player;
import com.airhockey.entities.Puck;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

public class Builder {

    private static float scale = 1f;

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

        return world;
    }

    public static void incarnate(Player player, int x, int y,  World world) {

        // player control setup
        Body master = new Body();
        Circle fixture = Geometry.createCircle(0.02);
        master.addFixture(fixture);
        master.setAngularDamping(1);
        master.setMass(MassType.INFINITE);
        master.setAutoSleepingEnabled(false);
        world.addBody(master);

        Body slave = new Body();
        slave.addFixture(Geometry.createCircle(scale));
        slave.setMass(MassType.NORMAL);
        slave.setAngularDamping(1);
        slave.setAutoSleepingEnabled(false);
        world.addBody(slave);

        MotorJoint motorJoint = new MotorJoint(slave, master);
        motorJoint.setCollisionAllowed(false);
        motorJoint.setMaximumForce(2000.0);
        motorJoint.setMaximumTorque(0.0);
        world.addJoint(motorJoint);

        master.translate(x, y);
        player.master = master;
        player.slave = slave;
    }

    public static Puck createPuck(int x, int y, World world) {

        Puck circle = new Puck();
        circle.addFixture(Geometry.createCircle(scale));
        circle.setMass(MassType.NORMAL);
        circle.setAngularDamping(1);
        circle.setAutoSleepingEnabled(false);
        circle.translate(x,y);
        world.addBody(circle);
        return circle;
    }
}
