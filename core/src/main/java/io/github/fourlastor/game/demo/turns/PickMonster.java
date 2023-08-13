package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;

public class PickMonster extends TurnState {

    private final StateRouter router;

    @Inject
    public PickMonster(StateRouter router) {
        this.router = router;
    }

    @Override
    public void enter(GameState entity) {
        for (Unit unit : entity.units) {
            unit.image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    router.pickMove(unit);
                }
            });
        }
    }

    @Override
    public void exit(GameState entity) {
        for (Unit unit : entity.units) {
            for (EventListener listener : unit.image.getListeners()) {
                if (listener instanceof PickMoveListener) {
                    unit.image.removeListener(listener);
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
