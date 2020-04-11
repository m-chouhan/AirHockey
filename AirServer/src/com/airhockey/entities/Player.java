package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

public class Player {
    Vector2D position = new Vector2D();
    Vector2D velocity = new Vector2D();

    int id;
    int score;

    public Player(int id, float x, float y, int score) {
        this.id = id;
        position.x = x;
        position.y = y;
        this.score = score;
    }

    public void fromSfs(SFSObject sfsObject) {
        this.id = sfsObject.getInt("id");
        position.x = sfsObject.getFloat("x");
        position.y = sfsObject.getFloat("y");
        this.score = sfsObject.getInt("score");
    }

    public SFSObject toSfs() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt("id", id);
        sfsObject.putFloat("x", position.x);
        sfsObject.putFloat("y", position.y);
        return sfsObject;
    }
}