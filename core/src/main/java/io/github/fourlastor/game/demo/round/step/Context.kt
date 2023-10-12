package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import java.util.LinkedList

class Context {

    private val cleanups: MutableList<() -> Unit> = LinkedList()

    fun Actor.selectable(promptColor: Color) {
        cleanups.add { color = Color.WHITE }
        color = promptColor
    }

    fun doOnExit(action: () -> Unit) {
        cleanups.add(action)
    }

    fun Actor.doOnClick(click: () -> Unit) {
        val listener = object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                click()
            }
        }

        cleanups.add { removeListener(listener) }
        addListener(listener)
    }

    fun cleanup() = cleanups.forEach { it() }
}
