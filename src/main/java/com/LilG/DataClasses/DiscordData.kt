package com.LilG.DataClasses


import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*

/**
 * Created by ggonz on 9/7/2016.
 * Storage class for Discord Stuff
 */
object DiscordData {
    var channelRoleMap: Map<TextChannel, List<Role>> = HashMap()

    var wordFilter: Map<TextChannel, List<String>> = HashMap()
}
