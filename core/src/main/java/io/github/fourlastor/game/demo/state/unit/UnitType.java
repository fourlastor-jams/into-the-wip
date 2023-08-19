package io.github.fourlastor.game.demo.state.unit;

import java.util.Arrays;

public enum UnitType {
    CEREBRY("cerebry", true, 2),
    BLOBHOT("blobhot", false, 3);

    public final String mapName;
    public final boolean canFly;

    public final int speed;

    UnitType(String mapName, boolean canFly, int speed) {
        this.mapName = mapName;
        this.canFly = canFly;
        this.speed = speed;
    }

    public static UnitType fromMap(String mapName) {
        return Arrays.stream(values())
                .filter((it) -> it.mapName.equalsIgnoreCase(mapName))
                .findFirst()
                .get();
    }
}
