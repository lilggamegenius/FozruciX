package com.LilG.Com.DataClasses;

import com.LilG.Com.DND.DNDPlayer;
import net.dv8tion.jda.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
public class SaveDataStore {
    @NotNull
    private static SaveDataStore INSTANCE;
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
    private HashMap<String, HashMap<String, ArrayList<String>>> allowedCommands = new HashMap<>();
    @NotNull
    private ConcurrentHashMap<String, String> checkJoinsAndQuits = new ConcurrentHashMap<>();
    @NotNull
    private LinkedList<String> mutedServerList = new LinkedList<>();
    @NotNull
    private Map<TextChannel, List<String>> wordFilter = new HashMap<>();
    @NotNull
    private ConcurrentHashMap<String, LinkedList<String>> markovChain = new ConcurrentHashMap<>();

    public SaveDataStore() {
    }

    public static SaveDataStore getINSTANCE() {
        return INSTANCE;
    }

    public static void setINSTANCE(SaveDataStore INSTANCE) {
        SaveDataStore.INSTANCE = INSTANCE;
    }

    @NotNull
    public ConcurrentHashMap<String, LinkedList<String>> getMarkovChain() {
        // Create the first two entries (k:_start, k:_end)
        if (markovChain.isEmpty()) {
            markovChain.put("_start", new LinkedList<>());
            markovChain.put("_end", new LinkedList<>());
        }
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

    @NotNull
    public HashMap<String, HashMap<String, ArrayList<String>>> getAllowedCommands() {
        if (allowedCommands == null) {
            allowedCommands = new HashMap<>();
        }
        if (allowedCommands.isEmpty()) {
            HashMap<String, ArrayList<String>> temp = new HashMap<>();
            temp.put("#retro", new ArrayList<>(Arrays.asList("GayDar", "url checker")));
            temp.put("#origami64", new ArrayList<>(Arrays.asList("markov", "my", "url checker")));
            allowedCommands.put("BadnikZONE", temp);
            temp = new HashMap<>();
            temp.put("#deltasmash", new ArrayList<>(Arrays.asList("FC", "version")));
            allowedCommands.put("twitch", temp);
            temp = new HashMap<>();
            temp.put("#pmd", new ArrayList<>(Collections.singletonList("url checker")));
            allowedCommands.put("CaffieNET", temp);
            temp = new HashMap<>();
            temp.put("#general", new ArrayList<>(Collections.singletonList("url checker")));
            temp.put("#development", new ArrayList<>(Collections.singletonList("url checker")));
            allowedCommands.put("Discord Bots", temp);
        }
        return allowedCommands;
    }

    public ConcurrentHashMap<String, String> getCheckJoinsAndQuits() {
        if (checkJoinsAndQuits.isEmpty()) {
            checkJoinsAndQuits.put("191548246332538880", "214906329498648576");
        }
        return checkJoinsAndQuits;
    }

    public LinkedList<String> getMutedServerList() {
        if(mutedServerList.isEmpty()){
            mutedServerList.add("110373943822540800");
        }
        return mutedServerList;
    }

    @NotNull
    public Map<TextChannel, List<String>> getWordFilter() {
        return wordFilter;
    }
}
