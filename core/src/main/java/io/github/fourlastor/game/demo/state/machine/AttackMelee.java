package io.github.fourlastor.game.demo.state.machine;

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

public class AttackMelee extends BaseState {

    private final StateRouter router;

    private final Unit source;
    private final Unit target;
    float moveDuration = 0.05f;
    // Amount to scale the animation by.
    Vector3 scale = new Vector3(1f / 16f, 1f, 1f);
    int damage = 2;

    @AssistedInject
    public AttackMelee(@Assisted Attack attack, StateRouter router) {
        this.router = router;
        this.source = attack.source;
        this.target = attack.target;
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
        return target.cpy().sub(source).angleDeg();
    }

    public Action setupAttackAnimation(float distance, float rotationDegrees) {
        // Base animation goes left-to-right.
        Vector3[] positions = {
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
            () -> {
                if (target != null) target.refreshHpLabel();
            },
            null,
            null,
            null,
            null,
            null,
        };
        Vector3 scale = this.scale.cpy().scl(distance, 1f, 1f);
        return AttackAnimation.makeSequence(source.actor, runnables, positions, moveDuration, rotationDegrees, scale);
    }

    @Override
    public void enter(GameState state) {
        Vector2 originalPosition = new Vector2(source.getActorPosition());
        target.changeHp(-damage);
        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(target.getActorPosition());

        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(source.getActorPosition(), target.getActorPosition());
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(distance, rotationDegrees),
                Actions.run(() -> source.setActorPosition(originalPosition)),
                Actions.run(router::pickMonster));
        source.actor.addAction(attackAnimation);
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
