package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.SnapshotArray;
import java.util.Comparator;

public class YSort extends WidgetGroup {

    private static final Comparator<Actor> COMPARATOR = Comparator.comparing((actor -> -actor.getY()), Float::compare);

    public void sortChildren() {
        SnapshotArray<Actor> children = getChildren();
        children.sort(COMPARATOR);
    }
}
