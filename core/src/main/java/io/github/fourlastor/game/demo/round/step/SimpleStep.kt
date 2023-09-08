package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import io.github.fourlastor.game.demo.state.GameState
import java.util.function.Consumer

abstract class SimpleStep : Step<Unit>() {
    abstract fun enter(state: GameState, continuation: Runnable)
    override fun enter(state: GameState, continuation: Consumer<Unit>, cancel: Runnable) {
        enter(state) { continuation.accept(Unit) }
    }

    fun healthDeplete(stage: Stage, assetManager: AssetManager, x: Float, y: Float, damageAmount: Int): Action {
        // (sheerst) - can we do this without having to pass stage and assetmanager?
        return Actions.run {
            val labelStyle = Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.RED)
            val healthDeplete = Label((-damageAmount).toString(), labelStyle)
            healthDeplete.setPosition(x, y)
            val duration = 0.5f
            healthDeplete.addAction(Actions.moveTo(x, y + 18, duration, Interpolation.linear))
            healthDeplete.addAction(Actions.fadeOut(duration, Interpolation.linear))
            stage.addActor(healthDeplete)
        }
    }
}
