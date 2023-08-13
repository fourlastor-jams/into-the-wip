package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.SnapshotArray;
import io.github.fourlastor.game.demo.state.map.Tile;
import java.util.Comparator;

public class YSort extends WidgetGroup {

    private static final Comparator<Actor> COMPARATOR = Comparator.comparing((actor -> -actor.getY()), Float::compare);

    public void sortChildren() {
        SnapshotArray<Actor> children = getChildren();

        SnapshotArray<Actor> unitChildren = new SnapshotArray<>();
        SnapshotArray<Actor> tileChildren = new SnapshotArray<>();

        for (Actor child : children) {
            if (child instanceof Image) {
                unitChildren.add(child);
            } else if (child instanceof Tile) {
                tileChildren.add(child);
            } else if (child instanceof Label) {
                unitChildren.add(child);
            }
        }

        unitChildren.sort(COMPARATOR);
        tileChildren.sort(COMPARATOR);

        children.clear();
        children.addAll(unitChildren);
        children.addAll(tileChildren);
    }
}
