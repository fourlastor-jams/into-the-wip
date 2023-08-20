package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Null;

public class UnitOnMap extends WidgetGroup {

    Image image;

    public UnitOnMap(@Null TextureRegion region) {
        super();
        image = new Image(region);
        addActor(image);
    }
}
