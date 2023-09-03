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
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import space.earlygrey.simplegraphs.algorithms.SearchStep;

public class BlobTossAbility extends Ability {

    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public BlobTossAbility(
            @Assisted UnitInRound unitInRound, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(unitInRound, router, stateFactory);
        this.unit = unitInRound.unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        Predicate<SearchStep<Tile>> movementLogic = Filter.maxDistance(unit.type.speed);
        BiPredicate<GameState, Tile> searchLogic = Filter.all(
                Filter.canReach(state.tileAt(unit.hex), movementLogic),
                Filter.ofType(TileType.SOLID).negate(),
                Filter.ofType(TileType.WATER).negate());

        return start(steps.searchTile(searchLogic))
                .then(hex -> steps.blobToss(
                        unit, state.unitAt(it -> unit.hex.equals(hex) && it != unit), state.tileAt(hex)));
    }

    @AssistedFactory
    public interface Factory {
        BlobTossAbility create(UnitInRound unitInRound);
    }
}
