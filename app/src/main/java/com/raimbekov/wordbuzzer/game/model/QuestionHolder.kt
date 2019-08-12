package com.raimbekov.wordbuzzer.game.model

sealed class QuestionHolder {
    class GameEnded(val winner: Player?) : QuestionHolder()
    class NextQuestion(val question: Question) : QuestionHolder()
}