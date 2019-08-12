package com.raimbekov.wordbuzzer.game.model

import com.raimbekov.wordbuzzer.word.model.Word

data class Question(
    val correctAnswer: Word,
    val display: Word
)