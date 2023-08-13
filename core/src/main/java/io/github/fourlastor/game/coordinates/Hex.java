package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

/**
 * Hex coordinates, in offset (even-q) and cube systems.
 * See <a href="https://www.redblobgames.com/grids/hexagons/#coordinates">original blog post</a>.
 * Cube is useful for pathfinding, offset is what we get from tiled, and it's useful for the on-screen coordinates.
 */
public class Hex {

    public final GridPoint2 offset;
    public final GridPoint3 cube;

    public Hex(GridPoint2 offset) {
        this.offset = offset;
        this.cube = toCube(offset);
    }

    public Hex(GridPoint3 cube) {
        this.cube = cube;
        this.offset = toOffset(cube);
    }

    /** x coordinate, in offset. */
    public int x() {
        return offset.x;
    }

    /** y coordinate, in offset. */
    public int y() {
        return offset.y;
    }

    /** q coordinate, in cube. */
    public int q() {
        return cube.x;
    }

    /** r coordinate, in cube. */
    public int r() {
        return cube.y;
    }

    /** s coordinate, in cube. */
    public int s() {
        return cube.z;
    }

    public static GridPoint3 toCube(GridPoint2 offset) {
        int q = offset.x;
        int r = offset.y - (offset.x + (Math.abs(offset.x % 2))) / 2;
        return new GridPoint3(q, r, -q - r);
    }

    public static GridPoint2 toOffset(GridPoint3 cube) {
        int x = cube.x;
        int y = cube.y + (cube.x + (Math.abs(cube.x % 2))) / 2;
        return new GridPoint2(x, y);
    }
}
