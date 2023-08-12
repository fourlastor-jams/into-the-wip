package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.tiles.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class Move extends TurnState {

    private final StateRouter router;

    private final Unit unit;

    @AssistedInject
    public Move(@Assisted Unit unit, StateRouter router) {
        this.router = router;
        this.unit = unit;
    }

    @Override
    public void enter(GameState entity) {
        for (Tile tile : entity.tiles) {
            tile.actor.addListener(new MoveListener(tile));
        }
    }

    @Override
    public void exit(GameState entity) {
        for (Tile tile : entity.tiles) {
            for (EventListener listener : tile.actor.getListeners()) {
                if (listener instanceof MoveListener) {
                    tile.actor.removeListener(listener);
                }
            }
        }
    }

    @AssistedFactory
    public interface Factory {
        Move create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;

        private MoveListener(Tile tile) {
            this.tile = tile;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            unit.actor.addAction(Actions.sequence(
                    Actions.moveTo(tile.actor.getX(), tile.actor.getY(), 0.25f, Interpolation.sine),
                    Actions.run(router::pickMonster)));
        }
    }
}
