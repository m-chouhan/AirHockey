package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

public class Player {
    float x;
    float y;
    int id;
    int score;

    public Player(int id, float x, float y, int score) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.score = score;
    }

    public void fromSfs(SFSObject sfsObject) {
        this.id = sfsObject.getInt("id");
        this.x = sfsObject.getFloat("x");
        this.y = sfsObject.getFloat("y");
        this.score = sfsObject.getInt("score");
    }

    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt("id", id);
        sfsObject.putFloat("x", x);
        sfsObject.putFloat("y", y);
        return sfsObject;
    }
}
