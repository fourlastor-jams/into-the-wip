package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

/**
 * Hex coordinates, in offset (odd-q) and cube systems.
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

    public static GridPoint3 toCube(GridPoint2 offset) {
        int q = offset.x;
        int r = offset.y - (offset.x - (Math.abs(offset.x % 2))) / 2;
        return new GridPoint3(q, r, -q - r);
    }

    public static GridPoint2 toOffset(GridPoint3 cube) {
        int x = cube.x;
        int y = cube.y + (cube.x - (Math.abs(cube.x % 2))) / 2;
        return new GridPoint2(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hex hex = (Hex) o;

        if (!offset.equals(hex.offset)) return false;
        return cube.equals(hex.cube);
    }

    @Override
    public int hashCode() {
        int result = offset.hashCode();
        result = 31 * result + cube.hashCode();
        return result;
    }

    public boolean isOnSameAxisAs(Hex other) {
        return cube.x == other.cube.x || cube.y == other.cube.y || cube.z == other.cube.z;
    }

    public void set(Hex other) {
        offset.set(other.offset);
        cube.set(other.cube);
    }
}
