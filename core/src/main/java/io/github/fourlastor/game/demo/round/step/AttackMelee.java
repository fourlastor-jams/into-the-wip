package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.TileOnMap;

public class AttackMelee extends SimpleStep {

    private final Unit source;
    private Unit targetUnit;
    private Tile targetTile;
    float moveDuration = 0.05f;
    // Amount to scale the animation by.
    private final Vector3 scale = new Vector3(1f / 16f, 1f, 1f);
    int damage = 2;
    TextureAtlas textureAtlas;

    @AssistedInject
    public AttackMelee(@Assisted("source") Unit source, @Assisted("target") Unit targetUnit) {
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
                if (targetUnit != null) targetUnit.refreshHpLabel();
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

    public void doAttackAnimation(Vector2 originalPosition, Vector2 targetPosition, Runnable continuation) {
        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(targetPosition);
        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(originalPosition, targetPosition);
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(distance, rotationDegrees),
                Actions.run(() -> source.setActorPosition(originalPosition)),
                Actions.run(continuation));
        source.actor.addAction(attackAnimation);
    }

    public void attackUnit(Runnable continuation) {
        targetUnit.changeHp(-damage);
        Vector2 originalPosition = new Vector2(source.getActorPosition());
        Vector2 targetPosition = targetUnit.getActorPosition();
        doAttackAnimation(originalPosition, targetPosition, continuation);
    }

    public void smashTile(Runnable continuation) {
        targetTile.type = TileType.TERRAIN;
        targetTile.actor = new TileOnMap(textureAtlas.findRegion("tiles/white"));
        Vector2 originalPosition = new Vector2(source.getActorPosition());
        Vector2 targetPosition = targetTile.getActorPosition();
        doAttackAnimation(originalPosition, targetPosition, continuation);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {
        if (targetUnit != null) {
            attackUnit(continuation);
        } else if (targetTile != null) {
            smashTile(continuation);
        }
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
        AttackMelee create(@Assisted("source") Unit source, @Assisted("target") Unit target);
    }
}
