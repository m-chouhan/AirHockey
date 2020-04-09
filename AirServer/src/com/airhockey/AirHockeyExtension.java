package com.airhockey;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class AirHockeyExtension extends SFSExtension {

    @Override
    public void init() {
        trace("mahendra in init");
        this.addRequestHandler("ready", MathHandler.class);
    }

    void startGame() {
        ISFSObject resObj = new SFSObject();
        resObj.putInt("sum", 100);
        send("start", resObj, getParentRoom().getUserList());
    }

}
