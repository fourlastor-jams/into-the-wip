package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Objects;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SearchStep extends Step<SearchStep.Result> {

    private final Unit unit;

    @AssistedInject
    public SearchStep(@Assisted Unit unit) {
        this.unit = unit;
    }

    @Override
    public void enter(GameState state, Consumer<Result> continuation) {
        MapGraph localGraph = state.graph.forUnit(unit);
        for (Tile tile : state.tiles) {
            if (localGraph.getIndex(tile) == -1) {
                continue;
            }
            GraphPath<Tile> path = localGraph.calculatePath(unit.hex, tile);
            // path includes the tile at the unit's location, so it's 1 longer than expected
            if (path.getCount() <= 1 || path.getCount() > unit.type.speed + 1) {
                continue;
            }
            Unit tileUnit = state.unitAt(tile.hex);
            if (unit == tileUnit || tileUnit == null) {
                tile.actor.addListener(new MoveListener(tile, path, continuation));
                tile.actor.setColor(Color.CORAL);
            } else {
                tileUnit.actor.addListener(new AttackListener(tileUnit, path, continuation));
                tileUnit.actor.setColor(Color.CORAL);
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
        SearchStep create(Unit unit);
    }

    private static class MoveListener extends ClickListener {

        private final Tile tile;
        private final GraphPath<Tile> path;
        private final Consumer<Result> continuation;

        private MoveListener(Tile tile, GraphPath<Tile> path, Consumer<Result> continuation) {
            this.tile = tile;
            this.path = path;
            this.continuation = continuation;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            continuation.accept(Result.tile(tile, path));
        }
    }

    private static class AttackListener extends ClickListener {

        private final Unit target;
        private final GraphPath<Tile> path;
        private final Consumer<Result> continuation;

        private AttackListener(Unit target, GraphPath<Tile> path, Consumer<Result> continuation) {
            this.target = target;
            this.path = path;
            this.continuation = continuation;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            continuation.accept(Result.unit(target, path));
        }
    }

    public static class Result {

        @Null
        private final Unit unit;

        @Null
        private final Tile tile;

        public final GraphPath<Tile> path;

        private Result(@Null Unit unit, @Null Tile tile, GraphPath<Tile> path) {
            this.unit = unit;
            this.tile = tile;
            this.path = path;
        }

        public static Result unit(@NonNull Unit unit, GraphPath<Tile> path) {
            return new Result(unit, null, path);
        }

        public static Result tile(@NonNull Tile tile, GraphPath<Tile> path) {
            return new Result(null, tile, path);
        }

        public Unit unit() {
            return Objects.requireNonNull(unit);
        }

        public Tile tile() {
            return Objects.requireNonNull(tile);
        }
    }

    public interface Filter {
        boolean test(GameState state, Hex location);
    }
}
