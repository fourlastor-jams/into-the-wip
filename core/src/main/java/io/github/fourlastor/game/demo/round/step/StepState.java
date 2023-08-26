package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.demo.round.AbilityState;
import io.github.fourlastor.game.demo.state.GameState;
import java.util.function.Consumer;
import javax.inject.Inject;

public class StepState<T> extends AbilityState {

    private final Step<T> step;
    private final Consumer<T> next;
    private final Runnable cancel;

    public StepState(Step<T> step, Consumer<T> next, Runnable cancel) {
        this.step = step;
        this.next = next;
        this.cancel = cancel;
    }

    @Override
    public void enter(GameState state) {
        step.enter(state, next, cancel);
    }

    @Override
    public void update(GameState state) {
        step.update(state, next, cancel);
    }

    @Override
    public void exit(GameState state) {
        step.exit(state);
    }

    @Override
    public boolean onMessage(GameState state, Telegram telegram) {
        return step.onMessage(state, telegram);
    }

    public static class Factory {

        @Inject
        public Factory() {}

        public <T> StepState<T> create(Step<T> step, Consumer<T> next, Runnable cancel) {
            return new StepState<>(step, next, cancel);
        }
    }
}
