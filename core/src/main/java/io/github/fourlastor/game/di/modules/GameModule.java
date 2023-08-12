package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import dagger.Module;
import dagger.Provides;
import java.util.Random;
import javax.inject.Singleton;

@Module
public class GameModule {

    @Provides
    @Singleton
    public Random random() {
        return new Random();
    }

    @Provides
    @Singleton
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }
}
