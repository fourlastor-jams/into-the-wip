package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.ActorSupport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class Round extends RoundState {

    private final StateRouter router;
    private List<CurrentFaction> factions = null;
    private int factionCounter = 0;

    @Inject
    public Round(StateRouter router) {
        this.router = router;
    }

    @Override
    public void enter(GameState state) {
        if (factions == null) {
            factions = Arrays.stream(Faction.values())
                    .map(faction -> new CurrentFaction(state.byFaction(faction)))
                    .collect(Collectors.toCollection(ObjectList::new));
            startFirstTurn(state);
        } else {
            advanceToNextTurn(state);
        }
    }

    @Override
    public void exit(GameState state) {
        for (Unit unit : state.units) {
            if (ActorSupport.removeListeners(unit.group.image, it -> it instanceof TurnListener)) {
                state.tileAt(unit.hex).actor.setColor(Color.WHITE);
            }
        }
    }

    private void startFirstTurn(GameState state) {
        factionCounter = 0;
        startTurn(state);
    }

    private void advanceToNextTurn(GameState state) {
        CurrentFaction currentFaction = factions.get(factionCounter);
        if (currentFaction.allUnitsActed()) {
            factionCounter += 1;
        }
        if (factionCounter >= factions.size()) {
            router.round();
            return;
        }
        startTurn(state);
    }

    private void startTurn(GameState state) {
        CurrentFaction currentFaction = factions.get(factionCounter);
        currentFaction.units.stream().filter(unitInTurn -> !unitInTurn.hasActed).forEach(unitInTurn -> {
            state.tileAt(unitInTurn.unit.hex).actor.setColor(Color.PINK);
            unitInTurn.unit.group.image.addListener(new TurnListener(unitInTurn));
        });
    }

    private static class CurrentFaction {
        final List<UnitInRound> units;

        private CurrentFaction(List<Unit> units) {
            this.units = units.stream().map(UnitInRound::new).collect(Collectors.toList());
        }

        boolean allUnitsActed() {
            return units.stream().allMatch(it -> it.hasActed);
        }
    }

    private class TurnListener extends ClickListener {

        private final UnitInRound unit;

        public TurnListener(UnitInRound unit) {
            this.unit = unit;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.turn(unit);
        }
    }
}
