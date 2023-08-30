package io.github.fourlastor.game.demo.state.map

enum class TileType(val mapName: String, val allowWalking: Boolean) {
  TERRAIN("terrain", true),
  WATER("water", false),
  SOLID("solid", false);

  companion object {
    fun fromMap(mapName: String): TileType =
        values().first { it.mapName.equals(mapName, ignoreCase = true) }
  }
}
