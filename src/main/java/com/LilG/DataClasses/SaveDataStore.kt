package com.LilG.DataClasses

import net.dv8tion.jda.core.entities.TextChannel

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
class SaveDataStore {
    val noteList = LinkedList<Note>()
    val authedUser = LinkedList<String>()
    val authedUserLevel = LinkedList<Int>()
    val avatarLink = "http://puu.sh/oiLvW.gif"
    val memes = TreeMap<String, Meme>()
    val fcList = TreeMap<String, String>()
    val dndJoined = LinkedList<String>()
    private var allowedCommands = HashMap<String, HashMap<String, ArrayList<String>>>()
    private val checkJoinsAndQuits = ConcurrentHashMap<String, String>()
    private val mutedServerList = LinkedList<String>()
    val wordFilter: Map<TextChannel, List<String>> = HashMap()
    private val markovChain = ConcurrentHashMap<String, LinkedList<String>>()

    fun getMarkovChain(): ConcurrentHashMap<String, LinkedList<String>> {
        // Create the first two entries (k:_start, k:_end)
        if (markovChain.isEmpty()) {
            markovChain.put("_start", LinkedList<String>())
            markovChain.put("_end", LinkedList<String>())
        }
        return markovChain
    }

    fun getAllowedCommands(): HashMap<String, HashMap<String, ArrayList<String>>> {
        if (allowedCommands == null) {
            allowedCommands = HashMap<String, HashMap<String, ArrayList<String>>>()
        }
        if (allowedCommands.isEmpty()) {
            var temp = HashMap<String, ArrayList<String>>()
            temp.put("#retro", ArrayList(Arrays.asList("GayDar", "url checker")))
            temp.put("#origami64", ArrayList(Arrays.asList("markov", "my", "url checker")))
            allowedCommands.put("BadnikZONE", temp)
            temp = HashMap<String, ArrayList<String>>()
            temp.put("#deltasmash", ArrayList(Arrays.asList("FC", "version")))
            allowedCommands.put("twitch", temp)
            temp = HashMap<String, ArrayList<String>>()
            temp.put("#pmd", ArrayList(listOf("url checker")))
            allowedCommands.put("CaffieNET", temp)
            temp = HashMap<String, ArrayList<String>>()
            temp.put("#general", ArrayList(listOf("url checker")))
            temp.put("#development", ArrayList(listOf("url checker")))
            allowedCommands.put("Discord Bots", temp)
        }
        return allowedCommands
    }

    fun getCheckJoinsAndQuits(): ConcurrentHashMap<String, String> {
        if (checkJoinsAndQuits.isEmpty()) {
            checkJoinsAndQuits.put("191548246332538880", "214906329498648576")
        }
        return checkJoinsAndQuits
    }

    fun getMutedServerList(): LinkedList<String> {
        if (mutedServerList.isEmpty()) {
            mutedServerList.add("110373943822540800")
        }
        return mutedServerList
    }

    companion object {
        var instance: SaveDataStore = null!!
    }
}
