package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.state.GameState
import java.util.function.Consumer

abstract class Step<T> {

    var context: Context? = null

    fun enter(state: GameState, continuation: Consumer<T>, cancel: Runnable) {
        context = Context().apply { enter(state, continuation, cancel) }
    }
    abstract fun Context.enter(state: GameState, continuation: Consumer<T>, cancel: Runnable)

    open fun exit(state: GameState) {
        context?.cleanup()
    }
    open fun onMessage(state: GameState, telegram: Telegram): Boolean = false

    open fun update(state: GameState, next: Consumer<T>, cancel: Runnable) {}

    companion object {
        fun simple(
            onMessage: (telegram: Telegram) -> Boolean,
            onExit: () -> Unit,
            onEnter: (continuation: Runnable) -> Unit
        ) = object : Step<Unit>() {

            override fun Context.enter(state: GameState, continuation: Consumer<Unit>, cancel: Runnable) {
                onEnter { continuation.accept(Unit) }
            }

            override fun onMessage(state: GameState, telegram: Telegram): Boolean {
                return onMessage(telegram)
            }

            override fun exit(state: GameState) {
                onExit()
            }
        }
    }
}
