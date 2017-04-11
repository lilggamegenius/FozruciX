package com.LilG.DataClasses

import java.util.*

/**
 * Created by ggonz on 10/31/2015.
 * Note class - Stores a note from one user to another
 */
class Note(val sender: String, val receiver: String, private val message: String, private val channel: String) {
    private val date: String = Date().toString()
    val id: UUID = UUID.randomUUID()

    fun displayMessage(): String {
        return "\"$message\". Message left by $sender in $channel at $date. "

    }

    val messageForList: String
        get() = "to: $receiver| Message: $message"

    val uuidForList: String
        get() = "to: $receiver| UUID: $id"


    override fun toString(): String {
        return "Sender: $sender. Receiver: $receiver. Date: $date. Channel: $channel. Message: $message."
    }
}
