package io.github.fourlastor.game.demo.turns;

public enum TurnMessage {
    SET_STATE;

    public boolean handles(int code) {
        return this.ordinal() == code;
    }
}
