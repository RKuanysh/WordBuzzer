package com.raimbekov.wordbuzzer.word.domain

import com.raimbekov.wordbuzzer.game.model.Question
import io.reactivex.Completable
import io.reactivex.Single
import kotlin.random.Random

class WordInteractor(
    private val wordRepository: WordRepository
) {

    fun initialize(): Completable = wordRepository.initialize()

    fun getQuestions(numberOfQuestions: Int): Single<List<Question>> =
        Single.fromCallable {
            val result = mutableListOf<Question>()
            val size = wordRepository.getSize()

            for (i in 0..numberOfQuestions) {
                val index = Random.nextInt(size)
                val isCorrect = Random.nextBoolean()

                val translationIndex = if (isCorrect) index else Random.nextInt(size)
                result.add(Question(wordRepository.getWord(index), wordRepository.getWord(translationIndex)))
            }

            result
        }
}