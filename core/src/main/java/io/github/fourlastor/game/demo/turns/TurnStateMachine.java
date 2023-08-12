package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;

public class TurnStateMachine extends DefaultStateMachine<GameState, TurnState> {

    @AssistedInject
    public TurnStateMachine(
            @Assisted GameState gameState, @Assisted TurnState initialState, MessageDispatcher dispatcher) {
        super(gameState, initialState);
        dispatcher.addListener(this, TurnMessage.SET_STATE.ordinal());
        initialState.enter(gameState);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        if (TurnMessage.SET_STATE.handles(telegram.message)) {
            changeState((TurnState) telegram.extraInfo);
            return true;
        } else {
            return super.handleMessage(telegram);
        }
    }

    @AssistedFactory
    public interface Factory {
        TurnStateMachine create(GameState state, TurnState initialState);
    }
}
