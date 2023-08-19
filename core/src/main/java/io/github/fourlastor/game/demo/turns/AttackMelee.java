package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.KeyFrameAnimation;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class AttackMelee extends TurnState {

    private final StateRouter router;

    private final Unit source;
    private final Unit target;
    Vector2 originalPosition = new Vector2();
    float moveDuration = 0.05f;
    // Amount to scale the animation by.
    Vector3 scale = new Vector3(1f / 16f, 1f, 1f);
    int damage = 2;

    @AssistedInject
    public AttackMelee(@Assisted Attack attack, StateRouter router) {
        this.router = router;
        this.source = attack.source;
        this.target = attack.target;
        this.originalPosition.set(source.getActorPosition());
    }

    /**
     * TODO: This functionality can be used generically. Unsure where to move it to.
     *
     * Calculate the angle in degrees between two 2D vectors.
     *
     * @param source The source vector.
     * @param target The target vector.
     * @return The angle in degrees between the vectors.
     */
    public static float calculateAngle(Vector2 source, Vector2 target) {
        float deltaX = target.x - source.x;
        float deltaY = target.y - source.y;

        float angleRad = MathUtils.atan2(deltaY, deltaX);
        float angleDeg = MathUtils.radiansToDegrees * angleRad;

        source.angleDeg(target);

        return angleDeg;
    }

    public KeyFrameAnimation setupAttackAnimation(float distance, float rotationDegrees) {
        // Base animation goes left-to-right.
        KeyFrameAnimation attackAnimation = new KeyFrameAnimation(source.image);
        attackAnimation.positionsRelative = new Vector3[] {
            new Vector3(-.25f, 0f, 0f),
            new Vector3(-.125f, 0f, 0f),
            new Vector3(0f, 0f, 2f),
            new Vector3(2f, 0f, 0f),
            new Vector3(8f, 0f, 1f),
            new Vector3(8f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, -1f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, -2f),
            new Vector3(-3f, 0f, 16f),
            new Vector3(-3f, 0f, 8f),
            new Vector3(-3f, 0f, 0f),
            new Vector3(-3f, 0f, -8f),
            new Vector3(-3f, 0f, -16f),
        };
        attackAnimation.runnables = new Runnable[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            () -> {
                if (target != null) target.refreshHpLabel();
            },
            null,
            null,
            null,
            null,
            null,
        };
        attackAnimation.scale.set(scale.cpy().scl(distance, 1f, 1f));
        attackAnimation.moveDuration = moveDuration;
        attackAnimation.rotationDegrees = rotationDegrees;
        attackAnimation.makeSequence();
        return attackAnimation;
    }

    @Override
    public void enter(GameState entity) {

        // (sheerst) Note: this is model code, does it go here?
        target.changeHp(-damage, false);

        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(target.getActorPosition());
        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(source.getActorPosition(), target.getActorPosition());
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(distance, rotationDegrees),
                Actions.run(() -> source.setActorPosition(originalPosition)),
                Actions.run(router::pickMonster));
        // After movement, reset the sources's position to it's original position.
        source.image.addAction(attackAnimation);
    }

    @Override
    public void exit(GameState entity) {
        // optional cleanup
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    public interface Factory {
        AttackMelee create(Attack attack);
    }

    /**
     * Represents an attack event with source and target units.
     */
    public static class Attack {
        public final Unit source;
        public final Unit target;

        public Attack(Unit source, Unit target) {
            this.source = source;
            this.target = target;
        }
    }
}
