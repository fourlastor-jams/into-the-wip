package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import javax.inject.Inject;
import javax.inject.Provider;

public class StateRouter {

    private final MessageDispatcher dispatcher;
    private final Provider<PickMonster> pickMonsterProvider;
    private final PickMove.Factory pickMoveFactory;
    private final Move.Factory moveFactory;
    private final AttackMelee.Factory attackMeleeFactory;
    private final AttackRanged.Factory attackRangedFactory;

    @Inject
    public StateRouter(
            MessageDispatcher dispatcher,
            Provider<PickMonster> pickMonsterProvider,
            PickMove.Factory pickMoveFactory,
            Move.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            AttackRanged.Factory attackRangedFactory) {
        this.dispatcher = dispatcher;
        this.pickMonsterProvider = pickMonsterProvider;
        this.pickMoveFactory = pickMoveFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.attackRangedFactory = attackRangedFactory;
    }

    public void pickMonster() {
        goTo(pickMonsterProvider.get());
    }

    public void pickMove(Unit unit) {
        goTo(pickMoveFactory.create(unit));
    }

    public void move(Unit unit, Tile tile) {
        goTo(moveFactory.create(unit, tile));
    }

    public void attackMelee(Unit unit, Unit target) {
        goTo(attackMeleeFactory.create(new AttackMelee.Attack(unit, target)));
    }

    public void attackRanged(Unit unit, Unit target) {
        goTo(attackRangedFactory.create(new AttackRanged.Attack(unit, target)));
    }

    private void goTo(TurnState state) {
        dispatcher.dispatchMessage(TurnMessage.SET_STATE.ordinal(), state);
    }
}
