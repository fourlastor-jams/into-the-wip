package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility;
import io.github.fourlastor.game.demo.round.ability.MoveAbility;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.ActorSupport;

public class Turn extends RoundState {

    private final Unit unit;
    private final StateRouter router;
    private final MeleeAttackAbility.Factory meleeAttackFactory;
    private final MoveAbility.Factory moveFactory;

    private boolean acted = false;

    @AssistedInject
    public Turn(
            @Assisted Unit unit,
            StateRouter router,
            MeleeAttackAbility.Factory meleeAttackFactory,
            MoveAbility.Factory moveFactory) {
        this.unit = unit;
        this.router = router;
        this.meleeAttackFactory = meleeAttackFactory;
        this.moveFactory = moveFactory;
    }

    @Override
    public void enter(GameState state) {
        if (!acted) {
            state.ui.meleeAttack.addListener(
                    new PickMoveListener(() -> router.startAbility(meleeAttackFactory.create(unit))));
            state.ui.move.addListener(new PickMoveListener(() -> router.startAbility(moveFactory.create(unit))));
        } else {
            router.endOfTurn();
        }
    }

    @Override
    public void exit(GameState state) {
        ActorSupport.removeListeners(state.ui.meleeAttack, PickMoveListener.class);
        ActorSupport.removeListeners(state.ui.move, PickMoveListener.class);
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
