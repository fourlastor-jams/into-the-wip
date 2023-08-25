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
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import space.earlygrey.simplegraphs.Path;

public class TectonSmashAbility extends Ability {

    private final Unit unit;
    private final Steps steps;

    @AssistedInject
    public TectonSmashAbility(@Assisted Unit unit, StateRouter router, StepState.Factory stateFactory, Steps steps) {
        super(router, stateFactory);
        this.unit = unit;
        this.steps = steps;
    }

    @Override
    protected Builder<?> createSteps(GameState state) {
        GraphMap.Filter movementLogic = GraphMap.Filter.all(
                step -> unit.hex.isOnSameAxisAs(step.vertex().hex), step -> unit.canTravel(step.vertex()));

        return start(steps.searchTile(unit.hex, movementLogic)).sequence(hex -> {
            Path<Tile> path = state.newGraph.path(state.tileAt(unit.hex), state.tileAt(hex), movementLogic);
            if (path.size >= 2) {
                return start(steps.move(unit, path.get(path.size - 2), movementLogic))
                        .then(steps.tileSmash(unit, state.tileAt(hex)));
            } else {
                return start(steps.tileSmash(unit, state.tileAt(hex)));
            }
        });
    }

    @AssistedFactory
    public interface Factory {
        TectonSmashAbility create(Unit unit);
    }
}
