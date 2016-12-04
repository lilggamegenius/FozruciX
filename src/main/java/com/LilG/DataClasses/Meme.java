package com.LilG.DataClasses;

import org.jetbrains.annotations.NotNull;

/**
 * The thing that makes the world go round
 * Better than left shark
 */

public class Meme {
    private final String creator;
    private String Meme;

    public Meme(String creator, String Meme) {
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
