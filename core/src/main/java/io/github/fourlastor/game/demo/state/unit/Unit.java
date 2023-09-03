package io.github.fourlastor.game.demo.state.unit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.fourlastor.game.coordinates.Hex;
import io.github.fourlastor.game.coordinates.HexCoordinates;
import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.UnitInRound;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.round.monster.MonsterAbilities;
import io.github.fourlastor.game.demo.state.map.Tile;
import io.github.fourlastor.game.demo.state.map.TileType;
import io.github.fourlastor.game.ui.UnitOnMap;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.inject.Inject;

public class Unit {

    public final Faction faction;
    public final UnitOnMap group;
    private final Label hpLabel;
    public final Hex hex;
    public final HexCoordinates coordinates;
    public final UnitType type;
    private final int maxHp = 20;
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

    public interface Abilities {
        List<Description> create();

        class Description {
            public final String name;
            public final String icon;
            public final Function<UnitInRound, Ability> factory;

            public Description(String name, String icon, Function<UnitInRound, Ability> factory) {
                this.name = name;
                this.icon = icon;
                this.factory = factory;
            }
        }

        /** Default fallback if monster abilities are not found. */
        class Defaults implements Abilities {

            private final MonsterAbilities.Descriptions descriptions;

            @Inject
            public Defaults(MonsterAbilities.Descriptions descriptions) {
                this.descriptions = descriptions;
            }

            @Override
            public List<Description> create() {
                return Arrays.asList(descriptions.move, descriptions.melee);
            }
        }
    }
}
