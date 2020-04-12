package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import com.sun.istack.internal.Nullable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import java.util.Optional;

public class Player extends Circle {

    int id;
    int score;

    public Player(int id, float radius, float x, float y, int score) {
        super(radius, x, y, 10, BodyType.STATIC);
        this.id = id;
        this.score = score;
    }

    @Override
    public void fromSfs(SFSObject sfsObject) {
        super.fromSfs(sfsObject);
        this.id = sfsObject.getInt("id");
        this.score = sfsObject.getInt("score");
    }

    @Override
    public SFSObject toSfs() {
        SFSObject sfsObject = super.toSfs();
        sfsObject.putInt("id", id);
        return sfsObject;
    }

    @Override
    public String toString() {
        return "{ id " + id +", "+ getPosition().toString() + ", " + score + " }";
    }

    public int getId() { return id; }
}
