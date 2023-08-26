package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;
import javax.inject.Provider;

public class StateRouter {

    private final MessageDispatcher dispatcher;
    private final Provider<Round> roundProvider;
    private final Turn.Factory turnFactory;
    private final MeleeAttackAbility.Factory meleeAttackFactory;

    @Inject
    public StateRouter(
            MessageDispatcher dispatcher,
            Provider<Round> roundProvider,
            Turn.Factory turnFactory,
            MeleeAttackAbility.Factory meleeAttackFactory) {
        this.dispatcher = dispatcher;
        this.roundProvider = roundProvider;
        this.turnFactory = turnFactory;
        this.meleeAttackFactory = meleeAttackFactory;
    }

    public void startAbility(Ability ability) {
        dispatcher.dispatchMessage(GameMessage.ABILITY_START.ordinal(), ability);
    }

    public void round() {
        dispatcher.dispatchMessage(GameMessage.ROUND_START.ordinal(), roundProvider.get());
    }

    public void turn(Unit unit) {
        dispatcher.dispatchMessage(GameMessage.TURN_START.ordinal(), turnFactory.create(unit));
    }

    public void endOfTurn() {
        dispatcher.dispatchMessage(GameMessage.TURN_END.ordinal());
    }

    public void nextStep(StepState<?> result) {
        dispatcher.dispatchMessage(GameMessage.NEXT_STEP.ordinal(), result);
    }

    public void endOfAbility() {
        dispatcher.dispatchMessage(GameMessage.ABILITY_END.ordinal());
    }
}
