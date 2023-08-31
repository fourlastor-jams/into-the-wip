package io.github.fourlastor.game.demo.round.ability;

import com.badlogic.gdx.math.Interpolation;
import com.github.tommyettinger.ds.ObjectList;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.StateRouter;
import io.github.fourlastor.game.demo.round.UnitInTurn;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.round.step.Steps;
import io.github.fourlastor.game.demo.state.Filter;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public class TileSmashAbility extends Ability {

    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public TileSmashAbility(
            @Assisted UnitInTurn unitInTurn, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(unitInTurn, router, stateFactory);
        this.unit = unitInTurn.unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        Predicate<SearchStep<Tile>> movementLogic = Filter.all(
                Filter.sameAxisAs(unit.hex),
                step -> unit.canTravel(step.previous()) && step.vertex().type == TileType.SOLID);
        BiPredicate<GameState, Tile> searchLogic =
                Filter.all(Filter.canReach(state.tileAt(unit.hex), movementLogic), Filter.ofType(TileType.SOLID));

        return start(steps.searchTile(searchLogic)).sequence(hex -> {
            List<Tile> path =
                    new ObjectList<>(state.graph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic));
            if (path.size() >= 2) {
                return start(steps.move(unit, path.get(path.size() - 2), path, Interpolation.linear))
                        .then(steps.tileSmash(unit, state.tileAt(hex)));
            } else {
                return start(steps.tileSmash(unit, state.tileAt(hex)));
            }
        });
    }

    @AssistedFactory
    public interface Factory {
        TileSmashAbility create(UnitInTurn unitInTurn);
    }
}
