package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
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
}
