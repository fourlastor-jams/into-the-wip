package io.github.fourlastor.game.demo.round.ability;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.StateRouter;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.round.step.Steps;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.GraphMap;
import io.github.fourlastor.game.demo.state.unit.Unit;

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
        GraphMap.Filter movementLogic = GraphMap.Filter.all(
                step -> step.depth() <= unit.type.speed,
                step -> unit.canTravel(step.vertex()),
                step -> state.unitAt(step.vertex().hex) == null);
        return start(steps.searchTile(unit.hex, movementLogic))
                .then(result -> steps.move(unit, state.tileAt(result), movementLogic));
    }

    @AssistedFactory
    public interface Factory {
        MoveAbility create(Unit unit);
    }
}
