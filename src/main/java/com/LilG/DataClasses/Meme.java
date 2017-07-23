package com.LilG.DataClasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * The thing that makes the world go round
 * Better than left shark
 */

public class Meme {
	private final String creator;
	private String Meme;

	@JsonCreator
	public Meme(@JsonProperty("creator") String creator,
	            @JsonProperty("Meme") String Meme) {
		this.creator = creator;
		this.Meme = Meme;
	}

	public String getCreator() {
		return creator;
	}

	public String getMeme() {
		return Meme;
	}

	public void setMeme(String meme) {
		Meme = meme;
	}

	@NotNull
	public String toString() {
		return "Creator: " + creator + " - " + Meme;
	}
}
