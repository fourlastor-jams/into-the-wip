package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.AttackAnimation.makeSequence
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.ChargeBeamEffect
import io.github.fourlastor.game.demo.state.unit.effect.Effect
import java.util.function.Consumer
import io.github.fourlastor.game.extensions.Vector2s.calculateAngle

class ChargeBeam @AssistedInject constructor(
    @Assisted("source") private val source: Mon
) : SimpleStep() {

    override fun enter(state: GameState, continuation: Runnable) {
        val effect = source.getEffect(ChargeBeamEffect::class.java)

        if (effect == null) {
            source.addEffect(ChargeBeamEffect(source), -1)
        }
        else {
            (effect as ChargeBeamEffect).chargeTier += 1
        }
        
        source.group.addAction(Actions.run(continuation))
    }

    override fun exit(state: GameState) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("source") source: Mon
        ): ChargeBeam
    }
}
