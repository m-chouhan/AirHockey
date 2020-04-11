package com.airhockey.handlers;

import com.airhockey.AirHockeyExtension;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class ReadyHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        trace("Player : " + user.getId() + " in Ready Handler");
        AirHockeyExtension airHockeyExtension = (AirHockeyExtension) getParentExtension();
        if(airHockeyExtension.getParentRoom().getUserList().size() == 1)
            airHockeyExtension.startGame();
    }
}