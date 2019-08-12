package com.raimbekov.wordbuzzer.game

import com.raimbekov.wordbuzzer.game.domain.GameInteractor
import com.raimbekov.wordbuzzer.game.domain.GameRepository
import com.raimbekov.wordbuzzer.game.gateway.GameMemoryRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

object GameModule {

    val module = module {
        factory { GameInteractor(get(), get()) }
        factory { GameMemoryRepository() } bind GameRepository::class
        viewModel { (numberOfPlayers: Int) -> GameViewModel(numberOfPlayers, get()) }
    }
}