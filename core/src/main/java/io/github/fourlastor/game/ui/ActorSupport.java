package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import java.util.function.Predicate;

public class ActorSupport {
    private ActorSupport() {}

    /**
     * Removes all the listeners of the given type from .
     * @return true if the listener was found, false otherwise. */
    public static boolean removeListeners(Actor actor, Predicate<EventListener> filter) {
        boolean found = false;
        for (EventListener listener : actor.getListeners()) {
            if (filter.test(listener)) {
                found = true;
                actor.removeListener(listener);
            }
        }
        return found;
    }
}
