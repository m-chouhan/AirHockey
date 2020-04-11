package com.airhockey.handlers;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class MovementHandler extends BaseClientRequestHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        List<User> list = getParentExtension().getParentRoom().getUserList();
        List<User> otherPlayersList = new ArrayList<>(list);
        otherPlayersList.removeIf(user1 -> user1.getId() == user.getId());
        ISFSObject payload = new SFSObject();
        payload.putSFSObject(String.valueOf(user.getId()), isfsObject);
        send("move", payload, otherPlayersList);
    }
}
