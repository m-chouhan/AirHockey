package com.airhockey;

import com.airhockey.entities.Player;
import com.airhockey.entities.Puck;
import com.airhockey.handlers.MovementHandler;
import com.airhockey.handlers.ReadyHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AirHockeyExtension extends SFSExtension {

    private SmartFoxServer sfs;
    private ScheduledFuture<?> gameTask;
    @Override
    public void init() {
        trace("mahendra in AirHockeyExtension init");
        this.addRequestHandler("ready", ReadyHandler.class);
        this.addRequestHandler("move", MovementHandler.class);
    }

    public void startGame() {
        ISFSObject resObj = new SFSObject();
        List<User> userList = getParentRoom().getUserList();
        Player player1 = new Player(userList.get(0).getId(), -1, 0, 0);
        Player player2 = new Player(userList.get(1).getId(), 1, 0, 0);
        Puck puck = new Puck(0,0);
        resObj.putSFSObject("p1", player1.toSfs());
        resObj.putSFSObject("p2", player2.toSfs());
        resObj.putSFSObject("puck", puck.toSfs());
        //CoreGame game = new CoreGame();
        // Schedule task: executes the game logic on the same frame basis (25 fps) used by the Flash client
        //gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 0, 40, TimeUnit.MILLISECONDS);
        send("start", resObj, userList);
    }
}
