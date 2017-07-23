package com.LilG.DataClasses;


import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ggonz on 9/7/2016.
 * Storage class for Discord Stuff
 */
public class DiscordData {
	public static Map<TextChannel, List<Role>> channelRoleMap = new HashMap<>();

	public static Map<String, List<String>> wordFilter = new HashMap<>();

	private DiscordData() {
	}
}
