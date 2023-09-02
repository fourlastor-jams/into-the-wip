package io.github.fourlastor.game.demo.round.ability;

import io.github.fourlastor.game.demo.round.Ability;
import io.github.fourlastor.game.demo.round.UnitInRound;
import java.util.function.Function;
import javax.inject.Inject;

public class Abilities {
    public final Description melee;
    public final Description ranged;
    public final Description move;
    public final Description smash;

    @Inject
    public Abilities(
            MeleeAttackAbility.Factory meleeFactory,
            RangedAttackAbility.Factory rangedFactory,
            MeleeAttackAbility.Factory moveFactory,
            TileSmashAbility.Factory smashFactory) {
        this.melee = new Description("Melee attack", "abilities/buffs/attack_boost", meleeFactory::create);
        this.ranged = new Description("Ranged attack", "abilities/spells/ranged_attack", rangedFactory::create);
        this.move = new Description("Move", "abilities/buffs/swiftness", moveFactory::create);
        this.smash = new Description("Smash tile", "abilities/spells/tile_smash", smashFactory::create);
    }

    public static class Description {
        public final String name;
        public final String icon;
        public final Function<UnitInRound, Ability> factory;

        public Description(String name, String icon, Function<UnitInRound, Ability> factory) {
            this.name = name;
            this.icon = icon;
            this.factory = factory;
        }
    }
}
