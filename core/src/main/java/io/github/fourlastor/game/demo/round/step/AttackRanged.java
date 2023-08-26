package io.github.fourlastor.game.demo.round.step;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class AttackRanged extends SimpleStep {

    private final Unit source;
    private final Unit target;
    Vector2 originalPosition = new Vector2();
    private final TextureAtlas textureAtlas;

    @AssistedInject
    public AttackRanged(@Assisted Attack attack, TextureAtlas textureAtlas) {
        this.source = attack.source;
        this.target = attack.target;
        this.textureAtlas = textureAtlas;
        this.originalPosition.set(source.getActorPosition());
    }

    @Override
    public void enter(GameState state, Runnable continuation) {

        // (sheerst) Note: this is model code, does it go here?
        target.changeHp(-1);

        // Distance between source and target is used to scale the animation if needed.
        float distance = source.getActorPosition().dst(target.getActorPosition());

        // Create a projectile. Add an action that will animate it to the target.
        Vector2 sourcePos = source.getActorPosition().add(source.group.getWidth() / 2, source.group.getHeight() / 2);
        Vector2 targetPos = target.getActorPosition().add(target.group.getWidth() / 2, target.group.getHeight() / 2);
        Image projectile = new Image(textureAtlas.findRegion("ball1"));
        projectile.setPosition(sourcePos.x, sourcePos.y);
        SequenceAction moveAnimation = Actions.sequence();
        moveAnimation.addAction(Actions.moveTo(targetPos.x, targetPos.y, distance / 400));
        moveAnimation.addAction(Actions.run(() -> {
            if (target != null) target.refreshHpLabel();
        }));
        moveAnimation.addAction(Actions.run(continuation));
        moveAnimation.addAction(Actions.run(projectile::remove));
        projectile.addAction(moveAnimation);
        source.group.getStage().addActor(projectile);
    }

    /**
     * Factory interface for creating instances of the AttackRanged class.
     */
    @AssistedFactory
    public interface Factory {
        AttackRanged create(Attack attack);
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
