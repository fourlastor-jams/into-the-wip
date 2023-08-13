package io.github.fourlastor.game.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.tiles.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.turns.PickMonster;
import io.github.fourlastor.game.demo.turns.TurnStateMachine;
import io.github.fourlastor.game.ui.TileOnMap;
import io.github.fourlastor.game.ui.YSort;
import javax.inject.Inject;
import javax.inject.Provider;

public class DemoScreen extends ScreenAdapter {

    private static final String UNITS_LAYER_NAME = "monsters";
    private static final String TILES_LAYER_NAME = "terrain";
    private final Stage stage;
    private final Viewport viewport;
    private final TurnStateMachine stateMachine;

    @Inject
    public DemoScreen(
            TextureAtlas atlas,
            TurnStateMachine.Factory stateMachineFactory,
            Provider<PickMonster> pickMonsterProvider) {
        viewport = new FitViewport(512, 288);
        SpriteBatch batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        TiledMap map = new AtlasTmxMapLoader().load("maps/demo.tmx");
        int hexSideLength = map.getProperties().get("hexsidelength", Integer.class);
        ObjectList<Unit> units = new ObjectList<>();
        ObjectList<Tile> tiles = new ObjectList<>();
        for (MapLayer mapLayer : map.getLayers()) {

            if (!(mapLayer instanceof TiledMapTileLayer)) {
                continue;
            }

            TiledMapTileLayer mapTiles = (TiledMapTileLayer) mapLayer;

            int tileWidth = mapTiles.getTileWidth();
            int tileHeight = mapTiles.getTileHeight() - 1;
            HexCoordinates coordinates = new HexCoordinates(tileWidth, tileHeight, hexSideLength);
            YSort tilesGroup = new YSort();

            // NOTE: in the future there's the possibility of multiple
            // tilesets per mapLayer, this won't work in that case.
            String mapLayerName = mapLayer.getName();

            for (int x = 0; x < mapTiles.getWidth(); ++x) {
                for (int y = 0; y < mapTiles.getHeight(); ++y) {

                    Cell cell = mapTiles.getCell(x, y);
                    if (cell == null) {
                        continue;
                    }

                    TextureRegion textureRegion = cell.getTile().getTextureRegion();

                    Image image = new TileOnMap(textureRegion);
                    tilesGroup.addActor(image);
                    if (mapLayerName.equals(UNITS_LAYER_NAME)) {
                        units.add(new Unit(image, new GridPoint2(x, y), coordinates));
                        Vector2 position = coordinates.toWorldAtCenter(x, y, new Vector2());
                        image.setPosition(position.x, position.y, Align.center);
                    }
                    if (mapLayerName.equals(TILES_LAYER_NAME)) {
                        tiles.add(new Tile(image, new GridPoint2(x, y)));
                        Vector2 position = coordinates.toWorldAtOrigin(x, y, new Vector2());
                        image.setPosition(position.x, position.y - 15);
                    }
                }
            }
            tilesGroup.sortChildren();
            stage.addActor(tilesGroup);
        }
        GameState state = new GameState(units, tiles);
        stateMachine = stateMachineFactory.create(state, pickMonsterProvider.get());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY, true);
        stateMachine.update();
        viewport.apply();
        stage.act();
        stage.draw();
    }
}
