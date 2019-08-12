package com.raimbekov.wordbuzzer.word.domain

import com.raimbekov.wordbuzzer.word.model.Word
import io.reactivex.Completable
import io.reactivex.Single

interface WordRepository {

    fun initialize(): Completable

    fun getSize(): Int

    fun getWord(index: Int): Word
}