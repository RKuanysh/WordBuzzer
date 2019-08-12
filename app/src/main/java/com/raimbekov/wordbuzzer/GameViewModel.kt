package com.raimbekov.wordbuzzer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raimbekov.wordbuzzer.game.domain.GameInteractor
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GameViewModel(
    numberOfPlayers: Int,
    private val gameInteractor: GameInteractor
) : ViewModel() {

    private val gameSubscription: Disposable
    private var answerSubscription: Disposable? = null
    private var questionSubscription: Disposable? = null
    private var timerSubscription: Disposable? = null

    val playersLiveData = MutableLiveData<List<Player>>()
    val questionLiveData = MutableLiveData<Question>()
    val scoreLiveData = MutableLiveData<Map<Player, Int>>()

    init {
        gameSubscription = gameInteractor.start(numberOfPlayers)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { game ->
                    playersLiveData.value = game.players
                    requestQuestion()
                },
                { }
            )
    }

    fun setCorrectAnswer(player: Player) {
        answerSubscription?.dispose()

        answerSubscription = gameInteractor.setCorrectAnswer(player)
            .flatMap { score ->
                gameInteractor.moveToNextQuestion()
                    .toSingleDefault(score)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    scoreLiveData.value = it
                    requestQuestion()
                },
                { }
            )
    }

    fun requestQuestion() {
        timerSubscription?.dispose()
        questionSubscription?.dispose()

        questionSubscription = gameInteractor.getCurrentQuestion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    questionLiveData.value = it
                    launchTimer()
                },
                { }
            )
    }

    override fun onCleared() {
        super.onCleared()
        gameSubscription.dispose()
        answerSubscription?.dispose()
        questionSubscription?.dispose()
    }

    private fun launchTimer() {
        timerSubscription?.dispose()
        timerSubscription = Completable.timer(3, TimeUnit.SECONDS)
            .andThen(gameInteractor.moveToNextQuestion())
            .subscribe(
                { requestQuestion() },
                { }
            )
    }
}