package com.raimbekov.wordbuzzer.game.domain

import com.raimbekov.wordbuzzer.BuzzerRunner
import com.raimbekov.wordbuzzer.game.model.Game
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.game.model.QuestionHolder
import com.raimbekov.wordbuzzer.word.domain.WordInteractor
import com.raimbekov.wordbuzzer.word.model.Word
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(BuzzerRunner::class)
class GameInteractorTest {

    private lateinit var gameInteractor: GameInteractor
    private lateinit var wordInteractor: WordInteractor
    private lateinit var gameRepository: GameRepository

    @Before
    fun before() {
        wordInteractor = mockk()
        gameRepository = mockk()
        gameInteractor = GameInteractor(wordInteractor, gameRepository)
    }

    @Test
    fun start() {
        val game: Game = mockk()
        every { wordInteractor.initialize() } returns Completable.complete()
        every { gameRepository.setGame(any()) } returns Completable.complete()
        every { wordInteractor.getQuestions(any()) } returns Single.just(mockk())
        every { gameRepository.setQuestions(any()) } returns Completable.complete()
        every { gameRepository.getGame() } returns Single.just(game)

        val subscription = gameInteractor.start(2).test()
        subscription.assertValue(game)
    }

    @Test
    fun `setCurrentAnswer - correct answer`() {
        val questionHolder = QuestionHolder.NextQuestion(Question(Word("a", "b"), Word("a", "b")))
        val player: Player = mockk()
        val score: Map<Player, Int> = mockk()
        every { gameRepository.getQuestion() } returns Single.just(questionHolder)
        every { gameRepository.incrementScore(player) } returns Completable.complete()
        every { gameRepository.getScore() } returns Single.just(score)

        val subscription = gameInteractor.setCorrectAnswer(player).test()

        subscription.assertValue(score)
        verify { gameRepository.decrementScore(player) wasNot Called }
    }

    @Test
    fun `setCurrentAnswer - incorrect answer`() {
        val questionHolder = QuestionHolder.NextQuestion(Question(Word("a", "b"), Word("c", "d")))
        val player: Player = mockk()
        val score: Map<Player, Int> = mockk()
        every { gameRepository.getQuestion() } returns Single.just(questionHolder)
        every { gameRepository.decrementScore(player) } returns Completable.complete()
        every { gameRepository.getScore() } returns Single.just(score)

        val subscription = gameInteractor.setCorrectAnswer(player).test()

        subscription.assertValue(score)
        verify { gameRepository.incrementScore(player) wasNot Called }
    }
}