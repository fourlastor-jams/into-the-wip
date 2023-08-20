package io.github.fourlastor.game.demo.state.machine;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;

public class PickMonster extends BaseState {

    private final StateRouter router;

    @Inject
    public PickMonster(StateRouter router) {
        this.router = router;
    }

    @Override
    public void enter(GameState state) {
        for (Unit unit : state.units) {
            unit.actor.addListener(new PickMoveListener(unit));
        }
    }

    @Override
    public void exit(GameState state) {
        for (Unit unit : state.units) {
            for (EventListener listener : unit.actor.getListeners()) {
                if (listener instanceof PickMoveListener) {
                    unit.actor.removeListener(listener);
                }
            }
        }
    }

    private class PickMoveListener extends ClickListener {

        private final Unit unit;

        private PickMoveListener(Unit unit) {
            this.unit = unit;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.pickMove(unit);
        }
    }
}
