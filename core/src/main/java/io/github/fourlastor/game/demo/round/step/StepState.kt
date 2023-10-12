package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.round.AbilityState
import io.github.fourlastor.game.demo.state.GameState
import java.util.function.Consumer
import javax.inject.Inject

class StepState<T>(private val step: Step<T>, private val next: Consumer<T>, private val cancel: Runnable) :
    AbilityState() {
    override fun enter(state: GameState) {
        step.enter(state, next, cancel)
    }

    override fun update(state: GameState) {
        step.update(state, next, cancel)
    }

    override fun exit(state: GameState) {
        step.exit()
    }

    override fun onMessage(state: GameState, telegram: Telegram): Boolean {
        return step.onMessage(state, telegram)
    }

    class Factory @Inject constructor() {
        fun <T> create(step: Step<T>, next: Consumer<T>, cancel: Runnable): StepState<T> =
            StepState(step, next, cancel)
    }
}
