package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.demo.round.ability.Abilities;
import io.github.fourlastor.game.demo.round.monster.MonsterAbilities;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.demo.state.unit.UnitType;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Turn extends RoundState {

    private final UnitInRound unitInRound;
    private final StateRouter router;
    private final MonsterAbilities.DefaultAbilities defaultAbilities;
    private final Map<UnitType, MonsterAbilities> abilitiesMap;
    private final TextureAtlas atlas;

    private final Queue<Actor> addedImages = new LinkedList<>();

    private boolean acted = false;

    @AssistedInject
    public Turn(
            @Assisted UnitInRound unitInRound,
            StateRouter router,
            MonsterAbilities.DefaultAbilities defaultAbilities,
            Map<UnitType, MonsterAbilities> abilitiesMap,
            TextureAtlas atlas) {
        this.unitInRound = unitInRound;
        this.router = router;
        this.defaultAbilities = defaultAbilities;
        this.abilitiesMap = abilitiesMap;
        this.atlas = atlas;
    }

    @Override
    public void enter(GameState state) {
        Unit unit = unitInRound.unit;
        state.tileAt(unit.hex).actor.setColor(Color.PINK);

        if (!acted) {
            MonsterAbilities monsterAbilities = abilitiesMap.getOrDefault(unit.type, defaultAbilities);
            for (Abilities.Description description : monsterAbilities.create()) {
                Image image = new Image(atlas.findRegion(description.icon));
                image.addListener(
                        new PickMoveListener(() -> router.startAbility(description.factory.apply(unitInRound))));
                state.ui.add(image);
                addedImages.add(image);
            }
        } else {
            router.endOfTurn();
        }
    }

    @Override
    public void update(GameState state) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            router.endOfTurn();
        }
    }

    @Override
    public void exit(GameState state) {
        state.tileAt(unitInRound.unit.hex).actor.setColor(Color.WHITE);
        while (!addedImages.isEmpty()) {
            addedImages.remove().remove();
        }
    }

    private class PickMoveListener extends ClickListener {

        private final Runnable onMove;

        private PickMoveListener(Runnable onMove) {
            this.onMove = onMove;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            acted = true;
            onMove.run();
        }
    }

    @AssistedFactory
    public interface Factory {
        Turn create(UnitInRound unit);
    }
}
