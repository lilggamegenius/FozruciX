using System.Collections.Generic;
using net.dv8tion.jda.core.entities;

namespace FozruciCS.DataStructs{
	public struct SaveDataStore {
		public static SaveDataStore Instance;

		private Dictionary<string, Dictionary<string, List<string>>> _allowedCommands;
		private Dictionary<string, string> _checkJoinsAndQuits;
		private List<string> _mutedServerList;
		private Dictionary<string, List<string>> _markovChain;
		private List<Note> _noteList;
		private List<string> _authedUser;
		private List<int> _authedUserLevel;
		private string _avatarLink;
		private Dictionary<string, Meme> _memes;
		private Dictionary<string, string> _fcList;
		private List<string> _dndJoined;
		private Dictionary<TextChannel, List<string>> _wordFilter;

		public static List<Note> NoteList{
			get => Instance._noteList;
			set => Instance._noteList = value;
		}

		public static List<string> AuthedUser{
			get => Instance._authedUser;
			set => Instance._authedUser = value;
		}

		public static List<int> AuthedUserLevel{
			get => Instance._authedUserLevel;
			set => Instance._authedUserLevel = value;
		}

		public static string AvatarLink{
			get => Instance._avatarLink;
			set => Instance._avatarLink = value;
		}

		public static Dictionary<string, Meme> Memes{
			get => Instance._memes;
			set => Instance._memes = value;
		}

		public static Dictionary<string, string> FcList{
			get => Instance._fcList;
			set => Instance._fcList = value;
		}

		public static List<string> DndJoined{
			get => Instance._dndJoined;
			set => Instance._dndJoined = value;
		}

		public static Dictionary<string, Dictionary<string, List<string>>> AllowedCommands{
			get{
				if(Instance._allowedCommands == null){ Instance._allowedCommands = new Dictionary<string, Dictionary<string, List<string>>>(); }
				if(Instance._allowedCommands.Count >= 1) return Instance._allowedCommands;
				var temp =
					new Dictionary<string, List<string>>{
						["#retro"] = new List<string>(new[]{"GayDar", "url checker"}),
						["#origami64"] = new List<string>(new[]{"markov", "my", "url checker"})
					};
				Instance._allowedCommands["BadnikZONE"] = temp;
				temp = new Dictionary<string, List<string>>{["#deltasmash"] = new List<string>(new[]{"FC", "version"})};
				Instance._allowedCommands["twitch"] = temp;
				temp = new Dictionary<string, List<string>>{["#pmd"] = new List<string>(new[]{"url checker"})};
				Instance._allowedCommands["CaffieNET"] = temp;
				temp = new Dictionary<string, List<string>>{
					["#general"] = new List<string>(new[]{"url checker"}),
					["#development"] = new List<string>(new[]{"url checker"})
				};
				Instance._allowedCommands["Discord Bots"] = temp;
				return Instance._allowedCommands;
			}
			set=>Instance._allowedCommands = value;
		}

		public static Dictionary<string, string> CheckJoinsAndQuits{
			get{
				if(Instance._checkJoinsAndQuits == null) Instance._checkJoinsAndQuits = new Dictionary<string, string>();
				if(Instance._checkJoinsAndQuits.Count < 1){ Instance._checkJoinsAndQuits["191548246332538880"] = "214906329498648576"; }
				return Instance._checkJoinsAndQuits;
			}
			set=>Instance._checkJoinsAndQuits = value;
		}

		public static List<string> MutedServerList{
			get{
				if(Instance._mutedServerList== null) Instance._mutedServerList = new List<string>();
				if(Instance._mutedServerList.Count < 1){ Instance._mutedServerList.Add("110373943822540800"); }
				return Instance._mutedServerList;
			}
			set=>Instance._mutedServerList = value;
		}

		public static Dictionary<TextChannel, List<string>> WordFilter{
			get => Instance._wordFilter;
			set => Instance._wordFilter = value;
		}

		public static Dictionary<string, List<string>> MarkovChain{
			get{
				if(Instance._markovChain == null) Instance._markovChain = new Dictionary<string, List<string>>();
				if(Instance._markovChain.Count >= 1) return Instance._markovChain; // Create the first two entries (k:_start, k:_end)
				Instance._markovChain["_start"] = new List<string>();
				Instance._markovChain["_end"] = new List<string>();
				return Instance._markovChain;
			}
			set=>Instance._markovChain = value;
		}

		public SaveDataStore(string avatarLink = "http://puu.sh/oiLvW.gif"){
			_allowedCommands = new Dictionary<string, Dictionary<string, List<string>>>();
			_checkJoinsAndQuits = new Dictionary<string, string>();
			_mutedServerList = new List<string>();
			_markovChain = new Dictionary<string, List<string>>();
			_noteList = new List<Note>();
			_authedUser = new List<string>();
			_authedUserLevel = new List<int>();
			_avatarLink = avatarLink;
			_memes = new Dictionary<string, Meme>();
			_fcList = new Dictionary<string, string>();
			_dndJoined = new List<string>();
			_wordFilter = new Dictionary<TextChannel, List<string>>();
		}
	}
}
