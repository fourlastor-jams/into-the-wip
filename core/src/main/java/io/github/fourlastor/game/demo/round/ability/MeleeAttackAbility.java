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

public class MeleeAttackAbility extends Ability {

    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public MeleeAttackAbility(@Assisted Unit unit, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(router, stateFactory);
        this.unit = unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        Predicate<SearchStep<Tile>> movementLogic =
                Filter.all(Filter.maxDistance(unit.type.speed), Filter.canTravel(unit));
        BiPredicate<GameState, Tile> searchLogic = Filter.all(
                Filter.canReach(state.tileAt(unit.hex), movementLogic),
                Filter.hasUnit(),
                (ignored, tile) -> !unit.hex.equals(tile.hex));
        return start(steps.searchUnit(searchLogic)).sequence(hex -> {
            ObjectList<Tile> path =
                    new ObjectList<>(state.graph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic));
            if (path.size() >= 2) {
                int tileIndex = path.size() - 1;
                return start(steps.move(unit, path.get(tileIndex), path.subList(0, tileIndex)))
                        .then(steps.attackMelee(unit, state.unitAt(hex)));
            } else {
                return start(steps.attackMelee(unit, state.unitAt(hex)));
            }
        });
    }

    @AssistedFactory
    public interface Factory {
        MeleeAttackAbility create(Unit unit);
    }
}
