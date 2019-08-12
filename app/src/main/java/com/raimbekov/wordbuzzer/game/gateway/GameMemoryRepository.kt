package com.raimbekov.wordbuzzer.game.gateway

import com.raimbekov.wordbuzzer.game.domain.GameRepository
import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.game.model.QuestionHolder
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class GameMemoryRepository : GameRepository {

    private lateinit var game: Game
    private lateinit var questions: List<Question>
    private var currentQuestion = AtomicInteger(0)
    private val scores = ConcurrentHashMap<Player, Int>()

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
            if (questions.size == currentQuestion.get()) {
                var winner: Player? = null
                var max: Int = -1000 // FIXME
                scores.keys.forEach {
                    val score = scores.get(it)
                    if (score!!.compareTo(max) > 0) {
                        winner = it
                        max = score
                    } else if (score.compareTo(max) == 0) {
                        winner = null
                    }
                }
                QuestionHolder.GameEnded(winner)
            } else {
                QuestionHolder.NextQuestion(questions.get(currentQuestion.get()))
            }
        }

    override fun incrementScore(player: Player): Completable =
        Completable.fromAction {
            val newScore = scores.get(player)!! + 1
            scores.put(player, newScore)
        }

    override fun decrementScore(player: Player): Completable =
        Completable.fromAction {
            val newScore = scores.get(player)!! - 1
            scores.put(player, newScore)
        }

    override fun incrementCurrentQuestion(): Completable =
        Completable.fromAction {
            currentQuestion.incrementAndGet()
        }

    override fun getScore(): Single<Map<Player, Int>> = Single.fromCallable { scores }
}