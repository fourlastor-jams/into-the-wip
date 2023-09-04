package io.github.fourlastor.game.demo.state.unit.effect

import com.badlogic.gdx.scenes.scene2d.Action
import io.github.fourlastor.game.demo.state.unit.Unit

interface Effect {
    interface OnRoundStart : Effect {
        fun onRoundStart(unit: Unit, stackAmount: Int): Action
    }
}
