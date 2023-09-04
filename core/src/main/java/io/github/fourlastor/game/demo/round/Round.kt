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
        if (factions == null) {
            stage.addAction(Actions.sequence(startOfRound(state), Actions.run { startFirstTurn(state) }))
        } else {
            advanceToNextTurn(state)
        }
    }

    private fun startOfRound(state: GameState): Action {
        factions = Faction.values().map { CurrentFaction(state.byFaction(it)) }
        val actions = factions!!
            .flatMap { it.units.map { unitInRound -> unitInRound.mon.onRoundStart() } }
        return Actions.sequence(*actions.toTypedArray())
    }

    override fun exit(state: GameState) {
        for (tile in state.tiles) {
            if (removeListeners(tile.actor) { it: EventListener? -> it is TurnListener }) {
                tile.actor.color = Color.WHITE
            }
        }
    }

    private fun startFirstTurn(state: GameState) {
        factionCounter = 0
        startTurn(state)
    }

    private fun advanceToNextTurn(state: GameState) {
        val currentFaction = factions!![factionCounter]
        if (currentFaction.allUnitsActed()) {
            factionCounter += 1
        }
        if (factionCounter >= factions!!.size) {
            factions!!.forEach(
                Consumer { faction: CurrentFaction ->
                    faction.units.forEach(
                        Consumer { unitInRound: UnitInRound -> unitInRound.mon.onRoundEnd() }
                    )
                }
            )
            router.round()
            return
        }
        startTurn(state)
    }

    private fun startTurn(state: GameState) {
        val currentFaction = factions!![factionCounter]
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
