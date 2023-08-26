package io.github.fourlastor.game.demo.round;

public enum GameMessage {
    ROUND_START,
    TURN_START,
    TURN_END,
    ABILITY_START,
    NEXT_STEP,
    ABILITY_END;

    public boolean handles(int code) {
        return this.ordinal() == code;
    }
}
