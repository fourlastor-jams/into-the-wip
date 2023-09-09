package io.github.fourlastor.game.demo.round

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.Telegram
import io.github.fourlastor.game.demo.round.step.Step
import io.github.fourlastor.game.demo.round.step.StepState
import io.github.fourlastor.game.demo.state.GameState

abstract class Ability(
    private val unitInRound: UnitInRound,
    private val router: StateRouter,
    private val stateFactory: StepState.Factory,
) : RoundState() {
    private lateinit var stateMachine: StateMachine
    protected fun <T> start(initial: Step<T>): Builder<T> {
        return Builder(initial)
    }

    protected fun <T> start(initial: T): Builder<T> {
        return Builder(initial)
    }

    override fun enter(state: GameState) {
        stateMachine = StateMachine(state, InitialState())
        createSteps(state)
            .run(
                {
                    unitInRound.hasActed = true
                    router.endOfAbility()
                },
                { router.endOfAbility() }
            )
    }

    protected abstract fun createSteps(state: GameState): Builder<*>
    override fun update(state: GameState) {
        stateMachine.update()
    }

    override fun exit(state: GameState) {
        stateMachine.changeState(FinalState())
    }

    override fun onMessage(state: GameState, telegram: Telegram): Boolean {
        return if (GameMessage.NEXT_STEP.handles(telegram.message)) {
            stateMachine.changeState(telegram.extraInfo as AbilityState)
            true
        } else {
            super.onMessage(state, telegram)
        }
    }

    private class StateMachine(owner: GameState, initialState: AbilityState) :
        DefaultStateMachine<GameState, AbilityState>(owner, initialState)

    private class InitialState : AbilityState() {
        override fun enter(state: GameState) {}
    }

    private class FinalState : AbilityState() {
        override fun enter(state: GameState) {}
    }

    protected inner class Builder<T> {
        private val current: ((T) -> Unit, () -> Unit) -> Unit

        constructor(initial: T) {
            current = { next, _ -> next(initial) }
        }

        constructor(initial: Step<T>) {
            current = { next, cancel ->
                router.nextStep(
                    stateFactory.create(initial, next, cancel)
                )
            }
        }

        constructor(initial: ((T) -> Unit, () -> Unit) -> Unit) {
            current = initial
        }

        fun <R> sequence(factory: (T) -> Builder<R>): Builder<R> {
            return Builder { next, cancel ->
                current({ result -> factory(result).run(next, cancel) }, cancel)
            }
        }

        fun <R> then(next: Step<R>): Builder<R> {
            return then { _ -> next }
        }

        fun <R> then(factory: (T) -> Step<R>): Builder<R> {
            return Builder { next, cancel ->
                current(
                    { result -> router.nextStep(stateFactory.create(factory(result), next, cancel)) },
                    cancel
                )
            }
        }

        fun run(completion: (T) -> Unit, cancellation: () -> Unit) {
            current(completion, cancellation)
        }
    }
}
