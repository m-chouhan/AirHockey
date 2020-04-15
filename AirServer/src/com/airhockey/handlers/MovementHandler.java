package com.airhockey.handlers;

import com.airhockey.AirHockeyExtension;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class MovementHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        AirHockeyExtension extension = (AirHockeyExtension) getParentExtension();
        extension
                .getGame()
                .getState()
                .updateState(user.getId(), isfsObject.getFloat("x"), isfsObject.getFloat("y"));
    }
}
