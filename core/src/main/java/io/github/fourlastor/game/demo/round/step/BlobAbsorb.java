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
import io.github.fourlastor.game.demo.state.unit.Unit;

/**
 * Ideas/notes:
 *  - Blobanera can only move one space while in absorb mode
 *  - Option - could have blobanero move into the target unit instead of vice-versa.
 *    - Might be a little more balanced.
 */
public class BlobAbsorb extends SimpleStep {

    private static final float MOVE_DURATION = 0.05f;
    private static final Vector3 SCALE = new Vector3(1f / 16f, 1f, 1f);
    private static final int DAMAGE = 2;
    private final Unit source;
    private final Unit targetUnit;

    @AssistedInject
    public BlobAbsorb(@Assisted("source") Unit source, @Assisted("target") Unit targetUnit) {
        this.source = source;
        this.targetUnit = targetUnit;
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

    public Action setupAttackAnimation(float distance, float rotationDegrees) {
        // Base animation goes left-to-right.
        Vector3[] positions = {
            new Vector3(-.125f, 0f, 0f),
            new Vector3(-.125f, 0f, 0f),
            new Vector3(-.125f, 0f, 0f),
            new Vector3(-.125f, 0f, 0f),
            new Vector3(-.125f, 0f, 0f),
            new Vector3(-.5f, 0f, 0f),
            new Vector3(-.5f, 0f, 0f),
            new Vector3(-.5f, 0f, 0f),
            new Vector3(-1f, 0f, 0f),
            new Vector3(-1f, 0f, 0f),
            new Vector3(-1f, 0f, 0f),
            new Vector3(-2f, 0f, 0f),
            new Vector3(-8f, 0f, 0f),
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
            () -> targetUnit.group.image.setColor(.5f, 1f, .5f, 1f),
        };
        Vector3 scale = SCALE.cpy().scl(distance, 1f, 1f);
        return AttackAnimation.makeSequence(
                targetUnit.group, runnables, positions, MOVE_DURATION, rotationDegrees, scale);
    }

    public void doAttackAnimation(Vector2 originalPosition, Vector2 targetPosition, Runnable continuation) {
        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(targetPosition);
        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(originalPosition, targetPosition);
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(distance, rotationDegrees),
                // Move the target unit to the source's Tile.
                Actions.run(() -> targetUnit.hex.set(source.hex)),
                Actions.run(() -> targetUnit.setActorPosition(originalPosition)),
                Actions.run(continuation));
        targetUnit.group.addAction(attackAnimation);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {
        Vector2 originalPosition = source.getActorPosition();
        Vector2 targetPosition = targetUnit.getActorPosition();
        doAttackAnimation(originalPosition, targetPosition, continuation);
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
        BlobAbsorb create(@Assisted("source") Unit source, @Assisted("target") Unit target);
    }
}
