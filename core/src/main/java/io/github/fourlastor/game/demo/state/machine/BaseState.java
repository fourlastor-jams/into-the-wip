package io.github.fourlastor.game.demo.state.machine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.state.GameState;

public abstract class BaseState implements State<GameState> {

    @Override
    public abstract void enter(GameState state);

    public abstract void exit(GameState state);

    @Override
    public void update(GameState state) {}

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        return false;
    }
}
