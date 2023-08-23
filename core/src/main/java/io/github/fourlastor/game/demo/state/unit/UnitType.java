package io.github.fourlastor.game.demo.state.unit;

import java.util.Arrays;

public enum UnitType {
    CEREBRY("cerebry", true, 2),
    BLOBHOT("blobhot", false, 3),
    MON3("MON3", false, 3),
    MON4("MON4", false, 3),
    MON5("MON5", true, 3),
    MON6("MON6", false, 3),
    MON7("MON7", false, 3);

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
