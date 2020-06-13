package com.airhockey.handlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.IServerEventHandler;

public class RoomEventHandler extends BaseClientRequestHandler implements IServerEventHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        //trace("[RoomEventHandler- handleClient] User " + user.getName() + " joined room ");
    }

    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        trace("[RoomEventHandler] " + isfsEvent.toString());
        switch (isfsEvent.getType()) {
            case USER_JOIN_ROOM:
                break;
            case USER_LEAVE_ROOM:
                break;
            case USER_DISCONNECT:
                break;
        }
    }
}
