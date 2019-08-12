package com.raimbekov.wordbuzzer.word.model

import com.google.gson.annotations.SerializedName

data class Word(

    @SerializedName("text_eng")
    val word: String,

    @SerializedName("text_spa")
    val translation: String
)