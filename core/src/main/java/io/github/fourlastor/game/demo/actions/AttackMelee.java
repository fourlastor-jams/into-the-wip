package io.github.fourlastor.game.demo.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import io.github.fourlastor.game.demo.state.unit.Unit;

public class AttackMelee extends Action {

    Unit source;
    Unit target;
    Vector2 originalPosition = new Vector2();
    Vector2 targetPosition = new Vector2();
    Vector2 towardVector = new Vector2();
    boolean movingTowards = true;
    float speed = 500f;

    public AttackMelee(Unit source, Unit target) {
        this.originalPosition = source.getActorPosition();
        this.source = source;
        this.target = target;

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
}
