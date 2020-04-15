package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;

public class Player implements NetworkInterface {

    public Body master, slave;
    public final int id;
    public int score;
    public Player(int id) { this.id = id; }

    public void setPosition(float x, float y) {
        master.getTransform().setTranslationX(x);
        master.getTransform().setTranslationY(y);
    }

    @Override
    public void fromNetworkObj(SFSObject sfsObject) { }

    @Override
    public SFSObject toNetworkObj() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt("id", id);
        sfsObject.putFloat("x", (float) master.getTransform().getTranslationX());
        sfsObject.putFloat("y", (float) master.getTransform().getTranslationY());
        sfsObject.putInt("score", score);
        return sfsObject;
    }

    @Override
    public String toString() {
        return "{ id=" + id +", ("+ master.getTransform().getTranslationX() + ","+ master.getTransform().getTranslationY() + "), " + score + " }";
    }

    public void updateScore() {
        score++;
    }
}
