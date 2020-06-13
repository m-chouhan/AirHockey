package com.airhockey.handlers;

import com.airhockey.AirHockeyRoomExtension;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class ReadyHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        AirHockeyRoomExtension airHockeyRoomExtension = (AirHockeyRoomExtension) getParentExtension();
        trace("[ReadyHandler] Player : " + user.getId()
                + " parentRoomName : " + airHockeyRoomExtension.getParentRoom().getName()
                + " lastJoinedroom : " + user.getLastJoinedRoom().getName());
        if(airHockeyRoomExtension.getParentRoom().getUserList().size() == 2)
            airHockeyRoomExtension.startGame();
    }
}