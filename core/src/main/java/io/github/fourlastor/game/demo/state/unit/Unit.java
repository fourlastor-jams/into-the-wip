package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.ui.TileOnMap;

public class Unit {

    public final TileOnMap image;
    public final Label hpLabel;

    public final GridPoint2 position;
    public final HexCoordinates coordinates;
    public int maxHp = 20;
    public int currentHp;

    public Unit(TileOnMap image, GridPoint2 position, HexCoordinates coordinates, Stage stage) {
        this.image = image;
        this.position = position;
        this.coordinates = coordinates;

        image.setAlign(Align.center);
        image.setPosition(0, 0);

        // Set up the Hp bar Label.
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/wilds.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED);
        hpLabel = new Label("", labelStyle);
        hpLabel.setAlignment(Align.center);
        setHp(maxHp);
        hpLabel.debug();

        // hpLabel.setPosition(getX() + getWidth() / 2, getY() + 36);
    }

    public void changeHp(int changeAmount, boolean updateLabel) {
        setHp(currentHp + changeAmount, updateLabel);
    }

    public void refreshHpLabel() {
        hpLabel.setText("HP " + String.valueOf(currentHp));
    }

    public void setHp(int hpAmount) {
        setHp(hpAmount, true);
    }

    public void setHp(int hpAmount, boolean updateLabel) {
        currentHp = hpAmount;
        refreshHpLabel();
    }

    public Vector2 getActorPosition() {
        return new Vector2(image.getX(), image.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        setActorPosition(targetPosition.x, targetPosition.y);
    }

    public void setActorPosition(GridPoint2 targetPosition) {
        setActorPosition(targetPosition.x, targetPosition.y);
    }

    public void setActorPosition(float x, float y) {
        image.setPosition(x, y);
    }

    public void alignHpBars() {
        hpLabel.setPosition(image.getX() + image.getWidth() / 2, image.getY() + 36);
    }
}
