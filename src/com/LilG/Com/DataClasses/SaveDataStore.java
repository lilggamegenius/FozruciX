package com.LilG.Com.DataClasses;

import com.LilG.Com.DND.DNDPlayer;

import java.util.*;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
public class SaveDataStore {
    private static List<Note> noteList = new ArrayList<>();

    private static List<String> authedUser = new ArrayList<>();
    private static List<Integer> authedUserLevel = new ArrayList<>();
    private static String avatarLink = "http://puu.sh/mh1mu.png";
    private static HashMap<String, Meme> memes = new HashMap<>();
    private static HashMap<String, String> FCList = new HashMap<>();
    private static Hashtable<String, Vector<String>> markovChain = new Hashtable<>();
    private List<String> DNDJoined = new ArrayList<>();
    private List<DNDPlayer> DNDList = new ArrayList<>();


    public SaveDataStore(List<String> authedUser, List<Integer> authedUserLevel, List<String> DNDJoined, List<DNDPlayer> DNDList, List<Note> noteList, String avatarLink, HashMap<String, Meme> memes, HashMap<String, String> FCList, Hashtable<String, Vector<String>> markovChain) {
        SaveDataStore.authedUser = authedUser;
        SaveDataStore.authedUserLevel = authedUserLevel;
        this.DNDJoined = DNDJoined;
        this.DNDList = DNDList;
        SaveDataStore.noteList = noteList;
        SaveDataStore.avatarLink = avatarLink;
        SaveDataStore.memes = memes;
        SaveDataStore.FCList = FCList;
        SaveDataStore.markovChain = markovChain;
    }

    public static Hashtable<String, Vector<String>> getMarkovChain() {
        return markovChain;
    }

    public static void setMarkovChain(Hashtable<String, Vector<String>> markovChain) {
        SaveDataStore.markovChain = markovChain;
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
