package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.fsm.StackStateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import javax.inject.Provider;

public class GameStateMachine extends StackStateMachine<GameState, RoundState> {

    @AssistedInject
    public GameStateMachine(
            @Assisted GameState gameState, Provider<Round> roundProvider, MessageDispatcher dispatcher) {
        super(gameState, roundProvider.get());
        for (GameMessage message : GameMessage.values()) {
            dispatcher.addListener(this, message.ordinal());
        }
        currentState.enter(owner);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        if (GameMessage.ROUND_START.handles(telegram.message)) {
            RoundState initialState = getState(telegram);
            setInitialState(initialState);
            initialState.enter(getOwner());
            return true;
        } else if (GameMessage.TURN_START.handles(telegram.message)
                || GameMessage.ABILITY_START.handles(telegram.message)) {
            changeState((RoundState) telegram.extraInfo);
            return true;
        } else if (GameMessage.TURN_END.handles(telegram.message)
                || GameMessage.ABILITY_END.handles(telegram.message)) {
            revertToPreviousState();
            return true;
        } else {
            return super.handleMessage(telegram);
        }
    }

    private RoundState getState(Telegram telegram) {
        return (RoundState) telegram.extraInfo;
    }

    @AssistedFactory
    public interface Factory {
        GameStateMachine create(GameState state);
    }
}
