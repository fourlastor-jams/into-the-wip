package io.github.fourlastor.game.demo.round;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.tommyettinger.ds.ObjectList;
import io.github.fourlastor.game.demo.round.faction.Faction;
import io.github.fourlastor.game.demo.state.GameState;
import io.github.fourlastor.game.demo.state.unit.Unit;
import io.github.fourlastor.game.ui.ActorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import squidpony.squidmath.IRNG;

public class Round extends RoundState {

    private final StateRouter router;
    private final IRNG rng;
    private List<CurrentFaction> factions = null;
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
        CurrentFaction currentFaction = factions.get(factionCounter);
        currentFaction.units.stream().filter(unitInTurn -> !unitInTurn.hasActed).forEach(unitInTurn -> {
            state.tileAt(unitInTurn.unit.hex).actor.setColor(Color.PINK);
            unitInTurn.unit.group.image.addListener(new TurnListener(unitInTurn, currentFaction));
        });
    }

    private static class CurrentFaction {
        final List<UnitInTurn> units;
        final List<Unit> alreadyDidTurn = new ArrayList<>();

        private CurrentFaction(List<Unit> units) {
            this.units = units.stream().map(UnitInTurn::new).collect(Collectors.toList());
        }
    }

    private class TurnListener extends ClickListener {

        private final UnitInTurn unit;
        private final CurrentFaction currentFaction;

        public TurnListener(UnitInTurn unit, CurrentFaction currentFaction) {
            this.unit = unit;
            this.currentFaction = currentFaction;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            router.turn(unit);
        }
    }
}
