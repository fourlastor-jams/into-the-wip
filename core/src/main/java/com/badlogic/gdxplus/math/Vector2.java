package com.badlogic.gdxplus.math;

public class Vector2 extends com.badlogic.gdx.math.Vector2 {

    /** Constructs a new vector at (0,0) */
    public Vector2() {
        super();
    }

    /** Constructs a vector with the given components
     * @param x The x-component
     * @param y The y-component */
    public Vector2(float x, float y) {
        super(x, y);
    }

    public Vector2(Vector2 v) {
        super((com.badlogic.gdxplus.math.Vector2) v);
    }

    /**
     * Calculate the angle in degrees between two 2D vectors.
     *.
     * @param target The target vector.
     * @return The angle in degrees between this vector and the target.
     */
    public float calculateAngle(Vector2 target) {
        return target.cpy().sub(this).angleDeg();
    }
}
