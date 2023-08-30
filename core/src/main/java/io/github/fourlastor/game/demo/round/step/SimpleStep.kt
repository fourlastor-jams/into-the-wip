package io.github.fourlastor.game.demo.round.step

import io.github.fourlastor.game.demo.state.GameState

abstract class SimpleStep : Step<Unit>() {
  abstract fun enter(state: GameState, continuation: () -> Unit)
  override fun enter(state: GameState, continuation: (Unit) -> Unit, cancel: () -> Unit) {
    enter(state) { continuation(Unit) }
  }
}
