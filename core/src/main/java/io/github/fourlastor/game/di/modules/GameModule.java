package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import squidpony.squidmath.IRNG;
import squidpony.squidmath.SilkRNG;

@Module
public class GameModule {

    @Provides
    @Singleton
    public IRNG random() {
        return new SilkRNG();
    }

    @Provides
    @Singleton
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

    @Provides
    @Singleton
    public SpriteBatch batch() {
        return new SpriteBatch();
    }
}
