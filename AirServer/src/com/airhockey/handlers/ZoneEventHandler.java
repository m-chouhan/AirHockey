package com.airhockey.handlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.IServerEventHandler;

public class ZoneEventHandler extends BaseClientRequestHandler implements IServerEventHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        trace("[ZoneEventHandler] User " + user.getName() + " has joined zone");
    }
    //8971051900
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        trace("[ZoneEventHandler] " + isfsEvent.toString());
    }
}
