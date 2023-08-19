package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.harlequin.animation.AnimationNode;
import io.github.fourlastor.harlequin.loader.dragonbones.DragonBonesLoader;
import io.github.fourlastor.harlequin.loader.spine.SpineLoader;
import io.github.fourlastor.harlequin.loader.spine.model.SpineEntity;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class AssetsModule {

    private static final String PATH_TEXTURE_ATLAS = "images/packed/images.pack.atlas";
    public static final String WHITE_PIXEL = "white-pixel";

    @Provides
    public DragonBonesLoader dragonBonesLoader(JsonReader json) {
        return new DragonBonesLoader();
    }

    @Provides
    public SpineLoader spineLoader(JsonReader json) {
        return new SpineLoader(json);
    }

    @Provides
    @Singleton
    public AssetManager assetManager(SpineLoader spineLoader, DragonBonesLoader dragonBonesLoader) {
        AssetManager assetManager = new AssetManager();
        assetManager.setLoader(SpineEntity.class, spineLoader);
        assetManager.setLoader(AnimationNode.Group.class, dragonBonesLoader);
        assetManager.load(PATH_TEXTURE_ATLAS, TextureAtlas.class);
        assetManager.load("fonts/quan-pixel-16.fnt", BitmapFont.class);
        assetManager.finishLoading();
        return assetManager;
    }

    @Provides
    @Singleton
    public TextureAtlas textureAtlas(AssetManager assetManager) {
        return assetManager.get(PATH_TEXTURE_ATLAS, TextureAtlas.class);
    }

    @Provides
    @Singleton
    @Named(WHITE_PIXEL)
    public TextureRegion whitePixel(TextureAtlas atlas) {
        return atlas.findRegion("whitePixel");
    }
}
