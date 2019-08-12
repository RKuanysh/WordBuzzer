package com.raimbekov.wordbuzzer.game.model

sealed class QuestionHolder {
    object GameEnded : QuestionHolder()
    class NextQuestion(val question: Question) : QuestionHolder()
}