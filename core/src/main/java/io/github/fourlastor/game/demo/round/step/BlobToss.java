package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.AttackAnimation;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class BlobToss extends SimpleStep {

    private static final float MOVE_DURATION = 0.05f;
    private static final Vector3 SCALE = new Vector3(1f / 16f, 1f, 1f);
    private static final int DAMAGE = 2;
    private final Unit source;
    private final Unit targetUnit;
    private final Tile targetTile;

    @AssistedInject
    public BlobToss(
            @Assisted("source") Unit source, @Assisted("target") Unit targetUnit, @Assisted("target") Tile targetTile) {
        this.source = source;
        this.targetUnit = targetUnit;
        this.targetTile = targetTile;
    }

    /**
     * Calculate the angle in degrees between two 2D vectors.
     *
     * @param source The source vector.
     * @param target The target vector.
     * @return The angle in degrees between the vectors.
     */
    public static float calculateAngle(Vector2 source, Vector2 target) {
        return target.cpy().sub(source).angleDeg();
    }

    public Action setupAttackAnimation(GameState state, float distance, float rotationDegrees) {
        // Base animation goes left-to-right.
        Vector3[] positions = {
            new Vector3(1f, 0f, -.5f),
            new Vector3(1f, 0f, -.4f),
            new Vector3(1f, 0f, -.3f),
            new Vector3(1f, 0f, -.2f),
            new Vector3(1f, 0f, -.1f),
            new Vector3(1f, 0f, 0f),
            new Vector3(1f, 0f, 0f),
            new Vector3(1f, 0f, -.1f),
            new Vector3(1f, 0f, -.2f),
            new Vector3(1f, 0f, -.3f),
            new Vector3(1f, 0f, -.4f),
            new Vector3(1f, 0f, -.5f),
        };
        Runnable[] runnables = new Runnable[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            // Damage all mons on that tile.
            () -> state.units.forEach(unit -> unit.changeHp(-2)),
        };
        Vector3 scale = SCALE.cpy().scl(distance, 1f, 1f);
        return AttackAnimation.makeSequence(
                targetUnit.group, runnables, positions, MOVE_DURATION, rotationDegrees, scale);
    }

    public void doAttackAnimation(
            GameState state, Vector2 originalPosition, Vector2 targetPosition, Runnable continuation) {
        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(targetPosition);
        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(originalPosition, targetPosition);
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(state, distance, rotationDegrees),
                // Move the target unit to the source's Tile.
                Actions.run(() -> targetUnit.hex.set(targetTile.hex)),
                Actions.run(() -> targetUnit.setActorPosition(targetPosition)),
                Actions.run(continuation));
        targetUnit.group.addAction(attackAnimation);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {
        Vector2 originalPosition = source.getActorPosition();
        Vector2 targetPosition = targetUnit.getActorPosition();
        doAttackAnimation(state, originalPosition, targetPosition, continuation);
    }

    @Override
    public void exit(GameState state) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    public interface Factory {
        BlobToss create(
                @Assisted("source") Unit source,
                @Assisted("target") Unit targetUnit,
                @Assisted("target") Tile targetTile);
    }
}
