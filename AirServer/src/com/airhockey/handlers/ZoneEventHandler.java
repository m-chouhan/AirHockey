package com.airhockey.handlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.ISFSEventParam;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
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
    public void handleServerEvent(ISFSEvent evt) throws SFSException {
        switch (evt.getType()) {
            case USER_JOIN_ZONE:
                User user = (User)evt.getParameter(SFSEventParam.USER);
                Zone zone = (Zone)evt.getParameter(SFSEventParam.ZONE);
                trace("[ZoneEventHandler] " + user.getName() + " joined " + zone.getName());
        }
    }
}
