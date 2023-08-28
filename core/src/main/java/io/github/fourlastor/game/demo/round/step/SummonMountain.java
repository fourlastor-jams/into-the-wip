package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    private Hex.Direction facingDirection;
    private Unit hitUnit;

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

                    // (sheerst) Note: likely need to use N/S/E/W etc as keys in the texture atlas.
                    // Something like .findRegion(tiles/summon_mountain_mockup/NW)
                    // The texture atlas should return a default image if image doesn't exist(?)
                    String regionName = "tiles/summon_mountain_NW_mockup";
                    if (facingDirection == Hex.Direction.N) regionName = "tiles/summon_mountain_N_mockup";

                    TextureRegion region = textureAtlas.findRegion(regionName);
                    target.actor.setDrawable(new TextureRegionDrawable(region));
                    target.actor.setSize(target.actor.getPrefWidth(), target.actor.getPrefHeight());
                }

                // Visually move the Unit to a new Tile.
                if (hitUnit != null) {
                    // (sheerst) Note: I could see us wanting to trigger animations the visual part of other
                    // abilities here.
                    com.badlogic.gdx.math.Vector2 position =
                            hitUnit.coordinates.toWorldAtCenter(hitUnit.hex, new Vector2());
                    hitUnit.group.addAction(
                            Actions.moveToAligned(position.x, position.y, Align.bottom, 0.25f, Interpolation.sineOut));
                }
            },
        };
        return AttackAnimation.makeSequence(source.group, runnables, positions, moveDuration, rotationDegrees, scale);
    }

    @Override
    public void enter(GameState state, Runnable continuation) {

        target.type = TileType.SOLID; // Model code

        Vector2 originalPosition = new Vector2(source.group.getX(), source.group.getY());
        Vector2 targetPosition = target.getActorPosition();
        // Angle offset of target from source.
        float rotationDegrees = originalPosition.calculateAngle(targetPosition);
        facingDirection = Hex.Direction.fromRotation((int) rotationDegrees);
        System.out.println(facingDirection);

        // Move units on target tile off of it.
        hitUnit = state.unitAt(target.hex); // Model code
        if (hitUnit != null) {
            System.out.println(hitUnit.hex.cube);
            System.out.println(hitUnit.hex.offset);
            hitUnit.hex.set(hitUnit.hex.offset(facingDirection, 1)); // Model code
            System.out.println(hitUnit.hex.cube);
            System.out.println(hitUnit.hex.offset);
        }

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
