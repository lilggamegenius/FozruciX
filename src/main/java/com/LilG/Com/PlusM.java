package com.LilG.Com;

import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ggonz on 9/7/2016.
 * Storage class for the +m command
 */
class PlusM {
    static Map<TextChannel, List<Role>> channelRoleMap = new HashMap<>();

    private PlusM() {
    }
}
