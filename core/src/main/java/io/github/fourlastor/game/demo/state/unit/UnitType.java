package io.github.fourlastor.game.demo.state.unit;

import java.util.Arrays;

public enum UnitType {
    CEREBRY("cerebry", true),
    BLOBHOT("blobhot", false);

    public final String mapName;
    public final boolean canFly;

    UnitType(String mapName, boolean canFly) {
        this.mapName = mapName;
        this.canFly = canFly;
    }

    public static UnitType fromMap(String mapName) {
        return Arrays.stream(values())
                .filter((it) -> it.mapName.equalsIgnoreCase(mapName))
                .findFirst()
                .get();
    }
}
