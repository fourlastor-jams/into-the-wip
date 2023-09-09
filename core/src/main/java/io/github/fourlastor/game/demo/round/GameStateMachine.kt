package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.ai.fsm.StackStateMachine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import javax.inject.Provider

class GameStateMachine @AssistedInject constructor(
    @Assisted gameState: GameState,
    roundProvider: Provider<Round>,
    dispatcher: MessageDispatcher,
) : StackStateMachine<GameState, RoundState>(gameState, roundProvider.get()) {
    init {
        for (message in GameMessage.values()) {
            dispatcher.addListener(this, message.ordinal)
        }
        currentState.enter(owner)
    }

    override fun handleMessage(telegram: Telegram): Boolean {
        return if (GameMessage.ROUND_START.handles(telegram.message)) {
            val initialState = getState(telegram)
            setInitialState(initialState)
            initialState.enter(getOwner())
            true
        } else if (GameMessage.TURN_START.handles(telegram.message) ||
            GameMessage.ABILITY_START.handles(telegram.message)
        ) {
            changeState(telegram.extraInfo as RoundState)
            true
        } else if (GameMessage.TURN_END.handles(telegram.message) ||
            GameMessage.ABILITY_END.handles(telegram.message)
        ) {
            revertToPreviousState()
            true
        } else {
            super.handleMessage(telegram)
        }
    }

    private fun getState(telegram: Telegram): RoundState {
        return telegram.extraInfo as RoundState
    }

    @AssistedFactory
    interface Factory {
        fun create(state: GameState): GameStateMachine
    }
}
