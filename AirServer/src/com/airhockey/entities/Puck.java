package com.airhockey.entities;


import com.smartfoxserver.v2.entities.data.SFSObject;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

public class Puck extends Body {

    public boolean isDirty() {
        Vector2 velocity = getLinearVelocity();
        return Math.abs(velocity.x) > 0.1f || Math.abs(velocity.y) > 0.1f;
    }
}
