using System.Collections.Generic;
using net.dv8tion.jda.core.entities;

namespace FozruciCS.DataStructs{
	public struct DiscordData{
		public static Dictionary<TextChannel, List<Role>> channelRoleMap = new Dictionary<TextChannel, List<Role>>();

		public static Dictionary<TextChannel, List<string>> wordFilter = new Dictionary<TextChannel, List<string>>();
	}
}