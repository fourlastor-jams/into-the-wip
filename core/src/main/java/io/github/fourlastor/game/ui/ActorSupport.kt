package io.github.fourlastor.game.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import java.util.function.Predicate

object ActorSupport {
    /**
     * Removes all the listeners of the given type from .
     * @return true if the listener was found, false otherwise.
     */
    @JvmStatic
    fun removeListeners(actor: Actor, filter: Predicate<EventListener?>): Boolean {
        var found = false
        for (listener in actor.listeners) {
            if (filter.test(listener)) {
                found = true
                actor.removeListener(listener)
            }
        }
        return found
    }
}
