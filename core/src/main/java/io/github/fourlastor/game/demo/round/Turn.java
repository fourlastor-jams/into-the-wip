package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility;
import io.github.fourlastor.game.demo.round.ability.MoveAbility;
import io.github.fourlastor.game.demo.round.ability.RangedAttackAbility;
import io.github.fourlastor.game.demo.round.ability.TileSmashAbility;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.ActorSupport;

public class Turn extends RoundState {

    private final Unit unit;
    private final StateRouter router;
    private final MoveAbility.Factory moveFactory;
    private final MeleeAttackAbility.Factory meleeAttackFactory;
    private final RangedAttackAbility.Factory rangedAttackFactory;
    private final TileSmashAbility.Factory tileSmashFactory;

    private boolean acted = false;

    @AssistedInject
    public Turn(
            @Assisted Unit unit,
            StateRouter router,
            MeleeAttackAbility.Factory meleeAttackFactory,
            RangedAttackAbility.Factory rangedAttackFactory,
            MoveAbility.Factory moveFactory,
            TileSmashAbility.Factory tileSmashFactory) {
        this.unit = unit;
        this.router = router;
        this.meleeAttackFactory = meleeAttackFactory;
        this.rangedAttackFactory = rangedAttackFactory;
        this.moveFactory = moveFactory;
        this.tileSmashFactory = tileSmashFactory;
    }

    @Override
    public void enter(GameState state) {

        state.tileAt(unit.hex).actor.setColor(Color.PINK);

        if (!acted) {
            // Move unit button.
            state.ui.move.addListener(
                    new PickMoveListener(() -> router.startAbility(moveFactory.create(unit, () -> acted = false))));

            // Melee attack button.
            state.ui.meleeAttack.addListener(new PickMoveListener(
                    () -> router.startAbility(meleeAttackFactory.create(unit, () -> acted = false))));

            // Ranged attack button.
            state.ui.rangedAttack.addListener(new PickMoveListener(
                    () -> router.startAbility(rangedAttackFactory.create(unit, () -> acted = false))));

            // Tile smash ability button.
            state.ui.tileSmash.addListener(new PickMoveListener(
                    () -> router.startAbility(tileSmashFactory.create(unit, () -> acted = false))));
        } else {
            router.endOfTurn();
        }
    }

    @Override
    public void exit(GameState state) {
        state.tileAt(unit.hex).actor.setColor(Color.WHITE);
        ActorSupport.removeListeners(state.ui.meleeAttack, it -> it instanceof PickMoveListener);
        ActorSupport.removeListeners(state.ui.move, it -> it instanceof PickMoveListener);
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
        Turn create(Unit unit);
    }
}
