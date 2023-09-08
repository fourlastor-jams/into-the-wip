package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import io.github.fourlastor.game.demo.state.unit.Mon

interface Effect {
    interface OnRoundStart : Effect {
        fun onRoundStart(mon: Mon, stackAmount: Int): Action
    }
    interface OnTurnStart : Effect {
        fun onTurnStart(mon: Mon, stackAmount: Int): Action
    }
}
