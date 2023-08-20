package io.github.fourlastor.game.demo.round;

import java.util.Arrays;

public enum GameMessage {
    ROUND_START,
    TURN_START,
    TURN_END,
    ABILITY_START,
    ABILITY_PROCEED,
    ABILITY_END;

    public boolean handles(int code) {
        return this.ordinal() == code;
    }

    public static GameMessage parse(int code) {
        return Arrays.stream(values())
                .filter(it -> it.ordinal() == code)
                .findFirst()
                .get();
    }
}
