package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.demo.state.unit.effect.Effect;
import io.github.fourlastor.game.demo.state.unit.effect.EffectStacks;
import io.github.fourlastor.game.ui.UnitOnMap;

public class Unit {

    public final Faction faction;
    public final UnitOnMap group;
    private final Label hpLabel;
    public final Hex hex;
    public final HexCoordinates coordinates;
    public final UnitType type;
    private final int maxHp = 20;
    private final EffectStacks stacks = new EffectStacks();
    private int currentHp;

    public Unit(
            Faction faction,
            UnitOnMap unitOnMap,
            Label hpLabel,
            GridPoint2 position,
            HexCoordinates coordinates,
            UnitType type) {
        this.faction = faction;
        this.group = unitOnMap;
        this.hex = new Hex(position);
        this.coordinates = coordinates;
        this.type = type;
        this.hpLabel = hpLabel;
        setHp(maxHp);
    }

    public Action onRoundStart() {
        return stacks.onRoundStart(this);
    }

    public void onRoundEnd() {
        stacks.tickStacks();
    }

    public void addEffect(Effect effect) {
        stacks.addStack(effect, 3);
    }

    public boolean canTravel(Tile tile) {
        if (tile.type == TileType.WATER && (type.canSwim || type.canFly)) {
            return true;
        } else {
            return tile.type.allowWalking;
        }
    }

    public boolean inLineOfSight(Tile tile) {
        if (tile.type == TileType.WATER && (type.canSwim || type.canFly)) {
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
        return new Vector2(group.getX(), group.getY());
    }

    public void setActorPosition(Vector2 targetPosition) {
        group.setPosition(targetPosition.x, targetPosition.y);
    }

    public void alignHpBar() {
        hpLabel.setPosition(group.getX() + group.getWidth() / 2, group.getY() + 40);
    }
}
