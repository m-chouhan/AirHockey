package com.airhockey;

import com.airhockey.core.Core;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.handlers.MovementHandler;
import com.airhockey.handlers.RoomEventHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.dyn4j.dynamics.Body;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AirHockeyRoomExtension extends SFSExtension implements ApplicationWrapper {

    private SmartFoxServer sfs;
    private ScheduledFuture<?> gameTask;
    private Core game;

    @Override
    public void init() {
        trace("[AirHockeyRoomExt] init" + getParentRoom().getName());
        this.addRequestHandler("move", MovementHandler.class);
        this.addRequestHandler("ready", RoomEventHandler.class);
        addEventHandler(SFSEventType.USER_JOIN_ROOM, RoomEventHandler.class);
        addEventHandler(SFSEventType.USER_LEAVE_ROOM, RoomEventHandler.class);
        addEventHandler(SFSEventType.USER_DISCONNECT, RoomEventHandler.class);
        addEventHandler(SFSEventType.USER_RECONNECTION_TRY, RoomEventHandler.class);
        addEventHandler(SFSEventType.USER_RECONNECTION_SUCCESS, RoomEventHandler.class);
        addEventHandler(SFSEventType.ROOM_VARIABLES_UPDATE, RoomEventHandler.class);

        sfs = SmartFoxServer.getInstance();
    }

    public void startGame(Room room) {

        trace("[AirHockeyRoomExtension] starting game for " + room.getName());
        List<User> list = room.getUserList();
        Player player1 = new Player(list.get(0).getId());
        Player player2 = new Player(list.get(1).getId());
        game = new Core(this, player1, player2, 3);

        SFSObject sfsObject = game.getState().toNetworkObj();

        send("start", sfsObject, list);
        //send("start", sfsObject, user2);
        // Schedule task: executes the game logic on the same frame basis (25 fps) used by the Flash client
        gameTask = sfs.getTaskScheduler().scheduleAtFixedRate(game, 100, 10, TimeUnit.MILLISECONDS);
    }

    public Core getGame() { return game; }

    @Override
    public void destroy()
    {
        trace("[AirHockeyRoomExt] on destroy called on " + getParentRoom().getName());
        if(gameTask != null)
            gameTask.cancel(true);
        gameTask = null;
    }

    @Override
    public void print(String s) {
        //trace(s);
    }

    @Override
    public void render(GameState state, List<Body> bodies) {
        
        //only thing we need to send here is puck position
        List<User> users = getParentRoom().getUserList();

        User player1 = users.stream()
                        .filter(user -> user.getId() == state.player1.id)
                        .findAny()
                        .orElse(null);
        User player2 = users.stream()
                        .filter(user -> user.getId() == state.player2.id)
                        .findAny()
                        .orElse(null);

        SFSObject resp = new SFSObject();
        if(state.puck.isDirty()) {
            SFSObject puckPos = new SFSObject();
            puckPos.putFloat("x", (float) state.puck.getTransform().getTranslationX());
            puckPos.putFloat("y", (float) state.puck.getTransform().getTranslationY());
            resp.putSFSObject("puck", puckPos);
        }

        //update player1 iff
        if(state.player2.isDirty() || state.puck.isDirty()) {
            SFSObject playerPos = new SFSObject();
            playerPos.putFloat("x", (float) state.player2.slave.getTransform().getTranslationX());
            playerPos.putFloat("y", (float) state.player2.slave.getTransform().getTranslationY());
            resp.putSFSObject(String.valueOf(state.player2.id), playerPos);
            send("move", resp, player1);
            resp.removeElement(String.valueOf(state.player2.id));
        }
        //update player2 iff
        if(state.player1.isDirty() || state.puck.isDirty()) {
            SFSObject playerPos = new SFSObject();
            playerPos.putFloat("x", (float) state.player1.slave.getTransform().getTranslationX());
            playerPos.putFloat("y", (float) state.player1.slave.getTransform().getTranslationY());
            resp.putSFSObject(String.valueOf(state.player1.id), playerPos);
            send("move", resp, player2);
        }
    }

    //TODO ::
    @Override
    public void endGame(Player winner) {
        SFSObject response = new SFSObject();
        response.putInt("won", winner.id);
        send("end", response, getParentRoom().getUserList());
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
