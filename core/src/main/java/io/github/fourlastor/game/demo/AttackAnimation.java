package io.github.fourlastor.game.demo;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;

public class AttackAnimation {

    private AttackAnimation() {}

    /**
     * @return  Actions to this sequence in order to run the KeyFrame-based animation.
     */
    public static Action makeSequence(
            Actor actor,
            Runnable[] runnables,
            Vector3[] positionsRelative,
            float moveDuration,
            float rotationDegrees,
            Vector3 scale) {

        // TODO: this animation has an alignment issue.
        Vector2 nextUnitPosition = new Vector2(actor.getX(), actor.getY());

        ArrayList<Frame> frames = frames(runnables, positionsRelative);
        ArrayList<Action> actions = new ArrayList<>(frames.size());

        for (AttackAnimation.Frame frame : frames) {

            Vector3 rotatedPositionRelative = frame.positionRelative.cpy();
            // Scale the animation by a given value.
            rotatedPositionRelative.scl(scale.x, scale.y, scale.z);
            // Rotate around the z-axis so that animation points at target.
            rotatedPositionRelative.rotate(new Vector3(0, 0, 1), rotationDegrees);
            // Rotate around the x-axis 45 degress so that the animation 'hop' effect is visible.
            rotatedPositionRelative.rotate(new Vector3(1, 0, 0), 45f);
            // Using rotatedPositionRelative.z instead of .y here in order to see the jump in the y-axis.
            // (that data is currently in the z-axis).
            nextUnitPosition.add(rotatedPositionRelative.x, rotatedPositionRelative.z);
            ParallelAction parallelActions = Actions.parallel();
            parallelActions.addAction(Actions.moveToAligned(
                    nextUnitPosition.x, nextUnitPosition.y, Align.bottom, moveDuration, Interpolation.linear));
            // Run runnable for this Frame.
            parallelActions.addAction(Actions.run(frame.runnable));
            actions.add(parallelActions);
        }

        return Actions.sequence(actions.toArray(new Action[0]));
    }

    private static class Frame {
        public Vector3 positionRelative = new Vector3(0, 0, 0);
        public Runnable runnable = () -> {};
    }

    /**
     * @return List of animation frames.
     */
    private static ArrayList<Frame> frames(Runnable[] runnables, Vector3[] positionsRelative) {
        ArrayList<Frame> frames = new ArrayList<>();

        int numFrames = Math.max(positionsRelative.length, runnables.length);

        for (int i = 0; i < numFrames; ++i) {
            Frame frame = new Frame();
            if (i < positionsRelative.length && positionsRelative[i] != null)
                frame.positionRelative = positionsRelative[i];
            if (i < runnables.length && runnables[i] != null) frame.runnable = runnables[i];
            frames.add(frame);
        }

        return frames;
    }
}
