package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.PoisonEffect

class Poison @AssistedInject constructor(
    @Assisted private val target: Mon,
    private val poisonEffect: PoisonEffect
) : SimpleStep() {
    override fun enter(state: GameState, continuation: Runnable) {
        target.group.image.addAction(
            Actions.sequence(
                Actions.color(Color.OLIVE, 0.5f),
                Actions.color(Color.GREEN, 0.2f),
                Actions.color(Color.WHITE, 0.3f),
                Actions.run { target.addEffect(poisonEffect) },
                Actions.run { continuation.run() }
            )
        )
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    interface Factory {
        fun create(@Assisted target: Mon): Poison
    }
}
