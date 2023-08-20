package io.github.fourlastor.game.demo.state.machine;

import com.badlogic.gdx.ai.pfa.GraphPath;
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

public class Move extends BaseState {

    private final Unit unit;
    private final Tile tile;
    private final GraphPath<Tile> path;
    private final StateRouter router;

    @AssistedInject
    public Move(@Assisted Unit unit, @Assisted Tile tile, @Assisted GraphPath<Tile> path, StateRouter router) {
        this.unit = unit;
        this.tile = tile;
        this.path = path;
        this.router = router;
    }

    @Override
    public void enter(GameState state) {
        List<Action> actions = new ArrayList<>();
        for (int i = 1; i < path.getCount(); i++) {
            Tile pathTile = path.get(i);
            Vector2 position = unit.coordinates.toWorldAtCenter(pathTile.hex, new Vector2());
            actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sine));
        }
        Tile finalTile = path.get(path.getCount() - 1);
        Vector2 finalPosition = unit.coordinates.toWorldAtCenter(finalTile.hex, new Vector2());
        finalPosition.x -= unit.actor.getWidth() / 2f;
        SequenceAction steps = Actions.sequence(actions.toArray(new Action[0]));
        unit.actor.addAction(Actions.sequence(
                steps,
                Actions.run(() -> unit.hex.set(tile.hex)),
                Actions.run(() -> unit.setActorPosition(finalPosition)),
                Actions.run(router::pickMonster)));
    }

    @Override
    public void exit(GameState state) {}

    @AssistedFactory
    public interface Factory {
        Move create(Unit unit, Tile tile, GraphPath<Tile> path);
    }
}
