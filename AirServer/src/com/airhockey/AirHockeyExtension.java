package com.airhockey;

import com.airhockey.handlers.MovementHandler;
import com.airhockey.handlers.ReadyHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AirHockeyExtension extends SFSExtension {

    private SmartFoxServer sfs;
    private ScheduledFuture<?> gameTask;
    private CoreGame game;
    @Override
    public void init() {
        trace("mahendra in AirHockeyExtension init");
        this.addRequestHandler("ready", ReadyHandler.class);
        this.addRequestHandler("move", MovementHandler.class);
        sfs = SmartFoxServer.getInstance();
    }

    public void startGame() {
        List<User> userList = getParentRoom().getUserList();
        game = new CoreGame(this, userList.get(0), null);
        // Schedule task: executes the game logic on the same frame basis (25 fps) used by the Flash client
        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 0, 500, TimeUnit.MILLISECONDS);

        List<Integer> userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        userIds.add(100);
        SFSObject sfsObject = game.getState().toSfs();
        sfsObject.putIntArray("userIds", userIds);
        send("start", sfsObject, userList);
    }

    @Override
    public void destroy()
    {
        trace("on destroy called" + gameTask);
        if(gameTask != null)
            gameTask.cancel(true);
        gameTask = null;
    }
}
