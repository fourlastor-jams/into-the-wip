package io.github.fourlastor.game.demo

import dagger.BindsInstance
import dagger.Subcomponent
import io.github.fourlastor.game.demo.di.DemoModule
import io.github.fourlastor.game.demo.round.monster.MonsterAbilities
import io.github.fourlastor.game.di.ScreenScoped
import io.github.fourlastor.game.route.Router

@ScreenScoped
@Subcomponent(modules = [DemoModule::class, MonsterAbilities::class])
interface DemoComponent {
    @ScreenScoped
    fun screen(): DemoScreen

    @Subcomponent.Builder
    interface Builder {
        fun router(@BindsInstance router: Router): Builder
        fun build(): DemoComponent
    }
}
