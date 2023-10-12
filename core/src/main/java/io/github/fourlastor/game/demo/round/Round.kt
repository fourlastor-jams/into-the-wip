package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.ui.ActorSupport.removeListeners
import java.util.function.Consumer
import javax.inject.Inject

class Round @Inject constructor(private val router: StateRouter, private val stage: Stage) : RoundState() {
    private var factions: List<CurrentFaction>? = null
    private var factionCounter = 0
    override fun enter(state: GameState) {
        val currentFactions = factions
        if (currentFactions == null) {
            stage.addAction(startOfRound(state))
        } else {
            advanceToNextTurn(state, currentFactions)
        }
    }

    private fun startOfRound(state: GameState): Action {
        val currentFactions = Faction.values().map { CurrentFaction(state.byFaction(it)) }
        factions = Faction.values().map { CurrentFaction(state.byFaction(it)) }
        val actions = currentFactions
            .flatMap { it.units.map { unitInRound -> unitInRound.mon.onRoundStart() } }
        return Actions.sequence(*actions.toTypedArray(), Actions.run { startFirstTurn(state, currentFactions) })
    }

    override fun exit(state: GameState) {
        for (tile in state.tiles) {
            if (removeListeners(tile.actor) { it: EventListener? -> it is TurnListener }) {
                tile.actor.color = Color.WHITE
            }
        }
    }

    private fun startFirstTurn(state: GameState, currentFactions: List<CurrentFaction>) {
        factionCounter = 0
        startTurn(state, currentFactions)
    }

    private fun advanceToNextTurn(state: GameState, currentFactions: List<CurrentFaction>) {
        val currentFaction = currentFactions[factionCounter]
        if (currentFaction.allUnitsActed()) {
            factionCounter += 1
        }
        if (factionCounter >= currentFactions.size) {
            currentFactions.forEach(
                Consumer { faction: CurrentFaction ->
                    faction.units.forEach(
                        Consumer { unitInRound: UnitInRound -> unitInRound.mon.onRoundEnd() }
                    )
                }
            )
            router.round()
            return
        }
        startTurn(state, currentFactions)
    }

    private fun startTurn(state: GameState, currentFactions: List<CurrentFaction>) {
        val currentFaction = currentFactions[factionCounter]
        currentFaction.units.asSequence()
            .filter { unitInTurn: UnitInRound -> !unitInTurn.hasActed }
            .forEach { unitInTurn: UnitInRound ->
                val unitTile = state.tileAt(unitInTurn.mon.hex)
                unitTile.actor.color = Color.PINK
                unitTile.actor.addListener(TurnListener(unitInTurn))
            }
    }

    private class CurrentFaction(mons: List<Mon>) {
        val units: List<UnitInRound>

        init {
            this.units = mons.map { UnitInRound(it) }
        }

        fun allUnitsActed(): Boolean = units.all { it.hasActed }
    }

    private inner class TurnListener(private val unit: UnitInRound) : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            router.turn(unit)
        }
    }
}
