package com.airhockey;

import com.airhockey.handlers.RoomEventHandler;
import com.airhockey.handlers.ZoneEventHandler;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class AirHockeyZoneExtension extends SFSExtension {

    @Override
    public void init() {
        trace("[AirHockeyZoneExt] init!!");
        addEventHandler(SFSEventType.USER_JOIN_ZONE, ZoneEventHandler.class);
    }
}
