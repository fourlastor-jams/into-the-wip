package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.ObjectIntMap
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.effect.Effect.OnRoundStart
import io.github.fourlastor.game.demo.state.unit.effect.Effect.OnTurnStart

class EffectStacks {
    private val stacks = ObjectIntMap<Effect>()
    fun addStack(effect: Effect, quantity: Int) {
        val current = getStack(effect)
        stacks.put(effect, current + quantity)
    }

    fun getEffects(): ObjectIntMap<Effect> = stacks

    fun removeEffect(effect: Effect) {
        stacks.remove(effect, stacks.get(effect, 0))
    }

    fun tickStacks() {
        for (effect in stacks.keys()) {
            val current = getStack(effect)
            if (current == 1) {
                stacks.remove(effect, 0)
            } else {
                stacks.put(effect, current - 1)
            }
        }
    }

    private fun getStack(effect: Effect): Int = stacks[effect, 0]

    fun onRoundStart(mon: Mon): Action {
        val sequence = Actions.sequence()
        for (effect in stacks.keys()) {
            if (effect is OnRoundStart) {
                sequence.addAction(effect.onRoundStart(mon, getStack(effect)))
            }
        }
        return sequence
    }

    fun onTurnStart(mon: Mon): Action {
        val sequence = Actions.sequence()
        for (effect in stacks.keys()) {
            if (effect is OnTurnStart) {
                sequence.addAction(effect.onTurnStart(mon, getStack(effect)))
            }
        }
        return sequence
    }

    fun byType(type: Class<out Effect>): Effect? {
        for (effect in stacks.keys()) {
            if (type.isInstance(effect)) {
                return effect
            }
        }
        return null
    }
}
