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

    public Player(World world, int id, float radius, float x, float y, int score) {
        super(world, radius, x, y, 10, BodyType.STATIC);
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
    public void setPosition(float x, float y) {
        //setVelocity(x - position.x, y - position.y);
        super.setPosition(x, y);
    }

    @Override
    public void setPosition(Vec2 pos) {
        //setVelocity(pos.sub(position));
        super.setPosition(pos);
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
