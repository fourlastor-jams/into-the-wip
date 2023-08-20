package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Animated between the source and target.
 */
public class Projectile extends Image {

    public Projectile(TextureAtlas.AtlasRegion region) {
        super(region);
    }
}
