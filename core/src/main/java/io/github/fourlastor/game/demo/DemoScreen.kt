package io.github.fourlastor.game.demo

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.tommyettinger.ds.ObjectList
import io.github.fourlastor.game.coordinates.HexCoordinates
import io.github.fourlastor.game.demo.round.GameStateMachine
import io.github.fourlastor.game.demo.round.faction.Faction
import io.github.fourlastor.game.demo.state.GameState
import io.github.fourlastor.game.demo.state.map.Tile
import io.github.fourlastor.game.demo.state.map.TileType
import io.github.fourlastor.game.demo.state.unit.Mon
import io.github.fourlastor.game.demo.state.unit.UnitType
import io.github.fourlastor.game.ui.TileOnMap
import io.github.fourlastor.game.ui.UiLayer
import io.github.fourlastor.game.ui.UnitOnMap
import io.github.fourlastor.game.ui.YSort
import javax.inject.Inject

class DemoScreen @Inject constructor(
    stateMachineFactory: GameStateMachine.Factory,
    assetManager: AssetManager,
    private val viewport: Viewport,
    private val stage: Stage,
    private val multiplexer: InputMultiplexer,
) : ScreenAdapter() {
    private val stateMachine: GameStateMachine
    private val state: GameState

    init {
        val hpLabelStyle = Label.LabelStyle(assetManager.get("fonts/quan-pixel-16.fnt"), Color.RED)
        val map = AtlasTmxMapLoader().load("maps/demo.tmx")
        val hexSideLength = map.properties.get("hexsidelength", Int::class.java)
        val mons = ObjectList<Mon>()
        val tiles = ObjectList<Tile>()
        var factionIndex = 1
        for (mapLayer in map.layers) {
            if (mapLayer !is TiledMapTileLayer) {
                continue
            }
            val tileWidth = mapLayer.tileWidth
            val tileHeight = mapLayer.tileHeight - 1
            val coordinates = HexCoordinates(tileWidth, tileHeight, hexSideLength)
            val ySort = YSort()

            // NOTE: in the future there's the possibility of multiple
            // tilesets per mapLayer, this won't work in that case.
            val mapLayerName = mapLayer.getName()
            for (x in 0 until mapLayer.width) {
                for (y in 0 until mapLayer.height) {
                    val cell = mapLayer.getCell(x, y) ?: continue
                    val cellTile = cell.tile
                    val textureRegion = cellTile.textureRegion
                    if (mapLayerName == UNITS_LAYER_NAME) {
                        val position = coordinates.toWorldAtCenter(x, y, Vector2())
                        val unitOnMap = UnitOnMap(textureRegion)
                        val mapUnitType = cellTile.properties.get("unit", String::class.java)
                        unitOnMap.setPosition(position.x, position.y, Align.bottom)
                        // Set up the Hp bar Label.
                        val hpLabel = Label("", hpLabelStyle)
                        hpLabel.setAlignment(Align.center)
                        val faction = Faction.values()[factionIndex]
                        factionIndex += 1
                        factionIndex %= Faction.values().size
                        val mon = Mon(
                            faction,
                            unitOnMap,
                            hpLabel,
                            GridPoint2(x, y),
                            coordinates,
                            UnitType.fromMap(mapUnitType)
                        )
                        ySort.addActor(unitOnMap)
                        ySort.addActor(hpLabel)
                        mons.add(mon)
                    }
                    if (mapLayerName == TILES_LAYER_NAME) {
                        val position = coordinates.toWorldAtOrigin(x, y, Vector2())
                        val tileOnMap = TileOnMap(textureRegion)
                        tileOnMap.setPosition(position.x, position.y - 15)
                        ySort.addActor(tileOnMap)
                        val mapTileType = cellTile.properties.get("tile", String::class.java)
                        val tile = Tile(tileOnMap, GridPoint2(x, y), TileType.fromMap(mapTileType))
                        tiles.add(tile)
                    }
                }
            }
            ySort.sortChildren()
            stage.addActor(ySort)
        }
        val ui = UiLayer()
        stage.addActor(ui)
        (viewport as ScreenViewport).unitsPerPixel = 1f / 2f
        state = GameState(mons, tiles, ui)
        stateMachine = stateMachineFactory.create(state)
    }

    override fun show() {
        multiplexer.addProcessor(stage)
    }

    override fun hide() {
        multiplexer.removeProcessor(stage)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.DARK_GRAY, true)
        stateMachine.update()
        viewport.apply()
        stage.act()
        state.alignAllHpBars()
        stage.draw()
    }

    companion object {
        private const val UNITS_LAYER_NAME = "monsters"
        private const val TILES_LAYER_NAME = "terrain"
    }
}
