package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.ChargeBeamEffect

class ChargeBeam @AssistedInject constructor(
    @Assisted("source") private val source: Mon,
    private val chargeBeamFactory: ChargeBeamEffect.Factory,
) : SimpleStep() {

    override fun enter(state: GameState, continuation: Runnable) {
        val effect = source.getEffect(ChargeBeamEffect::class.java)

        if (effect == null) {
            source.stacks.addStack(chargeBeamFactory.create(source), -1)
        } else {
            source.group.addAction((effect as ChargeBeamEffect).triggerEffect(source))
        }

        source.group.addAction(Actions.run(continuation))
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("source") source: Mon,
        ): ChargeBeam
    }
}
