package io.github.fourlastor.game.demo;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.round.GameStateMachine;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.UnitType;
import io.github.fourlastor.game.ui.TileOnMap;
import io.github.fourlastor.game.ui.UnitOnMap;
import io.github.fourlastor.game.ui.YSort;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    private static final String UNITS_LAYER_NAME = "monsters";
    private static final String TILES_LAYER_NAME = "terrain";
    private final Stage stage;
    private final InputMultiplexer multiplexer;
    private final Viewport viewport;
    private final GameStateMachine stateMachine;
    private final GameState state;

    @Inject
    public DemoScreen(
            GameStateMachine.Factory stateMachineFactory,
            AssetManager assetManager,
            Viewport viewport,
            Stage stage,
            InputMultiplexer multiplexer) {
        this.viewport = viewport;
        this.stage = stage;
        this.multiplexer = multiplexer;
        Label.LabelStyle hpLabelStyle = new Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.RED);
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
            YSort ySort = new YSort();

            // NOTE: in the future there's the possibility of multiple
            // tilesets per mapLayer, this won't work in that case.
            String mapLayerName = mapLayer.getName();

            for (int x = 0; x < mapTiles.getWidth(); ++x) {
                for (int y = 0; y < mapTiles.getHeight(); ++y) {

                    Cell cell = mapTiles.getCell(x, y);
                    if (cell == null) {
                        continue;
                    }

                    TiledMapTile cellTile = cell.getTile();
                    TextureRegion textureRegion = cellTile.getTextureRegion();

                    if (mapLayerName.equals(UNITS_LAYER_NAME)) {
                        Vector2 position = coordinates.toWorldAtCenter(x, y, new Vector2());
                        UnitOnMap unitOnMap = new UnitOnMap(textureRegion);
                        String mapUnitType = cellTile.getProperties().get("unit", String.class);
                        unitOnMap.setPosition(position.x, position.y, Align.bottom);
                        // Set up the Hp bar Label.
                        Label hpLabel = new Label("", hpLabelStyle);
                        hpLabel.setAlignment(Align.center);
                        Unit unit = new Unit(
                                unitOnMap, hpLabel, new GridPoint2(x, y), coordinates, UnitType.fromMap(mapUnitType));
                        ySort.addActor(unitOnMap);
                        ySort.addActor(hpLabel);
                        units.add(unit);
                    }
                    if (mapLayerName.equals(TILES_LAYER_NAME)) {
                        Vector2 position = coordinates.toWorldAtOrigin(x, y, new Vector2());
                        TileOnMap tileOnMap = new TileOnMap(textureRegion);
                        tileOnMap.setPosition(position.x, position.y - 15);
                        ySort.addActor(tileOnMap);
                        String mapTileType = cellTile.getProperties().get("tile", String.class);
                        Tile tile = new Tile(tileOnMap, new GridPoint2(x, y), TileType.fromMap(mapTileType));
                        tiles.add(tile);
                    }
                }
            }
            ySort.sortChildren();
            this.stage.addActor(ySort);
        }
        WidgetGroup ui = new WidgetGroup();
        ui.setFillParent(true);
        stage.addActor(ui);
        state = new GameState(units, tiles, ui);
        stateMachine = stateMachineFactory.create(state);
    }

    @Override
    public void show() {
        multiplexer.addProcessor(stage);
    }

    @Override
    public void hide() {
        multiplexer.removeProcessor(stage);
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
        state.alignAllHpBars();
        stage.draw();
    }
}
