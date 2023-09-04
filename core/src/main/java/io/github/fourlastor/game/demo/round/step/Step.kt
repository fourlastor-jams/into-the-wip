package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.state.GameState
import java.util.function.Consumer

abstract class Step<T> {
    abstract fun enter(state: GameState, continuation: Consumer<T>, cancel: Runnable)
    open fun exit(state: GameState) {}
    open fun onMessage(state: GameState, telegram: Telegram): Boolean = false

    open fun update(state: GameState, next: Consumer<T>, cancel: Runnable) {}
}
