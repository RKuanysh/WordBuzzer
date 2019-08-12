package com.raimbekov.wordbuzzer.game.domain

import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.word.domain.WordInteractor
import io.reactivex.Completable
import io.reactivex.Single

private const val NUMBER_OF_QUESTIONS = 20

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

    fun setCorrectAnswer(player: Player): Single<Int> =
        gameRepository.getCurrentQuestion()
            .map { it.correctAnswer == it.display }
            .flatMap { isCorrect ->
                if (isCorrect) {
                    gameRepository.incrementScore(player)
                } else {
                    gameRepository.decrementScore(player)
                }
            }

    fun getCurrentQuestion(): Single<Question> = gameRepository.getCurrentQuestion()

    fun moveToNextQuestion(): Completable = gameRepository.incrementCurrentQuestion()

    private fun createGame(numberOfPlayers: Int): Game {
        val players = mutableListOf<Player>()
        for (i in 0..numberOfPlayers) {
            val player = Player(i)
            players.add(player)
        }
        return Game(players)
    }

}