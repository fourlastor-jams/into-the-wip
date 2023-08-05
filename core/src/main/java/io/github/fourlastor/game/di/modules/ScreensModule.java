package io.github.fourlastor.game.di.modules;

import dagger.Module;
import io.github.fourlastor.game.demo.DemoComponent;

@Module(
        subcomponents = {
            DemoComponent.class,
        })
public class ScreensModule {}
