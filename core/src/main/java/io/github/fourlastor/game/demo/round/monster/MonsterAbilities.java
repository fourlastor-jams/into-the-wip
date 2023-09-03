package io.github.fourlastor.game.demo.round.monster;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.github.fourlastor.game.demo.round.ability.Abilities;
import io.github.fourlastor.game.demo.state.unit.UnitType;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public interface MonsterAbilities {

    /** Register abilities by monster type here. */
    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @MonsterKey(UnitType.CEREBRY)
        MonsterAbilities cerebry(Cerebry cerebry);

        @Binds
        @IntoMap
        @MonsterKey(UnitType.TECTONNE)
        MonsterAbilities tectonne(Tectonne tectonne);

        @Binds
        @IntoMap
        @MonsterKey(UnitType.BLOBHOT)
        MonsterAbilities blobhot(Blobanero blobhot);

        @MapKey
        @interface MonsterKey {
            UnitType value();
        }
    }

    List<Abilities.Description> create();

    class Cerebry implements MonsterAbilities {

        private final Abilities abilities;

        @Inject
        public Cerebry(Abilities abilities) {
            this.abilities = abilities;
        }

        @Override
        public List<Abilities.Description> create() {
            return Arrays.asList(abilities.ranged, abilities.move);
        }
    }

    class Tectonne implements MonsterAbilities {

        private final Abilities abilities;

        @Inject
        public Tectonne(Abilities abilities) {
            this.abilities = abilities;
        }

        @Override
        public List<Abilities.Description> create() {
            return Arrays.asList(abilities.move, abilities.smash);
        }
    }

    class Blobanero implements MonsterAbilities {

        private final Abilities abilities;

        @Inject
        public Blobanero(Abilities abilities) {
            this.abilities = abilities;
        }

        @Override
        public List<Abilities.Description> create() {
            return Arrays.asList(abilities.move, abilities.blobAbsorb);
        }
    }

    /** Default fallback if monster abilities are not found. */
    class DefaultAbilities implements MonsterAbilities {

        private final Abilities abilities;

        @Inject
        public DefaultAbilities(Abilities abilities) {
            this.abilities = abilities;
        }

        @Override
        public List<Abilities.Description> create() {
            return Arrays.asList(abilities.move, abilities.melee);
        }
    }
}
