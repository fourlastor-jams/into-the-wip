package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.Effect.OnTurnStart
import javax.inject.Singleton

@Singleton
class BlobAbsorbTargetEffect(private val mon: Mon) : OnTurnStart {

    val targetMon: Mon

    init {
        targetMon = mon
    }

    override fun onTurnStart(mon: Mon, stackAmount: Int): Action {
        val sequence = Actions.sequence(
            Actions.run({ println("BlobAbsorbTargetEffect::onRoundStart") })
        )
        sequence.setActor(mon.group.image)
        return sequence
    }
}
