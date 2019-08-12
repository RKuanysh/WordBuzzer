package com.raimbekov.wordbuzzer

import android.app.Application
import com.raimbekov.wordbuzzer.game.GameModule
import com.raimbekov.wordbuzzer.word.WordModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WordApplication)

            modules(GameModule.module)
            modules(WordModule.module)
        }
    }
}