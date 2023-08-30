package io.github.fourlastor.game.di.modules

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import squidpony.squidmath.IRNG
import squidpony.squidmath.SilkRNG

@Module
class GameModule {
  @Provides @Singleton fun random(): IRNG = SilkRNG()

  @Provides @Singleton fun messageDispatcher(): MessageDispatcher = MessageDispatcher()

  @Provides @Singleton fun batch(): SpriteBatch = SpriteBatch()
}
