package io.github.fourlastor.game.demo.round.ability;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.StateRouter;
import io.github.fourlastor.game.demo.round.UnitInRound;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.round.step.Steps;
import io.github.fourlastor.game.demo.state.Filter;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public class SummonMountainAbility extends Ability {
    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public SummonMountainAbility(
            @Assisted UnitInRound unitInRound, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(unitInRound, router, stateFactory);
        this.unit = unitInRound.unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        Predicate<SearchStep<Tile>> movementLogic =
                Filter.all(Filter.sameAxisAs(unit.hex), Filter.maxDistance(unit.type.speed));
        BiPredicate<GameState, Tile> searchLogic = Filter.all(
                Filter.canReach(state.tileAt(unit.hex), movementLogic), (unused, tile) -> unit.canTravel(tile));

        return start(steps.searchTile(searchLogic)).then(hex -> steps.summonMountain(unit, state.tileAt(hex)));
    }

    @AssistedFactory
    public interface Factory {
        SummonMountainAbility create(UnitInRound unitInRound);
    }
}
