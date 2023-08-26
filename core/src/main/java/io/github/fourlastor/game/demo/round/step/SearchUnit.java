package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.GraphMap;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.List;
import java.util.function.Consumer;

public class SearchUnit extends Step<Hex> {

    private final Hex hex;
    private final GraphMap.Filter filter;

    @AssistedInject
    public SearchUnit(@Assisted Hex hex, @Assisted GraphMap.Filter filter) {
        this.hex = hex;
        this.filter = filter;
    }

    @Override
    public void enter(GameState state, Consumer<Hex> continuation) {
        List<Tile> searched = state.newGraph.search(state.tileAt(hex), filter);
        for (Tile tile : searched) {
            if (tile.hex.equals(hex)) {
                continue;
            }
            Unit unit = state.unitAt(tile.hex);
            if (unit != null) {
                unit.group.image.addListener(new SearchListener(unit, continuation));
                unit.group.image.setColor(Color.CORAL);
            }
        }
    }

    @Override
    public void exit(GameState state) {
        for (Unit unit : state.units) {
            for (EventListener listener : unit.group.image.getListeners()) {
                if (listener instanceof SearchListener) {
                    unit.group.image.removeListener(listener);
                }
                unit.group.image.setColor(Color.WHITE);
            }
        }
    }

    @AssistedFactory
    public interface Factory {
        SearchUnit create(Hex hex, GraphMap.Filter filter);
    }

    private static class SearchListener extends ClickListener {

        private final Unit unit;
        private final Consumer<Hex> continuation;

        private SearchListener(Unit unit, Consumer<Hex> continuation) {
            this.unit = unit;
            this.continuation = continuation;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            continuation.accept(unit.hex);
        }
    }
}
