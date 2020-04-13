package com.airhockey.core;

import com.airhockey.ApplicationWrapper;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

/**
 * Core game logic
 */

public class Core implements Runnable {

    final int Width = 8, Height = 8;
    final ApplicationWrapper appWrapper;
    final GameState state;
    final World world;

    public Core(ApplicationWrapper appWrapper, Player player1, Player player2) {
        appWrapper.print("core game setup started!! " + Width + "," + Height);
        this.appWrapper = appWrapper;
        this.world = Builder.setupWorld(Width, Height);
        Builder.incarnate(player1, -Width/2 + 2, 0, world);
        Builder.incarnate(player2, Width/2 - 2, 0, world);
        Body puck = Builder.createPuck(world, 0, 0);
        state = new GameState(player1, player2, puck);
    }

    //Core game loop
    @Override
    public void run() {

        if(getWinner(state) == null) {
            //1. processInput();
            //2. update world
            world.step(1);
            //3. render
            appWrapper.print(state.toString());
            // network broadcast ? show in ui ? do whatever you want :P
            appWrapper.render(state, world.getBodies());
        } else appWrapper.endGame(getWinner(state));
    }

    private Player getWinner(GameState state) {
        return null;
    }

    /*
        Since we are following event driven approach, no need to explicitly wait for input before each frame.
        does not makes sense;
     */
    private void processInput() { }

    public GameState getState() { return state; }

}
