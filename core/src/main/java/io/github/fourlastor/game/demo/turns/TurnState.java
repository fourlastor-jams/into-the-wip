package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.state.GameState;

public abstract class TurnState implements State<GameState> {

    @Override
    public void update(GameState entity) {}

    @Override
    public boolean onMessage(GameState entity, Telegram telegram) {
        return false;
    }
}
