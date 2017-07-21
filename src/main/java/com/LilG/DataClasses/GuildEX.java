package com.LilG.DataClasses;

import com.LilG.utils.LilGUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildEX{
	private static Map<Guild, GuildEX> map = new HashMap<>();

	private final Guild guild;
	private Role requiredRole = null;
	private final List<Role> colorRoleList;
	private final List<String> bannedColorRoleNames;

	private GuildEX(Guild guild){
		this.guild = guild;
		colorRoleList = new ArrayList<>();
		bannedColorRoleNames = new ArrayList<>();
	}

	public static GuildEX getGuildEX(Guild guild){
		GuildEX ret = map.get(guild);
		if(ret == null){
			ret = new GuildEX(guild);
			map.put(guild, ret);
		}
		return ret;
	}

	public Guild getGuild() {
		return guild;
	}

	public List<Role> getColorRoleList() {
		return colorRoleList;
	}

	public List<String> getBannedColorRoleNames() {
		return bannedColorRoleNames;
	}

	public Role getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(Role requiredRole) {
		this.requiredRole = requiredRole;
	}

	public void setRequiredRole(String roleStr) {
		for(Role role : guild.getRoles()){
			if(role.getId().equals(roleStr) || role.getName().equalsIgnoreCase(roleStr)){
				requiredRole = role;
			}
		}
	}

	public void setRequiredRole(long roleLong) {
		for(Role role : guild.getRoles()){
			if(role.getIdLong() == roleLong){
				requiredRole = role;
			}
		}
	}

	public boolean canUseColorRole(Member member){
		return requiredRole!=null && member.getRoles().contains(requiredRole);
	}

	public boolean canEditColorRole(Role colorRole){
		return canEditColorRole(colorRole,null);
	}

	public boolean canEditColorRole(Role colorRole, Member member){
		for(Member curMember : guild.getMembers()){
			if(curMember == member) continue;
			for(Role role : curMember.getRoles()){
				if(role.equals(colorRole)) return false;
			}
		}
		return true;
	}

	public Role getRole(String role){
		if(role == null || LilGUtil.equalsAnyIgnoreCase(role, "null", "nil", "none", "")) return null;
		if(LilGUtil.equalsAnyIgnoreCase(role, "everyone", "@everyone", guild.getPublicRole().getId())) return guild.getPublicRole();
		for(Role curRole : guild.getRoles()){
			if(isRole(curRole, role)){
				return curRole;
			}
		}
		return null;
	}

	public static boolean isRole(Role role, String roleStr){
		return role.getName().equalsIgnoreCase(roleStr) || role.getId().equals(roleStr);
	}
}
