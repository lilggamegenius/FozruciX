package com.LilG.DataClasses

/**
 * The thing that makes the world go round
 * Better than left shark
 */

class Meme(val creator: String, var meme: String?) {

    override fun toString(): String {
        return "Creator: $creator - $meme"
    }
}
