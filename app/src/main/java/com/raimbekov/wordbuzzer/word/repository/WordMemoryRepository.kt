package com.raimbekov.wordbuzzer.word.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raimbekov.wordbuzzer.util.FileUtil
import com.raimbekov.wordbuzzer.word.domain.WordRepository
import com.raimbekov.wordbuzzer.word.model.Word
import io.reactivex.Completable


class WordMemoryRepository(
    private val context: Context
) : WordRepository {

    private lateinit var words: List<Word>

    override fun initialize(): Completable =
        if (!::words.isInitialized)
            Completable.fromAction {
                val text = FileUtil.file(context, "words.json")
                val listType = object : TypeToken<List<Word>>() {}.type
                words = Gson().fromJson(text, listType)
            }
        else Completable.complete()

    override fun getSize(): Int = words.size

    override fun getWord(index: Int): Word = words.get(index)
}