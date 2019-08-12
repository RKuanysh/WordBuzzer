package com.raimbekov.wordbuzzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val NUMBER_OF_PLAYERS = 2

class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModel { parametersOf(NUMBER_OF_PLAYERS) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.testLiveData.observe(this, Observer {
        })
        viewModel.playersLiveData.observe(this, Observer { players ->
            button1.text = "player ${players[0].id}"
            button2.text = "player ${players[1].id}"

            button1.setOnClickListener {
                viewModel.setCorrectAnswer(players[0])
            }

            button2.setOnClickListener {
                viewModel.setCorrectAnswer(players[1])
            }
        })

        viewModel.questionLiveData.observe(this, Observer {
            wordTextView.text = it.correctAnswer.word
        })

        viewModel.scoreLiveData.observe(this, Observer { (player, score) ->
            if (player.id == 0) {
                score1.text = score.toString()
            } else if (player.id == 1) {
                score2.text = score.toString()
            }
        })
    }
}
