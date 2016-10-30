package com.LilG.Com.DataClasses;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ggonz on 9/7/2016.
 * Storage class for Admin commands
 */
public class AdminCommandData {
    public static Map<TextChannel, List<Role>> channelRoleMap = new HashMap<>();

    public static Map<TextChannel, List<String>> wordFilter = new HashMap<>();

    public static Map<Guild, TextChannel> loggingChan = new HashMap<>();

    private AdminCommandData() {
    }
}
