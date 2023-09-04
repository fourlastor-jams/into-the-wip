package io.github.fourlastor.game.demo

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdxplus.math.Vector2
import kotlin.math.max

object AttackAnimation {
    /**
     * @return Actions to this sequence in order to run the KeyFrame-based animation.
     */
    @JvmStatic
    fun makeSequence(
        actor: Actor,
        runnables: Array<Runnable?>,
        positionsRelative: Array<Vector3>,
        moveDuration: Float,
        rotationDegrees: Float,
        scale: Vector3
    ): Action {
        // TODO: this animation has an alignment issue.
        val nextUnitPosition = Vector2(actor.x, actor.y)
        val frames = frames(runnables, positionsRelative)
        val actions = ArrayList<Action>(frames.size)
        for (frame in frames) {
            val rotatedPositionRelative = frame.positionRelative.cpy()
            // Scale the animation by a given value.
            rotatedPositionRelative.scl(scale.x, scale.y, scale.z)
            // Rotate around the z-axis so that animation points at target.
            rotatedPositionRelative.rotate(Vector3(0f, 0f, 1f), rotationDegrees)
            // Rotate around the x-axis 45 degrees so that the animation 'hop' effect is visible.
            rotatedPositionRelative.rotate(Vector3(1f, 0f, 0f), 45f)
            // Using rotatedPositionRelative.z instead of .y here in order to see the jump in the y-axis.
            // (that data is currently in the z-axis).
            nextUnitPosition.add(rotatedPositionRelative.x, rotatedPositionRelative.z)
            val parallelActions = Actions.parallel()
            parallelActions.addAction(
                Actions.moveToAligned(
                    nextUnitPosition.x,
                    nextUnitPosition.y,
                    Align.bottom,
                    moveDuration,
                    Interpolation.linear
                )
            )
            // Run runnable for this Frame.
            parallelActions.addAction(Actions.run(frame.runnable))
            actions.add(parallelActions)
        }
        return Actions.sequence(*actions.toTypedArray<Action>())
    }

    /**
     * @return List of animation frames.
     */
    private fun frames(runnables: Array<Runnable?>, positionsRelative: Array<Vector3>): ArrayList<Frame> {
        val frames = ArrayList<Frame>()
        val numFrames = max(positionsRelative.size.toDouble(), runnables.size.toDouble()).toInt()
        for (i in 0 until numFrames) {
            val frame = Frame()
            if (i < positionsRelative.size) frame.positionRelative =
                positionsRelative[i]
            if (i < runnables.size && runnables[i] != null) frame.runnable = runnables[i]
            frames.add(frame)
        }
        return frames
    }

    private class Frame {
        var positionRelative: Vector3 = Vector3(0f, 0f, 0f)
        var runnable: Runnable? = Runnable {}
    }
}
