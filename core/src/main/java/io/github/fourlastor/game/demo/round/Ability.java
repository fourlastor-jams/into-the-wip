package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.round.step.Step;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.state.GameState;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Ability extends RoundState {
    private final StateRouter router;
    private StateMachine stateMachine;
    private final StepState.Factory stateFactory;

    public Ability(StateRouter router, StepState.Factory stateFactory) {
        this.router = router;
        this.stateFactory = stateFactory;
    }

    protected <T> Builder<T> start(Step<T> initial) {
        return new Builder<>(initial);
    }

    protected <T> Builder<T> start(T initial) {
        return new Builder<>(initial);
    }

    @Override
    public void enter(GameState state) {
        stateMachine = new StateMachine(state, new InitialState());
        createSteps().run();
    }

    protected abstract Builder<?> createSteps();

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
        } else if (GameMessage.NEXT_STEP.handles(telegram.message)) {
            stateMachine.changeState((AbilityState) telegram.extraInfo);
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

    protected class Builder<T> {

        private final Consumer<Consumer<T>> current;

        private Builder(T initial) {
            current = next -> next.accept(initial);
        }

        private Builder(Step<T> initial) {
            current = next -> router.nextStep(stateFactory.create(initial, next));
        }

        private Builder(Consumer<Consumer<T>> initial) {
            current = initial;
        }

        public <R> Builder<R> sequence(Function<T, Builder<R>> factory) {
            return new Builder<>(
                    (next) -> current.accept(result -> factory.apply(result).run()));
        }

        public <R> Builder<R> then(Step<R> next) {
            return then(ignored -> next);
        }

        public <R> Builder<R> then(Function<T, Step<R>> factory) {
            return new Builder<>((next) ->
                    current.accept(result -> router.nextStep(stateFactory.create(factory.apply(result), next))));
        }

        public void run() {
            current.accept(ignored -> router.endOfAbility());
        }
    }
}
