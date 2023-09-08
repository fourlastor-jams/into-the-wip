package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.ChargeBeamEffect

class ChargeBeamAttack @AssistedInject constructor(
    @Assisted("source") private val source: Mon,
    @Assisted("target") private val target: Mon,
    private val textureAtlas: TextureAtlas,
    private val stage: Stage
) : AttackRanged(source, target, textureAtlas, stage) {

    override fun enter(state: GameState, continuation: Runnable) {
        println("Hi")
        println("Hi")
        println("Hi")
        println("Hi")
        val effect = source.getEffect(ChargeBeamEffect::class.java)
        var damageAmount = 1
        println(effect)
        println(effect)
        println(effect)
        println(effect)
        if (effect != null) {
            source.removeEffect(effect)
            damageAmount = (effect as ChargeBeamEffect).chargeTier
        }
        stage.addActor(getAnimatedProjectile(continuation, 2 * damageAmount + (damageAmount - 1)))
    }

    override fun exit(state: GameState) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: Mon, @Assisted("target") target: Mon): ChargeBeamAttack
    }
}
