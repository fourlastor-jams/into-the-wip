package io.github.fourlastor.game.demo;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.ui.YSort;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    private final Stage stage;
    private final Viewport viewport;

    @Inject
    public DemoScreen(TextureAtlas atlas) {
        viewport = new FitViewport(512, 288);
        SpriteBatch batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        TiledMap map = new TmxMapLoader().load("maps/demo.tmx");
        int hexsidelength = map.getProperties().get("hexsidelength", Integer.class);

        for (MapLayer mapLayer : map.getLayers()) {

            if (!(mapLayer instanceof TiledMapTileLayer)) {
                continue;
            }

            TiledMapTileLayer tiles = (TiledMapTileLayer) mapLayer;

            int tileWidth = tiles.getTileWidth();
            float horizontalSpacing = (tileWidth - hexsidelength) / 2f + hexsidelength;
            float tileHeight = tiles.getTileHeight();
            float staggerSpacing = tileHeight / 2f;
            YSort tilesGroup = new YSort();
            TiledMapTileSet tileSet = map.getTileSets().getTileSet(mapLayer.getName());
            AtlasRegion atlasRegion = atlas.findRegion(mapLayer.getName());

            for (int x = 0; x < tiles.getWidth(); ++x) {
                for (int y = 0; y < tiles.getHeight(); ++y) {

                    Cell cell = tiles.getCell(x, y);
                    if (cell == null) {
                        continue;
                    }

                    float drawX = x * horizontalSpacing;
                    float stagger = x % 2 == 0 ? 0f : staggerSpacing;
                    float drawY = stagger + y * tileHeight;

                    final TiledMapTile tile = tiles.getCell(x, y).getTile();
                    TextureRegion tileRegion = tileSet.getTile(tile.getId()).getTextureRegion();
                    // Use the packed TextureRegion instead of the one loaded into the TiledMap.
                    TextureRegion textureRegion = new TextureRegion(
                            atlasRegion.getTexture(),
                            atlasRegion.getRegionX() + tileRegion.getRegionX(),
                            atlasRegion.getRegionY() + tileRegion.getRegionY(),
                            tileRegion.getRegionWidth(),
                            tileRegion.getRegionHeight());

                    Image image = new Image(textureRegion);
                    image.setPosition(drawX, drawY);
                    tilesGroup.addActor(image);
                }
            }
            stage.addActor(tilesGroup);
        }

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
