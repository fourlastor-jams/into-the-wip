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

    @Override
    public String toString() {
        return "Hex{" + "offset=" + offset + ", cube=" + cube + '}';
    }

    public static GridPoint3 toCube(GridPoint2 offset) {
        int q = offset.x;
        int r = offset.y - (offset.x - (Math.abs(offset.x % 2))) / 2;
        GridPoint3 gridPoint3 = new GridPoint3(q, r, -q - r);
        System.out.println("Convert " + offset + " to " + gridPoint3);
        return gridPoint3;
    }

    public static GridPoint2 toOffset(GridPoint3 cube) {
        int x = cube.x;
        int y = cube.y + (cube.x - (Math.abs(cube.x % 2))) / 2;
        return new GridPoint2(x, y);
    }
}
