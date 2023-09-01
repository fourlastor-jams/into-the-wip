package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility;
import io.github.fourlastor.game.demo.round.ability.MoveAbility;
import io.github.fourlastor.game.demo.round.ability.PoisonAbility;
import io.github.fourlastor.game.demo.round.ability.RangedAttackAbility;
import io.github.fourlastor.game.demo.round.ability.TileSmashAbility;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.ActorSupport;

public class Turn extends RoundState {

    private final UnitInRound unitInRound;
    private final StateRouter router;
    private final MoveAbility.Factory moveFactory;
    private final MeleeAttackAbility.Factory meleeAttackFactory;
    private final RangedAttackAbility.Factory rangedAttackFactory;
    private final PoisonAbility.Factory poisonFactory;
    private final TileSmashAbility.Factory tileSmashFactory;

    private boolean acted = false;

    @AssistedInject
    public Turn(
            @Assisted UnitInRound unitInRound,
            StateRouter router,
            MeleeAttackAbility.Factory meleeAttackFactory,
            RangedAttackAbility.Factory rangedAttackFactory,
            MoveAbility.Factory moveFactory,
            PoisonAbility.Factory poisonFactory,
            TileSmashAbility.Factory tileSmashFactory) {
        this.unitInRound = unitInRound;
        this.router = router;
        this.meleeAttackFactory = meleeAttackFactory;
        this.rangedAttackFactory = rangedAttackFactory;
        this.moveFactory = moveFactory;
        this.poisonFactory = poisonFactory;
        this.tileSmashFactory = tileSmashFactory;
    }

    @Override
    public void enter(GameState state) {
        Unit unit = unitInRound.unit;
        state.tileAt(unit.hex).actor.setColor(Color.PINK);

        if (!acted) {
            // Move unit button.
            state.ui.move.addListener(new PickMoveListener(() -> router.startAbility(moveFactory.create(unitInRound))));

            // Melee attack button.
            state.ui.meleeAttack.addListener(
                    new PickMoveListener(() -> router.startAbility(meleeAttackFactory.create(unitInRound))));

            // Ranged attack button.
            state.ui.rangedAttack.addListener(
                    new PickMoveListener(() -> router.startAbility(rangedAttackFactory.create(unitInRound))));

            // Tile smash ability button.
            state.ui.tileSmash.addListener(
                    new PickMoveListener(() -> router.startAbility(tileSmashFactory.create(unitInRound))));
            // Poison.
            state.ui.poison.addListener(
                    new PickMoveListener(() -> router.startAbility(poisonFactory.create(unitInRound))));
        } else {
            router.endOfTurn();
        }
    }

    @Override
    public void update(GameState state) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            router.endOfTurn();
        }
    }

    @Override
    public void exit(GameState state) {
        state.tileAt(unitInRound.unit.hex).actor.setColor(Color.WHITE);
        ActorSupport.removeListeners(state.ui.meleeAttack, it -> it instanceof PickMoveListener);
        ActorSupport.removeListeners(state.ui.move, it -> it instanceof PickMoveListener);
        ActorSupport.removeListeners(state.ui.rangedAttack, it -> it instanceof PickMoveListener);
        ActorSupport.removeListeners(state.ui.tileSmash, it -> it instanceof PickMoveListener);
        ActorSupport.removeListeners(state.ui.poison, it -> it instanceof PickMoveListener);
    }

    private class PickMoveListener extends ClickListener {

        private final Runnable onMove;

        private PickMoveListener(Runnable onMove) {
            this.onMove = onMove;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            acted = true;
            onMove.run();
        }
    }

    @AssistedFactory
    public interface Factory {
        Turn create(UnitInRound unit);
    }
}
