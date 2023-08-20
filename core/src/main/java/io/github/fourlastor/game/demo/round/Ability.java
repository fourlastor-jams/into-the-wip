package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class Ability extends RoundState {

    private final Unit unit;
    private final StateRouter router;
    private StateMachine stateMachine;

    @AssistedInject
    public Ability(@Assisted Unit unit, StateRouter router) {
        this.unit = unit;
        this.router = router;
    }

    @Override
    public void enter(GameState state) {
        stateMachine = new StateMachine(state, new InitialState());
        router.pickMove(unit);
    }

    @Override
    public void update(GameState state) {
        stateMachine.update();
    }

    @Override
    public void exit(GameState state) {
        stateMachine.changeState(new FinalState());
    }

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        if (GameMessage.ABILITY_PROCEED.handles(telegram.message)) {
            stateMachine.changeState(((AbilityState) telegram.extraInfo));
            return true;
        } else {
            return super.onMessage(state, telegram);
        }
    }

    private static class StateMachine extends DefaultStateMachine<GameState, AbilityState> {
        public StateMachine(GameState owner, AbilityState initialState) {
            super(owner, initialState);
        }
    }

    private static class InitialState extends AbilityState {
        @Override
        public void enter(GameState state) {}
    }

    private static class FinalState extends AbilityState {
        @Override
        public void enter(GameState state) {}
    }

    @AssistedFactory
    public interface Factory {
        Ability create(Unit unit);
    }
}
