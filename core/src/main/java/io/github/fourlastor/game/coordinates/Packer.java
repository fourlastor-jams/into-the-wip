package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.GridPoint2;

public class Packer {

    public static int pack(GridPoint2 position) {
        return position.x << 16 | position.y;
    }

    public static GridPoint2 unpack(int value, GridPoint2 target) {
        return target.set(value >> 16, (short) value);
    }
}
