using System.Collections.Generic;
using net.dv8tion.jda.core.entities;
using Newtonsoft.Json;

namespace FozruciCS.DataStructs{
	public struct SaveDataStore{
		public static SaveDataStore instance{get;set;}

		private Dictionary<string, Dictionary<string, List<string>>> _allowedCommands;

		private Dictionary<string, string> _checkJoinsAndQuits;
		private List<string> _mutedServerList;
		private Dictionary<string, List<string>> _markovChain;

		[JsonProperty("NoteList")] public List<Note> NoteList{get;set;}

		[JsonProperty("AuthedUser")] public List<string> AuthedUser{get;set;}

		[JsonProperty("AuthedUserLevel")] public List<int> AuthedUserLevel{get;set;}

		[JsonProperty("AvatarLink")] public string AvatarLink{get;set;}

		[JsonProperty("Memes")] public Dictionary<string, Meme> Memes{get;set;}

		[JsonProperty("FcList")] public Dictionary<string, string> FcList{get;set;}

		[JsonProperty("DndJoined")] public List<string> DndJoined{get;set;}

		[JsonProperty("AllowedCommands")] public Dictionary<string, Dictionary<string, List<string>>> AllowedCommands{
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

		[JsonProperty("CheckJoinsAndQuits")] public Dictionary<string, string> CheckJoinsAndQuits{
			get{
				if(_checkJoinsAndQuits.Count < 1){ _checkJoinsAndQuits["191548246332538880"] = "214906329498648576"; }
				return _checkJoinsAndQuits;
			}
			set=>_checkJoinsAndQuits = value;
		}

		[JsonProperty("MutedServerList")] public List<string> MutedServerList{
			get{
				if(_mutedServerList.Count < 1){ _mutedServerList.Add("110373943822540800"); }
				return _mutedServerList;
			}
			set=>_mutedServerList = value;
		}

		[JsonProperty("WordFilter")] public Dictionary<TextChannel, List<string>> WordFilter{get;set;}

		[JsonProperty("MarkovChain")] public Dictionary<string, List<string>> MarkovChain{
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
