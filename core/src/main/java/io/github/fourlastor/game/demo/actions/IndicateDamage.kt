package io.github.fourlastor.game.demo.actions

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import dagger.assisted.AssistedInject

class IndicateDamage @AssistedInject constructor(
    private val assetManager: AssetManager,
    private val stage: Stage,
) {
    fun create(
        position: Vector2,
        damageAmount: Int,
    ): Action = Actions.run {
        val labelStyle = Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.RED)
        val healthDeplete = Label((-damageAmount).toString(), labelStyle)
        healthDeplete.setPosition(position.x, position.y)
        val duration = 0.5f
        val parallel = Actions.parallel()
        parallel.addAction(Actions.moveTo(position.x, position.y + 18, duration, Interpolation.linear))
        parallel.addAction(Actions.fadeOut(duration, Interpolation.linear))
        val sequence = Actions.sequence(parallel)
        sequence.addAction(Actions.run { healthDeplete.remove() })
        healthDeplete.addAction(sequence)
        stage.addActor(healthDeplete)
    }
}
