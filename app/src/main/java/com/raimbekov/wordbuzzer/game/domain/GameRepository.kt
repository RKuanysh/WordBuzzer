package com.raimbekov.wordbuzzer.game.domain

import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.game.model.QuestionHolder
import io.reactivex.Completable
import io.reactivex.Single

interface GameRepository {
    fun setQuestions(questions: List<Question>): Completable
    fun getQuestion(): Single<QuestionHolder>
    fun incrementScore(player: Player): Single<Int>
    fun decrementScore(player: Player): Single<Int>
    fun setGame(game: Game): Completable
    fun getGame(): Single<Game>
    fun incrementCurrentQuestion(): Completable
    fun getScore(): Single<Map<Player, Int>>
}