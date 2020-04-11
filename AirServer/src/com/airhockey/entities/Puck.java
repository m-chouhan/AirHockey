package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

public class Puck {
    Vector2D position = new Vector2D();
    Vector2D velocity = new Vector2D();

    public Puck(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void fromSfs(SFSObject sfsObject) {
        position.x = sfsObject.getFloat("x");
        position.y = sfsObject.getFloat("y");
    }

    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }

}
