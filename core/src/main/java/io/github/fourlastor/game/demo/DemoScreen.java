package io.github.fourlastor.game.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.coordinates.Coordinate;
import io.github.fourlastor.game.coordinates.MapGraph;
import io.github.fourlastor.game.coordinates.Tile;
import io.github.fourlastor.scope.Group;
import io.github.fourlastor.scope.ObjectScope;
import io.github.fourlastor.scope.ScopeRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final int LEFT = 400;
    private static final int BOTTOM = 150;
    private final Stage stage;
    private final Viewport viewport;
    private final ScopeRenderer renderer = new ScopeRenderer(42);

    private final WorldSettings worldSettings = new WorldSettings();
    private final PathSettings pathSettings = new PathSettings();

    private final Group worldGroup = new Group(new ObjectScope("World", worldSettings), this::genWorld);

    private final Group pathGroup = new Group(new ObjectScope("Path", pathSettings), this::findPath);
    private final TextureAtlas atlas;
    private final Random random;
    private MapGraph graph = new MapGraph();

    public static class WorldSettings {
        public int width = WIDTH;
        public int height = HEIGHT;
        public float density = 1f;
    }

    public static class PathSettings {
        public GridPoint2 start = new GridPoint2(0, 0);
        public GridPoint2 end = new GridPoint2(WIDTH - 1, HEIGHT - 1);
    }

    @Inject
    public DemoScreen(TextureAtlas atlas, Random random) {
        this.atlas = atlas;
        this.random = random;
        viewport = new FitViewport(800, 600);
        stage = new Stage(viewport);

        genWorld();
    }

    private final IntMap<Image> images = new IntMap<>();

    private void genWorld() {
        for (Image image : images.values()) {
            image.remove();
        }
        images.clear();
        graph = new MapGraph();
        List<Tile> tiles = new ArrayList<>();
        for (int x = 0; x < worldSettings.width; x++) {
            for (int y = 0; y < worldSettings.height; y++) {
                if (random.nextFloat() >= worldSettings.density) {
                    continue;
                }
                Tile tile = new Tile(new GridPoint2(x, y));
                tiles.add(tile);
                graph.addTile(tile);
            }
        }
        for (Tile tile : tiles) {
            for (int dX = -1; dX <= 1; dX++) {
                for (int dY = -1; dY <= 1; dY++) {
                    if (dX == 0 && dY == 0 || dX != 0 && dY != 0) continue;
                    Tile other = graph.get(tile.position.x + dX, tile.position.y + dY);
                    if (other != null) {
                        graph.connect(tile, other);
                    }
                }
            }
        }

        TextureRegion region = atlas.findRegion("tile-gray-tall");
        for (Tile tile : tiles) {
            Image image = new Image(region);
            Vector2 position = Coordinate.toWorldAtOrigin(tile.position, LEFT, BOTTOM);
            image.setPosition(position.x, position.y);
            image.addAction(Actions.sequence(
                    Actions.moveBy(0, 300f),
                    Actions.delay(0.05f * (worldSettings.height - tile.position.y)),
                    Actions.moveBy(0, -300f, 1f, Interpolation.exp5Out)));
            images.put(tile.packedCoord(), image);
        }
        Array<Image> sorted = images.values().toArray();
        sorted.sort((o1, o2) -> -Float.compare(o1.getY(), o2.getY()));
        for (Image image : sorted) {
            stage.addActor(image);
        }
    }

    private void findPath() {
        for (Image image : images.values()) {
            image.setColor(Color.WHITE);
        }
        Tile from = graph.get(pathSettings.start);
        Tile to = graph.get(pathSettings.end);
        if (from == null || to == null) {
            Gdx.app.error("DemoScreen", "Invalid coordinates <" + pathSettings.start + ">, <" + pathSettings.end + ">");
        }
        GraphPath<Tile> tiles = graph.calculatePath(from, to);
        for (Tile tile : tiles) {
            images.get(tile.packedCoord()).setColor(Color.ORANGE);
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
        renderer.start();
        renderer.render(worldGroup);
        renderer.render(pathGroup);
        renderer.end();
    }
}
