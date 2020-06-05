package com.airhockey.core;

import com.airhockey.ApplicationWrapper;
import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import com.airhockey.entities.Puck;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

/**
 * Core game logic
 */

public class Core implements Runnable {

    final int Width = 5, Height = 10, GoalPostSize = 4;
    final ApplicationWrapper appWrapper;
    final GameState state;
    final Vector2 leftGoalPoint, rightGoalPoint;
    final World world;
    final int MaxScore;
    /** The conversion factor from nano to base */
    public static final double NANO_TO_BASE = 1.0e9;

    private long lastUpdateTime;
    private boolean isTestEnv = true;

    private boolean paused = false;

    //Player1 is always on the left side
    public Core(ApplicationWrapper appWrapper, Player player1, Player player2, int maxScore) {
        appWrapper.print("core game setup started!! " + Width + "," + Height);
        isTestEnv = System.getenv("ENV") != null && System.getenv("ENV").equals("TEST");
        this.appWrapper = appWrapper;
        this.MaxScore = maxScore;
        this.world = Builder.setupWorld(Width, Height);
        Builder.incarnate(player1, 0, 0, world);
        Builder.incarnate(player2, 0, 0, world);
        Puck puck = Builder.createPuck( 0, 0, world);
        state = new GameState(player1, player2, puck);
        resetGame(state);

        leftGoalPoint = new Vector2(0, -Height);
        rightGoalPoint = new Vector2(0, Height);
    }

    //Core game loop
    @Override
    public void run() {
        if(paused) return;
        //1. processInput(); -> not needed here at present
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
        appWrapper.render(state, isTestEnv ? world.getBodies() : null);
    }

    private void resetGame(GameState state) {
        state.player1.setPosition(0, -Height/2);
        state.player2.setPosition(0, Height/2);
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
