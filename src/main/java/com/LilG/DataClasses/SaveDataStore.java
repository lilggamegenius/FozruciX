package com.LilG.DataClasses;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class SaveDataStore {
	@NotNull
	private static SaveDataStore INSTANCE;
	@NotNull
	private List<Note> noteList = new LinkedList<>();
	@NotNull
	private Map<String, Integer> authedUser = new TreeMap<>();
	@NotNull
	private String avatarLink = "http://puu.sh/oiLvW.gif";
	@NotNull
	private Map<String, Meme> memes = new TreeMap<>();
	@NotNull
	private Map<String, String> FCList = new TreeMap<>();
	@NotNull
	private Map<String, Map<String, List<String>>> allowedCommands = new HashMap<>();
	@NotNull
	private Map<String, String> checkJoinsAndQuits = new ConcurrentHashMap<>();
	@NotNull
	private List<String> mutedServerList = new LinkedList<>();
	@NotNull
	private Map<String, List<String>> wordFilter = new HashMap<>();
	@NotNull
	private Map<String, GuildEX> guildGuildEXMap = new HashMap<>();
	@NotNull
	private Map<String, List<String>> markovChain = new ConcurrentHashMap<>();

	public SaveDataStore() {
	}

	public static SaveDataStore getINSTANCE() {
		return INSTANCE;
	}

	public static void setINSTANCE(SaveDataStore INSTANCE) {
		SaveDataStore.INSTANCE = INSTANCE;
	}

	@NotNull
	public Map<String, List<String>> getMarkovChain() {
		// Create the first two entries (k:_start, k:_end)
		if (markovChain.isEmpty()) {
			markovChain.put("_start", new LinkedList<>());
			markovChain.put("_end", new LinkedList<>());
		}
		return markovChain;
	}

	@NotNull
	public List<Note> getNoteList() {
		return noteList;
	}

	@NotNull
	public Map<String, Integer> getAuthedUser() {
		return authedUser;
	}

	@NotNull
	public String getAvatarLink() {
		return avatarLink;
	}

	@NotNull
	public Map<String, Meme> getMemes() {
		return memes;
	}

	@NotNull
	public Map<String, String> getFCList() {
		return FCList;
	}

	@NotNull
	public Map<String, Map<String, List<String>>> getAllowedCommands() {
		if (allowedCommands == null) {
			allowedCommands = new HashMap<>();
		}
		if (allowedCommands.isEmpty()) {
			Map<String, List<String>> temp = new HashMap<>();
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

	public Map<String, String> getCheckJoinsAndQuits() {
		if (checkJoinsAndQuits.isEmpty()) {
			checkJoinsAndQuits.put("191548246332538880", "214906329498648576");
		}
		return checkJoinsAndQuits;
	}

	public List<String> getMutedServerList() {
		if (mutedServerList.isEmpty()) {
			mutedServerList.add("110373943822540800");
		}
		return mutedServerList;
	}

	@NotNull
	public Map<String, List<String>> getWordFilter() {
		return wordFilter;
	}

	@NotNull
	public Map<String, GuildEX> getGuildGuildEXMap() {
		return guildGuildEXMap;
	}
}
