package io.github.fourlastor.game.demo.di;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.di.ScreenScoped;

@Module
public class DemoModule {

    @Provides
    @ScreenScoped
    public Viewport viewport() {
        return new FitViewport(512, 288);
    }

    @Provides
    @ScreenScoped
    public Stage stage(Viewport viewport, SpriteBatch batch) {
        return new Stage(viewport, batch);
    }
}
