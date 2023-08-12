package io.github.fourlastor.game.demo.state.unit;

public class Ability {

    public final int amount;
    public final int range;

    public final Type type;

    public Ability(int amount, int range, Type type) {
        this.amount = amount;
        this.range = range;
        this.type = type;
    }

    public enum Type {
        MOVE,
        ATTACK,
        HEAL
    }
}
