package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import io.github.fourlastor.game.coordinates.HexCoordinates;

public class Unit extends Image {

    public final Image actor;
    public final GridPoint2 position;
    public final HexCoordinates coordinates;

    public int maxHp = 20;
    public int currentHp;
    private final Label hpLabel;

    public Unit(TextureRegion textureRegion, GridPoint2 position, HexCoordinates coordinates, Stage stage) {
        super(textureRegion);
        this.actor = this; // TODO: update apis everywhere once change confirmed.
        this.position = position;
        this.coordinates = coordinates;

        // Set up the Hp bar Label.
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/wilds.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED);
        hpLabel = new Label("", labelStyle);
        hpLabel.setAlignment(Align.center);
        setHp(this.maxHp);
        stage.addActor(hpLabel);
    }

    @Null
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() - 5 ? this : null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Hp label stays aligned with this.actor.
        // (sheerst) Note: scenetree-like relative position would be nice here.
        // (sheerst) Note: ChangeListener did not work for Image actor - ChangeEvent handling isn't implemented in Image
        // afaict.
        hpLabel.setPosition(getX() + getWidth() / 2, getY() + 36);
    }

    public void setHp(int hpAmount) {

        this.currentHp = hpAmount;
        this.hpLabel.setText("HP " + String.valueOf(this.currentHp));
    }

    public Vector2 getActorPosition() {
        return new Vector2(this.actor.getX(), this.actor.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        setActorPosition(targetPosition.x, targetPosition.y);
    }

    public void setActorPosition(GridPoint2 targetPosition) {
        setActorPosition(targetPosition.x, targetPosition.y);
    }

    public void setActorPosition(float x, float y) {
        this.actor.setPosition(x, y);
    }
}
