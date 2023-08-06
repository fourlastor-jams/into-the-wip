package io.github.fourlastor.game.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.GdxGame;
import io.github.fourlastor.game.ui.YSort;
import javax.inject.Inject;

public class DemoScreen extends ScreenAdapter {

    private final Stage stage;
    private final Viewport viewport;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Rectangle currBounds = new Rectangle(0, 0, 0, 0);

    private enum State {
        SELECT_MOVE,
        SELECT_UNIT,
        ;
    }

    Actor selectedUnit = null;
    private State state = State.SELECT_UNIT;

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

                    TextureRegion tileRegion = cell.getTile().getTextureRegion();
                    // Use the packed TextureRegion instead of the one loaded into the TiledMap.
                    TextureRegion textureRegion = new TextureRegion(
                            atlasRegion.getTexture(),
                            atlasRegion.getRegionX() + tileRegion.getRegionX(),
                            atlasRegion.getRegionY() + tileRegion.getRegionY(),
                            tileRegion.getRegionWidth(),
                            tileRegion.getRegionHeight());

                    Image image = new Image(textureRegion);
                    image.setPosition(drawX, drawY);
                    // NOTE: image.setAlign() may not work depending on how scene-2d / ysort handles rendering order
                    // (remove this comment once confirmed to work).
                    // image.setAlign(Align.center);
                    tilesGroup.addActor(image);
                    setClickListener(image, mapLayer.getName());
                }
            }
            stage.addActor(tilesGroup);
        }
        map.dispose();
    }

    private void setClickListener(Actor actor, String actorType) {
        // NOTE (SheerSt): imo the clickListener approach is hard to read
        // and (probably) debug.
        // Ideally all of the stage's click/state logic would just be in
        // one single place to solve this issue.
        // Enemy onClick listener.
        if (actorType.equals("monsters")) {
            actor.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                    // TODO: move this property to have a stronger coupling with a Tile;
                    Rectangle bounds = new Rectangle(actor.getX(), actor.getY(), 32, 32);
                    currBounds.set(bounds);

                    if (!bounds.contains(x, y)) {
                        Gdx.app.log("Click", "Monster missed!");
                        return false;
                    }

                    Gdx.app.log("Click", "Monster clicked!");
                    if (state == State.SELECT_UNIT) {
                        state = State.SELECT_MOVE;
                        selectedUnit = actor;
                        return true;
                    }
                    return false;
                }
            });
        }
        // Tile onClick listener.
        else if (actorType.equals("tileset")) {
            actor.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                    // TODO: move this property to have a stronger coupling with a Monster;
                    Rectangle bounds = new Rectangle(actor.getX(), actor.getY(), 61, 48);
                    currBounds.set(bounds);

                    if (!bounds.contains(x, y)) {
                        Gdx.app.log("Click", "Tile missed!");
                        return false;
                    }

                    Gdx.app.log("Click", "Tile clicked!");
                    if (state == State.SELECT_MOVE) {
                        // TODO: tween
                        selectedUnit.setPosition(actor.getX(), actor.getY());
                        state = State.SELECT_UNIT;
                        selectedUnit = null;
                        return true;
                    }
                    return false;
                }
            });
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

        // Debug - render shape of currently selected actor.
        if (!GdxGame.debugMode) {
            return;
        }
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Set color to red (RGBA)
        shapeRenderer.rect(currBounds.x, currBounds.y, currBounds.width, currBounds.height);
        shapeRenderer.end();
    }
}
