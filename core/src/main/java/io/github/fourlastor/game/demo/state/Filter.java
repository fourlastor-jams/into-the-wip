package io.github.fourlastor.game.demo.state;

import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public interface Filter {
    static BiPredicate<GameState, Tile> ofType(TileType type) {
        return (state, tile) -> tile.type == type;
    }

    static <T> Predicate<T> all(Predicate<T>... filters) {
        return Arrays.stream(filters).reduce(Predicate::and).orElse(a -> true);
    }

    static <T> Predicate<T> any(Predicate<T>... filters) {
        return Arrays.stream(filters).reduce(Predicate::or).orElse(a -> true);
    }

    static <T, R> BiPredicate<T, R> all(BiPredicate<T, R>... filters) {
        return Arrays.stream(filters).reduce(BiPredicate::and).orElse((a, b) -> true);
    }

    static <T, R> BiPredicate<T, R> any(BiPredicate<T, R>... filters) {
        return Arrays.stream(filters).reduce(BiPredicate::or).orElse((a, b) -> true);
    }

    static BiPredicate<GameState, Tile> canReach(Tile origin, Predicate<SearchStep<Tile>> filter) {
        return (state, tile) -> !state.graph.path(origin, tile, filter).isEmpty();
    }

    /* Movement filters - directly on SearchStep<Tile>. */
    static Predicate<SearchStep<Tile>> maxDistance(int distance) {
        return step -> step.distance() <= distance;
    }

    static Predicate<SearchStep<Tile>> atDistance(int distance) {
        return step -> step.distance() == distance;
    }

    static Predicate<SearchStep<Tile>> canTravel(Unit unit) {
        return step -> unit.canTravel(step.vertex());
    }

    static Predicate<SearchStep<Tile>> sameAxisAs(Hex hex) {
        return step -> step.vertex().hex.sameAxisAs(hex);
    }

    static BiPredicate<GameState, Tile> hasUnit() {
        return (state, tile) -> state.unitAt(tile.hex) != null;
    }
}
