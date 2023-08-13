package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Set;

public class PickMove extends TurnState {

    private final StateRouter router;

    private final Unit unit;

    @AssistedInject
    public PickMove(@Assisted Unit unit, StateRouter router) {
        this.router = router;
        this.unit = unit;
    }

    @Override
    public void enter(GameState entity) {
        for (Tile tile : tilesFromUnit(entity)) {
            tile.actor.addListener(new MoveListener(tile));
            tile.actor.setColor(Color.CORAL);
        }
        for (Unit unit : entity.units) {
            if (this.unit == unit) {
                continue;
            }
            unit.actor.addListener(new AttackListener(unit));
        }
    }

    @Override
    public void exit(GameState entity) {
        for (Tile tile : entity.tiles) {
            for (EventListener listener : tile.actor.getListeners()) {
                if (listener instanceof MoveListener) {
                    tile.actor.removeListener(listener);
                }
                tile.actor.setColor(Color.WHITE);
            }
        }
        for (Unit unit : entity.units) {
            for (EventListener listener : unit.actor.getListeners()) {
                if (listener instanceof AttackListener) {
                    unit.actor.removeListener(listener);
                }
                unit.actor.setColor(Color.WHITE);
            }
        }
    }

    private Set<Tile> tilesFromUnit(GameState entity) {
        MapGraph graph = entity.graph;
        Tile unitTile = graph.get(unit.position);
        return graph.tilesAtDistance(unitTile, 2);
    }

    @AssistedFactory
    public interface Factory {
        PickMove create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;

        private MoveListener(Tile tile) {
            this.tile = tile;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.move(unit, tile);
        }
    }

    private class AttackListener extends ClickListener {

        private final Unit target;

        private AttackListener(Unit target) {
            this.target = target;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.attackMelee(unit, target);
        }
    }
}
