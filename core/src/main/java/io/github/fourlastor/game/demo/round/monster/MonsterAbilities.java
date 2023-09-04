package io.github.fourlastor.game.demo.round.monster;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility;
import io.github.fourlastor.game.demo.round.ability.PoisonAbility;
import io.github.fourlastor.game.demo.round.ability.RangedAttackAbility;
import io.github.fourlastor.game.demo.round.ability.SummonMountainAbility;
import io.github.fourlastor.game.demo.round.ability.TileSmashAbility;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.UnitType;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/** Register abilities by monster type here. */
@Module
public interface MonsterAbilities {

    @Binds
    @IntoMap
    @MonsterKey(UnitType.CEREBRY)
    Unit.Abilities cerebry(Cerebry cerebry);

    @Binds
    @IntoMap
    @MonsterKey(UnitType.TECTONNE)
    Unit.Abilities tectonne(Tectonne tectonne);

    @Binds
    @IntoMap
    @MonsterKey(UnitType.BLOBHOT)
    Unit.Abilities blobhot(Blobhot blobhot);

    @MapKey
    @interface MonsterKey {
        UnitType value();
    }

    class Cerebry implements Unit.Abilities {

        private final Descriptions descriptions;

        @Inject
        public Cerebry(Descriptions descriptions) {
            this.descriptions = descriptions;
        }

        @Override
        public List<Unit.Abilities.Description> create() {
            return Arrays.asList(descriptions.ranged, descriptions.poison, descriptions.move);
        }
    }

    class Tectonne implements Unit.Abilities {

        private final Descriptions descriptions;

        @Inject
        public Tectonne(Descriptions descriptions) {
            this.descriptions = descriptions;
        }

        @Override
        public List<Unit.Abilities.Description> create() {
            return Arrays.asList(descriptions.move, descriptions.smash, descriptions.summonMountain);
        }
    }

    class Blobhot implements Unit.Abilities {

        private final Descriptions descriptions;

        @Inject
        public Blobhot(Descriptions descriptions) {
            this.descriptions = descriptions;
        }

        @Override
        public List<Unit.Abilities.Description> create() {
            return Arrays.asList(descriptions.move, descriptions.ranged);
        }
    }

    class Descriptions {
        public final Unit.Abilities.Description melee;
        public final Unit.Abilities.Description ranged;
        public final Unit.Abilities.Description move;
        public final Unit.Abilities.Description smash;
        public final Unit.Abilities.Description summonMountain;
        private final Unit.Abilities.Description poison;

        @Inject
        public Descriptions(
                MeleeAttackAbility.Factory meleeFactory,
                RangedAttackAbility.Factory rangedFactory,
                MeleeAttackAbility.Factory moveFactory,
                TileSmashAbility.Factory smashFactory,
                SummonMountainAbility.Factory summonMountainFactory,
                PoisonAbility.Factory poisonFactory) {
            this.melee = new Unit.Abilities.Description(
                    "Melee attack", "abilities/buffs/attack_boost", meleeFactory::create);
            this.ranged = new Unit.Abilities.Description(
                    "Ranged attack", "abilities/spells/ranged_attack", rangedFactory::create);
            this.move = new Unit.Abilities.Description("Move", "abilities/buffs/swiftness", moveFactory::create);
            this.smash =
                    new Unit.Abilities.Description("Smash tile", "abilities/spells/tile_smash", smashFactory::create);
            this.summonMountain = new Unit.Abilities.Description(
                    "Summon mountain", "abilities/spells/summon_mountain", summonMountainFactory::create);
            this.poison = new Unit.Abilities.Description("Poison", "abilities/debuffs/poisoned", poisonFactory::create);
        }
    }
}
