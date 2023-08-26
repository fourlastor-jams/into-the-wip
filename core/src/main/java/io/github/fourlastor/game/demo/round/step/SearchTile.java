package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.ui.ActorSupport;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SearchTile extends Step<Hex> {

    private final BiPredicate<GameState, Tile> filter;

    @AssistedInject
    public SearchTile(@Assisted BiPredicate<GameState, Tile> filter) {
        this.filter = filter;
    }

    @Override
    public void enter(GameState state, Consumer<Hex> continuation) {
        List<Tile> searched = state.search(filter);
        Gdx.app.log("SearchTile", "Found " + searched.size());
        for (Tile tile : searched) {
            tile.actor.addListener(new SearchListener(tile, continuation));
            tile.actor.setColor(Color.CORAL);
        }
    }

    @Override
    public void exit(GameState state) {
        for (Tile tile : state.tiles) {
            if (ActorSupport.removeListeners(tile.actor, it -> it instanceof SearchListener)) {
                tile.actor.setColor(Color.WHITE);
            }
        }
    }

    @AssistedFactory
    public interface Factory {
        SearchTile create(BiPredicate<GameState, Tile> filter);
    }

    private static class SearchListener extends ClickListener {

        private final Tile tile;
        private final Consumer<Hex> continuation;

        private SearchListener(Tile tile, Consumer<Hex> continuation) {
            this.tile = tile;
            this.continuation = continuation;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            continuation.accept(tile.hex);
        }
    }
}
