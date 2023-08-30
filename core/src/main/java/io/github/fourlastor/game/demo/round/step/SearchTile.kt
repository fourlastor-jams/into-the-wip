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
import io.github.fourlastor.game.ui.ActorSupport
import java.util.function.BiPredicate
import java.util.function.Consumer

class SearchTile
@AssistedInject
constructor(@Assisted private val filter: BiPredicate<GameState, Tile>) : Step<Hex>() {
  override fun enter(state: GameState, continuation: (Hex) -> Unit, cancel: () -> Unit) {
    val searched = state.search(filter)
    for (tile in searched) {
      tile.actor.addListener(SearchListener(tile, continuation))
      tile.actor.color = Color.CORAL
    }
  }

  override fun update(state: GameState, next: (Hex) -> Unit, cancel: () -> Unit) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      cancel()
    }
  }

  override fun exit(state: GameState) {
    for (tile in state.tiles) {
      if (ActorSupport.removeListeners(tile.actor) { it is SearchListener }) {
        tile.actor.color = Color.WHITE
      }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(filter: BiPredicate<GameState, Tile>): SearchTile
  }

  private class SearchListener(private val tile: Tile, private val continuation: Consumer<Hex>) :
      ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
      continuation.accept(tile.hex)
    }
  }
}
