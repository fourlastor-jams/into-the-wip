package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class ActorSupport {
    private ActorSupport() {}

    /**
     * Removes all the listeners of the given type from .
     * @return true if the listener was found, false otherwise. */
    public static boolean removeListeners(Actor actor, Class<?> type) {
        boolean found = false;
        for (EventListener listener : actor.getListeners()) {
            if (type.isInstance(listener)) {
                found = true;
                actor.removeListener(listener);
            }
        }
        return found;
    }
}
