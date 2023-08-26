package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class UnitOnMap extends WidgetGroup {

    public Image image;

    public UnitOnMap(@Null TextureRegion region) {
        super();
        image = new Image(region);
        image.setAlign(Align.bottom);
        // Center the image horizontally.
        float imageX = getWidth() / 2 - image.getWidth() / 2;
        image.setPosition(imageX, image.getY());
        addActor(image);
    }
}
