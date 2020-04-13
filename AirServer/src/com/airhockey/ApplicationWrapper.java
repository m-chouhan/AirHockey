package com.airhockey;

import com.airhockey.entities.GameState;
import com.airhockey.entities.Player;

public interface ApplicationWrapper {
    void print(String s);
    void render(GameState state);
    void endGame(Player winner);
}
