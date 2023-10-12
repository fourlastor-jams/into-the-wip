package io.github.fourlastor.game.demo.round.step

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.game.coordinates.Hex
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import java.util.function.BiPredicate
import java.util.function.Consumer

class SearchTile @AssistedInject constructor(@Assisted private val filter: BiPredicate<GameState, Tile>) :
    Step<Hex>() {
    override fun Context.enter(state: GameState, continuation: Consumer<Hex>, cancel: Runnable) {
        val searched = state.search(filter)
        for (tile in searched) {
            tile.actor.selectable(Color.CORAL) { continuation.accept(tile.hex) }
        }
    }

    override fun update(state: GameState, next: Consumer<Hex>, cancel: Runnable) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            cancel.run()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(filter: BiPredicate<GameState, Tile>): SearchTile
    }
}
