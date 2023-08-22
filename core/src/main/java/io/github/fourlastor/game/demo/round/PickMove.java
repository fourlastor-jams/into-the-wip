package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.MapGraph;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class PickMove extends AbilityState {

    private final StateRouter router;

    private final Unit unit;
    private TextureAtlas textureAtlas;
    Image arrow;

    @AssistedInject
    public PickMove(@Assisted Unit unit, TextureAtlas textureAtlas, StateRouter router) {
        this.unit = unit;
        this.textureAtlas = textureAtlas;
        this.router = router;
    }

    // TODO: need @fourlastor's help to move this code.
    private Color fromHex(String hexValue) {
        if (hexValue.startsWith("#")) hexValue = hexValue.substring(1);
        float r = (float) Integer.parseInt(hexValue.substring(0, 2), 16) / 255f;
        float g = (float) Integer.parseInt(hexValue.substring(2, 4), 16) / 255f;
        float b = (float) Integer.parseInt(hexValue.substring(4, 6), 16) / 255f;
        return new Color(r, g, b, 1f);
    }

    @Override
    public void enter(GameState state) {

        arrow = new Image(textureAtlas.findRegion("arrow-down1"));
        arrow.setColor(Color.GREEN);
        arrow.setPosition(0, 55, Align.center);
        unit.group.addActor(arrow);

        MapGraph localGraph = state.graph.forUnit(unit);
        for (Tile tile : state.tiles) {
            if (localGraph.getIndex(tile) == -1) {
                continue;
            }
            GraphPath<Tile> path = localGraph.calculatePath(unit.hex, tile);

            if (path.getCount() <= 1 || path.getCount() > unit.type.speed + 2) {
                continue;
            }

            boolean isRanged = path.getCount() == unit.type.speed + 2;
            Unit tileUnit = state.unitAt(tile.hex);
            if (unit == tileUnit || tileUnit == null) {
                if (isRanged) {
                    tile.actor.setColor(fromHex("#03c2fc"));
                } else {
                    tile.actor.addListener(new MoveListener(tile, path));
                    tile.actor.setColor(Color.CORAL);
                }
            } else {
                if (isRanged) {
                    tile.actor.setColor(fromHex("#03c2fc"));
                } else {
                    tileUnit.group.setColor(Color.CORAL);
                }
                tileUnit.group.addListener(new AttackListener(tileUnit, isRanged));
            }
        }
    }

    @Override
    public void exit(GameState state) {
        arrow.remove();
        for (Tile tile : state.tiles) {
            for (EventListener listener : tile.actor.getListeners()) {
                if (listener instanceof MoveListener) {
                    tile.actor.removeListener(listener);
                }
            }
            tile.actor.setColor(Color.WHITE);
        }
        for (Unit unit : state.units) {
            for (EventListener listener : unit.group.getListeners()) {
                if (listener instanceof AttackListener) {
                    unit.group.removeListener(listener);
                }
            }
            unit.group.setColor(Color.WHITE);
        }
    }

    @AssistedFactory
    public interface Factory {
        PickMove create(Unit unit);
    }

    private class MoveListener extends ClickListener {

        private final Tile tile;
        private final GraphPath<Tile> path;

        private MoveListener(Tile tile, GraphPath<Tile> path) {
            this.tile = tile;
            this.path = path;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.move(unit, tile, path);
        }
    }

    private class AttackListener extends ClickListener {

        private final Unit target;
        private boolean isRanged;

        private AttackListener(Unit target, boolean isRanged) {
            this.target = target;
            this.isRanged = isRanged;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (isRanged) {
                router.attackRanged(unit, target);
            } else {
                router.attackMelee(unit, target);
            }
        }
    }
}
