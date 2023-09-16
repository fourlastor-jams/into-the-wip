package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.fourlastor.game.demo.state.unit.Mon
import javax.inject.Singleton

@Singleton
class BlobAbsorbEffect(private val mon: Mon) : Effect.OnTurnStart, Effect.ApplySlow {

    override val slowAmount: Int
        get() = 2

    val otherMon: Mon = mon

    override fun onTurnStart(mon: Mon, stackAmount: Int): Action {
        return Actions.sequence(Actions.run({ println("BlobAbsorbSourceEffect::onTurnStart") })).apply { setActor(mon.group.image) }
    }
}
