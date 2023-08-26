package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.AttackAnimation;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class TileSmash extends SimpleStep {

    private final Unit source;
    private final Tile target;
    float moveDuration = 0.05f;
    // Amount to scale the animation by.
    private final Vector3 scale = new Vector3(1f / 16f, 1f, 1f);
    int damage = 2;
    TextureAtlas textureAtlas;

    @AssistedInject
    public TileSmash(@Assisted("source") Unit source, @Assisted("target") Tile target, TextureAtlas textureAtlas) {
        this.source = source;
        this.target = target;
        this.textureAtlas = textureAtlas;
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
                if (target != null) {
                    TextureRegion region = textureAtlas.findRegion("tiles/white");
                    target.actor.setDrawable(new TextureRegionDrawable(region));
                    target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight());
                }
            },
            null,
            null,
            null,
            null,
            null,
        };
        Vector3 scale = this.scale.cpy().scl(distance, 1f, 1f);
        return AttackAnimation.makeSequence(source.group, runnables, positions, moveDuration, rotationDegrees, scale);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {

        target.type = TileType.TERRAIN;

        Vector2 originalPosition = new Vector2(source.getActorPosition());
        Vector2 targetPosition = target.getActorPosition();
        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(targetPosition);
        // Angle offset of target from source.
        float rotationDegrees = calculateAngle(originalPosition, targetPosition);
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(distance, rotationDegrees),
                Actions.run(() -> source.setActorPosition(originalPosition)),
                Actions.run(continuation));
        source.group.addAction(attackAnimation);
    }

    @AssistedFactory
    public interface Factory {
        TileSmash create(@Assisted("source") Unit source, @Assisted("target") Tile target);
    }
}
