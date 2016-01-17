package com.LilG.Com.DataClasses;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ggonz on 10/31/2015.
 */
public class Note implements Serializable {
    final String sender;
    private final String receiver;
    private final String message;
    private final String date;
    private final String channel;
    private final UUID id;

    public Note(String sender, String receiver, String message, String channel) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = new Date().toString();
        this.channel = channel;

        this.id = UUID.randomUUID();
    }

    public String displayMessage() {
        return "\"" + message + "\". Message left by " + sender + " in " + channel + " at " + date + ". ";

    }

    public UUID getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageForList() {
        return "to: " + receiver + "| Message: " + message;
    }

    public String getUUIDForList() {
        return "to: " + receiver + "| UUID: " + id;
    }


    public String toString() {
        return "Sender: " + sender + ". Receiver: " + receiver + ". Date: " + date + ". Channel: " + channel + ". Message: " + message + ".";
    }
}
