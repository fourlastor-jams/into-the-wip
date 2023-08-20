package io.github.fourlastor.game.demo.state.machine;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;

public class GameStateMachine extends DefaultStateMachine<GameState, BaseState> {

    @AssistedInject
    public GameStateMachine(
            @Assisted GameState gameState, @Assisted BaseState initialState, MessageDispatcher dispatcher) {
        super(gameState, initialState);
        dispatcher.addListener(this, GameMessage.SET_STATE.ordinal());
        initialState.enter(gameState);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        if (GameMessage.SET_STATE.handles(telegram.message)) {
            changeState((BaseState) telegram.extraInfo);
            return true;
        } else {
            return super.handleMessage(telegram);
        }
    }

    @AssistedFactory
    public interface Factory {
        GameStateMachine create(GameState state, BaseState initialState);
    }
}
