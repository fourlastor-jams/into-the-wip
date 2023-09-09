package io.github.fourlastor.game.demo.state.unit

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.ObjectIntMap
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.coordinates.HexCoordinates
import io.github.fourlastor.game.demo.round.Ability
import io.github.fourlastor.game.demo.round.UnitInRound
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.round.monster.MonsterAbilities.Descriptions
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.effect.Effect
import io.github.fourlastor.game.demo.state.unit.effect.EffectStacks
import io.github.fourlastor.game.ui.UnitOnMap
import java.util.function.Function
import javax.inject.Inject

class Mon(
    val faction: Faction,
    val group: UnitOnMap,
    private val hpLabel: Label,
    position: GridPoint2,
    val coordinates: HexCoordinates,
    val type: UnitType,
) {
    val hex: Hex
    private val maxHp = 20
    val stacks = EffectStacks()
    private var currentHp = 0

    init {
        hex = Hex(position)
        setHp(maxHp)
    }

    fun onRoundStart(): Action = stacks.onRoundStart(this)

    fun onTurnStart(): Action = stacks.onTurnStart(this)

    fun onRoundEnd() {
        stacks.tickStacks()
    }

    fun addEffect(effect: Effect) {
        stacks.addStack(effect, 3)
    }

    fun removeEffect(effect: Effect) {
        stacks.removeEffect(effect)
        effect.cleanup()
    }

    fun getEffects(): ObjectIntMap<Effect> = stacks.getEffects()

    fun getEffect(type: Class<out Effect>): Effect? {
        return stacks.byType(type)
    }

    fun canTravel(tile: Tile): Boolean = if (tile.type === TileType.WATER && (type.canSwim || type.canFly)) {
        true
    } else {
        tile.type.allowWalking
    }

    fun inLineOfSight(tile: Tile): Boolean = if (tile.type === TileType.WATER && (type.canSwim || type.canFly)) {
        true
    } else {
        tile.type.allowWalking
    }

    fun changeHp(changeAmount: Int) {
        setHp(currentHp + changeAmount)
    }

    fun refreshHpLabel() {
        hpLabel.setText("HP $currentHp")
    }

    private fun setHp(hpAmount: Int) {
        currentHp = hpAmount
        refreshHpLabel()
    }

    var actorPosition: Vector2
        get() = Vector2(group.x, group.y)
        set(targetPosition) {
            group.setPosition(targetPosition.x, targetPosition.y)
        }

    fun alignHpBar() {
        hpLabel.setPosition(group.x + group.width / 2, group.y + 40)
    }

    interface Abilities {
        fun create(): List<Description>
        class Description(val name: String, val icon: String, val factory: Function<UnitInRound, Ability>)

        /** Default fallback if monster abilities are not found.  */
        class Defaults @Inject constructor(private val descriptions: Descriptions) : Abilities {
            override fun create(): List<Description> {
                return listOf(
                    descriptions.move,
                    descriptions.melee
                )
            }
        }
    }
}
