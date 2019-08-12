package com.raimbekov.wordbuzzer.word.domain

import com.raimbekov.wordbuzzer.game.model.Question
import io.reactivex.Completable
import io.reactivex.Single

class WordInteractor(
    private val wordRepository: WordRepository
) {

    fun initialize(): Completable = wordRepository.initialize()

    fun getQuestions(numberOfQuestions: Int): Single<List<Question>> =
        Single.fromCallable {
            val result = mutableListOf<Question>()
            for (i in 0..numberOfQuestions) {
                result.add(Question(wordRepository.getWord(i), wordRepository.getWord(i)))
            }
            result
        }
}