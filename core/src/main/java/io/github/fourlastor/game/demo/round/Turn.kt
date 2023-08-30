package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.demo.round.ability.MeleeAttackAbility
import io.github.fourlastor.game.demo.round.ability.MoveAbility
import io.github.fourlastor.game.demo.round.ability.RangedAttackAbility
import io.github.fourlastor.game.demo.round.ability.TileSmashAbility
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.unit.Unit
import io.github.fourlastor.game.ui.ActorSupport

class Turn
@AssistedInject
constructor(
    @Assisted private val unit: Unit,
    private val router: StateRouter,
    private val meleeAttackFactory: MeleeAttackAbility.Factory,
    private val rangedAttackFactory: RangedAttackAbility.Factory,
    private val moveFactory: MoveAbility.Factory,
    private val tileSmashFactory: TileSmashAbility.Factory
) : RoundState {
  private var acted = false
  override fun enter(state: GameState) {
    state.tileAt(unit.hex).actor.color = Color.PINK
    if (!acted) {
      // Move unit button.
      state.ui.move.addListener(
          PickMoveListener { router.startAbility(moveFactory.create(unit) { acted = false }) })

      // Melee attack button.
      state.ui.meleeAttack.addListener(
          PickMoveListener {
            router.startAbility(meleeAttackFactory.create(unit) { acted = false })
          })

      // Ranged attack button.
      state.ui.rangedAttack.addListener(
          PickMoveListener {
            router.startAbility(rangedAttackFactory.create(unit) { acted = false })
          })

      // Tile smash ability button.
      state.ui.tileSmash.addListener(
          PickMoveListener { router.startAbility(tileSmashFactory.create(unit) { acted = false }) })
    } else {
      router.endOfTurn()
    }
  }

  override fun exit(state: GameState) {
    state.tileAt(unit.hex).actor.color = Color.WHITE
    ActorSupport.removeListeners(state.ui.meleeAttack) { it is PickMoveListener }
    ActorSupport.removeListeners(state.ui.move) { it is PickMoveListener }
  }

  private inner class PickMoveListener(private val onMove: () -> kotlin.Unit) : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
      acted = true
      onMove()
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(unit: Unit): Turn
  }
}
