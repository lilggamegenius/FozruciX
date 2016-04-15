package com.LilG.Com.DataClasses;

import com.LilG.Com.DND.DNDPlayer;

import java.util.*;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
public class SaveDataStore {
    private List<Note> noteList = new ArrayList<>();

    private List<String> authedUser = new ArrayList<>();
    private List<Integer> authedUserLevel = new ArrayList<>();
    private String avatarLink = "http://puu.sh/oiLvW.gif";
    private HashMap<String, Meme> memes = new HashMap<>();
    private HashMap<String, String> FCList = new HashMap<>();
    private Hashtable<String, Vector<String>> markovChain = new Hashtable<>();
    private List<String> DNDJoined = new ArrayList<>();
    private List<DNDPlayer> DNDList = new ArrayList<>();


    public SaveDataStore(List<String> authedUser, List<Integer> authedUserLevel, List<String> DNDJoined, List<DNDPlayer> DNDList, List<Note> noteList, String avatarLink, HashMap<String, Meme> memes, HashMap<String, String> FCList, Hashtable<String, Vector<String>> markovChain) {
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

    public Hashtable<String, Vector<String>> getMarkovChain() {
        return markovChain;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public List<String> getAuthedUser() {
        return authedUser;
    }

    public List<Integer> getAuthedUserLevel() {
        return authedUserLevel;
    }

    public List<String> getDNDJoined() {
        return DNDJoined;
    }

    public List<DNDPlayer> getDNDList() {
        return DNDList;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public HashMap<String, Meme> getMemes() {
        return memes;
    }

    public HashMap<String, String> getFCList() {
        return FCList;
    }
}
