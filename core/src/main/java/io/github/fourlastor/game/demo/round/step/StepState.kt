package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.round.AbilityState
import io.github.fourlastor.game.demo.state.GameState
import javax.inject.Inject

class StepState<T>(
    private val step: Step<T>,
    private val next: (T) -> Unit,
    private val cancel: () -> Unit
) : AbilityState() {

  override fun enter(state: GameState) {
    step.enter(state, next, cancel)
  }

  override fun update(state: GameState) {
    step.update(state, next, cancel)
  }

  override fun exit(state: GameState) {
    step.exit(state)
  }

  override fun onMessage(state: GameState, telegram: Telegram): Boolean {
    return step.onMessage(state, telegram)
  }

  class Factory @Inject constructor() {
    fun <T> create(step: Step<T>, next: (T) -> Unit, cancel: () -> Unit): StepState<T> {
      return StepState(step, next, cancel)
    }
  }
}
