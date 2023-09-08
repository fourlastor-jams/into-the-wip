package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.Effect.Triggered

class ChargeBeamEffect @AssistedInject constructor(
    @Assisted mon: Mon,
    assetManager: AssetManager
) : Triggered {

    var chargeTier: Int = 1
    val targetMon: Mon
    private val label: Label

    init {
        targetMon = mon
        val labelStyle = Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.BLUE)
        label = Label(chargeTier.toString(), labelStyle)
        label.setPosition(0f, targetMon.group.image.imageHeight + 8)
        targetMon.group.addActor(label)
    }

    override fun triggerEffect(mon: Mon): Action {
        val sequence = Actions.sequence(
            Actions.run {
                chargeTier += 1
                label.setText(chargeTier.toString())
            }
        )
        sequence.setActor(mon.group.image)
        return sequence
    }

    override fun cleanup() {
        targetMon.group.removeActor(label)
    }

    @AssistedFactory
    interface Factory {
        fun create(mon: Mon): ChargeBeamEffect
    }
}
