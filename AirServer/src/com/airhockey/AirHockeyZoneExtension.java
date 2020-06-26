package com.airhockey;

import com.airhockey.core.Builder;
import com.airhockey.handlers.RoomEventHandler;
import com.airhockey.handlers.ZoneEventHandler;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class AirHockeyZoneExtension extends SFSExtension {

    @Override
    public void init() {
        trace("[AirHockeyZoneExt] setting up zone event handlers!!");
        addEventHandler(SFSEventType.USER_JOIN_ZONE, ZoneEventHandler.class);
        addEventHandler(SFSEventType.USER_JOIN_ROOM, ZoneEventHandler.class);
    }

    @Override
    public void destroy() {
        trace("[AirHockeyZoneExt] destroying zone event handlers!!");
    }
}
