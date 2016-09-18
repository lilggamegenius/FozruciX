package com.LilG.Com.DataClasses;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ggonz on 10/31/2015.
 * Note class - Stores a note from one user to another
 */
public class Note {
    private final String sender;
    private final String receiver;
    private final String message;
    private final String date;
    private final String channel;
    @NotNull
    private final UUID id;

    public Note(String sender, String receiver, String message, String channel) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = new Date().toString();
        this.channel = channel;

        this.id = UUID.randomUUID();
    }

    @NotNull
    public String displayMessage() {
        return "\"" + message + "\". Message left by " + sender + " in " + channel + " at " + date + ". ";

    }

    @NotNull
    public UUID getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    @NotNull
    public String getMessageForList() {
        return "to: " + receiver + "| Message: " + message;
    }

    @NotNull
    public String getUUIDForList() {
        return "to: " + receiver + "| UUID: " + id;
    }


    @NotNull
    public String toString() {
        return "Sender: " + sender + ". Receiver: " + receiver + ". Date: " + date + ". Channel: " + channel + ". Message: " + message + ".";
    }
}
