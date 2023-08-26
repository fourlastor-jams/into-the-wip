package io.github.fourlastor.game.demo.state.unit;

import java.util.Arrays;

public enum UnitType {
    CEREBRY("cerebry", 2, true),
    BLOBHOT("blobhot", 3, false),
    MON3("MON3", 3, false),
    MON4("MON4", 3, false),
    TECTONNE("tectonne", 3, false),
    MON6("MON6", 3, false),
    MON7("MON7", 3, false);

    public final String mapName;
    public final boolean canFly;
    public final boolean canSwim;

    public final int speed;

    UnitType(String mapName, int speed, boolean canFly) {
        this(mapName, speed, canFly, false);
    }

    UnitType(String mapName, int speed, boolean canFly, boolean canSwim) {
        this.mapName = mapName;
        this.speed = speed;
        this.canFly = canFly;
        this.canSwim = canSwim;
    }

    public static UnitType fromMap(String mapName) {
        return Arrays.stream(values())
                .filter((it) -> it.mapName.equalsIgnoreCase(mapName))
                .findFirst()
                .get();
    }
}
