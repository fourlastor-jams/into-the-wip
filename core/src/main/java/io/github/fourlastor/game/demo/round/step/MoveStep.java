package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.ArrayList;
import java.util.List;

public class MoveStep extends SimpleStep {

    private final Unit unit;
    private final Tile tile;
    private final List<Tile> path;
    private final Interpolation interpolation;

    @AssistedInject
    public MoveStep(
            @Assisted Unit unit,
            @Assisted Tile tile,
            @Assisted List<Tile> path,
            @Assisted Interpolation interpolation) {
        this.unit = unit;
        this.tile = tile;
        this.path = path;
        this.interpolation = interpolation;
    }

    @Override
    public void enter(GameState state, Runnable continuation) {

        List<Action> actions = new ArrayList<>();
        for (Tile pathTile : path) {
            Vector2 position = unit.coordinates.toWorldAtCenter(pathTile.hex, new Vector2());
            actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, interpolation));
        }
        Tile finalTile = path.get(path.size() - 1);
        Vector2 finalPosition = unit.coordinates.toWorldAtCenter(finalTile.hex, new Vector2());
        finalPosition.x -= unit.group.getWidth() / 2f;
        SequenceAction steps = Actions.sequence(actions.toArray(new Action[0]));
        unit.group.addAction(Actions.sequence(
                steps,
                Actions.run(() -> unit.hex.set(tile.hex)),
                Actions.run(() -> unit.setActorPosition(finalPosition)),
                Actions.run(continuation)));
    }

    @AssistedFactory
    public interface Factory {
        MoveStep create(Unit unit, Tile tile, List<Tile> path, Interpolation interpolation);
    }
}
