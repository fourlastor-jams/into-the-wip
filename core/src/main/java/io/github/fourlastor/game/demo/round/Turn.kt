package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon.Abilities
import io.github.fourlastor.game.demo.state.unit.UnitType
import java.util.*

class Turn @AssistedInject constructor(
    @Assisted private val unitInRound: UnitInRound,
    private val router: StateRouter,
    private val defaults: Abilities.Defaults,
    private val abilitiesMap: Map<UnitType, @JvmSuppressWildcards Abilities>,
    private val atlas: TextureAtlas,
) : RoundState() {
    private val addedImages: Queue<Actor> = LinkedList()
    private var acted = false
    override fun enter(state: GameState) {
        val unit = unitInRound.mon
        state.tileAt(unit.hex).actor.color = Color.PINK
        if (!acted) {
            val monsterAbilities = abilitiesMap[unit.type] ?: defaults
            for (description in monsterAbilities.create()) {
                val image = Image(atlas.findRegion(description.icon))
                image.addListener(
                    PickMoveListener { router.startAbility(description.factory.apply(unitInRound)) }
                )
                state.ui.add(image)
                addedImages.add(image)
            }
        } else {
            router.endOfTurn()
        }
    }

    override fun update(state: GameState) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            router.endOfTurn()
        }
    }

    override fun exit(state: GameState) {
        state.tileAt(unitInRound.mon.hex).actor.color = Color.WHITE
        while (!addedImages.isEmpty()) {
            addedImages.remove().remove()
        }
    }

    private inner class PickMoveListener(private val onMove: () -> Unit) : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            acted = true
            onMove()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(unit: UnitInRound): Turn
    }
}
