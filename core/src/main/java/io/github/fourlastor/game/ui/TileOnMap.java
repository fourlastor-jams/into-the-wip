package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Null;

public class TileOnMap extends Image {

    public TileOnMap(@Null TextureRegion region) {
        super(region);
    }

    @Null
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() - 5 ? this : null;
    }
}
