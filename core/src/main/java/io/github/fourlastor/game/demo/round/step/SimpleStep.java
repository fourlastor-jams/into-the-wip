package io.github.fourlastor.game.demo.round.step;

import io.github.fourlastor.game.demo.state.GameState;
import java.util.function.Consumer;

public abstract class SimpleStep extends Step<Void> {

    public abstract void enter(GameState state, Runnable continuation);

    @Override
    public void enter(GameState state, Consumer<Void> continuation) {
        enter(state, () -> continuation.accept(null));
    }
}
