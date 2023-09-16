package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.state.unit.Mon

interface Effect {
    interface OnRoundStart : Effect {
        fun onRoundStart(mon: Mon, stackAmount: Int): Action
    }

    interface OnTurnStart : Effect {
        fun onTurnStart(mon: Mon, stackAmount: Int): Action
    }

    interface Triggered : Effect {
        fun triggerEffect(mon: Mon): Action
        fun cleanup()
    }

    interface ApplySlow : Effect {
        val slowAmount: Int

        fun applySlow(mon: Mon, duringAbility: Ability): Int {
            // Ignore slow during some abilities.
            if (duringAbility.ignoresSlow) return mon.type.speed
            return 1.coerceAtLeast(mon.type.speed - slowAmount)
        }
    }
}
