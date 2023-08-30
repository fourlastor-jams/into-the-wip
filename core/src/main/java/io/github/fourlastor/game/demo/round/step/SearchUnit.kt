package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.unit.Unit
import io.github.fourlastor.game.ui.ActorSupport
import java.util.function.BiPredicate
import java.util.function.Consumer

class SearchUnit
@AssistedInject
constructor(@Assisted private val filter: BiPredicate<GameState, Tile>) : Step<Hex>() {
  override fun enter(
      state: GameState,
      continuation: (Hex) -> kotlin.Unit,
      cancel: () -> kotlin.Unit
  ) {
    val searched = state.search(filter)
    for (tile in searched) {
      val unit = state.unitAt(tile.hex)
      if (unit != null) {
        val unitTile = state.tileAt(unit.hex)
        unitTile.actor.addListener(SearchListener(unit, continuation))
        unit.group.image.color = Color.CORAL
        unitTile.actor.color = Color.CORAL
      }
    }
  }

  override fun update(state: GameState, next: (Hex) -> kotlin.Unit, cancel: () -> kotlin.Unit) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      cancel()
    }
  }

  override fun exit(state: GameState) {
    for (tile in state.tiles) {
      if (ActorSupport.removeListeners(tile.actor) { it is SearchListener }) {
        tile.actor.color = Color.WHITE
        state.unitAt(tile.hex)!!.group.image.color = Color.WHITE
      }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(filter: BiPredicate<GameState, Tile>): SearchUnit
  }

  private class SearchListener(private val unit: Unit, private val continuation: Consumer<Hex>) :
      ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
      continuation.accept(unit.hex)
    }
  }
}
