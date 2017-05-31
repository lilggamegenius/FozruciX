using System.Collections.Generic;
using net.dv8tion.jda.core.entities;

namespace FozruciCS.DataStructs{
	public struct SaveDataStore{
		private static Dictionary<string, Dictionary<string, List<string>>> _allowedCommands =
			new Dictionary<string, Dictionary<string, List<string>>>();

		private static Dictionary<string, string> _checkJoinsAndQuits = new Dictionary<string, string>();
		private static List<string> _mutedServerList = new List<string>();
		private static Dictionary<string, List<string>> _markovChain = new Dictionary<string, List<string>>();

		public static List<Note> NoteList{get;set;} = new List<Note>();

		public static List<string> AuthedUser{get;set;} = new List<string>();

		public static List<int> AuthedUserLevel{get;set;} = new List<int>();

		public static string AvatarLink{get;set;} = "http://puu.sh/oiLvW.gif";

		public static Dictionary<string, Meme> Memes{get;set;} = new Dictionary<string, Meme>();

		public static Dictionary<string, string> FcList{get;set;} = new Dictionary<string, string>();

		public static List<string> DndJoined{get;set;} = new List<string>();

		public static Dictionary<string, Dictionary<string, List<string>>> AllowedCommands{
			get{
				if(_allowedCommands == null){ _allowedCommands = new Dictionary<string, Dictionary<string, List<string>>>(); }
				if(_allowedCommands.Count >= 1) return _allowedCommands;
				var temp =
					new Dictionary<string, List<string>>{
						["#retro"] = new List<string>(new[]{"GayDar", "url checker"}),
						["#origami64"] = new List<string>(new[]{"markov", "my", "url checker"})
					};
				_allowedCommands["BadnikZONE"] = temp;
				temp = new Dictionary<string, List<string>>{["#deltasmash"] = new List<string>(new[]{"FC", "version"})};
				_allowedCommands["twitch"] = temp;
				temp = new Dictionary<string, List<string>>{["#pmd"] = new List<string>(new[]{"url checker"})};
				_allowedCommands["CaffieNET"] = temp;
				temp = new Dictionary<string, List<string>>{
					["#general"] = new List<string>(new[]{"url checker"}),
					["#development"] = new List<string>(new[]{"url checker"})
				};
				_allowedCommands["Discord Bots"] = temp;
				return _allowedCommands;
			}
			set=>_allowedCommands = value;
		}

		public static Dictionary<string, string> CheckJoinsAndQuits{
			get{
				if(_checkJoinsAndQuits.Count < 1){ _checkJoinsAndQuits["191548246332538880"] = "214906329498648576"; }
				return _checkJoinsAndQuits;
			}
			set=>_checkJoinsAndQuits = value;
		}

		public static List<string> MutedServerList{
			get{
				if(_mutedServerList.Count < 1){ _mutedServerList.Add("110373943822540800"); }
				return _mutedServerList;
			}
			set=>_mutedServerList = value;
		}

		public static Dictionary<TextChannel, List<string>> WordFilter{get;set;} = new Dictionary<TextChannel, List<string>>();

		public static Dictionary<string, List<string>> MarkovChain{
			get{
				if(_markovChain.Count >= 1) return _markovChain; // Create the first two entries (k:_start, k:_end)
				_markovChain["_start"] = new List<string>();
				_markovChain["_end"] = new List<string>();
				return _markovChain;
			}
			set=>_markovChain = value;
		}


	}
}