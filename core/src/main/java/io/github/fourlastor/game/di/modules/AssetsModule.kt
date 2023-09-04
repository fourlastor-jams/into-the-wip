package io.github.fourlastor.game.di.modules

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.JsonReader
import dagger.Module
import dagger.Provides
import io.github.fourlastor.harlequin.animation.AnimationNode
import io.github.fourlastor.harlequin.loader.dragonbones.DragonBonesLoader
import io.github.fourlastor.harlequin.loader.spine.SpineLoader
import io.github.fourlastor.harlequin.loader.spine.model.SpineEntity
import javax.inject.Named
import javax.inject.Singleton

@Module
class AssetsModule {
    @Provides fun dragonBonesLoader(json: JsonReader): DragonBonesLoader = DragonBonesLoader()

    @Provides fun spineLoader(json: JsonReader): SpineLoader = SpineLoader(json)

    @Provides
    @Singleton
    fun assetManager(spineLoader: SpineLoader, dragonBonesLoader: DragonBonesLoader): AssetManager {
        return AssetManager().apply {
            setLoader(SpineEntity::class.java, spineLoader)
            setLoader(AnimationNode.Group::class.java, dragonBonesLoader)
            load(PATH_TEXTURE_ATLAS, TextureAtlas::class.java)
            load("fonts/quan-pixel-16.fnt", BitmapFont::class.java)
            finishLoading()
        }
    }

    @Provides
    @Singleton
    fun textureAtlas(assetManager: AssetManager): TextureAtlas =
        assetManager.get(PATH_TEXTURE_ATLAS, TextureAtlas::class.java)

    @Provides
    @Singleton
    @Named(WHITE_PIXEL)
    fun whitePixel(atlas: TextureAtlas): TextureRegion = atlas.findRegion("whitePixel")

    companion object {
        private const val PATH_TEXTURE_ATLAS = "images/packed/images.pack.atlas"
        private const val WHITE_PIXEL = "white-pixel"
    }
}
