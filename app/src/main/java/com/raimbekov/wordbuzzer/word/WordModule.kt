package com.raimbekov.wordbuzzer.word

import com.raimbekov.wordbuzzer.word.domain.WordInteractor
import com.raimbekov.wordbuzzer.word.domain.WordRepository
import com.raimbekov.wordbuzzer.word.repository.WordMemoryRepository
import org.koin.dsl.module

object WordModule {

    val module = module {
        factory { WordInteractor(get()) }
        factory<WordRepository> { WordMemoryRepository(get()) }
    }
}