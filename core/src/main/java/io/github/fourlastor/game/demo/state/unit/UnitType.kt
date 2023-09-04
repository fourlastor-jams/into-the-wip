package io.github.fourlastor.game.demo.state.unit

enum class UnitType(
    val mapName: String,
    val speed: Int,
    val canFly: Boolean,
    val canSwim: Boolean = false
) {
    CEREBRY("cerebry", 2, true),
    BLOBHOT("blobhot", 3, false),
    MON3("MON3", 3, false),
    MON4("MON4", 3, false),
    TECTONNE("tectonne", 3, false),
    MON6("MON6", 3, false),
    MON7("MON7", 3, false),
    MON8("MON8", 3, false);

    companion object {
        fun fromMap(mapName: String): UnitType = values().first { it.mapName.equals(mapName, ignoreCase = true) }
    }
}
