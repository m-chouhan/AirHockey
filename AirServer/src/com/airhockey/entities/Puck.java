package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.jbox2d.common.Vec2;

public class Puck implements SFSInterface {
    Vec2 position = new Vec2();
    Vec2 velocity = new Vec2();

    public Puck(float x, float y) {
        position.x = x;
        position.y = y;
    }

    @Override
    public void fromSfs(SFSObject sfsObject) {
        position.x = sfsObject.getFloat("x");
        position.y = sfsObject.getFloat("y");
    }

    @Override
    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }

}
