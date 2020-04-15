package com.airhockey;

import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;
import org.dyn4j.dynamics.Body;

import java.util.List;

public interface ApplicationWrapper {
    void print(String s);
    void render(GameState state, List<Body> bodies);
    void endGame(Player winner);
    void scoreUpdated(Player striker);

    void resetGame(GameState state);
}
