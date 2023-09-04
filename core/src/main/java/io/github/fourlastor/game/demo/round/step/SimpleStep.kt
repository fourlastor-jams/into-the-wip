package io.github.fourlastor.game.demo.round.step

import io.github.fourlastor.game.demo.state.GameState
import java.util.function.Consumer

abstract class SimpleStep : Step<Unit>() {
    abstract fun enter(state: GameState, continuation: Runnable)
    override fun enter(state: GameState, continuation: Consumer<Unit>, cancel: Runnable) {
        enter(state) { continuation.accept(Unit) }
    }
}
