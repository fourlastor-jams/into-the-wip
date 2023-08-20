package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.di.modules.AssetsModule;
import javax.inject.Inject;

public class AttackRanged extends TurnState {

    private final StateRouter router;

    @Inject
    Projectile.Factory projectileFactory;

    private final Unit source;
    private final Unit target;
    Vector2 originalPosition = new Vector2();
    float scale = 1f; // Amount to scale the animation by.
    int damage = 1;
    TextureAtlas textureAtlas;

    @AssistedInject
    public AttackRanged(@Assisted Attack attack, @Assisted TextureAtlas textureAtlas, StateRouter router) {
        this.router = router;
        this.source = attack.source;
        this.target = attack.target;
        this.textureAtlas = textureAtlas;
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

    @Override
    public void enter(GameState entity) {

        Projectile projectile = projectileFactory.create(textureAtlas);

        // (sheerst) Note: this is model code, does it go here?
        target.changeHp(-damage, false);

        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(target.getActorPosition());

        // Angle offset of target from source.
        // float rotationDegrees = calculateAngle(source.getActorPosition(), target.getActorPosition());
        // KeyFrameAnimation attackAnimation = setupAttackAnimation(distance, rotationDegrees);

        // Create a projectile. Add an action that will animate it to the target.
        // Once animation done, set hp.
        // This will exercise our assumptions a bit, projectile needs to move same speed always.
        SequenceAction attackAnimation = Actions.sequence();
        Vector2 targetPos = target.getActorPosition();
        attackAnimation.addAction(Actions.moveTo(targetPos.x, targetPos.y, distance));

        // Change enemy HP visually.
        attackAnimation.addAction(Actions.run(() -> {
            if (target != null) target.refreshHpLabel();
        }));

        // After movement, reset the sources's position to it's original position.
        attackAnimation.addAction(Actions.run(() -> source.setActorPosition(originalPosition)));
        source.image.addAction(attackAnimation);
    }

    @Override
    public void exit(GameState entity) {}

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    public interface Factory {
        AttackRanged create(Attack attack, TextureAtlas textureAtlas);
    }

    /**
     * Represents an attack event with source and target units.
     */
    static class Attack {
        public final Unit source;
        public final Unit target;

        public Attack(Unit source, Unit target) {
            this.source = source;
            this.target = target;
        }
    }
}

/**
 * Animated between the source and target.
 */
class Projectile extends Image {

    @AssistedInject
    public Projectile(@Assisted AssetsModule assets) {
        super(assets.assetManager().findRegion("images/include/ball1.png"));
    }

    /**
     * Factory interface for creating instances of the AttackMelee class.
     */
    @AssistedFactory
    public interface Factory {
        Projectile create(TextureAtlas textureAtlas);
    }
}
