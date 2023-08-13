package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.SnapshotArray;
import java.util.Comparator;

// (sheerst) TODO: layers.
public class YSort extends WidgetGroup {

    private static final Comparator<Actor> COMPARATOR = Comparator.comparing((actor -> -actor.getY()), Float::compare);

    public void sortChildren() {
        SnapshotArray<Actor> children = getChildren();

        SnapshotArray<Actor> labelChildren = new SnapshotArray<>();
        SnapshotArray<Actor> otherChildren = new SnapshotArray<>();

        for (Actor child : children) {
            if (child instanceof Label) {
                labelChildren.add(child);
            } else {
                otherChildren.add(child);
            }
        }

        labelChildren.sort(COMPARATOR);
        otherChildren.sort(COMPARATOR);

        children.clear();
        children.addAll(labelChildren);
        children.addAll(otherChildren);
    }
}
