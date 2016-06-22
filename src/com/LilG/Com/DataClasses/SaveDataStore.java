package com.LilG.Com.DataClasses;

import com.LilG.Com.DND.DNDPlayer;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
public class SaveDataStore {
    private LinkedList<Note> noteList = new LinkedList<>();

    private LinkedList<String> authedUser = new LinkedList<>();
    private LinkedList<Integer> authedUserLevel = new LinkedList<>();
    private String avatarLink = "http://puu.sh/oiLvW.gif";
    private TreeMap<String, Meme> memes = new TreeMap<>();
    private TreeMap<String, String> FCList = new TreeMap<>();
    private LinkedList<String> DNDJoined = new LinkedList<>();
    private LinkedList<DNDPlayer> DNDList = new LinkedList<>();
    private ConcurrentHashMap<String, LinkedList<String>> markovChain = new ConcurrentHashMap<>();


    public SaveDataStore(LinkedList<String> authedUser, LinkedList<Integer> authedUserLevel, LinkedList<String> DNDJoined, LinkedList<DNDPlayer> DNDList, LinkedList<Note> noteList, String avatarLink, TreeMap<String, Meme> memes, TreeMap<String, String> FCList, ConcurrentHashMap<String, LinkedList<String>> markovChain) {
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

    public ConcurrentHashMap<String, LinkedList<String>> getMarkovChain() {
        return markovChain;
    }

    public LinkedList<Note> getNoteList() {
        return noteList;
    }

    public LinkedList<String> getAuthedUser() {
        return authedUser;
    }

    public LinkedList<Integer> getAuthedUserLevel() {
        return authedUserLevel;
    }

    public LinkedList<String> getDNDJoined() {
        return DNDJoined;
    }

    public LinkedList<DNDPlayer> getDNDList() {
        return DNDList;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public TreeMap<String, Meme> getMemes() {
        return memes;
    }

    public TreeMap<String, String> getFCList() {
        return FCList;
    }
}
