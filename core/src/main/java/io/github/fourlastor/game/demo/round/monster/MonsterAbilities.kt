package io.github.fourlastor.game.demo.round.monster

import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.fourlastor.game.demo.round.ability.BlobAbsorbAbility
import io.github.fourlastor.game.demo.round.ability.BlobTossAbility
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility
import io.github.fourlastor.game.demo.round.ability.MoveAbility
import io.github.fourlastor.game.demo.round.ability.PoisonAbility
import io.github.fourlastor.game.demo.round.ability.RangedAttackAbility
import io.github.fourlastor.game.demo.round.ability.SummonMountainAbility
import io.github.fourlastor.game.demo.round.ability.TileSmashAbility
import io.github.fourlastor.game.demo.state.unit.Mon.Abilities
import io.github.fourlastor.game.demo.state.unit.UnitType
import javax.inject.Inject

/** Register abilities by monster type here.  */
@Module
interface MonsterAbilities {
    @Binds
    @IntoMap
    @MonsterKey(UnitType.CEREBRY)
    fun cerebry(cerebry: Cerebry): Abilities

    @Binds
    @IntoMap
    @MonsterKey(UnitType.TECTONNE)
    fun tectonne(tectonne: Tectonne): Abilities

    @Binds
    @IntoMap
    @MonsterKey(UnitType.BLOBHOT)
    fun blobanero(blobanero: Blobanero): Abilities

    @MapKey
    annotation class MonsterKey(val value: UnitType)
    class Cerebry @Inject constructor(private val descriptions: Descriptions) : Abilities {
        override fun create(): List<Abilities.Description> {
            return listOf(
                descriptions.ranged,
                descriptions.poison,
                descriptions.move
            )
        }
    }

    class Tectonne @Inject constructor(private val descriptions: Descriptions) : Abilities {
        override fun create(): List<Abilities.Description> {
            return listOf(
                descriptions.move,
                descriptions.smash,
                descriptions.summonMountain
            )
        }
    }

    class Blobanero @Inject constructor(private val descriptions: Descriptions) : Abilities {
        override fun create(): List<Abilities.Description> {
            return listOf(
                descriptions.move,
                descriptions.blobAbsorb,
                descriptions.blobToss
            )
        }
    }

    class Descriptions @Inject constructor(
        meleeFactory: MeleeAttackAbility.Factory,
        rangedFactory: RangedAttackAbility.Factory,
        moveFactory: MoveAbility.Factory,
        smashFactory: TileSmashAbility.Factory,
        summonMountainFactory: SummonMountainAbility.Factory,
        poisonFactory: PoisonAbility.Factory,
        blobAbsorbFactory: BlobAbsorbAbility.Factory,
        blobTossFactory: BlobTossAbility.Factory
    ) {
        val melee: Abilities.Description
        val ranged: Abilities.Description
        val move: Abilities.Description
        val smash: Abilities.Description
        val summonMountain: Abilities.Description
        val poison: Abilities.Description
        val blobAbsorb: Abilities.Description
        val blobToss: Abilities.Description

        init {
            melee = Abilities.Description("Melee attack", "abilities/buffs/attack_boost", meleeFactory::create)
            ranged = Abilities.Description("Ranged attack", "abilities/spells/ranged_attack", rangedFactory::create)
            move = Abilities.Description("Move", "abilities/buffs/swiftness", moveFactory::create)
            smash = Abilities.Description("Smash tile", "abilities/spells/tile_smash", smashFactory::create)
            summonMountain = Abilities.Description(
                "Summon mountain",
                "abilities/spells/summon_mountain",
                summonMountainFactory::create
            )
            poison = Abilities.Description("Poison", "abilities/debuffs/poisoned", poisonFactory::create)
            blobAbsorb = Abilities.Description("Absorb unit", "abilities/spells/healing_spell", blobAbsorbFactory::create)
            blobToss = Abilities.Description("Toss the absorbed unit", "abilities/spells/healing_spell", blobTossFactory::create)
        }
    }
}
