package io.github.fourlastor.game.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table

class UiLayer : Table() {
    init {
        setFillParent(true)
        defaults().bottom().expandY().pad(2f)
    }
}
