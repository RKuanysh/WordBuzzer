package com.raimbekov.wordbuzzer.game

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.raimbekov.wordbuzzer.R
import kotlinx.android.synthetic.main.activity_game.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.random.Random


private const val NUMBER_OF_PLAYERS = 2

class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModel { parametersOf(NUMBER_OF_PLAYERS) }

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

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
            translationTextView.text = it.display.translation

            translationTextView.clearAnimation()
            val animation = getTranslationAnimation()
            animation.duration = 3000
            animation.fillAfter = false
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    translationTextView.alpha = 0f
                }

                override fun onAnimationStart(animation: Animation?) {
                    translationTextView.alpha = 1f
                }
            })

            translationTextView.startAnimation(animation)

        })

        viewModel.scoreLiveData.observe(this, Observer { score ->
            score.keys.forEach { player ->
                if (player.id == 0) {
                    score1.text = score.get(player).toString()
                } else if (player.id == 1) {
                    score2.text = score.get(player).toString()
                }
            }
        })

        viewModel.endOfGameEvent.observe(this, Observer {
            translationTextView.clearAnimation()

            val message = if (it == null) "Draw game" else "player ${it.id} wins"
            AlertDialog.Builder(this)
                .setTitle("Game ended")
                .setMessage(message)
                .setOnDismissListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        })
    }

    private fun getTranslationAnimation(): TranslateAnimation =
        when (Random.nextInt(4)) {
            0 -> TranslateAnimation(
                -screenWidth / 2f + 50, screenWidth / 2f - 50,
                -screenHeight / 2f + 50, screenHeight / 2f - 50
            )
            1 -> TranslateAnimation(
                screenWidth / 2f - 50, -screenWidth / 2f + 50,
                -screenHeight / 2f + 50, screenHeight / 2f - 50
            )
            2 -> TranslateAnimation(
                screenWidth / 2f - 50, -screenWidth / 2f + 50,
                screenHeight / 2f - 50, -screenHeight / 2f + 50
            )
            else -> TranslateAnimation(
                -screenWidth / 2f + 50, screenWidth / 2f - 50,
                screenHeight / 2f - 50, -screenHeight / 2f + 50
            )
        }
}
