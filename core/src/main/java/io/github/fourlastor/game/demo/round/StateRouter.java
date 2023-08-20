package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.pfa.GraphPath;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;
import javax.inject.Provider;

public class StateRouter {

    private final MessageDispatcher dispatcher;
    private final Provider<Round> roundProvider;
    private final Turn.Factory turnFactory;
    private final PickMove.Factory pickMoveFactory;
    private final Move.Factory moveFactory;
    private final AttackMelee.Factory attackMeleeFactory;
    private final Ability.Factory abilityFactory;

    @Inject
    public StateRouter(
            MessageDispatcher dispatcher,
            Provider<Round> roundProvider,
            Turn.Factory turnFactory,
            PickMove.Factory pickMoveFactory,
            Move.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            Ability.Factory abilityFactory) {
        this.dispatcher = dispatcher;
        this.roundProvider = roundProvider;
        this.turnFactory = turnFactory;
        this.pickMoveFactory = pickMoveFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.abilityFactory = abilityFactory;
    }

    public void startAbility(Unit unit) {
        dispatcher.dispatchMessage(GameMessage.ABILITY_START.ordinal(), abilityFactory.create(unit));
    }

    public void pickMove(Unit unit) {
        ability(pickMoveFactory.create(unit));
    }

    public void move(Unit unit, Tile tile, GraphPath<Tile> path) {
        ability(moveFactory.create(unit, tile, path));
    }

    public void attackMelee(Unit unit, Unit target) {
        ability(attackMeleeFactory.create(new AttackMelee.Attack(unit, target)));
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

    private void ability(AbilityState state) {
        dispatcher.dispatchMessage(GameMessage.ABILITY_PROCEED.ordinal(), state);
    }

    public void endOfAction() {
        dispatcher.dispatchMessage(GameMessage.ABILITY_END.ordinal());
    }
}
