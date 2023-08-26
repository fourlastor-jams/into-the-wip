package io.github.fourlastor.game.demo.state.map;

import java.util.Arrays;

public enum TileType {
    TERRAIN("terrain", true),
    WATER("water", false),
    SOLID("solid", false),
    ;

    public final String mapName;
    public final boolean allowWalking;

    TileType(String mapName, boolean allowWalking) {
        this.mapName = mapName;
        this.allowWalking = allowWalking;
    }

    public static TileType fromMap(String mapName) {
        return Arrays.stream(values())
                .filter((it) -> it.mapName.equalsIgnoreCase(mapName))
                .findFirst()
                .get();
    }
}
