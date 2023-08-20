package io.github.fourlastor.game.demo.state.machine;

public enum GameMessage {
    SET_STATE;

    public boolean handles(int code) {
        return this.ordinal() == code;
    }
}
