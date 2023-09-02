package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UiLayer extends Table {

    public UiLayer() {
        setFillParent(true);
        defaults().bottom().expandY().pad(2);
    }
}
