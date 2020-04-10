package com.airhockey;

import com.airhockey.entities.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.List;

public class AirHockeyExtension extends SFSExtension {

    @Override
    public void init() {
        trace("mahendra in AirHockeyExtension init");
        this.addRequestHandler("ready", ReadyHandler.class);
        this.addRequestHandler("move", MovementHandler.class);
    }

    void startGame() {
        ISFSObject resObj = new SFSObject();
        List<User> userList = getParentRoom().getUserList();
        Player player1 = new Player(userList.get(0).getId(), -1, 0, 0);
        Player player2 = new Player(userList.get(1).getId(), 1, 0, 0);
        resObj.putSFSObject("p1", player1.toSfs());
        resObj.putSFSObject("p2", player2.toSfs());
        send("start", resObj, userList);
    }
}
