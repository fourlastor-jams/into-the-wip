package io.github.fourlastor.game.demo;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.coordinates.Coordinate;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    public static final Color CLEAR_COLOR = Color.valueOf("000000");
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final int LEFT = 400;
    private static final int BOTTOM = 150;
    private final Stage stage;
    private final Viewport viewport;

    @Inject
    public DemoScreen(TextureAtlas atlas) {
        viewport = new FitViewport(800, 600);
        stage = new Stage(viewport);

        TextureRegion region = atlas.findRegion("tile-gray-tall");
        List<Image> images = new ArrayList<>(WIDTH * HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Image image = new Image(region);
                Vector2 position = Coordinate.toWorldAtOrigin(new GridPoint2(x, y), LEFT, BOTTOM);
                image.setPosition(position.x, position.y);
                images.add(image);
            }
        }
        images.sort((o1, o2) -> -Float.compare(o1.getY(), o2.getY()));
        for (Image image : images) {
            stage.addActor(image);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY, true);
        stage.act(delta);
        stage.draw();
    }
}
