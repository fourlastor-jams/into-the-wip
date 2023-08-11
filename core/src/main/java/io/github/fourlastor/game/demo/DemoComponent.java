package io.github.fourlastor.game.demo;

import dagger.BindsInstance;
import dagger.Subcomponent;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.route.Router;

@ScreenScoped
@Subcomponent
public interface DemoComponent {

    @ScreenScoped
    DemoScreen screen();

    @Subcomponent.Builder
    interface Builder {

        Builder router(@BindsInstance Router router);

        DemoComponent build();
    }
}
