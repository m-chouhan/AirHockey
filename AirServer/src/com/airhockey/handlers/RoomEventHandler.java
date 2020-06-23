package com.airhockey.handlers;

import com.airhockey.AirHockeyRoomExtension;
import com.airhockey.entities.GameState;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.IServerEventHandler;

import java.util.Arrays;

public class RoomEventHandler extends BaseClientRequestHandler implements IServerEventHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        AirHockeyRoomExtension airHockeyRoomExtension = (AirHockeyRoomExtension) getParentExtension();
        Room parentRoom = airHockeyRoomExtension.getParentRoom();
        Room lastJoinedRoom = user.getLastJoinedRoom();
        trace("Player : " + user.getId() + " is ready "
                + " parentRoomName : " + parentRoom.getName() +","+ parentRoom.getSize()
                + " lastJoinedroom : " + lastJoinedRoom.getName() + "," + lastJoinedRoom.getSize());
        //setupRoomVariables(lastJoinedRoom);

        if(lastJoinedRoom.getUserList().size() == 2)
            airHockeyRoomExtension
                    .startGame(lastJoinedRoom);
    }

    @Override
    public void handleServerEvent(ISFSEvent evt) throws SFSException {
        trace("[RoomEventHandler] " + evt.toString());

        switch (evt.getType()) {
            case USER_DISCONNECT:
                User user = (User)evt.getParameter(SFSEventParam.USER);
                Room room = (Room)evt.getParameter(SFSEventParam.ROOM);
                trace(user.getName() + " is disconnected from " + room.getName());
                break;
            case USER_JOIN_ROOM:
                user = (User) evt.getParameter(SFSEventParam.USER);
                Zone zone = (Zone) evt.getParameter(SFSEventParam.ZONE);
                room = (Room) evt.getParameter(SFSEventParam.ROOM);
                trace("[RoomEventHandler] " + user.getName() + "," + zone.getName() + "," + room.getName());
                break;
            case USER_LEAVE_ROOM:
                user = (User) evt.getParameter(SFSEventParam.USER);
                room = (Room) evt.getParameter(SFSEventParam.ROOM);
                trace("[RoomEventHandler] " + user.getName() + " left " + room.getName());
                break;
            case USER_RECONNECTION_SUCCESS:
                break;
            case USER_RECONNECTION_TRY:
                break;
            case ROOM_VARIABLES_UPDATE:
                break;
        }
    }
}
