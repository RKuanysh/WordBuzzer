package com.raimbekov.wordbuzzer.util

import android.content.Context

object FileUtil {

    fun file(context: Context, fileName: String): String {
        val assertsManager = context.assets
        val file = assertsManager.open(fileName)
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()

        return String(formArray)
    }
}