package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.jbox2d.common.Vec2;

public class Player implements SFSInterface {

    Vec2 position = new Vec2();
    Vec2 velocity = new Vec2();

    int id;
    int score;

    public Player(int id, float x, float y, int score) {
        this.id = id;
        position.x = x;
        position.y = y;
        this.score = score;
    }

    @Override
    public void fromSfs(SFSObject sfsObject) {
        this.id = sfsObject.getInt("id");
        position.x = sfsObject.getFloat("x");
        position.y = sfsObject.getFloat("y");
        this.score = sfsObject.getInt("score");
    }

    @Override
    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt("id", id);
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }
}
