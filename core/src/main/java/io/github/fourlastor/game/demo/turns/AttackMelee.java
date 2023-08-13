package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
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

    // Base animation goes left-to-right.
    Vector3[] positionsRelative = new Vector3[] {
        new Vector3(-.25f, 0f, 0f),
        new Vector3(-.125f, 0f, 0f),
        new Vector3(0f, 0f, 0f),
        new Vector3(2f, 0f, 0f),
        new Vector3(8f, 0f, 0f),
        new Vector3(8f, 0f, 0f),
        new Vector3(0f, 0f, 0f),
        new Vector3(0f, 0f, 0f),
        new Vector3(0f, 0f, 0f),
        new Vector3(0f, 0f, 0f),
        new Vector3(-3f, 0f, 16f),
        new Vector3(-3f, 0f, 8f),
        new Vector3(-3f, 0f, 0f),
        new Vector3(-3f, 0f, -8f),
        new Vector3(-3f, 0f, -16f),
    };

    @AssistedInject
    public AttackMelee(@Assisted Attack attack, StateRouter router) {
        this.router = router;
        this.source = attack.source;
        this.target = attack.target;
        this.originalPosition.set(source.getActorPosition());
    }

    // TODO: this method can be used generically.
    public static float calculateAngle(Vector2 source, Vector2 target) {
        float deltaX = target.x - source.x;
        float deltaY = target.y - source.y;

        float angleRad = MathUtils.atan2(deltaY, deltaX);
        float angleDeg = MathUtils.radiansToDegrees * angleRad;

        return angleDeg;
    }

    // TODO: this method can be used generically.
    @Override
    public void enter(GameState entity) {

        // TODO: this animation has an alignment issue.
        Vector2 nextUnitPosition = source.getActorPosition();
        SequenceAction sequence = Actions.sequence();
        float rotationDegrees = calculateAngle(nextUnitPosition, target.getActorPosition());
        float distance = nextUnitPosition.dst(target.getActorPosition());

        for (Vector3 positionRelative : positionsRelative) {

            Vector3 rotatedPositionRelative = positionRelative.cpy();
            rotatedPositionRelative.scl(scale.x, scale.y, scale.z);
            rotatedPositionRelative.scl(distance, 1f, 1f);
            rotatedPositionRelative.rotate(new Vector3(0, 0, 1), rotationDegrees);
            rotatedPositionRelative.rotate(new Vector3(1, 0, 0), 45f);
            // Note: Using rotatedPositionRelative.z instead of .y here in order to apply the jump in the y-axis.
            nextUnitPosition.add(rotatedPositionRelative.x, rotatedPositionRelative.z);
            sequence.addAction(Actions.moveToAligned(
                    nextUnitPosition.x, nextUnitPosition.y, Align.bottom, moveDuration, Interpolation.linear));
        }
        sequence.addAction(Actions.run(() -> source.setActorPosition(originalPosition)));
        source.actor.addAction(sequence);
    }

    @Override
    public void exit(GameState entity) {
        // optional cleanup
    }

    @AssistedFactory
    public interface Factory {
        AttackMelee create(Attack attack);
    }

    public static class Attack {
        public final Unit source;
        public final Unit target;

        public Attack(Unit source, Unit target) {
            this.source = source;
            this.target = target;
        }
    }
}
