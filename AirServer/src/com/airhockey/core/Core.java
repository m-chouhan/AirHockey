package com.airhockey.core;

import com.airhockey.ApplicationWrapper;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

/**
 * Core game logic
 */

public class Core implements Runnable {

    final int Width = 9, Height = 5, GoalPostSize = 4;
    final ApplicationWrapper appWrapper;
    final GameState state;
    final Vector2 leftGoalPoint, rightGoalPoint;
    final World world;
    final int MaxScore;
    private long lastUpdateTime;
    /** The conversion factor from nano to base */
    public static final double NANO_TO_BASE = 1.0e9;
    private boolean paused = false;

    //Player1 is always on the left side
    public Core(ApplicationWrapper appWrapper, Player player1, Player player2, int maxScore) {
        appWrapper.print("core game setup started!! " + Width + "," + Height);
        this.appWrapper = appWrapper;
        this.MaxScore = maxScore;
        this.world = Builder.setupWorld(Width, Height);
        Builder.incarnate(player1, -Width/2, 0, world);
        Builder.incarnate(player2, Width/2, 0, world);
        Body puck = Builder.createPuck( -4, -2, world);
        state = new GameState(player1, player2, puck);
        resetGame(state);

        leftGoalPoint = new Vector2(-Width, 0);
        rightGoalPoint = new Vector2(Width, 0);
    }

    //Core game loop
    @Override
    public void run() {
        if(paused) return;

        //1. processInput(); -> not needed
        //2. update world
        long time = System.nanoTime();
        long diff = time - this.lastUpdateTime;
        this.lastUpdateTime = time;
        double elapsedTime = (double)diff / NANO_TO_BASE;

        world.update(elapsedTime);
        Player striker = checkForGoal(state);
        if(striker != null) {
            striker.updateScore();
            appWrapper.scoreUpdated(striker);
            if(striker.score == MaxScore) {
                appWrapper.endGame(striker);
                paused = true;
                return;
            }
            else {
                resetGame(state);
                appWrapper.resetGame(state);
            }
        }

        //3. render
        appWrapper.print(state.toString());
        // network broadcast ? show in ui ? do whatever you want :P
        appWrapper.render(state, world.getBodies());
    }

    private void resetGame(GameState state) {
        state.player1.setPosition(-(float)Width/2, 0);
        state.player2.setPosition((float)Width/2, 0);
        state.puck.getTransform().setTranslation(0,0);
        state.puck.setLinearVelocity(0,0);
    }

    private Player checkForGoal(GameState state) {
        double x = state.puck.getTransform().getTranslationX();
        double y = state.puck.getTransform().getTranslationY();
        //left goalpost belongs to player1
        if(leftGoalPoint.difference(x,y).getMagnitudeSquared() <= GoalPostSize)
           return state.player2;
        //right belongs to player2
        if(rightGoalPoint.difference(x,y).getMagnitudeSquared() <= GoalPostSize)
            return state.player1;
        return null;
    }

    private Player getWinner(GameState state) {
        if(state.player1.score == MaxScore) return state.player1;
        if(state.player2.score == MaxScore) return state.player2;
        return null;
    }

    /*
        Since we are following event driven approach, no need to explicitly wait for input before each frame.
        does not makes sense;
     */
    private void processInput() { }

    public GameState getState() { return state; }

}
