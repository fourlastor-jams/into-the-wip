package io.github.fourlastor.game.demo.round.ability;

import com.github.tommyettinger.ds.ObjectList;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.StateRouter;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.round.step.Steps;
import io.github.fourlastor.game.demo.state.Filter;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public class MoveAbility extends Ability {

    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public MoveAbility(@Assisted Unit unit, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(router, stateFactory);
        this.unit = unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        Predicate<SearchStep<Tile>> movementLogic =
                Filter.all(Filter.maxDistance(unit.type.speed), Filter.canTravel(unit));
        BiPredicate<GameState, Tile> searchLogic = Filter.canReach(state.tileAt(unit.hex), movementLogic);
        return start(steps.searchTile(searchLogic))
                .then(hex -> steps.move(
                        unit,
                        state.tileAt(hex),
                        new ObjectList<>(
                                state.newGraph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic))));
    }

    @AssistedFactory
    public interface Factory {
        MoveAbility create(Unit unit);
    }
}
