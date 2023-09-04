package io.github.fourlastor.game.di.modules

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.utils.JsonReader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GdxModule {
    @Provides @Singleton
    fun jsonReader(): JsonReader = JsonReader()

    @Provides @Singleton
    fun inputMultiplexer(): InputMultiplexer = InputMultiplexer()
}
