package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    private final TextureAtlas textureAtlas;

    @Inject
    public StateRouter(
            MessageDispatcher dispatcher,
            Provider<PickMonster> pickMonsterProvider,
            PickMove.Factory pickMoveFactory,
            Move.Factory moveFactory,
            AttackMelee.Factory attackMeleeFactory,
            AttackRanged.Factory attackRangedFactory,
            TextureAtlas textureAtlas) {
        this.dispatcher = dispatcher;
        this.pickMonsterProvider = pickMonsterProvider;
        this.pickMoveFactory = pickMoveFactory;
        this.moveFactory = moveFactory;
        this.attackMeleeFactory = attackMeleeFactory;
        this.attackRangedFactory = attackRangedFactory;
        this.textureAtlas = textureAtlas;
    }

    public void pickMonster() {
        goTo(pickMonsterProvider.get());
    }

    public void pickMove(Unit unit) {
        goTo(pickMoveFactory.create(unit));
    }

    public void move(Unit unit, Tile tile, GraphPath<Tile> path) {
        goTo(moveFactory.create(unit, tile, path));
    }

    public void attackMelee(Unit unit, Unit target) {
        goTo(attackMeleeFactory.create(new AttackMelee.Attack(unit, target)));
    }

    public void attackRanged(Unit unit, Unit target) {
        goTo(attackRangedFactory.create(new AttackRanged.Attack(unit, target), textureAtlas));
    }

    private void goTo(TurnState state) {
        dispatcher.dispatchMessage(TurnMessage.SET_STATE.ordinal(), state);
    }
}
