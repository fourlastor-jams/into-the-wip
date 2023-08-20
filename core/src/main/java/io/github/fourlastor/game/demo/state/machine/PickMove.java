package io.github.fourlastor.game.demo.state.machine;

import com.badlogic.gdx.ai.pfa.GraphPath;
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

public class PickMove extends BaseState {

    private final StateRouter router;

    private final Unit unit;

    @AssistedInject
    public PickMove(@Assisted Unit unit, StateRouter router) {
        this.router = router;
        this.unit = unit;
    }

    @Override
    public void enter(GameState state) {
        MapGraph localGraph = state.graph.forUnit(unit);
        for (Tile tile : state.tiles) {
            if (localGraph.getIndex(tile) == -1) {
                continue;
            }
            GraphPath<Tile> path = localGraph.calculatePath(unit.hex, tile);

            if (path.getCount() <= 1 || path.getCount() > unit.type.speed + 2) {
                continue;
            }

            boolean isRanged = path.getCount() == unit.type.speed + 2;
            Unit tileUnit = state.unitAt(tile.hex);
            if (unit == tileUnit || tileUnit == null) {
                if (isRanged) {
                    tile.actor.setColor(Color.TEAL);
                } else {
                    tile.actor.addListener(new MoveListener(tile, path));
                    tile.actor.setColor(Color.CORAL);
                }
            } else {
                if (isRanged) {
                    tileUnit.actor.setColor(Color.TEAL);
                } else {
                    tileUnit.actor.setColor(Color.CORAL);
                }
                tileUnit.actor.addListener(new AttackListener(tileUnit, isRanged));
            }
        }
    }

    @Override
    public void exit(GameState state) {
        for (Tile tile : state.tiles) {
            for (EventListener listener : tile.actor.getListeners()) {
                if (listener instanceof MoveListener) {
                    tile.actor.removeListener(listener);
                }
                tile.actor.setColor(Color.WHITE);
            }
        }
        for (Unit unit : state.units) {
            for (EventListener listener : unit.actor.getListeners()) {
                if (listener instanceof AttackListener) {
                    unit.actor.removeListener(listener);
                }
                unit.actor.setColor(Color.WHITE);
            }
        }
    }

    @AssistedFactory
    public interface Factory {
        PickMove create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;
        private final GraphPath<Tile> path;

        private MoveListener(Tile tile, GraphPath<Tile> path) {
            this.tile = tile;
            this.path = path;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.move(unit, tile, path);
        }
    }

    private class AttackListener extends ClickListener {

        private final Unit target;
        private boolean isRanged;

        private AttackListener(Unit target, boolean isRanged) {
            this.target = target;
            this.isRanged = isRanged;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (isRanged) {
                router.attackRanged(unit, target);
            } else {
                router.attackMelee(unit, target);
            }
        }
    }
}
