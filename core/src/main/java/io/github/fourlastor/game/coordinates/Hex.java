package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdxplus.math.GridPoint3;
import java.util.Arrays;
import java.util.Optional;

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

    public void set(Hex other) {
        offset.set(other.offset);
        cube.set(other.cube);
    }

    public boolean sameAxisAs(Hex hex) {
        return hex.cube.x == cube.x || hex.cube.y == cube.y || hex.cube.z == cube.z;
    }

    public Hex offset(Direction direction, int amount) {
        return new Hex(cube.cpy().add(direction.cube.cpy().scl(amount)));
    }

    public enum Direction {
        S(0, -1, 1),
        SE(1, -1, 0),
        NE(1, 0, -1),
        N(0, +1, -1),
        NW(-1, 1, 0),
        SW(-1, 0, 1);

        private final GridPoint3 cube;

        Direction(int x, int y, int z) {
            cube = new GridPoint3(x, y, z);
        }

        public static Direction byValue(GridPoint3 cube) {
            return Arrays.stream(values())
                    .filter(dir -> dir.cube.equals(cube))
                    .findFirst()
                    .get();
        }

        public static Direction byName(String value) {

            Optional<Direction> test = Arrays.stream(values())
                    .filter(dir -> dir.name().equalsIgnoreCase(value))
                    .findFirst();
            return test.orElse(null);
        }

        public Direction opposite() {
            return byValue(cube.cpy().inverse());
        }

        /**
         * Starting at 0 degrees = East direction, counter-clockwise.
         */
        public static Direction fromRotation(int degrees) {

            degrees = Math.floorMod(degrees, 360);
            if (degrees <= 60) return NE;
            else if (degrees <= 120) return N;
            else if (degrees <= 180) return NW;
            else if (degrees <= 240) return SW;
            else if (degrees <= 300) return S;
            else if (degrees <= 360) return SE;
            return null;
        }
    }
}
