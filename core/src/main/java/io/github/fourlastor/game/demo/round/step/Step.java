package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.state.GameState;
import java.util.function.Consumer;

public abstract class Step<T> {

    public abstract void enter(GameState state, Consumer<T> continuation);

    public void exit(GameState state) {}

    public boolean onMessage(GameState state, Telegram telegram) {
        return false;
    }
}
