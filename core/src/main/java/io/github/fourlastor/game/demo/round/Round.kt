package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Unit
import io.github.fourlastor.game.ui.ActorSupport
import javax.inject.Inject

class Round @Inject constructor(private val router: StateRouter) : RoundState {
  private var factions: List<CurrentFaction>? = null
  private var factionCounter = 0
  override fun enter(state: GameState) {
    if (factions == null) {
      factions = Faction.values().map { CurrentFaction(state.byFaction(it)) }
      startFirstTurn(state)
    } else {
      advanceToNextTurn(state)
    }
  }

  override fun exit(state: GameState) {
    for (unit in state.units) {
      if (ActorSupport.removeListeners(unit.group.image) { it: EventListener? ->
        it is TurnListener
      }) {
        state.tileAt(unit.hex).actor.color = Color.WHITE
      }
    }
  }

  private fun startFirstTurn(state: GameState) {
    factionCounter = 0
    startTurn(state)
  }

  private fun advanceToNextTurn(state: GameState) {
    val currentFaction = factions!![factionCounter]
    if (currentFaction.alreadyDidTurn.size >= currentFaction.units.size) {
      factionCounter += 1
    }
    if (factionCounter >= factions!!.size) {
      router.round()
      return
    }
    startTurn(state)
  }

  private fun startTurn(state: GameState) {
    val currentFaction = factions!![factionCounter]
    currentFaction.units
        .filter { !currentFaction.alreadyDidTurn.contains(it) }
        .forEach {
          state.tileAt(it.hex).actor.color = Color.PINK
          it.group.image.addListener(TurnListener(it, currentFaction))
        }
  }

  private class CurrentFaction(val units: List<Unit>) {
    val alreadyDidTurn: MutableList<Unit> = mutableListOf()
  }

  private inner class TurnListener(
      private val unit: Unit,
      private val currentFaction: CurrentFaction
  ) : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
      currentFaction.alreadyDidTurn.add(unit)
      router.turn(unit)
    }
  }
}
