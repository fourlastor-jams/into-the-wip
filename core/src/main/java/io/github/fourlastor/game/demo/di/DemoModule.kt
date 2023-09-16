package io.github.fourlastor.game.demo.di

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dagger.Module
import dagger.Provides
import io.github.fourlastor.game.di.ScreenScoped

@Module
class DemoModule {
    @Provides
    @ScreenScoped
    fun viewport(): Viewport = FitViewport(512f, 288f)

    @Provides
    @ScreenScoped
    fun stage(viewport: Viewport?, batch: SpriteBatch?): Stage = Stage(viewport, batch)
}
