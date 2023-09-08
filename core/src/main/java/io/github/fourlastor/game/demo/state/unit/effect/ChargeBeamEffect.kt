package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.Effect.Triggered
import javax.inject.Inject
import javax.inject.Singleton
import com.badlogic.gdx.scenes.scene2d.ui.Label

@Singleton
class ChargeBeamEffect(mon: Mon) : Triggered {

    var chargeTier: Int = 1
    val targetMon: Mon
    val label: Label

    init {
        targetMon = mon
        val hpLabelStyle = Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.BLUE)
        val hpLabel = Label(String.valueOf(chargeTier), hpLabelStyle)
        targetMon.group.addActor(hpLabel)
    }

    override fun triggerEffect(mon: Mon): Action {
        val sequence = Actions.sequence(
            Actions.run( { 
                println("ChargeBeamEffect::triggerEffect") 
                chargeTier += 1
            } )
        )
        sequence.setActor(mon.group.image)
        return sequence
    }

    override fun cleanup() {
        targetMon.group.removeActor(label)
    }
}
