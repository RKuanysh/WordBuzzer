package com.raimbekov.wordbuzzer.game.domain

import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.QuestionHolder
import com.raimbekov.wordbuzzer.word.domain.WordInteractor
import io.reactivex.Completable
import io.reactivex.Single

private const val NUMBER_OF_QUESTIONS = 10

class GameInteractor(
    private val wordInteractor: WordInteractor,
    private val gameRepository: GameRepository
) {

    fun start(numberOfPlayers: Int): Single<Game> =
        wordInteractor.initialize()
            .andThen(gameRepository.setGame(createGame(numberOfPlayers)))
            .andThen(wordInteractor.getQuestions(NUMBER_OF_QUESTIONS))
            .flatMapCompletable { questions -> gameRepository.setQuestions(questions) }
            .andThen(gameRepository.getGame())

    fun setCorrectAnswer(player: Player): Single<Map<Player, Int>> =
        gameRepository.getQuestion()
            .map { (it as QuestionHolder.NextQuestion).question }
            .map { it.correctAnswer == it.display }
            .flatMapCompletable { isCorrect ->
                if (isCorrect) {
                    gameRepository.incrementScore(player)
                } else {
                    gameRepository.decrementScore(player)
                }
            }
            .andThen(gameRepository.getScore())

    fun getCurrentQuestion(): Single<QuestionHolder> = gameRepository.getQuestion()

    fun moveToNextQuestion(): Completable = gameRepository.incrementCurrentQuestion()

    private fun createGame(numberOfPlayers: Int): Game {
        val players = mutableListOf<Player>()
        for (i in 0 until numberOfPlayers) {
            val player = Player(i)
            players.add(player)
        }
        return Game(players)
    }

}