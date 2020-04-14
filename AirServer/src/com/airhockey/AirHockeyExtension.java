package com.airhockey;

import com.airhockey.core.Core;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.handlers.MovementHandler;
import com.airhockey.handlers.ReadyHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.dyn4j.dynamics.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AirHockeyExtension extends SFSExtension implements ApplicationWrapper {

    private SmartFoxServer sfs;
    private ScheduledFuture<?> gameTask;
    private Core game;
    @Override
    public void init() {
        trace("mahendra in AirHockeyExtension init");
        this.addRequestHandler("ready", ReadyHandler.class);
        this.addRequestHandler("move", MovementHandler.class);
        sfs = SmartFoxServer.getInstance();
    }

    public void startGame() {

        List<User> userList = getParentRoom().getUserList();
        List<Integer> userIds = new ArrayList<>();
        for(User user : userList)
            userIds.add(user.getId());
//        userIds.add(100); //dummy id for testing purposes

        Player player1 = new Player(userIds.get(0));
        Player player2 = new Player(userIds.get(1));
        game = new Core(this, player1, player2, 5);

        SFSObject sfsObject = game.getState().toNetworkObj();
        sfsObject.putIntArray("userIds", userIds);
        // -> send the list of users as well initially,
        // TODO : redundunt info, can be removed using iterator on client side

        send("start", sfsObject, userList);
        // Schedule task: executes the game logic on the same frame basis (25 fps) used by the Flash client
        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 100, 15, TimeUnit.MILLISECONDS);
    }

    public Core getGame() { return game; }

    @Override
    public void destroy()
    {
        trace("on destroy called" + gameTask);
        if(gameTask != null)
            gameTask.cancel(true);
        gameTask = null;
    }

    @Override
    public void print(String s) { trace(s); }

    @Override
    public void render(GameState state, List<Body> bodies) {
        send("move", state.toNetworkObj(), getParentRoom().getUserList());
    }

    //TODO ::
    @Override
    public void endGame(Player winner) {
        send("end", winner.toNetworkObj(), getParentRoom().getUserList());
        destroy();
    }

    @Override
    public void scoreUpdated(Player striker) {
        SFSObject resp = new SFSObject();
        resp.putInt(String.valueOf(striker.id), striker.score);
        send("updateScore", resp, getParentRoom().getUserList());
    }

    @Override
    public void resetGame(GameState state) {
        send("reset", state.toNetworkObj(), getParentRoom().getUserList());
    }
}
