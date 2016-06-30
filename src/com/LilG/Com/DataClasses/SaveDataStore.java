package com.LilG.Com.DataClasses;

import com.LilG.Com.DND.DNDPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
public class SaveDataStore {
    @NotNull
    private LinkedList<Note> noteList = new LinkedList<>();
    @NotNull
    private LinkedList<String> authedUser = new LinkedList<>();
    @NotNull
    private LinkedList<Integer> authedUserLevel = new LinkedList<>();
    @NotNull
    private String avatarLink = "http://puu.sh/oiLvW.gif";
    @NotNull
    private TreeMap<String, Meme> memes = new TreeMap<>();
    @NotNull
    private TreeMap<String, String> FCList = new TreeMap<>();
    @NotNull
    private LinkedList<String> DNDJoined = new LinkedList<>();
    @NotNull
    private LinkedList<DNDPlayer> DNDList = new LinkedList<>();
    @NotNull
    private ConcurrentHashMap<String, LinkedList<String>> markovChain = new ConcurrentHashMap<>();


    public SaveDataStore(@NotNull LinkedList<String> authedUser, @NotNull LinkedList<Integer> authedUserLevel, @NotNull LinkedList<String> DNDJoined, @NotNull LinkedList<DNDPlayer> DNDList, @NotNull LinkedList<Note> noteList, @NotNull String avatarLink, @NotNull TreeMap<String, Meme> memes, @NotNull TreeMap<String, String> FCList, @NotNull ConcurrentHashMap<String, @NotNull LinkedList<String>> markovChain) {
        this.authedUser = authedUser;
        this.authedUserLevel = authedUserLevel;
        this.DNDJoined = DNDJoined;
        this.DNDList = DNDList;
        this.noteList = noteList;
        this.avatarLink = avatarLink;
        this.memes = memes;
        this.FCList = FCList;
        this.markovChain = markovChain;
    }

    @NotNull
    public ConcurrentHashMap<String, LinkedList<String>> getMarkovChain() {
        return markovChain;
    }

    @NotNull
    public LinkedList<Note> getNoteList() {
        return noteList;
    }

    @NotNull
    public LinkedList<String> getAuthedUser() {
        return authedUser;
    }

    @NotNull
    public LinkedList<Integer> getAuthedUserLevel() {
        return authedUserLevel;
    }

    @NotNull
    public LinkedList<String> getDNDJoined() {
        return DNDJoined;
    }

    @NotNull
    public LinkedList<DNDPlayer> getDNDList() {
        return DNDList;
    }

    @NotNull
    public String getAvatarLink() {
        return avatarLink;
    }

    @NotNull
    public TreeMap<String, Meme> getMemes() {
        return memes;
    }

    @NotNull
    public TreeMap<String, String> getFCList() {
        return FCList;
    }
}
