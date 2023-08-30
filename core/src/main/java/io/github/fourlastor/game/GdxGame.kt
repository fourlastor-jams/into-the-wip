package io.github.fourlastor.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import io.github.fourlastor.game.demo.DemoComponent
import io.github.fourlastor.game.di.GameComponent
import io.github.fourlastor.game.route.Router
import javax.inject.Inject

class GdxGame
@Inject
constructor(
    private val multiplexer: InputMultiplexer,
    private val demoComponentBuilder: DemoComponent.Builder
) : Game(), Router {
  private var pendingScreen: Screen? = null
  override fun create() {
    //        if (Gdx.app.getType() != Application.ApplicationType.Android) {
    //
    //            Cursor customCursor =
    //                    Gdx.graphics.newCursor(new
    // Pixmap(Gdx.files.internal("images/included/whitePixel.png")),
    // 0, 0);
    //            Gdx.graphics.setCursor(customCursor);
    //        }
    Gdx.input.inputProcessor = multiplexer
    goToDemo()
  }

  override fun render() {
    if (pendingScreen != null) {
      setScreen(pendingScreen)
      pendingScreen = null
    }
    super.render()
  }

  override fun goToDemo() {
    pendingScreen = demoComponentBuilder.router(this).build().screen()
  }

  override fun goToLevel() {
    Gdx.app.error("GdxGame", "Level screen is missing")
  }

  companion object {
    @JvmStatic fun createGame(): GdxGame = GameComponent.component().game()
  }
}
