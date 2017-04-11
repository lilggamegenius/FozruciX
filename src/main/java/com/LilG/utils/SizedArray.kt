package com.LilG.utils

import java.util.*

class SizedArray<T> @JvmOverloads constructor(private var maxSize: Int = 5) : LinkedList<T>() {

    fun setMaxSize(size: Int) {
        maxSize = size
    }

    override fun add(`object`: T): Boolean {
        //If the array is too big, remove elements until it's the right size.
        while (size >= maxSize) {
            removeAt(0)
        }
        return super.add(`object`)
    }

    fun get(): T? {
        if (size == 0) {
            return null
        }
        return super.get(size - 1)
    }
}