package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.ai.msg.MessageDispatcher
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.state.unit.Unit
import javax.inject.Inject
import javax.inject.Provider

class StateRouter
@Inject
constructor(
    private val dispatcher: MessageDispatcher,
    private val roundProvider: Provider<Round>,
    private val turnFactory: Turn.Factory
) {
  fun startAbility(ability: Ability) {
    dispatcher.dispatchMessage(GameMessage.ABILITY_START.ordinal, ability)
  }

  fun round() {
    dispatcher.dispatchMessage(GameMessage.ROUND_START.ordinal, roundProvider.get())
  }

  fun turn(unit: Unit) {
    dispatcher.dispatchMessage(GameMessage.TURN_START.ordinal, turnFactory.create(unit))
  }

  fun endOfTurn() {
    dispatcher.dispatchMessage(GameMessage.TURN_END.ordinal)
  }

  fun nextStep(result: StepState<*>) {
    dispatcher.dispatchMessage(GameMessage.NEXT_STEP.ordinal, result)
  }

  fun endOfAbility() {
    dispatcher.dispatchMessage(GameMessage.ABILITY_END.ordinal)
  }
}
