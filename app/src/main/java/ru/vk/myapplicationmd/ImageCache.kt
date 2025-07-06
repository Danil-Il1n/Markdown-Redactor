package ru.vk.myapplicationmd

import android.graphics.Bitmap

object ImageCache {
    private val memoryCache = mutableMapOf<String, Bitmap>()

    fun get(url: String): Bitmap? = memoryCache[url]

    fun put(url: String, bitmap: Bitmap) {
        memoryCache[url] = bitmap
    }
}