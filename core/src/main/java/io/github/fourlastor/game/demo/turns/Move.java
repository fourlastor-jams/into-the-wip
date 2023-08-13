package io.github.fourlastor.game.demo.turns;

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

public class Move extends TurnState {

    private final Unit unit;
    private final Tile tile;
    private final StateRouter router;

    @AssistedInject
    public Move(@Assisted Unit unit, @Assisted Tile tile, StateRouter router) {
        this.unit = unit;
        this.tile = tile;
        this.router = router;
    }

    @Override
    public void enter(GameState entity) {

        GraphPath<Tile> path = entity.graph.calculatePath(entity.graph.get(unit.position), tile);
        List<Action> actions = new ArrayList<>();
        for (int i = 1; i < path.getCount(); i++) {
            Tile pathTile = path.get(i);
            Vector2 position = unit.coordinates.toWorldAtCenter(
                    pathTile.coordinates.offset.x, pathTile.coordinates.offset.y, new Vector2());
            actions.add(Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sine));
        }
        SequenceAction steps = Actions.sequence(actions.toArray(new Action[0]));
        unit.actor.addAction(Actions.sequence(
                steps,
                Actions.run(() -> unit.position.set(tile.coordinates.offset)),
                Actions.run(router::pickMonster)));
    }

    @Override
    public void exit(GameState entity) {}

    @AssistedFactory
    public interface Factory {
        Move create(Unit unit, Tile tile);
    }
}
