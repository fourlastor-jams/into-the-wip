package io.github.fourlastor.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class UiLayer extends Table {

    public final Image meleeAttack;
    public final Image move;
    public final Image tileSmash;
    public final Image rangedAttack;
    public final Image poison;

    // TODO: Remove atlas from here
    public UiLayer(TextureAtlas atlas) {
        setFillParent(true);
        defaults().bottom().expandY().pad(2);
        meleeAttack = new Image(atlas.findRegion("abilities/buffs/attack_boost"));
        add(meleeAttack);
        move = new Image(atlas.findRegion("abilities/buffs/swiftness"));
        add(move);
        tileSmash = new Image(atlas.findRegion("abilities/spells/tile_smash"));
        add(tileSmash);
        rangedAttack = new Image(atlas.findRegion("abilities/spells/ranged_attack"));
        add(rangedAttack);
        poison = new Image(atlas.findRegion("abilities/debuffs/poisoned"));
        add(poison);
    }
}
