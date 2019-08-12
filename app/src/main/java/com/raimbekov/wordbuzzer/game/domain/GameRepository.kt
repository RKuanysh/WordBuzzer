package com.raimbekov.wordbuzzer.game.domain

import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import io.reactivex.Completable
import io.reactivex.Single

interface GameRepository {
    fun setQuestions(questions: List<Question>): Completable
    fun getCurrentQuestion(): Single<Question>
    fun incrementScore(player: Player): Single<Int>
    fun decrementScore(player: Player): Single<Int>
    fun getNextQuestion(): Single<Question>
    fun setGame(game: Game): Completable
    fun getGame(): Single<Game>
    fun incrementCurrentQuestion(): Completable
}