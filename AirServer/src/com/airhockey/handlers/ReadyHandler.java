package com.airhockey.handlers;

import com.airhockey.AirHockeyRoomExtension;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class ReadyHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        AirHockeyRoomExtension airHockeyRoomExtension = (AirHockeyRoomExtension) getParentExtension();
        Room parentRoom = airHockeyRoomExtension.getParentRoom();
        Room lastJoinedRoom = user.getLastJoinedRoom();
        trace("[ReadyHandler] Player : " + user.getId()
                + " parentRoomName : " + parentRoom.getName() +","+ parentRoom.getSize()
                + " lastJoinedroom : " + lastJoinedRoom.getName() + "," + lastJoinedRoom.getSize());
        if(lastJoinedRoom.getSize().getUserCount() == 2)
            airHockeyRoomExtension
                    .startGame(lastJoinedRoom);
    }
}