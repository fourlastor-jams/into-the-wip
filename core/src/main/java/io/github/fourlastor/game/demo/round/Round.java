package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import squidpony.squidmath.IRNG;

public class Round extends RoundState {

    private final StateRouter router;
    private final IRNG rng;
    private List<TurnOrder> factions = null;
    private int factionCounter = 0;

    @Inject
    public Round(StateRouter router, IRNG rng) {
        this.router = router;
        this.rng = rng;
    }

    @Override
    public void enter(GameState state) {
        if (factions == null) {
            factions = Arrays.stream(Faction.values())
                    .map(faction -> new TurnOrder(faction, state.byFaction(faction)))
                    .collect(Collectors.toCollection(ObjectList::new));
            startFirstTurn(state);
        } else {
            advanceToNextTurn(state);
        }
    }

    @Override
    public void exit(GameState state) {
        for (Unit unit : state.units) {
            unit.group.image.clearListeners();
            state.tileAt(unit.hex).actor.setColor(Color.WHITE);
        }
    }

    private void startFirstTurn(GameState state) {
        factionCounter = 0;
        startTurn(state);
    }

    private void advanceToNextTurn(GameState state) {
        TurnOrder currentFaction = factions.get(factionCounter);
        if (currentFaction.alreadyDidTurn.size() >= currentFaction.units.size()) {
            factionCounter += 1;
        }

        if (factionCounter >= factions.size()) {
            router.round();
            return;
        }
        startTurn(state);
    }

    private void startTurn(GameState state) {
        TurnOrder currentTurn = factions.get(factionCounter);
        currentTurn.units.stream()
                .filter(unit -> !currentTurn.alreadyDidTurn.contains(unit))
                .forEach(unit -> {
                    state.tileAt(unit.hex).actor.setColor(Color.PINK);
                    unit.group.image.addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            currentTurn.alreadyDidTurn.add(unit);
                            router.turn(unit);
                        }
                    });
                });
    }

    private static class TurnOrder {
        Faction faction;
        final List<Unit> units;
        final List<Unit> alreadyDidTurn = new ArrayList<>();

        private TurnOrder(Faction faction, List<Unit> units) {
            this.faction = faction;
            this.units = units;
        }
    }
}
