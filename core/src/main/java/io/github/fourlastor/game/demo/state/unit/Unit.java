package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.ui.UnitOnMap;

public class Unit {

    public final UnitOnMap actor;
    public final Label hpLabel;
    public final Hex hex;
    public final HexCoordinates coordinates;
    public final UnitType type;
    public int maxHp = 20;
    public int currentHp;

    public Unit(UnitOnMap actor, GridPoint2 position, HexCoordinates coordinates, UnitType type) {
        this.actor = actor;
        this.hex = new Hex(position);
        this.coordinates = coordinates;
        this.type = type;

        // Set up the Hp bar Label.
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/quan-pixel-16.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED);
        hpLabel = new Label("", labelStyle);
        hpLabel.setAlignment(Align.center);
        setHp(maxHp);
    }

    public boolean canTravel(Tile tile) {
        if (type.canFly) {
            return true;
        } else {
            return tile.type.allowWalking;
        }
    }

    public void changeHp(int changeAmount) {
        setHp(currentHp + changeAmount);
    }

    public void refreshHpLabel() {
        hpLabel.setText("HP " + currentHp);
    }

    public void setHp(int hpAmount) {
        currentHp = hpAmount;
        refreshHpLabel();
    }

    public Vector2 getActorPosition() {
        return new Vector2(actor.getX(), actor.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        actor.setPosition(targetPosition.x, targetPosition.y);
    }

    public void alignHpBar() {
        hpLabel.setPosition(actor.getX() + actor.getWidth() / 2, actor.getY() + 40);
    }
}
