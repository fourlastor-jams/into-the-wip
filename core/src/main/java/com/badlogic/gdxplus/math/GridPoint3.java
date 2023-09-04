package com.badlogic.gdxplus.math;

public class GridPoint3 extends com.badlogic.gdx.math.GridPoint3 {

    public GridPoint3() {}

    public GridPoint3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GridPoint3(GridPoint3 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    public GridPoint3 cpy() {
        return new GridPoint3(this);
    }

    public GridPoint3 add(GridPoint3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    // New stuff.

    public GridPoint3 inverse() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public GridPoint3 scl(int amount) {
        x *= amount;
        y *= amount;
        z *= amount;
        return this;
    }
}
