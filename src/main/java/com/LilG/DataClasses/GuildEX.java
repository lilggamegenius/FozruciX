package com.LilG.DataClasses;

import com.LilG.Serialization.GuildDeserializer;
import com.LilG.Serialization.RoleDeserializer;
import com.LilG.utils.LilGUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class GuildEX {

	@JsonDeserialize(using = GuildDeserializer.class)
	private final Guild guild;
	@JsonDeserialize(contentUsing = RoleDeserializer.class)
	private final List<Role> colorRoleList;
	private final List<String> bannedColorRoleNames;
	@JsonDeserialize(using = RoleDeserializer.class)
	private Role requiredRole = null;

	@JsonCreator
	private GuildEX(
			@JsonProperty("guild") Guild guild,
			@JsonProperty("colorRoleList") List<Role> colorRoleList,
			@JsonProperty("bannedColorRoleNames") List<String> bannedColorRoleNames,
			@JsonProperty("requiredRole") Role requiredRole
	) {
		this.guild = guild;
		this.colorRoleList = colorRoleList;
		this.bannedColorRoleNames = bannedColorRoleNames;
		this.requiredRole = requiredRole;
	}

	private GuildEX(Guild guild) {
		this.guild = guild;
		colorRoleList = new ArrayList<>();
		bannedColorRoleNames = new ArrayList<>();
	}

	public static GuildEX getGuildEX(Guild guild) {
		GuildEX ret = SaveDataStore.getINSTANCE().getGuildGuildEXMap().get(guild.getId());
		if (ret == null) {
			ret = new GuildEX(guild);
			SaveDataStore.getINSTANCE().getGuildGuildEXMap().put(guild.getId(), ret);
		}
		return ret;
	}

	public static Map<String, GuildEX> getMap() {
		return SaveDataStore.getINSTANCE().getGuildGuildEXMap();
	}

	/*public static void setMap(Map<Guild, GuildEX> map) {
		GuildEX.map = map;
	}*/

	public static boolean isRole(Role role, String roleStr) {
		return role.getName().equalsIgnoreCase(roleStr) || role.getId().equals(roleStr);
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

	public boolean addBannedColorRoleName(String format) {
		for (Role colorRole : colorRoleList) {
			if (LilGUtil.wildCardMatch(colorRole.getName(), format)) {
				return false;
			}
		}
		forceAddBannedColorRoleName(format, true);
		// Since no role matches this format, forceAddBannedColorRoleName() can be safely called
		return true;
	}

	public boolean forceAddBannedColorRoleName(String format) {
		return forceAddBannedColorRoleName(format, false);
	}

	private boolean forceAddBannedColorRoleName(String format, boolean skip) {
		boolean rolesDeleted = false;
		if (!skip) {
			for (Role role : colorRoleList) {
				if (LilGUtil.wildCardMatch(role.getName(), format)) {
					role.delete().reason("Matches now banned format " + format).queue(s -> colorRoleList.remove(role));
					rolesDeleted = true;
				}
			}
		}
		bannedColorRoleNames.add(format);
		return rolesDeleted;
	}

	public boolean removeBannedColorRoleName(String format) {
		return bannedColorRoleNames.remove(format);
	}

	public Role getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(Role requiredRole) {
		this.requiredRole = requiredRole;
	}

	public boolean setRequiredRole(String roleStr) {
		Role temp = getRole(roleStr);
		if (temp == null) {
			if (roleStr == null || LilGUtil.equalsAnyIgnoreCase(roleStr, "null", "nil", "none", "0", "")) return true;
		} else {
			requiredRole = temp;
			return true;
		}
		return false;
	}

	public boolean setRequiredRole(long roleLong) {
		for (Role role : guild.getRoles()) {
			if (role.getIdLong() == roleLong) {
				requiredRole = role;
				return true;
			}
		}
		return false;
	}

	public boolean changeRoleColor(Member member, Role role, Color color) {
		if (canEditColorRole(role, member)) {
			role.getManager().setColor(color).reason(String.format("%s: changing role %s's color", member.getEffectiveName(), role.getName())).queue();
			return true;
		}
		return false;
	}

	public boolean changeRoleName(Member member, Role role, String name) {
		if (canEditColorRole(role, member)) {
			role.getManager().setName(name).reason(String.format("%s: changing role %s's name", member.getEffectiveName(), role.getName())).queue();
			return true;
		}
		return false;
	}

	public boolean changeRole(Member member, Role role, String name, Color color) {
		if (isNameAllowed(name)) {
			role.getManager().setName(name).reason(String.format("%s: changing role %s", member.getEffectiveName(), role.getName())).queue();
			role.getManager().setColor(color).reason(String.format("%s: changing role %s", member.getEffectiveName(), role.getName())).queue();
			return true;
		}
		return false;
	}

	public boolean createColorRole(Member member, String name, Color color) {
		if (isNameAllowed(name)) {
			if (getRole(name) == null) {
				GuildController controller = guild.getController();
				controller.createRole().setName(name).setColor(color).queue(s -> {
					controller.addRolesToMember(member, s).queue();
					colorRoleList.add(s);
				});
				return true;
			}
		}
		return false;
	}

	public void removeRole(Role role) {
		colorRoleList.remove(role);
		role.delete().queue();
	}

	public boolean removeRole(Role role, Member member) {
		if (canEditColorRole(role, member)) {
			removeRole(role);
			return true;
		}
		guild.getController().removeRolesFromMember(member, role);
		return false;
	}

	public boolean canUseColorRole(Member member) {
		return requiredRole != null && (requiredRole.isPublicRole() || member.getRoles().contains(requiredRole));
	}

	public boolean canEditColorRole(Role colorRole) {
		return canEditColorRole(colorRole, null);
	}

	public boolean canEditColorRole(Role colorRole, Member member) {
		for (Member curMember : guild.getMembers()) {
			if (curMember == member) continue;
			for (Role role : curMember.getRoles()) {
				if (role.equals(colorRole)) return false;
			}
		}
		return true;
	}

	public Role getRole(String role) {
		if (role == null || LilGUtil.equalsAnyIgnoreCase(role, "null", "nil", "none", "0", "")) return null;
		if (LilGUtil.equalsAnyIgnoreCase(role, "everyone", "@everyone", guild.getPublicRole().getId()))
			return guild.getPublicRole();
		for (Role curRole : guild.getRoles()) {
			if (isRole(curRole, role)) {
				return curRole;
			}
		}
		return null;
	}

	public boolean isNameAllowed(String roleName) {
		for (String bannedFormat : bannedColorRoleNames) {
			if (LilGUtil.wildCardMatch(roleName, bannedFormat)) {
				return false;
			}
		}
		return true;
	}
}
