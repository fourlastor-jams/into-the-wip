package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.fourlastor.game.demo.state.unit.Unit
import io.github.fourlastor.game.demo.state.unit.effect.Effect.OnRoundStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoisonEffect @Inject constructor() : OnRoundStart {
    override fun onRoundStart(unit: Unit, stackAmount: Int): Action {
        val sequence = Actions.sequence(
            Actions.color(Color.OLIVE, 0.5f),
            Actions.color(Color.GREEN, 0.2f),
            Actions.color(Color.WHITE, 0.3f),
            Actions.run { unit.changeHp(-stackAmount) }
        )
        sequence.setActor(unit.group.image)
        return sequence
    }
}
