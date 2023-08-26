package io.github.fourlastor.game.coordinates;

import com.badlogic.gdx.math.Vector2;

public class HexCoordinates {

    private final int width;
    private final int height;
    private final int hexSideLength;
    private final float horizontalSpacing;
    private final float staggerSpacing;

    public HexCoordinates(int tileWidth, int tileHeight, int hexSideLength) {
        this.width = tileWidth;
        this.height = tileHeight - 1;
        this.hexSideLength = hexSideLength;
        this.horizontalSpacing = (tileWidth - hexSideLength) / 2f + hexSideLength - 1;
        this.staggerSpacing = height / 2f;
    }

    public Vector2 toWorldAtOrigin(int x, int y, Vector2 out) {
        float stagger = x % 2 == 0 ? 0f : staggerSpacing;
        return out.set(x * horizontalSpacing, stagger + y * height);
    }

    public Vector2 toWorldAtCenter(Hex hex, Vector2 out) {
        return toWorldAtCenter(hex.offset.x, hex.offset.y, out);
    }

    public Vector2 toWorldAtCenter(int x, int y, Vector2 out) {
        return toWorldAtOrigin(x, y, out).add(width / 2f, height / 2f);
    }
}
