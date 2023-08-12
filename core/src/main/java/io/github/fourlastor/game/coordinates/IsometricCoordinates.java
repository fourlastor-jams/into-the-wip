package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class IsometricCoordinates {
    private static final int TILE_WIDTH = 64;
    private static final float HALF_WIDTH = TILE_WIDTH / 2f;
    private static final int TILE_HEIGHT = 32;
    private static final float HALF_HEIGHT = TILE_HEIGHT / 2f;
    private static final int TILE_THICKNESS = 16;

    private static final int ORIGIN_BOTTOM = 2;
    private static final int ORIGIN_LEFT = 315;

    public static int pack(GridPoint2 position) {
        return position.x << 16 | position.y;
    }

    public static Vector2 toWorldAtOrigin(GridPoint2 map, int left, int bottom) {
        return new Vector2(left, bottom).add((map.x - map.y) * HALF_WIDTH, (map.x + map.y) * HALF_HEIGHT);
    }

    public static Vector2 toWorldAtCenter(GridPoint2 map) {
        return toWorldAtOrigin(map, ORIGIN_LEFT, ORIGIN_BOTTOM).add(HALF_WIDTH, TILE_THICKNESS + HALF_HEIGHT);
    }

    public static GridPoint2 unpack(int value, GridPoint2 target) {
        return target.set(value >> 16, (short) value);
    }
}
