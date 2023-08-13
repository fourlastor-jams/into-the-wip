package io.github.fourlastor.game.demo.turns;

import com.badlogic.gdx.math.Vector2;
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
    Vector2 targetPosition = new Vector2();
    Vector2 towardVector = new Vector2();
    boolean movingTowards = true;
    float speed = 500f;

    @AssistedInject
    public AttackMelee(@Assisted Attack attack, StateRouter router) {
        this.router = router;
        this.source = attack.source;
        this.target = attack.target;
        this.originalPosition = source.getActorPosition();

        // sheerst - Action needs a setup() method.
        this.towardVector.set(target.getActorPosition().cpy().sub(source.getActorPosition()));
        this.towardVector = this.towardVector.nor();
        this.targetPosition.set(target.getActorPosition());
    }

    public boolean act(float delta) {
        if (this.movingTowards) {

            this.source.actor.setPosition(
                    this.source.actor.getX() + (this.towardVector.x * delta * speed),
                    this.source.actor.getY() + (this.towardVector.y * delta * speed));

            if (this.source.getActorPosition().dst2(this.targetPosition) <= 40f) {
                this.source.setActorPosition(this.targetPosition);
                this.movingTowards = false;
            }
            return false;
        }

        this.source.actor.setPosition(
                this.source.actor.getX() + (this.towardVector.x * delta * speed * -1f),
                this.source.actor.getY() + (this.towardVector.y * delta * speed * -1f));

        System.out.println(this.source.getActorPosition().dst2(this.originalPosition));
        if (this.source.getActorPosition().dst2(this.originalPosition) <= 200f) {
            this.source.setActorPosition(this.originalPosition);
            return true;
        }

        return false;
    }

    @Override
    public void enter(GameState entity) {
        // TODO: add act logic here as action
        //  at the end of the attack use `Actions.run(router::pickMonster)`
    }

    @Override
    public void exit(GameState entity) {
        // TODO optional cleanup
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
