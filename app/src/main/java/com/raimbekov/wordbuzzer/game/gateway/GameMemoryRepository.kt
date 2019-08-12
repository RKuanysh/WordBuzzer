package com.raimbekov.wordbuzzer.game.gateway

import com.raimbekov.wordbuzzer.game.domain.GameRepository
import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.game.model.QuestionHolder
import io.reactivex.Completable
import io.reactivex.Single

class GameMemoryRepository : GameRepository {

    private lateinit var game: Game
    private lateinit var questions: List<Question>
    private var currentQuestion: Int = 0
    private val scores: HashMap<Player, Int> = HashMap()

    override fun setGame(game: Game): Completable =
        Completable.fromAction {
            game.players.forEach { player ->
                scores.put(player, 0)
            }
            this.game = game
        }


    override fun getGame(): Single<Game> = Single.fromCallable { game }

    override fun setQuestions(questions: List<Question>): Completable =
        Completable.fromAction { this.questions = questions }

    override fun getQuestion(): Single<QuestionHolder> =
        Single.fromCallable {
            if (questions.size == currentQuestion) {
                QuestionHolder.GameEnded
            } else {
                QuestionHolder.NextQuestion(questions.get(currentQuestion))
            }
        }

    override fun incrementScore(player: Player): Single<Int> =
        Single.fromCallable {
            val newScore = scores.get(player)!! + 1
            scores.put(player, newScore)
            newScore
        }

    override fun decrementScore(player: Player): Single<Int> =
        Single.fromCallable {
            val newScore = scores.get(player)!! - 1
            scores.put(player, newScore)
            newScore
        }

    override fun incrementCurrentQuestion(): Completable =
        Completable.fromAction {
            currentQuestion += 1
        }

    override fun getScore(): Single<Map<Player, Int>> = Single.fromCallable { scores }
}