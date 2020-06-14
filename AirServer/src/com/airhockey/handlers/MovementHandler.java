package com.airhockey.handlers;

import com.airhockey.AirHockeyRoomExtension;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class MovementHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        AirHockeyRoomExtension extension = (AirHockeyRoomExtension) getParentExtension();
        extension
                .getGame()
                .getState()
                .updateState(user.getId(), isfsObject.getFloat("x"), isfsObject.getFloat("y"));
    }
}
