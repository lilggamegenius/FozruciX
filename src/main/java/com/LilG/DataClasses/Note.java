package com.LilG.DataClasses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ggonz on 10/31/2015.
 * Note class - Stores a note from one user to another
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Note {
	private final String sender;
	private final String receiver;
	private final String message;
	private final String channel;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final Date date;
	private final UUID id;

	public Note(String sender, String receiver, String message, String channel) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.channel = channel;
		this.date = new Date();
		this.id = UUID.randomUUID();
	}

	@JsonCreator
	private Note(@JsonProperty("sender") String sender,
	             @JsonProperty("receiver") String receiver,
	             @JsonProperty("message") String message,
	             @JsonProperty("channel") String channel,
	             @JsonProperty("date") Date date,
	             @JsonProperty("id") UUID id) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.channel = channel;

		this.date = date;
		this.id = id;
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
		return "Sender: " + sender + ". Receiver: " + receiver + ". Date: " + date.toString() + ". Channel: " + channel + ". Message: " + message + ".";
	}
}
