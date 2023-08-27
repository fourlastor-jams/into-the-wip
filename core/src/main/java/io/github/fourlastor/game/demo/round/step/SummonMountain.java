package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdxplus.math.Vector2;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.demo.AttackAnimation;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class SummonMountain extends SimpleStep {

    private final Unit source;
    private final Tile target;
    float moveDuration = 0.025f;
    // Amount to scale the animation by.
    private final Vector3 scale = new Vector3(1 / 8f, 1f, 1f);
    int damage = 2;
    TextureAtlas textureAtlas;

    @AssistedInject
    public SummonMountain(@Assisted("source") Unit source, @Assisted("target") Tile target, TextureAtlas textureAtlas) {
        this.source = source;
        this.target = target;
        this.textureAtlas = textureAtlas;
    }

    public Action setupAttackAnimation(float rotationDegrees) {
        // Base animation goes left-to-right.
        Vector3[] positions = {
            new Vector3(-.25f, 4f, 0f),
            new Vector3(-.25f, 4f, 0f),
            new Vector3(-.25f, 4f, 0f),
            new Vector3(-.25f, 4f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(0f, 0f, 0f),
            new Vector3(.5f, -8f, 0f),
            new Vector3(.5f, -8f, 0f),
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
            null,
            null,
            null,
            null,
            () -> {
                if (target != null) {
                    Hex.Direction facingDirection = Hex.Direction.fromRotation(rotationDegrees);
                    String regionName = "tiles/summon_mountain_NW_mockup";
                    if (facingDirection == Hex.Direction.N) regionName = "tiles/summon_mountain_N_mockup";

                    TextureRegion region = textureAtlas.findRegion(regionName);
                    target.actor.setDrawable(new TextureRegionDrawable(region));
                    target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight());
                }
            },
        };
        return AttackAnimation.makeSequence(source.group, runnables, positions, moveDuration, rotationDegrees, scale);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {

        target.type = TileType.TERRAIN;

        Vector2 originalPosition = new Vector2(source.group.getX(), source.group.getY());
        Vector2 targetPosition = target.getActorPosition();
        // Angle offset of target from source.
        float rotationDegrees = originalPosition.calculateAngle(targetPosition);
        SequenceAction attackAnimation = Actions.sequence(
                setupAttackAnimation(rotationDegrees),
                Actions.run(() -> source.setActorPosition(originalPosition)),
                Actions.run(continuation));
        source.group.addAction(attackAnimation);
    }

    @AssistedFactory
    public interface Factory {
        SummonMountain create(@Assisted("source") Unit source, @Assisted("target") Tile target);
    }
}
