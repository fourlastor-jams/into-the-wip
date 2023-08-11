package io.github.fourlastor.game.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.ui.TileOnMap;
import io.github.fourlastor.game.ui.YSort;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DemoScreen extends ScreenAdapter {

    private static final String UNITS_LAYER_NAME = "monsters";
    private static final String TILES_LAYER_NAME = "tileset";
    private final Stage stage;
    private final Viewport viewport;
    private final List<Actor> tilesActors = new ArrayList<>();
    private final List<Actor> unitActors = new ArrayList<>();
    private final List<Runnable> cleanups = new LinkedList<>();

    @Inject
    public DemoScreen(TextureAtlas atlas) {
        viewport = new FitViewport(512, 288);
        SpriteBatch batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        TiledMap map = new TmxMapLoader().load("maps/demo.tmx");
        int hexsidelength = map.getProperties().get("hexsidelength", Integer.class);

        for (MapLayer mapLayer : map.getLayers()) {

            if (!(mapLayer instanceof TiledMapTileLayer)) {
                continue;
            }

            TiledMapTileLayer tiles = (TiledMapTileLayer) mapLayer;

            int tileWidth = tiles.getTileWidth();
            float horizontalSpacing = (tileWidth - hexsidelength) / 2f + hexsidelength - 1;
            float tileHeight = tiles.getTileHeight() - 1;
            float staggerSpacing = tileHeight / 2f;
            YSort tilesGroup = new YSort();

            // NOTE: in the future there's the possibility of multiple
            // tilesets per mapLayer, this won't work in that case.
            String mapLayerName = mapLayer.getName();
            AtlasRegion atlasRegion = Objects.requireNonNull(atlas.findRegion(mapLayerName));

            for (int x = 0; x < tiles.getWidth(); ++x) {
                for (int y = 0; y < tiles.getHeight(); ++y) {

                    Cell cell = tiles.getCell(x, y);
                    if (cell == null) {
                        continue;
                    }

                    TextureRegion textureRegion = getRegionFromAtlas(cell, atlasRegion);

                    Image image = new TileOnMap(textureRegion);

                    float drawX = x * horizontalSpacing;
                    float stagger = x % 2 == 0 ? 0f : staggerSpacing;
                    float drawY = stagger + y * tileHeight;
                    image.setPosition(drawX, drawY);
                    tilesGroup.addActor(image);
                    if (mapLayerName.equals(UNITS_LAYER_NAME)) {
                        unitActors.add(image);
                    }
                    if (mapLayerName.equals(TILES_LAYER_NAME)) {
                        tilesActors.add(image);
                    }

                }
            }
            stage.addActor(tilesGroup);
        }
        map.dispose();
        selectUnitToMove();
    }

    private TextureRegion getRegionFromAtlas(Cell cell, AtlasRegion atlasRegion) {
        TextureRegion tileRegion = cell.getTile().getTextureRegion();
        // Use the packed TextureRegion instead of the one loaded into the TiledMap.
        return new TextureRegion(
                atlasRegion.getTexture(),
                atlasRegion.getRegionX() + tileRegion.getRegionX(),
                atlasRegion.getRegionY() + tileRegion.getRegionY(),
                tileRegion.getRegionWidth(),
                tileRegion.getRegionHeight());
    }

    private void selectUnitToMove() {
        for (Actor unit : unitActors) {
            ClickListener listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cleanAll();
                    selectMove(unit);
                }
            };
            unit.addListener(listener);
            cleanups.add(() -> unit.removeListener(listener));
        }
    }

    private void selectMove(Actor monster) {
        for (Actor tile : tilesActors) {
            ClickListener listener = new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cleanAll();
                    monster.addAction(Actions.sequence(
                            Actions.moveTo(tile.getX(), tile.getY(), 0.25f, Interpolation.sine),
                            Actions.run(DemoScreen.this::selectUnitToMove)
                    ));
                }
            };
            tile.addListener(listener);
            cleanups.add(() -> tile.removeListener(listener));
        }
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

    private void cleanAll() {
        for (Runnable cleanup : cleanups) {
            cleanup.run();
        }
        cleanups.clear();
    }
}
