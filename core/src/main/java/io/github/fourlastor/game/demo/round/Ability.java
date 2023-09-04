package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.round.step.Step;
import io.github.fourlastor.game.demo.round.step.StepState;
import io.github.fourlastor.game.demo.state.GameState;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Ability extends RoundState {

    private final UnitInRound unitInRound;
    private final StateRouter router;
    private StateMachine stateMachine;
    private final StepState.Factory stateFactory;

    public Ability(UnitInRound unitInRound, StateRouter router, StepState.Factory stateFactory) {
        this.unitInRound = unitInRound;
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
        createSteps(state)
                .run(
                        ignored -> {
                            unitInRound.hasActed = true;
                            router.endOfAbility();
                        },
                        router::endOfAbility);
    }

    protected abstract Builder<?> createSteps(GameState state);

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
        if (GameMessage.NEXT_STEP.handles(telegram.message)) {
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

        private final BiConsumer<Consumer<T>, Runnable> current;

        private Builder(T initial) {
            current = (next, cancel) -> next.accept(initial);
        }

        private Builder(Step<T> initial) {
            current = (next, cancel) -> router.nextStep(stateFactory.create(initial, next, cancel));
        }

        private Builder(BiConsumer<Consumer<T>, Runnable> initial) {
            current = initial;
        }

        public <R> Builder<R> sequence(Function<T, Builder<R>> factory) {
            return new Builder<>((next, cancel) ->
                    current.accept(result -> factory.apply(result).run(next, cancel), cancel));
        }

        public <R> Builder<R> then(Step<R> next) {
            return then(ignored -> next);
        }

        public <R> Builder<R> then(Function<T, Step<R>> factory) {
            return new Builder<>((next, cancel) -> current.accept(
                    result -> router.nextStep(stateFactory.create(factory.apply(result), next, cancel)), cancel));
        }

        public void run(Consumer<T> completion, Runnable cancellation) {
            current.accept(completion, cancellation);
        }
    }
}
