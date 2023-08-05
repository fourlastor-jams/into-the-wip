package io.github.fourlastor.game.demo;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.ui.YSort;

import java.util.Random;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    private final Stage stage;
    private final Viewport viewport;
    private final TextureAtlas atlas;
    private final Random random;
    private final TiledMapTileLayer tiles;
    private final SpriteBatch batch;

    @Inject
    public DemoScreen(TextureAtlas atlas, Random random) {
        this.atlas = atlas;
        this.random = random;
        viewport = new FitViewport(512, 288);
        batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        TiledMap map = new TmxMapLoader().load("maps/demo.tmx");
        int hexsidelength = map.getProperties().get("hexsidelength", Integer.class);
        tiles = ((TiledMapTileLayer) map.getLayers().get("Tiles"));
        int tileWidth = tiles.getTileWidth();
        float horizontalSpacing = (tileWidth - hexsidelength) / 2f + hexsidelength;
        float tileHeight = tiles.getTileHeight();
        float staggerSpacing = tileHeight / 2f;
        TextureRegion tile = atlas.findRegion("hex");
        YSort tilesGroup = new YSort();
        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                float drawX = x * horizontalSpacing;
                float stagger = x % 2 == 0 ? 0f : staggerSpacing;
                float drawY = stagger + y * tileHeight;
                Image image = new Image(tile);
                image.setPosition(drawX, drawY);
                tilesGroup.addActor(image);
            }
        }
        stage.addActor(tilesGroup);
        map.dispose();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY, true);
        viewport.apply();
        stage.act();
        stage.draw();
    }
}
