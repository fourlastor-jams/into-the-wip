package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.state.GameState

abstract class RoundState : State<GameState> {
    abstract override fun enter(state: GameState)
    override fun exit(state: GameState) {}
    override fun update(state: GameState) {}
    override fun onMessage(state: GameState, telegram: Telegram) = false
}
