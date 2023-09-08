package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.Effect.OnTurnStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlobAbsorbSourceEffect(private val mon: Mon) : OnTurnStart {

    val targetMon: Mon

    init {
        targetMon = mon;
    }

    override fun onTurnStart(mon: Mon, stackAmount: Int): Action {
        val sequence = Actions.sequence(
            Actions.run( { println("BlobAbsorbSourceEffect::onRoundStart") } )
        )
        sequence.setActor(mon.group.image)
        return sequence
    }
}
