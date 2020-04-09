package com.airhockey;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class ReadyHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        trace("in Ready Handler");
        AirHockeyExtension airHockeyExtension = (AirHockeyExtension) getParentExtension();
        if(airHockeyExtension.getParentRoom().getUserList().size() == 2)
            airHockeyExtension.startGame();
    }
}