package com.raimbekov.wordbuzzer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raimbekov.wordbuzzer.game.domain.GameInteractor
import com.raimbekov.wordbuzzer.game.model.Player
import com.raimbekov.wordbuzzer.game.model.Question
import com.raimbekov.wordbuzzer.word.model.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class GameViewModel(
    numberOfPlayers: Int,
    private val gameInteractor: GameInteractor
) : ViewModel() {

    private val gameSubscription: Disposable
    private var answerSubscription: Disposable? = null
    private var questionSubscription: Disposable? = null

    val testLiveData = MutableLiveData<String>().apply { value = Random.nextDouble().toString() }
    val playersLiveData = MutableLiveData<List<Player>>()
    val questionLiveData = MutableLiveData<Question>()
    val scoreLiveData = MutableLiveData<Pair<Player, Int>>()

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
                { score ->
                    scoreLiveData.value = player to score
                    requestQuestion()
                },
                { }
            )
    }

    fun requestQuestion() {
        questionSubscription?.dispose()

        questionSubscription = gameInteractor.getCurrentQuestion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { questionLiveData.value = it },
                { }
            )
    }

    override fun onCleared() {
        super.onCleared()
        gameSubscription.dispose()
        answerSubscription?.dispose()
        questionSubscription?.dispose()
    }
}