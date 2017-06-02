using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using Thread = System.Threading.Thread;
using com.google.code.chatterbotapi;
using com.jcraft.jsch;
using com.rmtheis.yandtran.language;
using FozruciCS.Misc;
using FozruciCS.Utils;
using static FozruciCS.Utils.LilGUtil;
using ikvm.extensions;
using java.io;
using java.lang;
using java.net;
using java.nio.charset;
using java.util;
using java.util.regex;
using javax.imageio;
using net.dv8tion.jda.core;
using net.dv8tion.jda.core.entities;
using net.sourceforge.argparse4j;
using net.sourceforge.argparse4j.impl;
using net.sourceforge.argparse4j.inf;
using NLog;
using org.apache.commons.lang.time;
using org.apfloat;
using org.jsoup;
using org.pircbotx;
using org.pircbotx.hooks;
using org.pircbotx.hooks.events;
using org.pircbotx.hooks.types;
using Exception = java.lang.Exception;
using GenericMessageEvent = org.pircbotx.hooks.types.GenericMessageEvent;
using Logger = NLog.Logger;
using Object = java.lang.Object;
using Random = System.Random;
using User = org.pircbotx.User;
using FozruciCS.Math;
using com.fathzer.soft.javaluator;
using com.joestelmach.natty;
using com.rmtheis.yandtran.detect;
using com.rmtheis.yandtran.translate;
using com.wolfram.alpha;
using de.tudarmstadt.ukp.jwktl;
using de.tudarmstadt.ukp.jwktl.api;
using FozruciCS.Console;
using FozruciCS.DataStructs;
using FozruciCS.M68K;
using info.bliki.api;
using info.bliki.wiki.filter;
using info.bliki.wiki.model;
using java.lang.management;
using java.text;
using java.time;
using java.time.format;
using net.dv8tion.jda.core.events.guild;
using net.dv8tion.jda.core.events.guild.member;
using Array = System.Array;
using Configuration = org.pircbotx.Configuration;
using Date = java.util.Date;
using StringUtils = org.apache.commons.lang.StringUtils;
using static FozruciCS.Bits;

namespace FozruciCS {
	public enum Network {
		Normal,
		Twitch,
		Discord
	}

	public enum EventType {
		[Description(" <%s> ")] Message,
		[Description(" <%s> ")] PrivMessage,
		[Description(" *%s ")] Action,
		[Description(" *%s* ")] Notice,
		[Description(" %s ")] Join,
		[Description(" %s ")] Part,
		[Description(" %s ")] Quit
	}

	public enum MessageModes {
		Normal,
		Reversed,
		WordReversed,
		Scrambled,
		WordScrambled,
		Caps
	}

	[Flags]
	public enum Bits : int{
		JokeCommands,
		ArrayOffsetSet,
		CleverBotInt,
		PandoraBotInt,
		JabberBotInt,
		NickInUse,
		Color,
		RespondToPms,
		DataLoaded,
		CheckLinks
	}

	public class FozruciX : ListenerAdapter {
		public static float Version{ get; } = 2.7f;

		public static readonly string[] Dictionary = {
			"i don't know what \"%s\" is, do i look like a DICTIONARY?",
			"Go look it up yourself.",
			"Why not use your computer and look \"%s\" up.",
			"Google it.",
			"Nope.",
			"Get someone else to do it.",
			"Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?",
			"There appears to be a error between your "
			+ Colors.BOLD
			+ "seat"
			+ Colors.NORMAL
			+ " and the "
			+ Colors.BOLD
			+ "Keyboard"
			+ Colors.NORMAL
			+ " >_>",
			"Uh oh, there appears to be a User error.",
			"error: Fuck count too low, Cannot give Fuck.",
			">_>"
		};

		public static readonly string[] ListOfNoes = {
			" It’s not a priority for me at this time.", "I’d rather stick needles in my eyes.",
			"My schedule is up in the air right now. SEE IT WAFTING GENTLY DOWN THE CORRIDOR.",
			"I don’t love it, which means I’m not the right person for it.", "I would prefer another option.",
			"I would be the absolute worst person to execute, are you on crack?!",
			"Life is too short TO DO THINGS YOU don’t LOVE.", "I no longer do things that make me want to kill myself",
			"You should do this yourself, you would be awesome sauce.",
			"I would love to say yes to everything, but that would be stupid", "Fuck no.",
			"Some things have come up that need my attention.",
			"There is a person who totally kicks ass at this. I AM NOT THAT PERSON.", "Shoot me now...",
			"It would cause the slow withering death of my soul.", "I’d rather remove my own gallbladder with an oyster fork.",
			"I'd love to but I did my own thing and now I've got to undo it."
		};

		public static readonly string[] Commands = {
			"COMMANDS", " Time", " calcj", " RandomInt", " stringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot",
			" GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " prefix", " SayThis", " ToSciNo", " Trans",
			" DebugVar", " cmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction",
			" NoteJ", "Memes", " jToggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?",
			"Joke: getFinger", " Joke: GayDar"
		};

		private static readonly File WiktionaryDirectory = new File("Data/Wiktionary");
		private static readonly ChatterBotFactory BotFactory = new ChatterBotFactory();
		private static readonly ArbitraryPrecisionEvaluator Evaluator = new ArbitraryPrecisionEvaluator();
		private static readonly StaticVariableSet VariableSet = new StaticVariableSet();
		private static readonly string AppId = "RGHHEP-HQU7HL67W9";
		private static readonly List<RpsGame> RpsGames = new List<RpsGame>();
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();

		private static BitVector32 _bools = new BitVector32(0)
			; // true, false, null, null, null, false, true, true, false, false

		private static readonly string M68KPath = "M68KSimulator";
		public static volatile Dictionary<string, List<string>> MarkovChain;
		private static volatile Random _rnd = new Random();
		private static volatile ChatterBotSession _chatterBotSession;
		private static volatile ChatterBotSession _pandoraBotSession;
		private static volatile ChatterBotSession _jabberBotSession;
		private static volatile FixedSizedQueue<MessageEvent> _lastEvents = new FixedSizedQueue<MessageEvent>(30);
		private static volatile string _lastLinkTitle = "";
		private static long _lastLinkTime = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;

		private static volatile CMD _singleCmd = null;

		//------------- save data -----------------------------------
		private static volatile List<Note> _noteList = null;

		private static volatile List<string> _authedUser = null;
		private static volatile List<int> _authedUserLevel = null;
		private static volatile Dictionary<string, Dictionary<string, List<string>>> _allowedCommands = null;
		private static volatile Dictionary<string, string> _checkJoinsAndQuits = null;

		private static volatile List<string> _mutedServerList = null;

		//-----------------------------------------------------------
		private static volatile int _jokeCommandDebugVar = 30;

		private static volatile CommandLine _terminal = new CommandLine();
		private static volatile string _counter = "";
		private static volatile int _counterCount = 0;

		private static volatile MessageModes _messageMode = MessageModes.Normal;
		private static volatile int _arrayOffset = 0;

		//private static volatile JavaScript _js;

		//private static volatile Python _py;

		private static volatile string _consolePrefix = ">";
		private static volatile string _avatar;
		private static volatile Dictionary<string, Meme> _memes;
		private static volatile Dictionary<string, string> _fcList;
		private static volatile MultiBotManager _manager;

		private static volatile HashSet<string> _qList = new HashSet<string>();

		private static volatile Dictionary<User, long> _commandCooldown = new Dictionary<User, long>();

		private static volatile StopWatch _qTimer = new StopWatch();
		private static volatile DiscordAdapter _discord;
		private static volatile bool _updateAvatar;
		private static volatile int _saveTime = 20;
		private static volatile int _defaultCoolDownTime = 4;
		private static volatile FixedSizedQueue<System.Exception> _lastExceptions = new FixedSizedQueue<System.Exception>(30);
		private static volatile IM68KSim _m68K;

		private static readonly Thread SaveThread = new Thread(() => {
			var thisThread = Thread.CurrentThread;
			thisThread.Name = "save Thread";
			while(true){
				try{
					pause(randInt(_saveTime, _saveTime + 10), false);
					SaveData();
				}
				catch(InterruptedException){ Logger.Info("Save thread interuppted"); }
				catch(Exception){ }
			}
		});

		static FozruciX(){
			SaveThread.Start();
			try{
				//m68k = (M68kSim) Native.loadLibrary(M68kPath, M68kSim.class);
				System.Console.WriteLine(_m68K);
				_m68K.start();
				AppDomain.CurrentDomain.ProcessExit += _m68K.exit;
				AppDomain.CurrentDomain.ProcessExit += SaveData;
			}
			catch(UnsatisfiedLinkError e){
				Logger.Error("JNA Error: {0}", e);
				//System.exit(1);
			}
		}

		public readonly Network Network;

		private string _prefix = "!";
		private DebugWindow _debug;

		private User _currentUser;
		private UserHostmask _lastJsUser;
		private org.pircbotx.Channel _lastJsChannel;
		private PircBotX _bot;

		public FozruciX(MultiBotManager manager, Network network = Network.Normal){
			/*if (network == Network.twitch) {
			    currentNick = "lilggamegenuis";
			    currentUsername = currentNick;
			    currentHost = currentUsername + ".tmi.network.tv";
			} else if (network == Network.discord) {
			    currentNick = "Lil-G";
			    currentUsername = currentNick;
			    currentHost = "131494148350935040";
			}*/
			this.Network = network;
			// true, false, null, null, null, false, true, true
			_bools[JokeCommands] = true;
			_bools[Color] = true;
			_bools[RespondToPms] = true;
			_bools[CheckLinks] = true;

			FozruciX._manager = manager;

			LoadData(true);
			//Logger.setLevel(Level.ALL);
			Thread.CurrentThread.Name = "FozruciX: " + network.toString();
		}

		public static string GetScramble(string msgToSend){ return GetScramble(msgToSend, true); }


		public static string GetScramble(string msgToSend, bool replaceNewLines){
			if(replaceNewLines && (msgToSend.contains("\r") || msgToSend.contains("\n"))){
				msgToSend = msgToSend.replace("\r", "").replace("\n", "");
			}
			switch(_messageMode){
				case MessageModes.Reversed:
					msgToSend = new StringBuilder(msgToSend).reverse().toString();
					break;
				case MessageModes.WordReversed:{
					var message = new List<string>(msgToSend.split("\\s+").ToList());
					msgToSend = "";
					for(var i = message.Count - 1; i >= 0; i--){ msgToSend += message[i] + " "; }
				}
					break;
				case MessageModes.Scrambled:
					var msgChars = msgToSend.toCharArray();
					var chars = new List<char>();
					foreach(var msgChar in msgChars){ chars.Add(msgChar); }
					msgToSend = "";
					while(chars.Count != 0){
						var num = LilGUtil.randInt(0, chars.Count - 1);
						msgToSend += chars[num] + "";
						chars.RemoveAt(num);
					}
					break;
				case MessageModes.WordScrambled:{
					var message = new List<string>(msgToSend.split("\\s+").ToList());
					msgToSend = "";
					while(message.Count != 0){
						var num = LilGUtil.randInt(0, message.Count - 1);
						msgToSend += message[num] + " ";
						message.RemoveAt(num);
					}
				}
					break;
				case MessageModes.Caps:
					msgToSend = msgToSend.toUpperCase();
					break;
			}
			return msgToSend;
		}

		private static int GetUserLevel(List<UserLevel> levels){
			var ret = 0;
			ret = levels.Count == 0 ? 0 : levels.Select(level => level.ordinal()).Concat(new[]{ret}).Max();
			return ret;
		}

		private static void SendFile(MessageEvent @event, File file, string message = null, bool discordUpload = true){
			if(@event is DiscordMessageEvent && discordUpload){
				try{
					((DiscordMessageEvent) @event).getDiscordEvent()
						.getTextChannel()
						.sendFile(file, message != null ? new MessageBuilder().append(message).build() : null);
				}
				catch(IOException e){ SendError(@event, e); }
			}
			else{ UploadFile(@event, file, null, message); }
		}


		private static void UploadFile(GenericMessageEvent @event, File file, string folder, string suffix){
			Session session = null;
			com.jcraft.jsch.Channel channel = null;
			try{
				var ssh = new JSch();
				ssh.setKnownHosts(LilGUtil.IsLinux
					? "~/.ssh/known_hosts"
					: "C:/Users/ggonz/AppData/Local/lxss/home/lil-g/.ssh/known_hosts");
				session = ssh.getSession("lil-g",
					FozConfig.LilGNet,
					22);
				session.setPassword(CryptoUtil.decrypt(FozConfig.setPassword(Password.Normal)));
				UserInfo ui = new CommandLine.MyUserInfo();
				session.setUserInfo(ui);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				var sftp = (ChannelSftp) channel;
				folder = folder != null ? folder + "/" : "";
				sftp.put(file.getAbsolutePath(), "/var/www/html/upload/" + folder);
				SendMessage(@event,
					new URL("http://"
					        + FozConfig.Location
					        + "/upload/"
					        + folder
					        + file.getName()
					        + (suffix == null ? "" : " " + suffix)).toExternalForm());
			}
			catch(Exception e){ e.printStackTrace(); }
			finally{
				channel?.disconnect();
				session?.disconnect();
			}
		}

		private static void SendMessage(
			GenericMessageEvent @event,
			string msgToSend,
			bool addNick = true,
			bool splitMessage = false){
			var textSizeLimit = 460; // irc limit
			if(@event is DiscordMessageEvent){
				textSizeLimit = 700; // discord
			}
			if(msgToSend == "\u0002\u0002"){ return; }
			//msgToSend = msgToSend.replace("(Player)", @event.getUser().getNick()).replace("(items)", COMMANDS[LilGUtil.randInt(0, COMMANDS.length - 1)]).replace("(Pokémon)", @event.getBot().getNick()).replace("{random}", rnd.nextInt() + "");
			msgToSend = GetScramble(msgToSend);

			if(!splitMessage && msgToSend.length() > textSizeLimit){
				msgToSend = msgToSend.substring(0, textSizeLimit) + "-(snip)-";
			}

			if(((MessageEvent) @event).getChannel() != null){
				if(addNick){ @event.respond(msgToSend); }
				else{ @event.respondWith(msgToSend); }
			}
			else{ @event.getUser().send().message(msgToSend); }
			Log((MessageEvent) @event, msgToSend, true);
		}

		private static bool CheckOp(org.pircbotx.Channel chn){
			var bot = chn.getBot().getUserBot();
			return chn.isHalfOp(bot) || chn.isOp(bot) || chn.isSuperOp(bot) || chn.isOwner(bot);
		}

		/**
		 * tells the user they don't have permission to use the command
		 *
		 * @param user User trying to use command
		 */
		private static void PermError(User user){
			var num = LilGUtil.randInt(0, ListOfNoes.Length - 1);
			var comeback = ListOfNoes[num];
			user.send().notice(comeback);
		}

		/**
		 * same as permError() except to be used in channels
		 *
		 * @param @event Channel that the user used the command in
		 */
		private static void PermErrorchn(MessageEvent @event){
			var num = LilGUtil.randInt(0, ListOfNoes.Length - 1);
			var comeback = ListOfNoes[num];
			SendMessage(@event, comeback);
		}

		private static string BotTalk(string bot, string message){
			/*if (bot.equalsIgnoreCase("clever")) {
			    if (chatterBotSession == null) {
			        chatterBotSession = BOT_FACTORY.create(ChatterBotType.CLEVERBOT).createSession();
			    }
			    return chatterBotSession.think(message);
			} else */
			if(bot.equalsIgnoreCase("pandora") || bot.equalsIgnoreCase("clever")){
				if(_pandoraBotSession == null){
					_pandoraBotSession = BotFactory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
				}
				return _pandoraBotSession.think(message);
			}
			if(bot.equalsIgnoreCase("jabber") || bot.equalsIgnoreCase("jabberwacky")){
				if(_jabberBotSession == null){
					_jabberBotSession = BotFactory.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
				}
				return _jabberBotSession.think(message);
			}
			return "Error, not a valid bot";
		}

		private static string ArgJoiner(string[] args, int argToStartFrom){
			return ArgJoiner(args, argToStartFrom, _arrayOffset);
		}

		private static string ArgJoiner(string[] args, int argToStartFrom, int arrayOffset){
			if((args.Length - 1) == (argToStartFrom + arrayOffset)){ return GetArg(args, argToStartFrom, arrayOffset); }
			var strToReturn = "";
			for(var length = args.Length; length > (argToStartFrom + arrayOffset); argToStartFrom++){
				strToReturn += GetArg(args, argToStartFrom, arrayOffset) + " ";
			}
			Logger.Debug("Argument joined to: " + strToReturn);
			return strToReturn.isEmpty() ? strToReturn : strToReturn.substring(0, strToReturn.length() - 1);
		}

		private static void AddCooldown(User user){ AddCooldown(user, _defaultCoolDownTime); }

		private static void AddCooldown(User user, int cooldownTime){ AddCooldown(user, (long) cooldownTime * 1000); }

		private static void AddCooldown(User user, long cooldownTime){
			_commandCooldown[user] = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) + cooldownTime;
		}


		private static string FullNameTostring(Language language){ return language.toString(); }

		public static string GetArg(string[] args, int index){ return GetArg(args, index, _arrayOffset); }

		public static string GetArg(string[] args, int index, int arrayOffset){
			try{ return args[index + arrayOffset]; }
			catch(Exception){ return null; }
		}

		private static void Log(Event @event, bool botTalking){ Log(@event, null, botTalking); }

		private static void Log(Event @event, string messageOverride = null, bool botTalking = false){
			string network = null;
			var channel = new List<string>();
			string user = null;
			string message = null;
			var eventType = EventType.Message;

			var messageEvent = @event as MessageEvent;
			if(messageEvent != null){
				channel.Add(messageEvent.getChannel().getName());
				user = messageEvent.getUser().getHostmask();
				message = messageEvent.getMessage();
				eventType = EventType.Message;
			}
			var privateMessageEvent = @event as PrivateMessageEvent;
			if(privateMessageEvent != null){
				user = privateMessageEvent.getUser().getHostmask();
				message = privateMessageEvent.getMessage();
				eventType = EventType.PrivMessage;
			}
			var actionEvent = @event as ActionEvent;
			if(actionEvent != null){
				channel.Add(actionEvent.getChannel().getName());
				user = actionEvent.getUser().getHostmask();
				message = actionEvent.getMessage();
				eventType = EventType.Action;
			}
			var noticeEvent = @event as NoticeEvent;
			if(noticeEvent != null){
				user = noticeEvent.getUser().getHostmask();
				message = noticeEvent.getMessage();
				eventType = EventType.Notice;
			}
			var joinEvent = @event as JoinEvent;
			if(joinEvent != null){
				if(joinEvent.getChannel() != null){ channel.Add(joinEvent.getChannel().getName()); }
				user = joinEvent.getUser().getHostmask();
				message = "Joined " + channel;
				eventType = EventType.Join;
			}
			var partEvent = @event as PartEvent;
			if(partEvent != null){
				channel.Add(partEvent.getChannel().getName());
				user = partEvent.getUser().getHostmask();
				message = "parted " + channel;
				if(partEvent.getReason() != null){ message += " (" + partEvent.getReason() + ")"; }
				eventType = EventType.Part;
			}
			var quitEvent = @event as QuitEvent;
			if(quitEvent != null){
				var discordQuitEvent = @event as DiscordQuitEvent;
				if(discordQuitEvent != null){
					channel.AddRange(discordQuitEvent.getLeaveEvent()
						.getGuild()
						.getTextChannels()
						.toList<TextChannel>()
						.Select(chanName => "#" + ((MessageChannel) chanName).getName()));
					user = discordQuitEvent.getLeaveEvent().getMember().getUser().getName();
					message = "Quit " + discordQuitEvent.getLeaveEvent().getGuild().getName();
				}
				else{
					channel.AddRange(quitEvent.getUser().getChannels().toList<org.pircbotx.Channel>().Select(chan => chan.getName()));
					user = quitEvent.getUser().getHostmask();
					message = "Quit " + network;
					if(quitEvent.getReason() != null){ message += " (" + quitEvent.getReason() + ")"; }
				}
				eventType = EventType.Quit;
			}
			var kickEvent = @event as KickEvent;
			if(kickEvent != null){
				channel.Add(kickEvent.getChannel().getName());
				user = kickEvent.getRecipient().getHostmask();
				message = "Kicked " + network + " by " + kickEvent.getUser().getHostmask() + "(" + kickEvent.getReason() + ")";
				eventType = EventType.Part;
			}
			var outputEvent = @event as OutputEvent;
			if(outputEvent != null){
				botTalking = true;
				var lines = outputEvent.getLineParsed().toList<string>();
				switch(lines[0]){
					case "PRIVMSG":
						eventType = EventType.Message;
						break;
					case "ACTION":
						eventType = EventType.Action;
						break;
					case "NOTICE":
						eventType = EventType.Notice;
						break;
					case "JOIN":
						eventType = EventType.Join;
						break;
					case "PONG": return;
				}
				if(lines[1].contains("#")){ channel.Add(lines[1]); }
				else{
					if(outputEvent.getBot() == DiscordAdapter.pircBotX){
						foreach(var guild in DiscordAdapter.getJda().getGuilds().toList<Guild>()){
							foreach(var discordUser in guild.getMembers().toList<Member>()){
								var nick = discordUser.getEffectiveName();
								if(nick == lines[1]){
									channel.Add(nick + "!" + discordUser.getUser().getName() + "@" + discordUser.getUser().getId());
								}
							}
						}
					}
					else{
						foreach(var aChannel in outputEvent.getBot().getUserBot().getChannels().toList<org.pircbotx.Channel>()){
							foreach(var aUser in aChannel.getUsers().toList<User>()){
								if(aUser.getNick() == lines[1]){ channel.Add(aUser.getHostmask()); }
							}
						}
					}
				}
				message = lines[2];
			}
			if(botTalking){ user = @event.getBot().getUserBot().getHostmask(); }
			if(messageOverride != null){ message = messageOverride; }
			network = GetSeverName(@event);


			if(channel.Count == 0){ channel.Add(user); }
			foreach(var aChannel in channel){
				try{
					// APPEND MODE SET HERE
					var today = Calendar.getInstance();
					var parent = "logs/" + network + "/" + EscapePath(aChannel) + "/" + today.get(Calendar.YEAR) + "/";
					var parentDir = new File(parent);
					if(!parentDir.mkdirs() && !parentDir.exists()){ Logger.Error("Couldn't make dirs"); }
					var path = java.lang.String.format("%02d", today.get(Calendar.MONTH) + 1)
					           + "."
					           + java.lang.String.format("%02d", today.get(Calendar.DATE))
					           + ".txt";
					var file = new File(parent, path);
					var minute = java.lang.String.format("%02d", today.get(Calendar.MINUTE));
					if(minute.length() < 2){ minute = "0" + minute; }
					PrintWriter pw;
					var logFile = java.lang.String.format("%02d", today.get(Calendar.HOUR))
					              + ":"
					              + minute
					              + ":"
					              + java.lang.String.format("%02d", today.get(Calendar.SECOND))
					              + java.lang.String.format("%1$12s", java.lang.String.format(eventType.toString(), user) + message);
					if(file.exists() && !file.isDirectory()){
						pw = new PrintWriter(new FileOutputStream(file, true));
						pw.append(logFile).append(java.lang.System.lineSeparator());
						pw.close();
					}
					else{
						pw = new PrintWriter(parent + path);
						pw.println(logFile);
						pw.close();
					}
				}
				catch(IOException ioe){ ioe.printStackTrace(); }
			}
		}

		private static string GetSeverName(Event @event, bool trimAddress = false){
			var network = @event.getBot().getServerInfo().getNetwork();
			if(network == null){
				network = @event.getBot().getServerHostname();
				if(trimAddress){ network = network.substring(network.indexOf('.') + 1, network.lastIndexOf('.')); }
			}
			var messageEvent = @event as DiscordMessageEvent;
			if(messageEvent != null){ network = messageEvent.getDiscordEvent().getGuild().getName(); }
			return network;
		}

		private static string EscapePath(string path){
			if(path == null) return null;
			var fileSep = '/'; // ... or do this portably.
			var escape = '%'; // ... or some other legal char.
			var len = path.length();
			var sb = new StringBuilder(len);
			for(var i = 0; i < len; i++){
				var ch = path.charAt(i);
				if(ch < ' '
				   || ch >= 0x7F
				   || ch == fileSep
				   || ch == '|' // add other illegal chars
				   || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
				   || ch == escape){
					sb.append(escape);
					if(ch < 0x10){ sb.append('0'); }
					sb.append(Convert.ToByte(ch).ToString("X2"));
				}
				else{ sb.append(ch); }
			}
			return sb.toString();
		}

		private static void SendError(MessageEvent @event, Throwable t){
			SendError(@event, new System.Exception(t.GetType().Name, t));
		}

		public static void SendError(MessageEvent @event, System.Exception e){
			Logger.Error(e, "Error");
			var color = "";
			var discordFormatting = @event is DiscordMessageEvent ? "`" : "";
			var cause = "";
			string from;
			if(_bools[Color] && discordFormatting.isEmpty()){ color = Colors.RED; }
			if(e.getCause() != null){ cause = "Error: " + discordFormatting + e.getCause() + discordFormatting; }
			if(cause.isEmpty()){ from = "Error: " + discordFormatting + e + discordFormatting; }
			else{ from = ". From " + discordFormatting + e + discordFormatting; }
			if(cause.contains("jdk.nashorn.internal.runtime.ParserException") || from.contains("javax.script.ScriptException")){
				if(cause.contains("TypeError: Cannot read property")){
					SendMessage(@event, color + "There was a type error, Cannot read property", false);
				}
				else{
					if(cause.contains("\r") || cause.contains("\n")){
						SendMessage(@event, color + cause.substring(0, cause.indexOf("\r")), false);
					}
					else{ SendMessage(@event, color + cause, false); }
				}
			}
			else if(e is ArrayIndexOutOfBoundsException){
				SendMessage(@event, color + "Not enough arguments, try doing the command \"COMMANDS <command>\" for help", false);
			}
			else{ SendMessage(@event, color + cause + from, false); }
			e.printStackTrace();
			_lastExceptions.Enqueue(e);
		}

		private static bool CheckChatFunction(string args, string function){
			return LilGUtil.wildCardMatch(args, "[$" + function + "(*)]");
		}

		private static string[] GetChatArgs(string function){
			var args = function.substring(function.indexOf('(') + 1, function.indexOf(')'));
			return args.split(",");
		}

		private static void SaveData(object obj = null, EventArgs eventArgs = null){
			/*if (!BOOLS[DATA_LOADED]) {
			    Logger.Debug("Data save canceled because data hasn't been loaded yet");
			    return;
			}*/
			try{ FozConfig.saveData(); }
			catch(ConcurrentModificationException e){ Logger.Debug("Data not saved", e); }
			catch(Exception e){ Logger.Error("Couldn't save data", e); }
		}

		private void SendCommandHelp(GenericEvent @event, ArgumentParser parser){
			try{
				var stringWriter = new StringWriter();
				var writer = new PrintWriter(stringWriter);
				parser.printHelp(writer);
				var content = stringWriter.toString();
				Logger.Debug("Command help: " + content);
				if(@event is DiscordMessageEvent || @event is DiscordPrivateMessageEvent){
					SendNotice(@event, "```" + content + "```", false);
				}
				else{ SendNotice(@event, content, false); }
			}
			catch(Exception ex){ Logger.Error("Error sending command help", ex); }
		}

		private void RemoveFromCooldown(){
			var currentTime = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
			foreach(var user in _commandCooldown){
				if(currentTime >= user.Value){ // Check if we've waited long enough
					_commandCooldown.Remove(user.Key);
				}
				_commandCooldown.Remove(_currentUser);
			}
		}

		private bool CheckCooldown(GenericUserEvent @event){
			if(@event.getUser() == null || !_commandCooldown.ContainsKey(@event.getUser())) return false;
			var timeToWait = _commandCooldown[@event.getUser()] - (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
			if(timeToWait < 0){ //wtf? this shouldn't happen
				RemoveFromCooldown();
				return false;
			}
			SendNotice(@event,
				@event.getUser().getNick(),
				"Sorry, you have to wait " + timeToWait + " Milliseconds for the cool down");
			return true;
		}

		private void SendNotice(GenericEvent @event, string msgToSend){
			SendNotice(@event, ((GenericMessageEvent) @event).getUser().getNick(), msgToSend);
		}

		private void SendNotice(GenericEvent @event, string msgToSend, bool replaceNewLines){
			SendNotice(@event, ((GenericMessageEvent) @event).getUser().getNick(), msgToSend, replaceNewLines);
		}

		private void SendNotice(GenericEvent @event, string userToSendTo, string msgToSend, bool replaceNewLines = true){
			msgToSend = GetScramble(msgToSend, replaceNewLines);
			if(Network == Network.Discord){ SendPrivateMessage(@event, userToSendTo, msgToSend, replaceNewLines); }
			else{
				foreach(var messagePart in msgToSend.split("\n")){
					if(!messagePart.isEmpty()){ @event.getBot().send().notice(userToSendTo, messagePart); }
				}
			}
		}

		private void SendNotice(string userToSendTo, string msgToSend){
			if(Network == Network.Discord || Network == Network.Twitch){
				throw new RuntimeException("Not enough info to send to " + Network);
			}
			msgToSend = GetScramble(msgToSend);
			_bot.send().notice(userToSendTo, msgToSend);
		}

		private void SendPrivateMessage(string userToSendTo, string msgToSend){
			if(Network == Network.Discord || Network == Network.Twitch){
				throw new RuntimeException("Not enough info to send to " + Network);
			}
			msgToSend = GetScramble(msgToSend);
			_bot.send().message(userToSendTo, msgToSend);
		}

		private void SendPrivateMessage(GenericEvent @event, string msgToSend){
			SendPrivateMessage(@event, ((PrivateMessageEvent) @event).getUser().getNick(), msgToSend);
		}

		private void SendPrivateMessage(
			GenericEvent @event,
			string userToSendTo,
			string msgToSend,
			bool removeNewLines = true){
			msgToSend = GetScramble(msgToSend, removeNewLines);
			if(@event is DiscordPrivateMessageEvent || @event is DiscordMessageEvent){
				var users = DiscordAdapter.getJda().getUsersByName(userToSendTo, true).toList<net.dv8tion.jda.core.entities.User>();
				foreach(var name in users){
					if(!name.getName().equalsIgnoreCase(userToSendTo)) continue;
					if(!name.hasPrivateChannel()){
						var str = msgToSend;
						name.openPrivateChannel().complete();
						name.getPrivateChannel().sendMessage(str).queue();
					}
					name.getPrivateChannel().sendMessage(msgToSend).queue();
					return;
				}
				Logger.Warn("Couldn't find user with the name of " + userToSendTo);
			}
			else{ @event.getBot().send().message(userToSendTo, msgToSend); }
		}

		private void MakeDebug(ConnectEvent @event){
			Logger.Debug("Creating Debug window");
			_debug = new DebugWindow(@event, Network, this);
			Logger.Debug("Debug window created");
			_debug.CurrentNick = _currentUser.getHostmask();
		}

		private void MakeDebug(){
			if((_debug == null) || (_debug.ConnectEvent == null)){ return; }
			MakeDebug(_debug.ConnectEvent);
		}

		private void MakeDiscord(){
			if(_discord == null && Network != Network.Discord){ _discord = DiscordAdapter.makeDiscord(_bot); }
		}

		public override void onDisconnect(DisconnectEvent dc){
			if(_debug != null){ _debug.Dispose(); }
		}

		public override void onConnect(ConnectEvent @event){
			_bot = @event.getBot();
			_bot.sendIRC().mode(_bot.getNick(), "+BI");
			if(@event is DiscordConnectEvent){
				_currentUser = new DiscordUser(new DiscordUserHostmask(_bot, @event.getBot().getUserBot().getHostmask()),
					DiscordAdapter.getJda().getSelfUser(),
					null);
			}
			else{ _currentUser = @event.getBot().getUserBot(); }

			Thread.CurrentThread.Name = "FozruciX: " + GetSeverName(@event);
			LoadData(true);
			MakeDebug(@event);
			MakeDiscord();
		}

		private static void SendPage(MessageEvent @event, string[] arg, List<string> messagesToSend){
			try{
				Logger.Debug("Generating page...");
				var name = UUID.randomUUID();
				var dir = new File("Data/site/temp/");
				var f = new File(dir, name + ".htm");
				Logger.Debug("Result of making dirs: " + dir.mkdirs() + ". Result of create new file: " + f.createNewFile());
				var bw = new BufferedWriter(new FileWriter(f));
				bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
				bw.write("<html>");
				bw.write("<head>");
				bw.write("<link href=\"CommandStyles.css\" rel=\"stylesheet\" type=\"text/css\">");
				bw.write("<title>" + @event.getUser().getNick() + ": " + @event.getBot().getNick() + "'s Command output</title>");
				bw.write("</head>");
				bw.write("<body>");
				bw.write("<h1>" + @event.getUser().getNick() + ": " + ArgJoiner(arg, 0) + "</h1>");
				bw.write("<textarea cols=\"75\" rows=\"30\">");

				foreach(var aMessagesToSend in messagesToSend){
					bw.write(aMessagesToSend);
					bw.newLine();
				}

				bw.write("</textarea>");
				bw.write("</body>");
				bw.write("</html>");

				bw.close();
				UploadFile(@event, f, "output", null);
			}
			catch(Exception e){ SendError(@event, e); }
		}

		// ReSharper disable once ParameterHidesMember
		private static void SetArrayOffset(string prefix){
			if(_bools[ArrayOffsetSet]) return;
			if((prefix.length() > 1) && !prefix.endsWith(".")){ _arrayOffset = StringUtils.countMatches(prefix, " "); }
			else{ _arrayOffset = 0; }
			Logger.Debug("Setting arrayOffset to " + _arrayOffset + " based on string \"" + prefix + "\"");
			_bools[ArrayOffsetSet] = true;
		}

		private void SetArrayOffset(){ SetArrayOffset(_prefix); }


		private static string[] FormatstringArgs(string[] arg){ return TrimFrontOfArray(arg, 1 + _arrayOffset); }

		private static string[] TrimFrontOfArray(string[] arg, int amount){
			var ret = new string[arg.Length - amount];
			try{ Array.Copy(arg, amount, ret, 0, ret.Length); }
			catch(Exception e){ SendError(_lastEvents, e); }

			return ret;
		}


		public override void onMessage(MessageEvent @event){ OnMessage(@event, true); }

		public void OnMessage(MessageEvent @event, bool log){
			if(log){ FozruciX.Log(@event); }
			RemoveFromCooldown();
			CheckNote(@event, @event.getUser().getNick(), @event.getChannel().getName());
			if(_debug == null){
				if(_bot == null){ _bot = @event.getBot(); }
				MakeDebug();
			}
			_lastEvents.Enqueue(@event);
			if(!_bools[DataLoaded]){
				_bools[DataLoaded] = true;
				LoadData();
			}
			if((Network == Network.Normal) && _bools[NickInUse]){
				if(!_bot.getNick().equalsIgnoreCase(_bot.getConfiguration().getName())){
					SendNotice(@event, _currentUser.getNick(), "Ghost detected, recovering in 10 seconds");
					new Thread(() => {
						Thread.CurrentThread.Name = "ghost-thread";
						try{ LilGUtil.pause(10); }
						catch(Exception e){ e.printStackTrace(); }
						_bot.sendRaw()
							.rawLineNow("ns recover " + _bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.Password));
						_bot.sendRaw()
							.rawLineNow("ns ghost " + _bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.Password));
						_bot.sendIRC().changeNick(_bot.getConfiguration().getName());
					}).Start();
				}
			}
			_bools[NickInUse] = false;
			try{
				if(!(@event.getMessage().startsWith(_prefix) && @event.getMessage().startsWith("."))){
					if(LilGUtil.endsWithAny(@event.getMessage(), ".", "?", "!")){ AddWords(@event.getMessage()); }
					else{ AddWords(@event.getMessage() + "."); }
				}
			}
			catch(Exception e){ e.printStackTrace(); }
			_debug.CurrentNick = (_currentUser.getHostmask());
			_debug.Message = (@event.getUser().getNick() + ": " + @event.getMessage());
			var server = Network == Network.Discord
				? ((DiscordMessageEvent) @event).getDiscordEvent().getGuild().getId()
				: @event.getBot().getServerHostname();
			if(_mutedServerList.Contains(server) && !CheckPerm(@event.getUser(), 9001)){
				Logger.Trace("Ignoring message from server " + server);
				return;
			}
			if(@event.getMessage() != null){ DoCommand(@event); }

			// url checker - Checks if string contains a url and parses
			try{
				var channel = @event.getChannel().getName();
				var arg = LilGUtil.splitMessage(@event.getMessage());
				var checklink = !CommandChecker(@event, arg, "checkLink", false);
				var isBot = this.IsBot(@event);
				var isLinkShorterner = !@event.getMessage().contains("taglink: https://is.gd/");
				if(!checklink || !_bools[CheckLinks] || !isBot || !isLinkShorterner) return;
				var channelContains = false;
				var containsServer = false;
				var containsChannel = false;
				try{
					containsServer = _allowedCommands[GetSeverName(@event, true)] != null;
					containsChannel = _allowedCommands[GetSeverName(@event, true)][channel] != null;
					channelContains = _allowedCommands[GetSeverName(@event, true)][channel].Contains("url checker");
				}
				catch(NullReferenceException){ }
				Logger.Trace(GetSeverName(@event, true)
				             + ": containsServer: "
				             + containsServer
				             + " containsChannel: "
				             + containsChannel
				             + " channelContains: "
				             + channelContains
				             + " "
				             + _allowedCommands);
				if(channelContains) return;
				// NOTES:   1) \w includes 0-9, a-z, A-Z, _
				//             2) The leading '-' is the '-' character. It must go first in character class expression
				var validChars = "-\\w+&@#/%=~()|";
				var validNonTerminal = "?!:,.;";
				// Notes on the expression:
				//  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
				//  2) s? ==> the s is optional so either [http, https] accepted as scheme
				//  3) All valid chars accepted and then one or more
				//  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
				var uriFinderPattern = Pattern.compile("\\(*https?://[" + validChars + validNonTerminal + "]*[" + validChars + "]",
					Pattern.CASE_INSENSITIVE);

				/*
				         * <p>
				         * Finds all "URL"s in the given _rawText, wraps them in
				         * HTML link tags and returns the result (with the rest of the text
				         * html encoded).
				         * </p>
				         * <p>
				         * We employ the procedure described at:
				         * http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html
				         * which is a <b>must-read</b>.
				         * </p>
				         * Basically, we allow any number of left parenthesis (which will get stripped away)
				         * followed by http:// or https://.  Then any number of permitted URL characters
				         * (based on http://www.ietf.org/rfc/rfc1738.txt) followed by a single character
				         * of that set (basically, those minus typical punctuation).  We remove all sets of
				         * matching left & right parentheses which surround the URL.
				         *</p>
				         * <p>
				         * This method *must* be called from a tag/component which will NOT
				         * end up escaping the output.  For example:
				         * <PRE>
				         * <h:outputText ... escape="false" value="#{core:hyperlinkText(textThatMayHaveURLs, '_blank')}"/>
				         * </pre>
				         * </p>
				         * <p>
				         * Reason: we are adding <code>&lt;a href="..."&gt;</code> tags to the output *and*
				         * encoding the rest of the string.  So, encoding the output will result in
				         * double-encoding data which was already encoded - and encoding the <code>a href</code>
				         * (which will render it useless).
				         * </p>
				         * <p>
				         *
				         * @param   _rawText  - if <code>null</code>, returns <code>""</code> (empty string).
				         * @param   _target   - if not <code>null</code> or <code>""</code>, adds a target attributed to the generated link, using _target as the attribute value.
				         */
				var rawText = @event.getMessage();
				var matcher = uriFinderPattern.matcher(rawText);

				if(!matcher.find()) return;
				// Counted 15 characters aside from the target + 2 of the URL (max if the whole string is URL)
				// Rough guess, but should keep us from expanding the Builder too many times.

				int currentStart;
				int currentEnd;

				string currentUrl;

				do{
					currentStart = matcher.start();
					currentEnd = matcher.end();
					currentUrl = matcher.@group();

					// Adjust for URLs wrapped in ()'s ... move start/end markers
					//      and substring the _rawText for new URL value.
					while(currentUrl.startsWith("(") && currentUrl.endsWith(")")){
						currentStart = currentStart + 1;
						currentEnd = currentEnd - 1;

						currentUrl = rawText.substring(currentStart, currentEnd);
					}

					while(currentUrl.startsWith("(")){
						currentStart = currentStart + 1;

						currentUrl = rawText.substring(currentStart, currentEnd);
					}
				}
				while(matcher.find());
				Logger.Debug("Found URL - " + currentUrl);
				try{
					var title = Jsoup.connect(currentUrl)
						.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2783.4 Safari/537.36")
						.timeout(5000)
						.get()
						.title();
					if((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) > _lastLinkTime){ _lastLinkTitle = ""; }
					if(title.isEmpty()){ SendMessage(@event, "Title was empty", false); }
					else if(title != _lastLinkTitle){
						SendMessage(@event, "Title: " + title, false);
						_lastLinkTitle = title;
						_lastLinkTime = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) + 30000;
					}
				}
				catch(UnsupportedMimeTypeException e){
					try{
						var fileUrlConn = new URL(e.getUrl()).openConnection();
						if(e.getMimeType().split("/")[0] == "image"){
							var stream = fileUrlConn.getInputStream();
							var obj = ImageIO.createImageInputStream(stream);
							var reader = ImageIO.getImageReaders(obj).next() as ImageReader;
							reader.setInput(obj);
							long fileSize = fileUrlConn.getContentLength();
							SendMessage(@event,
								"type: "
								+ e.getMimeType()
								+ " size: [Width = "
								+ reader.getWidth(0)
								+ ", Height = "
								+ reader.getHeight(0)
								+ "] File Size: "
								+ (fileSize < 1 ? "Unknown" : LilGUtil.formatFileSize(fileSize)),
								false);
							stream.close();
						}
						else{
							SendMessage(@event,
								"type: " + e.getMimeType() + " File Size: " + LilGUtil.formatFileSize(fileUrlConn.getContentLength()),
								false);
						}
					}
					catch(Exception ex){ ex.printStackTrace(); }
				}
				catch(MalformedURLException e){ SendMessage(@event, "Unsupported URL", false); }
				catch(Exception e){ e.printStackTrace(); }
			}
			catch(Exception e){ e.printStackTrace(); }
		}

		private void DoCommand(MessageEvent @event){
			string channel = null;
			if(@event.getChannel() != null){ channel = @event.getChannel().getName(); }
			var message = @event.getMessage();
			message = DoChatFunctions(message);
			var arg = LilGUtil.splitMessage(message);

			if(!LilGUtil.containsAny(message, _prefix, _consolePrefix, _bot.getNick(), "s/")) return;
			SetArrayOffset();
			_bools[ArrayOffsetSet] = false;
			if(CheckCooldown(@event) || !CheckPerm(@event.getUser(), 0) || IsBot(@event)){ return; }

			// !getChannelName - Gets channel name, for debugging
			if(CommandChecker(@event, arg, "GetChannelName")){
				SendMessage(@event, channel ?? "This isn't a channel");
				AddCooldown(@event.getUser());
			}

			// !checkLinks
			else if(CommandChecker(@event, arg, "checkLinks")){
				if(!CheckPerm(@event.getUser(), 4)) return;
				_bools[CheckLinks] = !_bools[CheckLinks];
				SendMessage(@event, _bools[CheckLinks] ? "Link checking is on" : "Link checking is off");
				AddCooldown(@event.getUser());
			}

			// !formatting - toggles COLOR (Mostly in the errors)
			else if(CommandChecker(@event, arg, "formatting")){
				if(CheckPerm(@event.getUser(), 9001)){
					_bools[Color] = !_bools[Color];
					SendMessage(@event, _bools[Color] ? "Color formatting is now On" : "Color formatting is now Off");
				}
				else{ PermErrorchn(@event); }
			}

			// !HelpMe - redirect to !COMMANDS
			else if(CommandChecker(@event, arg, "HelpMe")){
				SendMessage(@event, "This command was changed to \"commands\".");
				AddCooldown(@event.getUser());
			}

			// !setGuildChan - Sets what channel to announce joins an quits in
			else if(CommandChecker(@event, arg, "setGuildChan")){
				if(!CheckPerm(@event.getUser(), 5)) return;
				if(!(@event is DiscordMessageEvent)) return;
				var guildId = ((DiscordMessageEvent) @event).getDiscordEvent().getGuild().getId();
				if(GetArg(arg, 1) != null){
					if(GetArg(arg, 1).toLowerCase().startsWith("rem")){
						_checkJoinsAndQuits.Remove(guildId);
						SendMessage(@event, "Removed Guild from Join and quit messages");
					}
					else{
						var channels = ((DiscordMessageEvent) @event).getDiscordEvent()
							.getGuild()
							.getTextChannels()
							.toList<TextChannel>();
						TextChannel textChannel = null;
						foreach(var textChannel0 in channels){
							if(textChannel0.getId() == GetArg(arg, 1)
							   || ((MessageChannel) textChannel0).getName().equalsIgnoreCase(GetArg(arg, 1))){ textChannel = textChannel0; }
						}
						if(textChannel == null) return;
						_checkJoinsAndQuits[guildId] = GetArg(arg, 1);
						SendMessage(@event, "set Join and quit message channel to #" + ((MessageChannel) textChannel).getName());
					}
				}
				else{
					_checkJoinsAndQuits[guildId] = ((DiscordMessageEvent) @event).getDiscordEvent().getTextChannel().getId();
					var channel_ = ((DiscordMessageEvent) @event).getDiscordEvent().getTextChannel() as MessageChannel;
					SendMessage(@event, "set Join and quit message channel to #" + channel_.getName());
				}
			}

			// !admin - contains most admin related commands - A super command
			else if(CommandChecker(@event, arg, "admin")){
				if(!CheckPerm(@event.getUser(), 2)) return;
				var discord = Network == Network.Discord;
				var args = FormatstringArgs(LilGUtil.splitMessage(message, 0, false));
				var parser = ArgumentParsers.newArgumentParser("admin")
					.description("contains most admin related commands")
					.defaultHelp(true);
				var subparsers = parser.addSubparsers()
					.title("Valid commands")
					.description("This is the list of valid admin commands you can use.")
					.dest("admin_command");
				var ban = subparsers.addParser("ban")
					.help("Bans a user");
				var kick = subparsers.addParser("kick")
					.help("kicks a user");
				ban.addArgument("users").nargs("*").type(typeof(string)).help("User to ban");
				kick.addArgument("users").nargs("*").type(typeof(string)).help("User to kick");
				ban.addArgument("-r", "--reason")
					.type(typeof(string))
					.help("Sets the reason for the ban")
					.setDefault("No reason given");
				kick.addArgument("-r", "--reason")
					.type(typeof(string))
					.help("Sets the reason for the kick")
					.setDefault("No reason given");
				if(discord){
					ban.addArgument("--remove-messages")
						.nargs(1)
						.type(typeof(int))
						.setDefault(0)
						.help("Amount of messages, by days, to remove by the user, if any");
					var delMsg = subparsers.addParser("delmsg")
						.help("Deletes a message, a span of messages, or a certain amount of messages from a certain user");

					var delMsgGroup = delMsg.addMutuallyExclusiveGroup();
					delMsgGroup.addArgument("-m", "--message-span")
						.nargs(2)
						.help("Specify 2 message IDs and any messages between (inclusive) will be deleted");
					delMsgGroup.addArgument("-u", "--user")
						.help("user to delete messages from with the metioned user infront and the amount of messages second")
						.nargs(2);
				}
				else{
					ban.addArgument("-k", "--kick-ban")
						.type(typeof(bool))
						.help("Specify to also kick")
						.action(Arguments.storeTrue());
				}
				var modeM = subparsers.addParser("+m")
					.help("Sets a channel so only certain users can speak")
					.defaultHelp(true);
				modeM.addArgument("-s", "--state")
					.type(Arguments.booleanType("on", "off"))
					.setDefault(null as object)
					.help("Sets +m mode on or off. Otherwise toggle");
				modeM.addArgument("roles")
					.nargs("*")
					.help("Roles to white/black list");
				modeM.addArgument("-w", "--whitelist")
					.type(typeof(bool))
					.action(Arguments.storeTrue())
					.help("Sets the channel to white list mode");

				var modeG = subparsers.addParser("+g")
					.help("makes it so messages containing a certain string cannot be sent")
					.defaultHelp(true);
				modeG.addArgument("expression")
					.type(typeof(string))
					.help("what to check messages against");
				modeG.addArgument("-l", "--list")
					.type(typeof(bool))
					.action(Arguments.storeTrue())
					.help("List out all +g for the server");
				modeG.addArgument("-r", "--remove")
					.type(typeof(bool))
					.action(Arguments.storeTrue())
					.help("Remove expression instead of adding it");

				var logging = subparsers.addParser("logging")
					.help("Sets the logging channel")
					.defaultHelp(true);

				var topic = subparsers.addParser("topic")
					.help("Sets the topic")
					.defaultHelp(true);
				topic.addArgument("newTopic")
					.nargs("*")
					.help("New topic");
				//Subparser op; // TODO: 10/3/16 Add subparser for giving permissions
				Namespace ns;
				try{
					ns = parser.parseArgs(args);
					Logger.Debug(ns.toString());
					var isBan = false;
					switch(ns.getString("admin_command")){
						case "ban":
							isBan = true;
							goto case "kick";
						case "kick":
							var users = ns.getList("users").toList<string>();
							if(discord){
								var discordEvent = ((DiscordMessageEvent) @event).getDiscordEvent();
								var permNeeded = isBan ? Permission.BAN_MEMBERS : Permission.KICK_MEMBERS;
								if(CheckPerm((DiscordUser) @event.getUser(), permNeeded)){
									var mentioned = discordEvent.getMessage().getMentionedUsers().toList<net.dv8tion.jda.core.entities.User>();
									Logger.Trace($"Mentioned users: {mentioned.toString()}");
									var guild = discordEvent.getGuild();
									var controller = guild.getController();
									if(mentioned.Count != 0){
										foreach(var mentionedUser in mentioned){
											var reason = ns.getString("reason");
											var readonlyBan = isBan;
											var sendMsg = new Thread(() => {
												if(reason == null) return;
												var action = readonlyBan ? "Banned" : "Kicked";
												SendPrivateMessage(@event,
													mentionedUser.getName(),
													action
													+ " by "
													+ ((DiscordMessageEvent) @event).getDiscordEvent().getMember().getEffectiveName()
													+ ". Reason: "
													+ reason);
											});
											if(!mentionedUser.hasPrivateChannel()){ mentionedUser.openPrivateChannel().complete(); }
											sendMsg.Start();
											if(isBan){
												controller.ban(mentionedUser, ns.getInt("remove_messages").intValue()).queue();
												SendMessage(@event, "Banned user: " + mentionedUser.getName());
											}
											else{
												controller.kick(guild.getMember(mentionedUser)).queue();
												SendMessage(@event, "Kicked user: " + mentionedUser.getName());
											}
										}
									}
									else{
										foreach(var user in users){
											Member currentDiscordMember = null;
											var nick = "Error getting name";
											foreach(var discordUser in guild.getMembers().toList<Member>()){
												nick = discordUser.getEffectiveName();
												if(!discordUser.getUser().getName().equalsIgnoreCase(user)
												   && !nick.equalsIgnoreCase(user)
												   && !discordUser.getUser().getId().equalsIgnoreCase(user)) continue;
												if(currentDiscordMember == null){ currentDiscordMember = discordUser; }
												else{
													if(isBan){ SendMessage(@event, "Ambiguous ban, not banning user " + user); }
													else{ SendMessage(@event, "Ambiguous kick, not kicking user " + user); }
													return;
												}
											}
											if(currentDiscordMember != null){
												var reason = ns.getString("reason");
												var currentDiscordUser = currentDiscordMember.getUser();
												var finalBan = isBan;
												var sendMsg = new Thread(() => {
													if(reason == null) return;
													string action = finalBan ? "Banned" : "Kicked";
													SendPrivateMessage(@event,
														currentDiscordUser.getName(),
														action
														+ " by "
														+ ((DiscordMessageEvent) @event)
														.getDiscordEvent()
														.getMember()
														.getEffectiveName()
														+ ". Reason: "
														+ reason);
												});
												if(!currentDiscordUser.hasPrivateChannel()){ currentDiscordUser.openPrivateChannel().complete(); }
												sendMsg.Start();
												if(isBan){
													controller.ban(currentDiscordMember, ns.getInt("remove_messages").intValue()).queue();
													SendMessage(@event, "Banned user: " + nick);
												}
												else{
													controller.kick(currentDiscordMember);
													SendMessage(@event, "Kicked user: " + nick);
												}
											}
											else{
												if(user.matches("\\d+") && user.length() == 18){
													Logger.Info("Treating {0} as id to be hackbanned", user);
													if(isBan){
														controller.ban(user, ns.getInt("remove_messages").intValue()).queue();
														Logger.Info("Banned user {0}", user);
													}
													else{ SendMessage(@event, "you cannot \"hackkick\" a user (user: " + user + ")"); }
												}
												else{ SendMessage(@event, "user " + user + " could not be found"); }
											}
										}
									}
								}
							}
							else{ // irc
								foreach(User user in @event.getChannel().getUsers()){
									foreach(var userStr in users){
										if(userStr.contains("@") && userStr.contains("!")){ // checks if given a hostmask
											if(!LilGUtil.matchHostMask(user.getHostmask(), userStr)) continue;
											string reason;
											if(isBan){
												@event.getChannel().send().ban(userStr);
												if(!ns.getBoolean("kick_ban").booleanValue()) continue;
												if((reason = ns.getString("reason")) != null){ @event.getChannel().send().kick(user, reason); }
												else{ @event.getChannel().send().kick(user); }
											}
											else{
												if((reason = ns.getString("reason")) != null){ @event.getChannel().send().kick(user, reason); }
												else{ @event.getChannel().send().kick(user); }
											}
										}
										else{
											if(!user.getNick().equalsIgnoreCase(userStr)) continue;
											string reason;
											if(isBan){
												@event.getChannel().send().ban("*!*@" + user.getHostname());
												if(!ns.getBoolean("kick_ban").booleanValue()) continue;
												if((reason = ns.getString("reason")) != null){ @event.getChannel().send().kick(user, reason); }
												else{ @event.getChannel().send().kick(user); }
											}
											else{
												if((reason = ns.getString("reason")) != null){ @event.getChannel().send().kick(user, reason); }
												else{ @event.getChannel().send().kick(user); }
											}
										}
									}
								}
							}
							break;
						case "delmsg": // only possible on discord so no need to check
							List<string> delMsgArgs;
							if((delMsgArgs = ns.getList("message_span").toList<string>()) != null){
								string firstMessage = delMsgArgs[0], secondMessage = delMsgArgs[1];
								var discordChannel =
									((DiscordMessageEvent) @event).getDiscordEvent().getTextChannel();
								var deleting = false;
								var messagesToDel = new List<Message>();
								foreach(var msg in discordChannel.getHistory().getRetrievedHistory().toList<Message>()){
									if(deleting){
										messagesToDel.Add(msg);
										if(msg.getId() == secondMessage){ return; }
									}
									else{
										if(msg.getId() != firstMessage) continue;
										deleting = true;
										messagesToDel.Add(msg);
									}
								}
								discordChannel.deleteMessages(messagesToDel.toJList());
							}
							break;
						case "+m":
							var state = ns.getBoolean("state")?.booleanValue(); // True = on, False = off, null = toggle
							var whiteListMode = ns.getBoolean("whitelist").booleanValue();
							if(discord){ // ---------------------------Discord---------------------------
								var topicStr = "+m | ";
								var mChannel = ((DiscordMessageEvent) @event).getDiscordEvent().getTextChannel();
								var publicRole = mChannel.getGuild().getPublicRole();
								var roleArgs = ns.getList("roles").toList<string>();
								var currentDiscordUser = ((DiscordUser) _currentUser).getDiscordUser();
								if(DiscordData.channelRoleMap.ContainsKey(mChannel)){
									var roles = DiscordData.channelRoleMap[mChannel];
									if(state == null || !state.Value){ // disabling +m mode
										foreach(var role in roles){
											var overRide = mChannel.getPermissionOverride(role);
											overRide?.getManager().clear(Permission.MESSAGE_WRITE).queue();
										}
										DiscordData.channelRoleMap.Remove(mChannel);
										if(mChannel.getTopic().startsWith(topicStr)){
											mChannel.getManager().setTopic(mChannel.getTopic().substring(topicStr.length())).queue();
										}
										/*if (currentDiscordUser != null) { // commented out due to lib dev forgetting to re-add the delete function lol
										    PermissionOverride overRide = mChannel.getPermissionOverride(mChannel.getGuild().getMember(currentDiscordUser));
										    if(overRide != null){
										        overRide.delete();
										    }
										}*/
										SendMessage(@event,
											((DiscordMessageEvent) @event).getDiscordEvent().getMember().getAsMention() + " has set mode -m");
									}
									else{ // overwriting role list?
										PermissionOverride overRide;
										foreach(var role in roles){
											overRide = mChannel.getPermissionOverride(role);
											overRide?.getManager().clear(Permission.MESSAGE_WRITE).queue();
										}
										roles.Clear();
										roles.Add(publicRole);
										overRide = mChannel.getPermissionOverride(publicRole);
										overRide?.getManager().clear(Permission.MESSAGE_WRITE).queue();
										var guildRoleList = mChannel.getGuild().getRoles().toList<Role>();
										if(currentDiscordUser != null){
											var currentDiscordMember = mChannel.getGuild().getMember(currentDiscordUser);
											overRide = (mChannel.getPermissionOverride(currentDiscordMember)
											            ?? mChannel.createPermissionOverride(currentDiscordMember)
												            .complete(false)) as PermissionOverride;
											overRide?.getManager().grant(Permission.MESSAGE_WRITE).queue();
										}
										if(roleArgs.Count != 0)
											foreach(var role in guildRoleList){
												foreach(var roleArg in roleArgs){
													if(!roleArg.equalsIgnoreCase(role.getName())) continue;
													overRide =
													(mChannel.getPermissionOverride(role)
													 ?? mChannel.createPermissionOverride(role).complete(false)) as PermissionOverride;
													if(whiteListMode){ overRide?.getManager().grant(Permission.MESSAGE_WRITE).queue(); }
													else{ overRide?.getManager().deny(Permission.MESSAGE_WRITE).queue(); }
													roles.Add(role);
													roleArgs.Remove(roleArg);
													break;
												}
											}
										SendMessage(@event,
											((DiscordMessageEvent) @event).getDiscordEvent().getMember().getAsMention() + " has updated the role list");
									}
								}
								else{
									var guildRoleList = mChannel.getGuild().getRoles().toList<Role>();
									var roles = new List<Role>();

									var overRide = mChannel.getPermissionOverride(publicRole);
									overRide?.getManager().deny(Permission.MESSAGE_WRITE).queue();
									if(currentDiscordUser != null){
										var currentDiscordMember = mChannel.getGuild().getMember(currentDiscordUser);
										overRide = (mChannel.getPermissionOverride(currentDiscordMember)
										            ?? mChannel.createPermissionOverride(currentDiscordMember).complete(false)) as PermissionOverride;
										overRide?.getManager().grant(Permission.MESSAGE_WRITE).queue();
									}
									roles.Add(publicRole);
									if(roleArgs.Count != 0)
										foreach(var role in guildRoleList){
											foreach(var roleArg in roleArgs){
												if(!roleArg.equalsIgnoreCase(role.getName())) continue;
												overRide = mChannel.getPermissionOverride(role)
												           ?? mChannel.createPermissionOverride(role).complete(false) as PermissionOverride;
												if(whiteListMode){ overRide.getManager().grant(Permission.MESSAGE_WRITE).queue(); }
												else{ overRide.getManager().deny(Permission.MESSAGE_WRITE).queue(); }
												roles.Add(role);
												roleArgs.Remove(roleArg);
												break;
											}
										}
									DiscordData.channelRoleMap[mChannel] = roles;
									mChannel.getManager().setTopic(topicStr + mChannel.getTopic()).queue();
									SendMessage(@event,
										((DiscordMessageEvent) @event).getDiscordEvent().getMember().getAsMention() + " has set mode +m");
								}
							}
							else{
// ----------------------------------------------------IRC----------------------------------------------------
								var chan = @event.getChannel();
								if(chan.containsMode('m')){ chan.send().removeModerated(); }
								else{ chan.send().setModerated(); }
							}
							break;
						case "+g":
							if(discord){
								var mChannel = ((DiscordMessageEvent) @event).getDiscordEvent().getTextChannel();
								var expressions = DiscordData.wordFilter[mChannel];
								if(ns.getBoolean("list").booleanValue()){
									if(expressions == null || expressions.Count == 0){ SendMessage(@event, "+g list is empty"); }
									else{ SendMessage(@event, expressions.toString()); }
								}
								else if(ns.getBoolean("remove").booleanValue()){
									if(expressions == null || expressions.Count == 0){
										SendMessage(@event, "+g list is empty, nothing to remove");
									}
									else{
										if(expressions.Remove(ns.getString("expression"))){
											SendMessage(@event, "removed \"" + ns.getString("expression") + "\" from the +g list");
										}
										else{ SendMessage(@event, "that expression wasn't in the +g list"); }
									}
								}
								else{
									if(expressions == null){
										expressions = new List<string>();
										DiscordData.wordFilter[mChannel] = expressions;
									}
									expressions.Add(ns.getString("expression"));
								}
							}
							else{
								var chan = @event.getChannel();
								chan.send()
									.setMode(ns.getBoolean("remove").booleanValue() ? "-g" : "+g",
										chan.getName(),
										ns.getString("expression"));
							}
							break;

						case "topic":{
							var topicStr = ArgJoiner(ns.getList("newTopic").toList<string>().ToArray(), 0);
							if(discord){
								((DiscordMessageEvent) @event).getDiscordEvent()
									.getTextChannel()
									.getManager()
									.setTopic(topicStr)
									.queue();
							}
							else{ @event.getChannel().send().setTopic(topicStr); }
						}
							break;
					}
				}
				catch(ArgumentParserException e){ SendCommandHelp(@event, parser); }
				catch(Exception e){ SendError(@event, e); }
			}

			// !muteServer - mutes entire server
			else if(CommandChecker(@event, arg, "muteServer")){
				if(!CheckPerm(@event.getUser(), 9001)) return;
				if(GetArg(arg, 1) != null){
					if(GetArg(arg, 1).equalsIgnoreCase("add")){ _mutedServerList.Add(GetArg(arg, 2)); }
					else if(GetArg(arg, 1).equalsIgnoreCase("del")){ _mutedServerList.Remove(GetArg(arg, 2)); }
				}
				else{
					_mutedServerList.Add(Network == Network.Discord
						? ((DiscordMessageEvent) @event).getDiscordEvent().getGuild().getId()
						: @event.getBot().getServerHostname());
				}
			}

			// !command - Sets what commands can be used where
			else if(CommandChecker(@event, arg, "command")){
				if(CheckPerm(@event.getUser(), 9001)){
					var commandArg = arg;
					var messageEvent = @event as DiscordMessageEvent;
					if(messageEvent != null){
						commandArg = splitMessage(messageEvent.getDiscordEvent().getMessage().getStrippedContent());
					}
					var allowedCommands = FozruciX._allowedCommands[GetSeverName(@event, true)];
					if(allowedCommands == null){
						allowedCommands = new Dictionary<string, List<string>>();
						FozruciX._allowedCommands[GetSeverName(@event, true)] = allowedCommands;
					}
					if(GetArg(commandArg, 2) != null){
						sbyte mode = 0;
						var chan = GetArg(commandArg, 1);
						for(byte i = 2; GetArg(commandArg, i) != null; i++){
							var command = GetArg(commandArg, i);
							if(command.startsWith("-")){
								mode = -1;
								command = command.substring(1, command.length());
							}
							else if(command.startsWith("+")){
								mode = +1;
								command = command.substring(1, command.length());
							}
							switch(mode){
								case -1:
									allowedCommands[chan].Remove(command);
									SendMessage(@event, "Removed command ban on " + command + " For channel " + chan);
									break;
								case 1:
									if(!allowedCommands.ContainsKey(chan)){ allowedCommands[chan] = new List<string>(); }
									allowedCommands[chan].Add(command);
									SendMessage(@event, "Added command ban on " + command + " for channel " + chan);
									break;
								default:
									SendMessage(@event,
										"The command "
										+ command
										+ " is "
										+ ((allowedCommands[chan]).Contains(command) ? "" : "not ")
										+ "Banned from "
										+ chan);
									break;
							}
						}
					}
					else if(GetArg(commandArg, 1) != null){ SendMessage(@event, allowedCommands[GetArg(commandArg, 1)].toString()); }
					else{ SendMessage(@event, allowedCommands.toString()); }
					AddCooldown(@event.getUser());
				}
				else{ PermErrorchn(@event); }
			}

			// !Commands - lists commands that can be used
			else if(CommandChecker(@event, arg, "Commands")){
				if(GetArg(arg, 1) == null){
					SendNotice(@event,
						@event.getUser().getNick(),
						"List of Commands so far. for more info on these Commands do "
						+ _prefix
						+ "Commands. Commands with \"Joke: \" are joke Commands that can be disabled");
					SendNotice(@event, @event.getUser().getNick(), new[]{Commands}.toString());
				}
				else{ GetHelp(@event, GetArg(arg, 1)); }
				AddCooldown(@event.getUser());
			}

			// !getBots - gets all bots
			else if(CommandChecker(@event, arg, "getBots")){
				if(CheckPerm(@event.getUser(), 9001)){
					try{
						var temp = _manager.getBots().toArray();
						var bots = "";
						foreach(var aTemp in temp){
							var server = ((PircBotX) aTemp).getServerInfo().getNetwork() ?? ((PircBotX) aTemp).getServerHostname();
							var nick = ((PircBotX) aTemp).getNick();
							bots += "Server: " + server + " Nick: " + nick + " | ";
						}
						SendMessage(@event, bots.substring(0, bots.lastIndexOf("|")));
					}
					catch(Exception e){ SendError(@event, e); }
				}
				else{ PermErrorchn(@event); }
			}

			// !addServer - adds a bot to a server
			else if(CommandChecker(@event, arg, "addServer")){
				if(CheckPerm(@event.getUser(), 9001)){
					var args = FormatstringArgs(arg);
					var parser = ArgumentParsers.newArgumentParser("addServer")
						.description("Connects the bot to a server")
						.defaultHelp(true);
					parser.addArgument("address")
						.type(typeof(string))
						.help("The server to connect to");
					parser.addArgument("channelList")
						.type(typeof(string))
						.help("List of channels to autoconnect to");
					parser.addArgument("-k", "--key")
						.type(typeof(string))
						.setDefault((Object) null)
						.help("The server to connect to");
					parser.addArgument("-p", "--port")
						.type(typeof(int))
						.setDefault(6667)
						.help("Sets what port to connect to");
					parser.addArgument("-s", "--ssl")
						.type(typeof(bool))
						.action(Arguments.storeTrue())
						.help("Specifies if the server port is SSL");
					Namespace ns;
					try{
						ns = parser.parseArgs(args);
						Logger.Debug(ns.toString());
						Configuration.Builder normal = null;
						var server = ns.getString("address");
						var port = ns.getInt("port").intValue();
						if(equalsAny(server.toLowerCase(), "badnik", "network", "caffie", "esper", "nova")){
							port = 6697;
#if DEBUG
							switch(server.toLowerCase()){
								case "badnik":
									normal = FozConfig.DebugConfig;
									server = FozConfig.Badnik;
									break;
								case "network":
									//normal = FozConfig.twitchDebug;
									server = FozConfig.Twitch;
									break;
								case "caffie":
									normal = FozConfig.DebugConfigSmwc;
									server = FozConfig.Caffie;
									break;
								case "esper":
									normal = FozConfig.DebugConfigEsper;
									server = FozConfig.Esper;
									break;
								case "nova":
									normal = FozConfig.DebugConfigNova;
									server = FozConfig.Nova;
									break;
							}
#else
                                    switch (ns.getString("address").toLowerCase()) {
                                        case "badnik":
                                            normal = FozConfig.Normal;
                                            server = FozConfig.Badnik;
                                            break;
                                        case "network":
                                            //normal = FozConfig.twitchNormal;
                                            server = FozConfig.Twitch;
                                            break;
                                        case "caffie":
                                            normal = FozConfig.NormalSmwc;
                                            server = FozConfig.Caffie;
                                            break;
                                        case "esper":
                                            normal = FozConfig.NormalEsper;
                                            server = FozConfig.Esper;
                                            break;
                                        case "nova":
                                            normal = FozConfig.NormalNova;
                                            server = FozConfig.Nova;
                                            break;
                                    }
#endif
						}
						else if(ns.getBoolean("ssl").booleanValue()){
							normal = new Configuration.Builder()
								.setEncoding(Charset.forName("UTF-8"))
								.setAutoReconnect(true)
								.setAutoReconnectAttempts(5)
								.setNickservPassword(CryptoUtil.decrypt(FozConfig.Password))
								.setName(_bot.getConfiguration().getName()) //Set the nick of the bot.
								.setLogin(_bot.getConfiguration().getLogin())
								.setRealName(_bot.getConfiguration().getRealName())
								.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
								.addListener(new FozruciX(_manager));
						}
						else{
							normal = new Configuration.Builder()
								.setEncoding(Charset.forName("UTF-8"))
								.setAutoReconnect(true)
								.setAutoReconnectAttempts(5)
								.setNickservPassword(CryptoUtil.decrypt(FozConfig.Password))
								.setName(_bot.getConfiguration().getName()) //Set the nick of the bot.
								.setLogin(_bot.getConfiguration().getLogin())
								.setRealName(_bot.getConfiguration().getRealName())
								.addListener(new FozruciX(_manager));
						}
						Debug.Assert(normal != null);
						_manager.addBot(normal.buildForServer(server, port, ns.getString("key")));
						SendMessage(@event, "Connecting bot to " + ns.getString("address"), false);
					}
					catch(ArgumentParserException e){ SendCommandHelp(@event, parser); }
					catch(Exception e){ SendError(@event, e); }
				}
				else{ PermErrorchn(@event); }
			}

			// !serverHostName - Gets the Server Host Name
			else if(CommandChecker(@event, arg, "serverHostName")){
				if(CheckPerm(@event.getUser(), 9001)){ SendMessage(@event, _bot.getServerHostname(), false); }
				else{ PermErrorchn(@event); }
			}

			// !clearLogin - Clears login info to test auth related thing
			else if(CommandChecker(@event, arg, "clearLogin")){
				if(CheckPerm(@event.getUser(), 9001)){
					_currentUser = _bot.getUserBot();
					SendMessage(@event, "Logged out", false);
				}
				else{ PermErrorchn(@event); }
			}

			// !RESPOND_TO_PMS - sets whether or not to respond to PMs
			else if(CommandChecker(@event, arg, "RESPOND_TO_PMS")){
				if(CheckPerm(@event.getUser(), 9001)){
					_bools[RespondToPms] = !_bools[RespondToPms];
					SendMessage(@event, "Responding to PMs: " + _bools[RespondToPms], false);
				}
				else{ PermErrorchn(@event); }
			}

			// !Connect - joins a channel
			else if(CommandChecker(@event, arg, "Connect")){
				if(CheckPerm(@event.getUser(), 9001)){ _bot.send().joinChannel(GetArg(arg, 1)); }
				else{ PermErrorchn(@event); }
			}

			// !setDebugLevel - sets debugging level
/*                else if (commandChecker(@event, arg, "setDebugLevel")) {
                    if (checkPerm(@event.getUser(), 9001)) {
                        Logger.setLevel(Level.toLevel(getArg(arg, 1).toUpperCase()));
                        sendMessage(@event, "Set debug level to " + Logger.getLevel().toString());
                    } else {
                        permErrorchn(@event);
                    }
                }*/

			// !setAvatar - sets the avatar of the bot
			else if(CommandChecker(@event, arg, "setAvatar")){
				if(CheckPerm(@event.getUser(), 9001)){
					_avatar = GetArg(arg, 1);
					SendMessage(@event, "Avatar set", false);
				}
				else{ PermErrorchn(@event); }
			}

			// !loadData - force a reload of the save data
			else if(CommandChecker(@event, arg, "loadData")){
				if(CheckPerm(@event.getUser(), 2)){ LoadData(); }
				else{ PermErrorchn(@event); }
			}

			// !SkipLoad - skips loading save data
			else if(CommandChecker(@event, arg, "SkipLoad")){
				if(CheckPerm(@event.getUser(), 9001)){ _bools[DataLoaded] = true; }
				else{ PermErrorchn(@event); }
			}

			// !reverseList - Reverses a list
			else if(CommandChecker(@event, arg, "reverseList")){
				SendMessage(@event, arg.Skip(1).ToString());
				AddCooldown(@event.getUser());
			}

			// !getDate - test get date
			else if(CommandChecker(@event, arg, "getDate")){
				try{
					var parser = new Parser();
					/*
					long time = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
					WaitForQueue queue = new WaitForQueue(bot);
					@event.getUser().send().ctcpCommand("TIME");

					//Infinite loop since we might receive messages that aren't WaitTest's.
					while ((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) < time+5000) {

					    //Use the waitFor() method to wait for a MessageEvent.
					    //This will block (wait) until a message @event comes in, ignoring
					    //everything else
					    NoticeEvent currentEvent = queue.waitFor(NoticeEvent.class);
					    //Check if this message is the "ping" command
					    if (currentEvent.getMessage().toLowerCase().contains("time")) {
					        List<DateGroup> groups = parser.parse((currentEvent.getMessage()));
					        ZonedDateTime time2 = groups[0].getDates()[0];

					        TimeZone.setDefault(new SimpleTimeZone(time2.getTimezoneOffset(), new ZoneId()));
					    }
					}
					*/


					List<DateGroup> groups = parser.parse(ArgJoiner(arg, 1)).toList<DateGroup>();
					SendMessage(@event, groups[0].getDates().get(0).toString());
				}
				catch(Exception e){ SendError(@event, e); }
				Logger.Debug(ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				AddCooldown(@event.getUser());
			}

			// !do nothing - does nothing
			else if(CommandChecker(@event, arg, "do")){
				if(GetArg(arg, 1).equalsIgnoreCase("nothing")){
					if(randInt(0, 2) == 0){
						SendMessage(@event, "no");
						AddCooldown(@event.getUser());
					}
				}
			}

			// !markov - makes Markov chains
			else if(CommandChecker(@event, arg, "markov")){
				if(MarkovChain == null){ MarkovChain = new Dictionary<string, List<string>>(); }
				var loop = true;
				var newPhrase = "";
				try{
					while(loop){
						// List to hold the phrase

						// string for the next word
						string nextWord = null;
						//bool matches = getArg(arg, 1) != null;
						var matches = false;
						var matchAttempts = 0;
						do{
							newPhrase = "";
							for(var loops = 0; randInt(0, 3) == 1 && loops < 3; loops++){
								if(loops > 0){ newPhrase += " "; }
								// Select the first word
								var startWords = MarkovChain["_start"];

								for(var i = 1 + _arrayOffset; i < arg.Length; i++){
									if(startWords.Contains(arg[i])){
										matches = false;
										nextWord = startWords[startWords.IndexOf(arg[i])];
									}
								}
								if(nextWord == null){
									var startWordsLen = startWords.Count;
									nextWord = startWords[_rnd.Next(startWordsLen)];
								}


								var greaterThanOne = nextWord.length() > 1 ? 2 : (nextWord.length() > 0 ? 1 : 0);
								newPhrase += nextWord.substring(0, greaterThanOne)
								             + "\u200B"
								             + nextWord.substring(greaterThanOne, nextWord.length());

								// Keep looping through the words until we've reached the end
								while(nextWord.charAt(nextWord.length() - 1) != '.'){
									var wordSelection = MarkovChain[nextWord];
									nextWord = null;

									for(var i = 1; i < arg.Length; i++){
										if(startWords.Contains(arg[i])){
											matches = false;
											nextWord = startWords[startWords.IndexOf(arg[i])];
										}
									}
									if(nextWord == null){
										var wordSelectionLen = wordSelection.Count;
										nextWord = wordSelection[_rnd.Next(wordSelectionLen)];
									}

									greaterThanOne = nextWord.length() > 1 ? 2 : (nextWord.length() > 0 ? 1 : 0);
									if(newPhrase.isEmpty()){
										newPhrase = nextWord.substring(0, greaterThanOne)
										            + "\u200B"
										            + nextWord.substring(greaterThanOne, nextWord.length());
									}
									else{
										newPhrase += " "
										             + nextWord.substring(0, greaterThanOne)
										             + "\u200B"
										             + nextWord.substring(greaterThanOne, nextWord.length());
									}
								}
								if(newPhrase.lastIndexOf(" ") != newPhrase.indexOf(" ") && !newPhrase.contains("!")){ loop = false; }
							}
							matchAttempts++;
						}
						while(matches || matchAttempts > 50);
					}
					SendMessage(@event, "\u0002\u0002" + newPhrase, false);
					Logger.Debug(newPhrase.replace('\u200B', '▮'));
				}
				catch(IllegalArgumentException){
					SendError(@event, new Exception("No words have been added to the database, Try saying something!"));
				}
				catch(Exception e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			// !8ball - ALL HAIL THE MAGIC 8-BALL
			else if(CommandChecker(@event, arg, "8Ball")){
				var choice = LilGUtil.randInt(1, 20);
				var response = "";

				switch(choice){
					case 1:
						response = "It is certain";
						break;
					case 2:
						response = "It is decidedly so";
						break;
					case 3:
						response = "Without a doubt";
						break;
					case 4:
						response = "Yes - definitely";
						break;
					case 5:
						response = "You may rely on it";
						break;
					case 6:
						response = "As I see it, yes";
						break;
					case 7:
						response = "Most likely";
						break;
					case 8:
						response = "Outlook good";
						break;
					case 9:
						response = "Signs point to yes";
						break;
					case 10:
						response = "Yes";
						break;
					case 11:
						response = "Reply hazy, try again";
						break;
					case 12:
						response = "Ask again later";
						break;
					case 13:
						response = "Better not tell you now";
						break;
					case 14:
						response = "Cannot predict now";
						break;
					case 15:
						response = "Concentrate and ask again";
						break;
					case 16:
						response = "Don't count on it";
						break;
					case 17:
						response = "My reply is no";
						break;
					case 18:
						response = "My sources say no";
						break;
					case 19:
						response = "Outlook not so good";
						break;
					case 20:
						response = "Very doubtful";
						break;
				}
				SendMessage(@event, response);
				AddCooldown(@event.getUser());
			}

			// !setMessage - Sets different message formats
			else if(CommandChecker(@event, arg, "setMessage")){
				if(CheckPerm(@event.getUser(), 8)){
					switch(GetArg(arg, 1).toLowerCase()){
						case "normal":
							_messageMode = MessageModes.Normal;
							SendMessage(@event, "Message mode set back to normal");
							break;
						case "reverse":
							_messageMode = MessageModes.Reversed;
							SendMessage(@event, "Message is now reversed");
							break;
						case "wordreverse":
							_messageMode = MessageModes.WordReversed;
							SendMessage(@event, "Message words reversed");
							break;
						case "scramble":
							_messageMode = MessageModes.Scrambled;
							SendMessage(@event, "Messages are scrambled");
							break;
						case "wordscramble":
							_messageMode = MessageModes.WordScrambled;
							SendMessage(@event, "Message words are scrambled");
							break;
						case "caps":
							_messageMode = MessageModes.Caps;
							SendMessage(@event, "Messages are in all caps");
							break;
						default:
							SendMessage(@event, "Not a message mode");
							break;
					}
				}
				else{ PermErrorchn(@event); }
			}

			// !CheckLink - checks links, duh
			else if(CommandChecker(@event, arg, "CheckLink")){
				try{
					var doc = Jsoup.connect(GetArg(arg, 1))
						.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2783.4 Safari/537.36")
						.timeout(5000)
						.get();
					SendMessage(@event, "Title: " + doc.title(), false);
					AddCooldown(@event.getUser());
				}
				catch(UnsupportedMimeTypeException e){ SendMessage(@event, "type: " + e.getMimeType()); }
				catch(Exception e){ SendError(@event, e); }
			}

			// !comeback - gets one of the permission error statements
			else if(CommandChecker(@event, arg, "comeback")){
				PermErrorchn(@event);
				AddCooldown(@event.getUser());
			}

			// !Time - Tell the time
			else if(CommandChecker(@event, arg, "time")){
				try{
					var time = new Date().toString();
					SendMessage(@event, " The time is now " + time);
				}
				catch(Exception e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			// !perms - edit privileged users
			else if(CommandChecker(@event, arg, "perms")){
				if(CheckPerm(@event.getUser(), int.MaxValue)){
					if(GetArg(arg, 1).equalsIgnoreCase("set")){
						try{
							if(_authedUser.Contains(GetArg(arg, 2))){
								try{ _authedUserLevel[_authedUser.IndexOf(GetArg(arg, 2))] = Convert.ToInt32(GetArg(arg, 3)); }
								catch(Exception e){ SendError(@event, e); }
								SendMessage(@event, "Set " + GetArg(arg, 2) + " To level " + GetArg(arg, 3));
							}
							else{
								try{
									_authedUser.Add(GetArg(arg, 2));
									_authedUserLevel.Add(Convert.ToInt32(GetArg(arg, 3)));
								}
								catch(Exception e){ SendError(@event, e); }
								SendMessage(@event, "Added " + GetArg(arg, 2) + " To authed users with level " + GetArg(arg, 3));
							}
						}
						catch(Exception e){ SendError(@event, e); }
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("del")){
						try{
							var index = _authedUser.IndexOf(GetArg(arg, 2));
							_authedUserLevel.RemoveAt(index);
							_authedUser.RemoveAt(index);
						}
						catch(Exception e){ SendError(@event, e); }
						SendMessage(@event, "Removed " + GetArg(arg, 2) + " from the authed user list");
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("clear")){
						_authedUser.Clear();
						_authedUserLevel.Clear();
						SendMessage(@event, "Permission list cleared");
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("List")){ SendMessage(@event, _authedUser.toString()); }
					else{
						var place = -1;
						try{
							for(var i = 0; _authedUser.Count >= i; i++){
								if(((string) _authedUser[i]).equalsIgnoreCase(GetArg(arg, 1))){ place = i; }
							}
						}
						catch(IndexOutOfBoundsException e){
							SendMessage(@event, "That user wasn't found in the list of authed users", false);
						}
						if(place == -1){ SendMessage(@event, "That user wasn't found in the list of authed users", false); }
						else{
							SendMessage(@event,
								"User " + (string) _authedUser[place] + " Has permission level " + (int) _authedUserLevel[place],
								false);
						}
					}
				}
				else{ PermErrorchn(@event); }
			}

			// !CalcA - Calculates with Wolfram Alpha
			else if(CommandChecker(@event, arg, "CalcA")){
				if(!CheckPerm(@event.getUser(), 0)) return;
				// The WAEngine is a BOT_FACTORY for creating WAQuery objects,
				// and it also used to perform those queries. You can set properties of
				// the WAEngine (such as the desired API output format types) that will
				// be inherited by all WAQuery objects created from it. Most applications
				// will only need to crete one WAEngine object, which is used throughout
				// the life of the application.
				WAEngine engine;
				try{ engine = new WAEngine(); }
				catch(Exception e){
					SendError(@event, e);
					return;
				}

				// These properties will be set in all the WAQuery objects created from this WAEngine.
				engine.setAppID(AppId);
				engine.addFormat("plaintext");

				// Create the query.
				var query = engine.createQuery();

				// Set properties of the query.
				query.setInput(ArgJoiner(arg, 1));

				try{
					// For educational purposes, print out the URL we are about to send:
					Logger.Debug("Query URL:" + engine.toURL(query));

					// This sends the URL to the Wolfram|Alpha server, gets the XML result
					// and parses it into an object hierarchy held by the WAQueryResult object.
					var queryResult = engine.performQuery(query);

					if(queryResult.isError()){
						Logger.Error("Query error");
						Logger.Error("  error code: " + queryResult.getErrorCode());
						Logger.Error("  error message: " + queryResult.getErrorMessage());
					}
					else if(!queryResult.isSuccess()){
						SendMessage(@event, "Query was not understood; no results available.");
						Logger.Warn("Query was not understood; no results available.");
					}
					else{
						// Got a result.
						Logger.Debug("Successful query. Pods follow:\n");
						byte results = 0;
						var backupResults = new List<string>();
						foreach(var pod in queryResult.getPods()){
							if(pod.isError()) continue;
							Logger.Debug("pod start: " + pod.getTitle());
							var solutions = "";
							foreach(var subPod in pod.getSubpods()){
								foreach(var element in subPod.getContents()){
									if(!(element is WAPlainText)) continue;
									Logger.Debug("subpod start");
									var elementResult = ((WAPlainText) element).getText()
										.replace('\uF7D9', '=')
										.replace("\uF74E", "\u001Di\u001D");
									if(LilGUtil.containsAny(pod.getTitle(), "Result", "Exact result", "Decimal approximation")
									   && results < 2){
										SendMessage(@event, pod.getTitle() + ": " + elementResult);
										results++;
									}
									else if(LilGUtil.equalsAny(pod.getTitle(), "Solution", "Complex solution", "Roots", "Complex roots")){
										if(solutions.isEmpty()){ solutions = pod.getTitle() + ": " + elementResult; }
										else{ solutions += " or " + elementResult; }
									}
									else if(LilGUtil.containsAny(pod.getTitle(),
										"Alternate form assuming ",
										"Alternate form",
										"Alternative representations",
										"Input interpretation")){ backupResults.Add(pod.getTitle() + ": " + elementResult); }
									else{ Logger.Debug(pod.getTitle() + ": " + elementResult); }
									Logger.Debug("end of sub pod");
								}
							}
							if(!solutions.isEmpty()){
								SendMessage(@event, solutions);
								results++;
							}
							Logger.Debug("End of pod");
						}
						if(results < 2){
							if(results == 0 && backupResults.Count == 0){ SendMessage(@event, "Sorry, no result was found"); }
							else{
								foreach(var backupResult in backupResults){
									if(results < 2){
										SendMessage(@event, backupResult);
										results++;
									}
									break;
								}
							}
						}
						// We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
						// These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
					}
				}
				catch(WAException e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			// !CalcJ - calculate a expression
			else if(CommandChecker(@event, arg, "CalcJ")){
				var args = FormatstringArgs(arg);
				var parser = ArgumentParsers.newArgumentParser("CalcJ")
					.description("Calculates an expression")
					.defaultHelp(true);
				parser.addArgument("expression")
					.nargs("*")
					.help("The expression to evaluate");
				parser.addArgument("-v", "--Val")
					.type(typeof(double))
					.setDefault(-1.0)
					.help("Sets what the variable starts at");
				parser.addArgument("-c", "--char")
					.type(typeof(string))
					.setDefault("x")
					.help("Sets what character the variable is");
				parser.addArgument("-s", "--step")
					.type(typeof(double))
					.setDefault(1.0)
					.help("Sets How much to increase x at");
				parser.addArgument("-a", "--amount")
					.type(typeof(sbyte))
					.setDefault(3)
					.help("Sets How many times to increase");
				parser.addArgument("-p", "--precision")
					.type(typeof(long))
					.setDefault(64L)
					.help("Sets what precision to calculate to");
				Namespace ns;
				try{
					ns = parser.parseArgs(args);
					Logger.Debug(ns.toString());
					Evaluator.setPrecision(ns.getLong("precision").longValue());
					if(LilGUtil.containsAny(message, "-v", "-c", "-s", "-a", "--Val", "--char", "--step", "--amount")){
						var x = ns.getDouble("Val").doubleValue();
						var step = ns.getDouble("step").doubleValue();
						var calcAmount = ns.getByte("amount").byteValue();
						if(calcAmount > 5 && !CheckPerm(@event.getUser(), 8)){ calcAmount = 5; }
						var count = 0;
						var eval = new List<Apfloat>();
						while(count <= calcAmount){
							VariableSet.set((string) ns.get("char"), new Apfloat(x));
							//noinspection SuspiciousToArrayCall
							eval.Add(Evaluator.evaluate(ArgJoiner(ns.getList("expression").toList<string>().ToArray(), 0).toLowerCase(),
								VariableSet) as Apfloat);
							x += step;
							count++;
						}
						SendMessage(@event, eval.toString().replace("[", "").replace("]", "").replace(", ", " | "));
					}
					else{
						//noinspection SuspiciousToArrayCall
						var expression = ns.getList("expression").toList<string>().ToArray();
						var eval = Evaluator.evaluate(ArgJoiner(expression, 0, 0)) as Apfloat;
						var df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
						df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
						df.setMaximumIntegerDigits(340);
						SendMessage(@event, df.format(eval));
					}
				}
				catch(ArgumentParserException e){ SendCommandHelp(@event, parser); }
				catch(Exception e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			// !pix - shows everyone the real face
			else if(CommandChecker(@event, arg, "pix")){
				if(CheckPerm(@event.getUser(), int.MaxValue)){
					if(GetArg(arg, 1) != null){
						var toggle = GetArg(arg, 1).toLowerCase()[0];
						switch(toggle){
							case '0':
							case 'n':
								_updateAvatar = false;
								SendMessage(@event, "Avatar updates are now off");
								break;
							case '1':
							case 'y':
								_updateAvatar = true;
								SendMessage(@event, "Avatar updates are now on");
								break;
						}
					}
					if(GetArg(arg, 1).equalsIgnoreCase("upload") && CheckPerm(@event.getUser(), 1)){
						SendFile(@event, DiscordAdapter.avatarFile, "[Me]");
					}
				}
				else{ SendMessage(@event, _avatar + " [Me]"); }
				AddCooldown(@event.getUser());
			}

			// !Git - gets the link to source code
			else if(CommandChecker(@event, arg, "Git")){
				SendMessage(@event, "Link to source code: https://github.com/lilggamegenuis/FozruciX");
			}

			// !Bugs - gets the link to issues
			else if(CommandChecker(@event, arg, "Bugs")){
				SendMessage(@event, "Link to issue tracker: https://github.com/lilggamegenuis/FozruciX/issues");
			}

			// !vgm - links to my New mix tapes :V
			else if(CommandChecker(@event, arg, "vgm")){
				SendMessage(@event, "Link to My smps music: https://drive.google.com/open?id=0B3aju_x5_V--ZjAyLWZEUnV1aHc");
			}

			// !cleanMarkov - cleans duplicates from markov chain list
			/*else if (CommandChecker(@event, arg, "cleanMarkov")) {
			    var amountCleared = 0;
			    for(var i = 0; i < MarkovChain.Values.Count; i++) {
			        LilGUtil.removeDuplicates(ref MarkovChain.Values.ToList()[i]);
			        MarkovChain.Values.ToList()[i] = new List<string>();
			        amountCleared++;
			    }
			    SendMessage(@event, "Cleared " + amountCleared);
			}*/

			// !GC - Runs the garbage collector
			else if(CommandChecker(@event, arg, "GC")){
				var num = LilGUtil.gc();
				if(num == 1){ SendMessage(@event, "Took out the trash"); }
				else{ SendMessage(@event, "Took out " + num + " Trash bags"); }
			}

			// !JS - evaluates a expression in JavaScript
			/*else if (CommandChecker(@event, arg, "JS")) {
			    var args = FormatstringArgs(LilGUtil.splitMessage(message, 0, false));
			    var parser = ArgumentParsers.newArgumentParser("JS")
			            .description("Calculates an expression")
			            .defaultHelp(true);
			    parser.addArgument("expression").nargs("*")
			            .help("The expression to evaluate");
			    parser.addArgument("-b", "--base", "-r", "--radix").type(typeof(int)).setDefault(10)
			            .help("Sets what radix to output to. Only applies if output is numeric");
			    parser.addArgument("-k", "--kill").type(typeof(bool)).action(Arguments.storeTrue())
			            .help("Kill the thread");
			    Namespace ns;
			    try {
			        ns = parser.parseArgs(args);
			        Logger.Debug(ns.toString());
			        if (ns.getBoolean("kill").booleanValue()) {
			            //noinspection deprecation
			            _js.Interrupt();
			            _js = null;
			            SendMessage(@event, "JavaScript Thread killed", false);
			        } else {
			            if (GetArg(arg, 1) != null) {
			                /*Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
			                    if (ex is ThreadDeath) {
			                        lastJsChannel.send().message(@event.getUser(), "JavaScript thread killed by " + lastJsUser.getNick() + " in " + @event.getChannel().getName());
			                    } else {
			                        sendError(@event, ex);
			                    }
			                };* /
			                if (_js == null) {
			                    //noinspection SuspiciousToArrayCall
			                    _js = new JavaScript(@event, ArgJoiner(ns.getList("expression").toList<string>().ToArray(), 0, 0), ns.getInt("base").intValue());
			                    //js.setUncaughtExceptionHandler(exceptionHandler);
			                    _js.Start();
			                } else {
			                    //noinspection SuspiciousToArrayCall
			                    _js.RunNewJavaScript(@event, ArgJoiner(ns.getList("expression").toList<string>().ToArray(), 0, 0), ns.getInt("base").intValue());
			                }
			                Logger.Debug(ns.getString("expression"));
			                _lastJsUser = @event.getUserHostmask();
			                if (channel != null) {
			                    _lastJsChannel = @event.getChannel();
			                }
			            } else {
			                SendMessage(@event, "Requires more arguments");
			            }
			        }
			    } catch (ArgumentParserException e) {
			        SendCommandHelp(@event, parser);
			    } catch (Exception e) {
			        SendError(@event, e);
			    }
			    AddCooldown(@event.getUser());

			}

// !py - Evaluates python code
			else if (CommandChecker(@event, arg, "py")) {
			    var args = FormatstringArgs(LilGUtil.splitMessage(message, 0, false));
			    var parser = ArgumentParsers.newArgumentParser("py")
			            .description("Calculates an expression")
			            .defaultHelp(true);
			    parser.addArgument("expression").nargs("*")
			            .help("The expression to evaluate");
			    parser.addArgument("-b", "--base", "-r", "--radix").type(typeof(int)).setDefault(10)
			            .help("Sets what radix to output to. Only applies if output is numeric");
			    parser.addArgument("-k", "--kill").type(typeof(bool)).action(Arguments.storeTrue())
			            .help("Kill the thread");
			    Namespace ns;
			    try {
			        ns = parser.parseArgs(args);
			        Logger.Debug(ns.toString());
			        if (ns.getBoolean("kill").booleanValue()) {
			            //noinspection deprecation
			            _py.Interrupt();
			            _py = null;
			            SendMessage(@event, "Python Thread killed", false);
			        } else {
			            if (GetArg(arg, 1) != null) {
			                /*Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
			                    if (ex is ThreadDeath) {
			                        lastJsChannel.send().message(@event.getUser(), "Python thread killed by " + lastJsUser.getNick() + " in " + @event.getChannel().getName());
			                    } else {
			                        sendError(@event, ex);
			                    }
			                };* /
			                if (_py == null) {
			                    //noinspection SuspiciousToArrayCall
			                    _py = new Python();
			                    //py.setUncaughtExceptionHandler(exceptionHandler);
			                    _py.Start();
			                }
			                //noinspection SuspiciousToArrayCall
			                _py.RunNewPython(@event, ArgJoiner(ns.getList("expression").toList<string>().ToArray(), 0, 0), ns.getInt("base").intValue());

			                Logger.Debug(ns.getString("expression"));
			                _lastJsUser = @event.getUserHostmask();
			                if (channel != null) {
			                    _lastJsChannel = @event.getChannel();
			                }
			            } else {
			                SendMessage(@event, "Requires more arguments");
			            }
			        }
			    } catch (ArgumentParserException e) {
			        SendCommandHelp(@event, parser);
			    } catch (Exception e) {
			        SendError(@event, e);
			    }
			    AddCooldown(@event.getUser());

			}*/

			// if someone tells the bot to "Go to hell" do this
			else if(message.contains(_bot.getNick()) && message.toLowerCase().contains("go to hell")){
				if(!CheckPerm(@event.getUser(), 9001)){
					SendMessage(@event, "I Can't go to hell, i'm all out of vacation days", false);
				}
			}

			// !count - counts amount of something
			else if(CommandChecker(@event, arg, "count")){
				if(CheckPerm(@event.getUser(), 1)){
					if(GetArg(arg, 1) != null && GetArg(arg, 1).equalsIgnoreCase("setup")){
						_counter = GetArg(arg, 2);
						if(GetArg(arg, 3) != null){ _counterCount = Convert.ToInt32(GetArg(arg, 3)); }
					}
					if(CommandChecker(@event, arg, "count")){
						_counterCount++;
						SendMessage(@event, "Number of times that " + _counter + " is: " + _counterCount, false);
					}
					AddCooldown(@event.getUser());
				}
				else{ PermErrorchn(@event); }
			}

			// !stringToBytes - convert a string into a Byte array
			else if(CommandChecker(@event, arg, "stringToBytes")){
				try{ SendMessage(@event, LilGUtil.getBytes(ArgJoiner(arg, 1))); }
				catch(ArrayIndexOutOfBoundsException e){ SendMessage(@event, "Not enough args. Must provide a string"); }
				AddCooldown(@event.getUser());
			}

			// !LookUpWord - Looks up a word in the Wiktionary
			else if(CommandChecker(@event, arg, "LookupWord")){
				try{
					var lookedUpWord = "Null";
					Logger.Debug("Looking up word");
					// Connect to the Wiktionary database.
					Logger.Debug("Opening DICTIONARY");
					var wkt = JWKTL.openEdition(WiktionaryDirectory);
					Logger.Debug("Getting page for word");
					var page = wkt.getPageForWord(GetArg(arg, 1));
					if(page != null){
						Logger.Debug("Getting entry");
						IWiktionaryEntry entry;
						if(GetArg(arg, 2) != null && LilGUtil.isNumeric(GetArg(arg, 2))){
							entry = page.getEntry(Convert.ToInt32(GetArg(arg, 2)));
						}
						else{ entry = page.getEntry(0); }
						Logger.Debug("getting sense");
						var sense = entry.getSense(1);
						Logger.Debug("getting Plain text");
						if(GetArg(arg, 2) != null){
							var subCommandNum = 2;
							if(LilGUtil.isNumeric(GetArg(arg, 2))){ subCommandNum++; }
							if(arg.Length > subCommandNum + _arrayOffset && arg[subCommandNum - 1].equalsIgnoreCase("Example")){
								if(sense.getExamples().size() > 0){ lookedUpWord = ((IWikiString) sense.getExamples().get(0)).getPlainText(); }
								else{ SendMessage(@event, "No examples found"); }
							}
							else{ lookedUpWord = sense.getGloss().getPlainText(); }
						}
						else{ lookedUpWord = sense.getGloss().getPlainText(); }
						Logger.Debug("Sending message");
						SendMessage(@event,
							!lookedUpWord.isEmpty() ? lookedUpWord : "Empty response from Database");
					}
					else{ SendMessage(@event, "That page couldn't be found."); }

					// Close the database connection.
					wkt.close();
				}
				catch(Exception e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			//!lookup - Looks up something in Wikipedia
			else if(CommandChecker(@event, arg, "Lookup")){
				if(GetArg(arg, 1) != null){ SendMessage(@event, "You forgot a param ya dingus"); }
				else
					try{
						string[] listOfTitlestrings = {ArgJoiner(arg, 1)};
						var user = new info.bliki.api.User("", "", "http://en.wikipedia.org/w/api.php");
						user.login();
						var pages = user.queryContent(listOfTitlestrings).toList<Page>();
						var found = false;
						while(pages.Count > 0){
							var page = pages[0];
							if(page.toString().contains("#REDIRECT")){
								Logger.Debug("Found redirect");
								var link = page.toString();
								link = link.substring(link.indexOf("[[") + 2, link.indexOf("]]"));
								Logger.Debug("Going to " + link);
								pages = user.queryContent(new[]{link}).toList<Page>();
								continue;
							}
							found = true;
							var wikiModel = new WikiModel("${image}", "${title}");
							var plainStr = page.toString();
							var related = new List<string>();
							if(plainStr.contains(" may refer to:")){
								Logger.Debug("Found disambiguation page");
								var strings = new List<string>(plainStr.split("[\n]"));
								var category = true;
								for(var i = 0; strings.Count > i; i++){
									Logger.Trace((string) strings[i]);
									if(LilGUtil.wildCardMatch((string) strings[i], "==*==")){ category = false; }
									else if(!category){
										if(!((string) strings[i]).isEmpty()){ related.Add((string) strings[i]); }
										else if(((string) strings[i + 1]).isEmpty()){ category = true; }
									}
								}
							}
							if(related != null){
								plainStr = page.getTitle() + " may refer to: ";
								for(var i = 0; i < related.Count && i < 5; i++){
									plainStr += ((string) related[i]).replace("* ", "").replace("*", "") + "; ";
								}
								var lastIndex = plainStr.lastIndexOf(",");
								if(lastIndex != -1) plainStr = plainStr.substring(0, lastIndex);
							}
							else{ plainStr = page.getCurrentContent(); }
							plainStr = wikiModel.render(new PlainTextConverter(), plainStr);
							if(related == null){
								Logger.Debug(plainStr);
								var charIndex = StringUtils.ordinalIndexOf(plainStr, ".", 2);
								if(charIndex == -1){ charIndex = plainStr.indexOf("."); }
								if(charIndex != -1){ plainStr = plainStr.substring(0, charIndex); }
							}
							var pattern = Pattern.compile("[^.]*");
							var matcher = pattern.matcher(plainStr);
							if(matcher.find()){
								plainStr = plainStr.replaceAll("\\{\\{[^\\}]+\\}\\}", "");
								if(plainStr.isEmpty()){ SendMessage(@event, "That page couldn't be found."); }
								else{ SendMessage(@event, plainStr + "."); }
							}
							break;
						}
						if(!found){ SendMessage(@event, "That page couldn't be found."); }
					}
					catch(IndexOutOfBoundsException e){
						e.printStackTrace();
						SendError(@event, new Exception("Error getting data, please try again"));
					}
					catch(Exception e){ SendError(@event, e); }
				AddCooldown(@event.getUser());
			}

			// !chat - chat's with a internet conversation bot
			else if(CommandChecker(@event, arg, "chat")){
				if(GetArg(arg, 1).equalsIgnoreCase("clever")){
					if(!_bools[CleverBotInt]){
						try{
							_chatterBotSession = BotFactory.create(ChatterBotType.CLEVERBOT).createSession();
							_bools[CleverBotInt] = true;
							//noinspection ConstantConditions
							@event.getUser().send().notice("CleverBot started");
						}
						catch(Exception e){ SendMessage(@event, "Error: Could not create clever bot session. Error was: " + e); }
					}
					try{ SendMessage(@event, " " + BotTalk("clever", ArgJoiner(arg, 2))); }
					catch(Exception e){ SendMessage(@event, "Error: Problem with bot. Error was: " + e); }
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("pandora")){
					if(!_bools[PandoraBotInt]){
						try{
							_pandoraBotSession = BotFactory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
							_bools[PandoraBotInt] = true;
							//noinspection ConstantConditions
							@event.getUser().send().notice("PandoraBot started");
						}
						catch(Exception e){ SendMessage(@event, "Error: Could not create pandora bot session. Error was: " + e); }
					}
					try{ SendMessage(@event, " " + BotTalk("pandora", ArgJoiner(arg, 2))); }
					catch(Exception e){ SendMessage(@event, "Error: Problem with bot. Error was: " + e); }
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("jabber")){
					if(!_bools[JabberBotInt]){
						try{
							_jabberBotSession = BotFactory.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
							_bools[JabberBotInt] = true;
							//noinspection ConstantConditions
							@event.getUser().send().notice("PandoraBot started");
						}
						catch(Exception e){ SendMessage(@event, "Error: Could not create pandora bot session. Error was: " + e); }
					}
					try{ SendMessage(@event, " " + BotTalk("clever", ArgJoiner(arg, 1))); }
					catch(Exception e){ SendError(@event, e); }
				}
				AddCooldown(@event.getUser());
			}

			// !temp - Converts a unit of temperature to another
			else if(CommandChecker(@event, arg, "temp")){
				var temp = Convert.ToInt32(GetArg(arg, 3));
				double ans = 0;
				var unit = "err";
				if(GetArg(arg, 1).equalsIgnoreCase("F")){
					if(GetArg(arg, 2).equalsIgnoreCase("C")){
						ans = (temp - 32) * 5 / 9;
						unit = "C";
					}
					else if(GetArg(arg, 2).equalsIgnoreCase("K")){
						ans = (temp - 32) * 5 / 9 + 273.15;
						unit = "K";
					}
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("C")){
					if(GetArg(arg, 2).equalsIgnoreCase("F")){
						ans = (temp * 9 / 5) + 32;
						unit = "F";
					}
					else if(GetArg(arg, 2).equalsIgnoreCase("K") && temp < 0){
						ans = temp + 273.15;
						unit = "K";
					}
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("K")){
					if(GetArg(arg, 2).equalsIgnoreCase("F")){
						ans = (temp - 273.15) * 9 / 5 + 32;
						unit = "F";
					}
					else if(GetArg(arg, 2).equalsIgnoreCase("C")){
						ans = temp - 273.15;
						unit = "C";
					}
				}
				if(unit.equalsIgnoreCase("err")){ SendMessage(@event, "Incorrect arguments."); }
				else{ SendMessage(@event, " " + ans + unit); }
				AddCooldown(@event.getUser());
			}

			// !BlockConv - Converts blocks to bytes
			else if(CommandChecker(@event, arg, "BlockConv")){
				var data = Convert.ToInt32(GetArg(arg, 3));
				double ans = 0;
				var unit = "err";
				var notify = true;
				var blocks = 128;
				if(GetArg(arg, 1).equalsIgnoreCase("blocks")){
					if(GetArg(arg, 2).equalsIgnoreCase("kb")){
						ans = blocks * data;
						unit = "KB";
						notify = false;
					}
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("kb")){
					if(GetArg(arg, 2).equalsIgnoreCase("blocks")){
						ans = data / blocks;
						unit = "Blocks";
						notify = false;
					}
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("mb")){
					if(GetArg(arg, 2).equalsIgnoreCase("blocks")){
						var blocksMb = 8 * blocks;
						ans = data / blocksMb;
						unit = "Blocks";
					}
				}
				else if(GetArg(arg, 1).equalsIgnoreCase("gb")){
					if(GetArg(arg, 2).equalsIgnoreCase("blocks")){
						var blocksGb = 8192 * blocks;
						ans = data / blocksGb;
						unit = "Blocks";
					}
				}
				if(unit == "err"){ SendMessage(@event, "Incorrect arguments."); }
				else{
					SendMessage(@event, " " + ans + unit);
					if(notify)
						SendMessage(@event,
							"NOTICE: this command currently doesn't work like it should. The only conversion that works is blocks to kb and kb to blocks");
				}
				AddCooldown(@event.getUser());
			}

			// !FC - Friend code database
			else if(CommandChecker(@event, arg, "FC")){
				try{
					if(GetArg(arg, 2) != null){
						if(GetArg(arg, 1).equalsIgnoreCase("set")){
							if(_fcList.ContainsKey(@event.getUser().getNick().toLowerCase())){
								var fc = GetArg(arg, 2).replaceAll("[^\\d]", "");
								if(fc.length() == 12){
									_fcList[@event.getUser().getNick().toLowerCase()] = fc;
									SendMessage(@event, "FC Edited");
								}
								else{ SendMessage(@event, "Incorrect FC"); }
							}
							else{
								var fc = GetArg(arg, 2).replaceAll("[^\\d]", "");
								if(fc.length() == 12){
									_fcList[@event.getUser().getNick().toLowerCase()] = GetArg(arg, 2).replaceAll("[^\\d]", "");
									SendMessage(@event,
										"Added " + @event.getUser().getNick() + "'s FC to the DB as " + GetArg(arg, 2).replaceAll("[^\\d]", ""));
								}
								else{ SendMessage(@event, "Incorrect FC"); }
							}
						}
					}
					else{
						if(GetArg(arg, 1).equalsIgnoreCase("list")){ SendMessage(@event, _fcList.Keys.toString()); }
						else if(GetArg(arg, 1).equalsIgnoreCase("del")){
							if(_fcList.ContainsKey(@event.getUser().getNick().toLowerCase())){
								_fcList.Remove(@event.getUser().getNick().toLowerCase());
								SendMessage(@event, "Friend code removed");
							}
							else{ SendMessage(@event, "You haven't entered your Friend code yet"); }
						}
						else if(_fcList.ContainsKey(GetArg(arg, 1).toLowerCase())){
							var fc = _fcList[GetArg(arg, 1).toLowerCase()];
							var fcParts = new string[3];
							fcParts[0] = fc.substring(0, 4);
							fcParts[1] = fc.substring(4, 8);
							fcParts[2] = fc.substring(8);
							fc = fcParts[0] + "-" + fcParts[1] + "-" + fcParts[2];
							SendMessage(@event, GetArg(arg, 1) + ": " + fc, false);
						}
						else{ SendMessage(@event, "That user hasn't entered their FC yet", false); }
					}
					AddCooldown(@event.getUser());
				}
				catch(NullPointerException e){
					_fcList = new Dictionary<string, string>();
					SendMessage(@event, "Try the command again");
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !sql - execute sql statements
			else if(CommandChecker(@event, arg, "sql")){
				if(CheckPerm(@event.getUser(), 9001)){ }
				else{ PermErrorchn(@event); }
			}

			// !memes - Got all dem memes
			else if(CommandChecker(@event, arg, "memes")){
				if(GetArg(arg, 1) != null){
					try{
						if(GetArg(arg, 1).equalsIgnoreCase("set")){
							if(_memes.ContainsKey(GetArg(arg, 2).toLowerCase().replace("\u0001", ""))){
								var meme = _memes[GetArg(arg, 2).toLowerCase()];
								if(CheckPerm(@event.getUser(), 9001) || meme.getCreator().equalsIgnoreCase(@event.getUser().getNick())){
									if(GetArg(arg, 3) == null){
										_memes.Remove(GetArg(arg, 2).toLowerCase().replace("\u0001", ""));
										SendMessage(@event, "Meme " + GetArg(arg, 2) + " Deleted!");
									}
									else{
										meme.setMeme(ArgJoiner(arg, 3));
										_memes[GetArg(arg, 2).toLowerCase().replace("\u0001", "")] = meme;
										SendMessage(@event, "Meme " + GetArg(arg, 2) + " Edited!");
									}
								}
								else{ SendMessage(@event, "Sorry, Only the creator of the meme can edit it"); }
							}
							else{
								_memes[GetArg(arg, 2).toLowerCase().replace("\u0001", "")] =
									new Meme(@event.getUser().getNick(), ArgJoiner(arg, 3).replace("\u0001", ""));
								SendMessage(@event, "Meme " + GetArg(arg, 2) + " Created as " + ArgJoiner(arg, 3));
							}
						}
						else if(GetArg(arg, 1).equalsIgnoreCase("list")){ SendMessage(@event, _memes.toString().replace("\u0001", "")); }
						else{
							if(_memes.ContainsKey(GetArg(arg, 1).toLowerCase())){
								SendMessage(@event,
									GetArg(arg, 1).replace("\u0001", "")
									+ ": "
									+ ((Meme) _memes[GetArg(arg, 1).toLowerCase()]).getMeme().replace("\u0001", ""),
									false);
							}
							else{ SendMessage(@event, "That Meme doesn't exist!"); }
						}
					}
					catch(Exception e){ SendError(@event, e); }
					AddCooldown(@event.getUser());
				}
				else{ SendMessage(@event, "Missing arguments"); }
			}

			// !q - adds to q
			else if(CommandChecker(@event, arg, "q")){
				try{
					if(GetArg(arg, 1).equalsIgnoreCase("del")
					   || GetArg(arg, 1).equalsIgnoreCase("pop")
					   || GetArg(arg, 1).equalsIgnoreCase("rem")){
						if(_qList.Remove(@event.getUser().getNick())){ SendMessage(@event, "Removed from Q"); }
						else{ SendMessage(@event, "You weren't in Q!"); }
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("add") || GetArg(arg, 1).equalsIgnoreCase("push")){
						if(_qList.Add(@event.getUser().getNick())){ SendMessage(@event, "Added to Q"); }
						else{ SendMessage(@event, "You are already in Q!"); }
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("list")){
						SendMessage(@event, "Current people in Q: " + _qList.toString().replace("[", "").replace("]", ""));
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("clear")){
						_qList.Clear();
						_qTimer = new StopWatch();
						SendMessage(@event, "Q has been qilled");
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("start")){
						_qTimer = new StopWatch();
						var time = 10;
						if(GetArg(arg, 2) != null){
							time = Convert.ToInt32(GetArg(arg, 2));
							time = time > 60 ? 30 : time;
						}
						SendMessage(@event,
							"READY?!?!1/1 "
							+ (time != 10 ? "starting in " + time + " sec!!!! " : "")
							+ _qList.toString().replace("[", "").replace("]", ""),
							false);
						LilGUtil.pause(time);
						SendMessage(@event, "3", false);
						LilGUtil.pause(1);
						SendMessage(@event, "2", false);
						LilGUtil.pause(1);
						SendMessage(@event, "1", false);
						LilGUtil.pause(1);
						SendMessage(@event, "GO! " + _qList.toString().replace("[", "").replace("]", ""), false);
						_qTimer = new StopWatch();
						_qTimer.start();
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("time")){ SendMessage(@event, "Current time: " + _qTimer.toString()); }
					AddCooldown(@event.getUser());
				}
				catch(InterruptedException ex){ Thread.CurrentThread.Interrupt(); }
				catch(Exception e){ SendError(@event, e); }
			}

			// !NoteJ - Leaves notes
			else if(CommandChecker(@event, arg, "NoteJ")){
				try{
					if(GetArg(arg, 1).equalsIgnoreCase("del")){
						var i = 0;
						var index = -1;
						var found = false;
						while(i < _noteList.Count && !found){
							if((_noteList[i]).id.toString() == GetArg(arg, 2)){
								found = true;
								index = i;
							}
							else{ i++; }
						}
						if(found){
							if(@event.getUser().getNick().equalsIgnoreCase(_noteList[index].sender) || CheckPerm(@event.getUser(), 9001)){
								_noteList.RemoveAt(index);
								SendMessage(@event, "Note " + GetArg(arg, 2) + " Deleted");
							}
							else{
								SendMessage(@event,
									"Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it");
							}
						}
						else{ SendMessage(@event, "That ID wasn't found."); }
					}
					else if(GetArg(arg, 1).equalsIgnoreCase("list")){
						var i = 0;
						List<string> found = new List<string>();
						List<string> foundUuid = new List<string>();
						while(_noteList.Count > i){
							if(_noteList[i].sender.equalsIgnoreCase(@event.getUser().getNick())){
								found.Add(_noteList[i].getMessageForList());
								foundUuid.Add(_noteList[i].getGuidForList());
							}
							i++;
						}
						SendMessage(@event, found.toString());
						@event.getUser().send().notice(foundUuid.toString());
					}
					else{
						var note = new Note(@event.getUser().getNick(), GetArg(arg, 1), ArgJoiner(arg, 2), channel);
						_noteList.Add(note);
						SendMessage(@event, "Left note \"" + ArgJoiner(arg, 2) + "\" for \"" + GetArg(arg, 1) + "\".", false);
						@event.getUser().send().notice("ID is \"" + _noteList[_noteList.IndexOf(note)].id.toString() + "\"");
					}
					AddCooldown(@event.getUser());
				}
				catch(StringIndexOutOfBoundsException e){ SendMessage(@event, Colors.RED + "You need more parameters ya dingus"); }
				catch(Exception e){ SendError(@event, e); }
			}

			// !Hello - Standard "Hello world" command
			else if(CommandChecker(@event, arg, "hello")){
				SendMessage(@event, "Hello World!");
				AddCooldown(@event.getUser());
			}

			// !Bot - Explains that "yes this is a bot"
			else if(CommandChecker(@event, arg, "bot")){
				SendMessage(@event, "Yes, this is " + _currentUser.getNick() + "'s bot.");
				AddCooldown(@event.getUser());
			}

			// !getName - gets the name of the bot
			else if(CommandChecker(@event, arg, "getName")){
				SendMessage(@event, _bot.getUserBot().getRealName());
				AddCooldown(@event.getUser());
			}

			// !version - gets the version of the bot
			else if(CommandChecker(@event, arg, "version")){
				var version = "PircBotX: "
				              + PircBotX.VERSION
				              + ". BotVersion: "
				              + Version
				              + ". Java version: "
				              + global::java.lang.System.getProperty("java.version");
				SendMessage(@event, "Version: " + version);
				AddCooldown(@event.getUser());
			}

			// !login - attempts to login to NickServ
			else if(CommandChecker(@event, arg, "login")){
				_bot.sendIRC().mode(_bot.getNick(), "+B");
				_bot.sendIRC().identify(CryptoUtil.decrypt(FozConfig.Password));
				_bot.sendRaw().rawLineNow("cs op #Lil-G|bot " + _bot.getNick());
				_bot.sendRaw().rawLineNow("cs op #Lil-G|bot Lil-G");
				_bot.sendRaw().rawLineNow("cs op #SSB " + _bot.getNick());
				_bot.sendRaw().rawLineNow("cs op #SSB Lil-G");
				_bot.sendRaw().rawLineNow("ns recover FozruciX " + CryptoUtil.decrypt(FozConfig.Password));
				AddCooldown(@event.getUser());
			}

			// !getLogin - gets the login of the bot
			else if(CommandChecker(@event, arg, "getLogin")){
				SendMessage(@event, _bot.getUserBot().getLogin());
				AddCooldown(@event.getUser());
			}

			// !getID - gets the ID of the user
			else if(CommandChecker(@event, arg, "getID")){
				SendMessage(@event, "You are :" + @event.getUser().getUserId());
				AddCooldown(@event.getUser());
			}

			// !RandomInt - Gives the user a random Number
			else if(CommandChecker(@event, arg, "RandomInt")){
				int num1, num2;
				if(GetArg(arg, 1) != null && GetArg(arg, 2) != null){
					num1 = Convert.ToInt32(GetArg(arg, 1));
					num2 = Convert.ToInt32(GetArg(arg, 2));
					SendMessage(@event, "" + LilGUtil.randInt(num1, num2));
				}
			}

			// !RandomDec - Gives the user a random Number
			else if(CommandChecker(@event, arg, "RandomDec")){
				double num1, num2;
				if(GetArg(arg, 1) != null && GetArg(arg, 2) != null){
					num1 = Convert.ToDouble(GetArg(arg, 1));
					num2 = Convert.ToDouble(GetArg(arg, 2));
					SendMessage(@event, "" + LilGUtil.randDec(num1, num2));
				}
			}

			// !getState - Displays what version the bot is on
			else if(CommandChecker(@event, arg, "getState")){
				SendMessage(@event, "State is: " + _bot.getState());
				AddCooldown(@event.getUser());
			}

			// !prefix - Changes the command prefix when it isn't the standard "!"
			else if(arg[0].equalsIgnoreCase("!prefix") && !_prefix.Equals("!")){
				if(CheckPerm(@event.getUser(), 9001)){
					if(GetArg(arg, 1) != null){
						_prefix = ArgJoiner(arg, 1);
						SendMessage(@event, "Command variable is now \"" + _prefix + "\"");
					}
					else{ SendMessage(@event, "Command variable is \"" + _prefix + "\""); }
				}
				else{ PermError(@event.getUser()); }
			}

			// !prefix - Changes the command prefix
			else if(CommandChecker(@event, arg, "prefix")){
				if(CheckPerm(@event.getUser(), 9001)){
					_prefix = GetArg(arg, 1);
					if(_prefix.length() > 1
					   && !endsWithAny(_prefix,
						   ".",
						   "!",
						   "`",
						   "~",
						   "@",
						   "-",
						   "/",
						   "*",
						   "&",
						   "^",
						   "%",
						   "$",
						   "#",
						   "+",
						   "_",
						   "?",
						   "\\",
						   ";",
						   ":",
						   "|")){ _arrayOffset = 1; }
					else{ _arrayOffset = 0; }
					SendMessage(@event, "Command variable is now \"" + _prefix + "\"");
				}
				else{ PermError(@event.getUser()); }
			}

			// !highlightAll - Highlights everyone
			else if(CommandChecker(@event, arg, "highlightAll")){
				if(CheckPerm(@event.getUser(), 6)){ SendMessage(@event, ArgJoiner(arg, 1), false); }
				else{ PermErrorchn(@event); }
			}

			// !upload - Uploads a file to discord
			else if(CommandChecker(@event, arg, "upload")){
				if(CheckPerm(@event.getUser(), 9001)){
					if(GetArg(arg, 2) != null){ SendFile(@event, new File(GetArg(arg, 1)), ArgJoiner(arg, 2)); }
					else if(GetArg(arg, 1) != null){ SendFile(@event, new File(GetArg(arg, 1))); }
					else{ SendMessage(@event, "Fail"); }
				}
				else{ PermErrorchn(@event); }
			}

			// !SayThis - Tells the bot to say something
			else if(CommandChecker(@event, arg, "SayThis")){
				if(CheckPerm(@event.getUser(), 5)){ SendMessage(@event, ArgJoiner(arg, 1), false); }
				else{ PermErrorchn(@event); }
			}

			// !makeDebug - reCreates the debug Window
			else if(CommandChecker(@event, arg, "makeDebug")){
				if(CheckPerm(@event.getUser(), 9001)){ MakeDebug(); }
				else{ PermErrorchn(@event); }
			}

			// !makeDiscord - reCreates the discord connection
			else if(CommandChecker(@event, arg, "makeDiscord")){
				if(CheckPerm(@event.getUser(), 9001)){
					try{ MakeDiscord(); }
					catch(Exception e){ SendError(@event, e); }
				}
				else{ PermErrorchn(@event); }
			}

			// !LoopSay - Tells the bot to say something and loop it
			else if(CommandChecker(@event, arg, "LoopSay")){
				if(CheckPerm(@event.getUser(), 9001)){
					var i = Convert.ToInt32(GetArg(arg, 1));
					var loopCount = 0;
					try{
						while(i > loopCount){
							SendMessage(@event, ArgJoiner(arg, 2), false);
							loopCount++;
						}
					}
					catch(Exception e){ SendError(@event, e); }
				}
				else{ PermErrorchn(@event); }
			}

			// !ToSciNo - converts a number to scientific notation
			else if(CommandChecker(@event, arg, "ToSciNo")){
				NumberFormat formatter = new DecimalFormat("0.######E0");

				var num = Convert.ToInt64(GetArg(arg, 1));
				try{ SendMessage(@event, formatter.format(num)); }
				catch(Exception e){
					SendError(@event, e);
					//log(e.toString());
				}
				AddCooldown(@event.getUser());
			}

			// !S1TCG - Create Title card info
			else if(CommandChecker(@event, arg, "S1TCG")){
				if(GetArg(arg, 1) != null){
					try{
						List<string> title = s1tcg.S1TCG.Process(FormatstringArgs(arg));
						if(title.Count < 7){ foreach(var aTitle in title){ SendNotice(@event, @event.getUser().getNick(), aTitle); } }
						else{ SendPage(@event, arg, new List<string>(title)); }
						AddCooldown(@event.getUser());
					}
					catch(Exception e){ SendError(@event, e); }
				}
				else{
					SendNotice(@event, @event.getUser().getNick(), "Incorrect arguments");
					GetHelp(@event, "s1tcg");
				}
			}

			// !acc/68kcyc/asmcyclecounter - counts asm cycles
			else if(CommandChecker(@event, arg, "acc")
			        || CommandChecker(@event, arg, "68kcyc")
			        || CommandChecker(@event, arg, "asmcyclecounter")){
				try{
					var asm = ArgJoiner(arg, 1).replace("||", "\r\n\t").replace("//", "\r\n\t");
					var process = new System.Diagnostics.Process{
						StartInfo = {
							RedirectStandardOutput = true,
							RedirectStandardError = true,
							CreateNoWindow = true
						}
					};
					if(!IsLinux){
						process.StartInfo.FileName = "asmcyclecount/asmCycleCount.exe";
						process.StartInfo.Arguments = "t t \t" + asm;
					}
					else{
						process.StartInfo.FileName = "mono";
						process.StartInfo.Arguments = "asmcyclecount/asmCycleCount.exe t t \t" + asm;
					}
					process.Start();
					List<string> output = process.StandardOutput.ReadToEnd().Split('\n').ToList();
					process.WaitForExit();
					output = new List<string>(output.GetRange(0, output.Count / 2));
					Logger.Debug(output.toString());
					if(output.Count > 3){ SendPage(@event, arg, output); }
					else{ foreach(string anOutput in output){ SendMessage(@event, anOutput.replace('\t', ' ').replace(";", "  ;")); } }
					AddCooldown(@event.getUser());
				}
				catch(Exception e2){ SendError(@event, e2); }
			}

			// !disasm - disassembles machine code for the specified CPU
			else if(CommandChecker(@event, arg, "disasm")){
				var byteStr = ArgJoiner(arg, 2).replace(" ", "");
				try{
					var processor = GetArg(arg, 1).toLowerCase();
					var pb = new ProcessBuilder("rasm2", "-a", processor, "-d", byteStr);
					pb.redirectErrorStream(true);
					var process = pb.start();
					var disasm = new BufferedReader(new InputStreamReader(process.getInputStream()));
					//noinspection StatementWithEmptyBody
					process.waitFor();
					string disasmTemp;
					var stdoutWasEmpty = true;
					var messagesToSend = new List<string>();
					while((disasmTemp = disasm.readLine()) != null){
						Logger.Debug("disasm: %s", disasmTemp);
						messagesToSend.Add(disasmTemp);
						if(stdoutWasEmpty){ stdoutWasEmpty = false; }
					}
					disasm.close();
					if(stdoutWasEmpty){
						SendMessage(@event, "Processor is either not supported or some other error has occurred: No Data in stdout");
					}
					else{
						if(messagesToSend.Count > 3){ SendPage(@event, arg, messagesToSend); }
						else{ foreach(var aMessagesToSend in messagesToSend){ SendMessage(@event, aMessagesToSend); } }
					}
					AddCooldown(@event.getUser());
				}
				catch(IllegalArgumentException e){
					SendMessage(@event, "Arguments have to be a Hexadecimal number: " + e.getCause());
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !Trans - Translate from 1 language to another
			else if(CommandChecker(@event, arg, "trans")){
				string text;
				var args = FormatstringArgs(arg);
				var parser = ArgumentParsers.newArgumentParser("Trans")
					.description("Translates from one language to another")
					.defaultHelp(true);
				parser.addArgument("text")
					.nargs("*")
					.help("Text to translate");
				parser.addArgument("-t", "--to")
					.type(typeof(string))
					.setDefault("English")
					.help("Sets language to translate to");
				parser.addArgument("-f", "--from")
					.type(typeof(string))
					.setDefault("detect")
					.help("Sets language to translate from");
				parser.addArgument("-d", "--detect")
					.type(typeof(bool))
					.action(Arguments.storeTrue())
					.help("Kill the thread");
				Namespace ns;
				try{
					ns = parser.parseArgs(args);
					Logger.Debug(ns.toString());
					if(ns.getBoolean("detect").booleanValue()){
						SendMessage(@event, FullNameTostring(Detect.execute(ns.getString("text"))));
					}
					else{
						//noinspection SuspiciousToArrayCall
						var textToTrans = ArgJoiner(ns.getList("text").toList<string>().ToArray(), 0);
						Language to = Language.valueOf(ns.getString("to").toUpperCase());
						Language from;
						from = ns.getString("from").Equals("detect")
							? Detect.execute(textToTrans)
							: Language.valueOf(ns.getString("from").toUpperCase());
						text = Translate.execute(textToTrans, from, to);
						Logger.Debug("Translating: " + text);
						SendMessage(@event, text);
					}
					AddCooldown(@event.getUser());
				}
				catch(IllegalArgumentException e){ SendError(@event, new Exception("That Language doesn't exist!")); }
				catch(ArgumentParserException e){ SendCommandHelp(@event, parser); }
				catch(IOException e){
					SendError(@event, new Exception("IOException! try again"));
					Logger.Error("Trans error", e);
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !BadTrans - Translate from english to.... english... badly
			else if(CommandChecker(@event, arg, "BadTrans")){
				try{
					if(GetArg(arg, 1) != null){
						var text = ArgJoiner(arg, 1);
						global::System.Console.WriteLine("Translating: " + text + " - ");
						text = Translate.execute(text, Language.ENGLISH, Language.JAPANESE);
						global::System.Console.WriteLine("Translating: " + text + " - ");
						text = Translate.execute(text, Language.JAPANESE, Language.VIETNAMESE);
						global::System.Console.WriteLine("Translating: " + text + " - ");
						text = Translate.execute(text, Language.VIETNAMESE, Language.CHINESE);
						global::System.Console.WriteLine("Translating: " + text + " - ");
						text = Translate.execute(text, Language.CHINESE, Language.ENGLISH);
						Logger.Debug("Translating: " + text);
						SendMessage(@event, text);
					}
					else{ SendMessage(@event, ">_>"); }
					AddCooldown(@event.getUser());
				}
				catch(IllegalArgumentException e){ SendError(@event, new Exception("That class doesn't exist!")); }
				catch(IOException e){
					SendError(@event, new Exception("IOException! try again"));
					Logger.Error("Trans error", e);
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !DebugVar - changes a variable to the value
			else if(CommandChecker(@event, arg, "DebugVar")){
				if(CheckPerm(@event.getUser(), 9001)){
					switch(GetArg(arg, 1).toLowerCase()){ //Make sure strings are lowercase
						case "i":
							var i = Convert.ToInt32(GetArg(arg, 2));
							SendMessage(@event, "DEBUG: Var \"i\" is now \"" + i + "\"");
							break;
						case "jokenum":
							_jokeCommandDebugVar = Convert.ToInt32(GetArg(arg, 2));
							SendMessage(@event, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + _jokeCommandDebugVar + "\"");
							break;
					}
				}
				else{ PermErrorchn(@event); }
			}

			// !cmd - Tells the bot to run a OS command
			else if(CommandChecker(@event, arg, "cmd")){
				if(CheckPerm(@event.getUser(), 9001)){
					try{
						if(GetArg(arg, 1).equalsIgnoreCase("stop")){
							SendMessage(@event, "Stopping");
							_singleCmd.Interrupt();
						}
						else{
							try{
								_singleCmd = new CMD(@event, arg.Skip(1).ToArray());
								_singleCmd.Start();
							}
							catch(Exception e){ SendError(@event, e); }
						}
					}
					catch(Exception e){ SendError(@event, e); }
				}
				else{ PermError(@event.getUser()); }
			}

			// > - runs COMMANDS without closing at the end
			else if(arg[0].startsWith(_consolePrefix)){
				if(CheckPerm(@event.getUser(), 9001)){
					if(arg[0].substring(_consolePrefix.length()).equalsIgnoreCase(_consolePrefix + "start")){
						_terminal.Interrupt();
						_terminal = new CommandLine(@event, arg.Skip(1).ToArray());
						_terminal.Start();
						if(!_terminal.isAlive()){ SendMessage(@event, "Command line started", false); }
					}
					else if(arg[0].substring(_consolePrefix.length()).equalsIgnoreCase(_consolePrefix + "close")){
						_terminal.doCommand(@event, "exit");
					}
					else if(arg[0].substring(_consolePrefix.length()).equalsIgnoreCase(_consolePrefix + "stop")){
						_terminal.Interrupt();
					}
					else if(arg[0].substring(_consolePrefix.length()).equalsIgnoreCase(_consolePrefix + "prefix")){
						_consolePrefix = arg[1];
						SendMessage(@event, "Console Prefix is now " + _consolePrefix);
					}
					else if(arg[0].equalsIgnoreCase(">_>")){
						// do nothing
					}
					else{
						var command = message.substring(1);
						_terminal.doCommand(@event, command);
						Logger.Debug("Running " + command);
					}
				}
			}
			else if(arg[0].equalsIgnoreCase(">>prefix")){
				if(!CheckPerm(@event.getUser(), 9001)) return;
				_consolePrefix = arg[1];
				SendMessage(@event, "Console Prefix is now " + _consolePrefix);
			}

			// !SayRaw - Tells the bot to send a raw line
			else if(CommandChecker(@event, arg, "SayRaw")){
				if(CheckPerm(@event.getUser(), 9001)){ _bot.sendRaw().rawLineNow(ArgJoiner(arg, 1)); }
				else{ PermErrorchn(@event); }
			}

			// !SayNotice - Tells the bot to send a notice
			else if(CommandChecker(@event, arg, "SayNotice")){
				if(CheckPerm(@event.getUser(), 6)){ _bot.sendIRC().notice(GetArg(arg, 1), ArgJoiner(arg, 2)); }
				else{ PermErrorchn(@event); }
			}

			// !SayCTCPCommand - Tells the bot to send a CTCP Command
			else if(CommandChecker(@event, arg, "SayCTCPCommand")){
				if(CheckPerm(@event.getUser(), 9001)){ _bot.sendIRC().ctcpCommand(GetArg(arg, 1), ArgJoiner(arg, 2)); }
				else{ PermErrorchn(@event); }
			}

			//// !SayMethod - Tells the bot to run a method
			//		if (arguments[0].equalsIgnoreCase(prefix + "sayMethod")){
			//			if(checkPerm(@event.getUser())){
			//				sendRawLineViaQueue(arguments[1]);
			//			}
			//			else {
			//				permErrorchn(@event, "can use this command");
			//			}
			//		}

			// !leave - Tells the bot to leave the current channel
			else if(CommandChecker(@event, arg, "leave")){
				if(CheckPerm(@event.getUser(), 5)){
					if(!CommandChecker(@event, arg, "leave")){ @event.getChannel().send().part(ArgJoiner(arg, 2)); }
					else{ @event.getChannel().send().part("Ugh... Why do i always get the freaks..."); }
				}
				else if(Network != Network.Twitch){ PermErrorchn(@event); }
			}

			// !ReVoice - gives everyone voice if they didn't get it
			else if(CommandChecker(@event, arg, "ReVoice")){
				foreach(User user1 in @event.getChannel().getUsers()){ user1.send().mode("+v"); }
			}

			// !kill - Tells the bot to disconnect from server and exit
			else if(CommandChecker(@event, arg, "kill")){
				if(CheckPerm(@event.getUser(), 9001)){
					//noinspection ConstantConditions
					SaveData();
					@event.getUser().send().notice("Disconnecting from server and exiting");
					try{
						var exit = new Thread(() => {
							if(GetArg(arg, 1) != null){ _manager.stop(ArgJoiner(arg, 1)); }
							else{ _manager.stop("I'm only a year old and have already wasted my entire life."); }
							try{
								LilGUtil.pause(1);
								global::java.lang.System.exit(0);
							}
							catch(Exception e){ e.printStackTrace(); }
						});
						exit.Start();
						//noinspection StatementWithEmptyBody
						pause(5);
					}
					catch{ }
					Environment.Exit(0);
				}
				else{ PermErrorchn(@event); }
			}

			// !quitServ - Tells the bot to disconnect from server
			else if(CommandChecker(@event, arg, "quitServ")){
				if(CheckPerm(@event.getUser(), 9001)){
					//noinspection ConstantConditions
					SaveData();
					@event.getUser().send().notice("Disconnecting from server");
					if(GetArg(arg, 1) != null){ _bot.sendIRC().quitServer(ArgJoiner(arg, 1)); }
					else{ _bot.sendIRC().quitServer("I'm only a year old and have already wasted my entire life."); }
					_bot.stopBotReconnect();
				}
				else{ PermErrorchn(@event); }
			}

			// !respawn - Tells the bot to restart and reconnect
			else if(CommandChecker(@event, arg, "respawn")){
				if(CheckPerm(@event.getUser(), 5)){
					SaveData();
					_bot.sendIRC().quitServer("Died! Respawning in about 5 seconds");
				}
				else{ PermErrorchn(@event); }
			}

			// !recycle - Tells the bot to part and rejoin the channel
			else if(CommandChecker(@event, arg, "recycle")){
				if(CheckPerm(@event.getUser(), 2)){
					SaveData();
					@event.getChannel().send().cycle();
					AddCooldown(@event.getUser());
				}
				else{ PermErrorchn(@event); }
			}

			// !getUserLevels - gets the user levels of the user
			else if(CommandChecker(@event, arg, "getUserLevels")){
				if(channel == null){ return; }
				try{
					List<UserLevel> userLevels = @event.getUser().getUserLevels(@event.getChannel()).toList<UserLevel>();
					SendMessage(@event, userLevels.toString());
					AddCooldown(@event.getUser());
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !getCpu - Gets info about CPU
			else if(CommandChecker(@event, arg, "getCpu")){
				try{
					string processorTime = java.lang.String.format("%03f", getProcessCpuLoad());
					SendMessage(@event, "Processor time: " + processorTime);
					AddCooldown(@event.getUser());
				}
				catch(Exception e){ SendError(@event, e); }
			}

			// !getBat - Gets info about battery
			/*else if (CommandChecker(@event, arg, "getBat")) {
			    try {
			        if (!IsLinux) {
			            string[] statuses = {"discharging",
			                    "The system has access to AC so no battery is being discharged. However, the battery is not necessarily charging.",
			                    "fully charged",
			                    "low",
			                    "critical",
			                    "charging",
			                    "charging and high",
			                    "charging and low",
			                    "charging and critical",
			                    "UNDEFINED",
			                    "partially charged"};
			            var batteryStatus = Convert.ToInt32(getWMIValue("Select BatteryStatus from Win32_Battery", "BatteryStatus"));
			            string batteryPercentRemaining = getWMIValue("Select EstimatedChargeRemaining from Win32_Battery", "EstimatedChargeRemaining");
			            SendMessage(@event, "Remaining battery: " + batteryPercentRemaining + "% Battery status: " + statuses[batteryStatus]);
			        } else {
			            //todo
			        }
			        AddCooldown(@event.getUser());
			    } catch (Exception e) {
			        SendError(@event, e);
			    }
			}*/

			// !getMem - Gets various info about memory
			else if(CommandChecker(@event, arg, "getMem")){
				var runtime = Runtime.getRuntime();
				var send = "Current memory usage: "
				           + LilGUtil.formatFileSize(runtime.totalMemory() - runtime.freeMemory())
				           + "/"
				           + LilGUtil.formatFileSize(runtime.totalMemory())
				           + ". Total memory that can be used: "
				           + LilGUtil.formatFileSize(runtime.maxMemory())
				           + ".  Active Threads: "
				           + System.Diagnostics.Process.GetCurrentProcess().Threads.Count
				           + "/"
				           + ManagementFactory.getThreadMXBean().getThreadCount()
				           + ".  Available Processors: "
				           + runtime.availableProcessors();
				SendMessage(@event, send, false);
				AddCooldown(@event.getUser());
			}

			// !formatBytes -
			else if(CommandChecker(@event, arg, "formatBytes")){
				SendMessage(@event, LilGUtil.formatFileSize(Convert.ToInt64(GetArg(arg, 1))));
				AddCooldown(@event.getUser());
			}

			// !getDiscordStatus - does what it says
			else if(CommandChecker(@event, arg, "getDiscordStatus")){
				try{ SendMessage(@event, DiscordAdapter.getJda().getStatus().toString(), false); }
				catch(Exception e){ SendError(@event, e); }
			}

			// !ChangeNick - Changes the nick of the bot
			else if(CommandChecker(@event, arg, "changeNick")){
				if(CheckPerm(@event.getUser(), 9001)){
					if(@event is DiscordMessageEvent){
						var guild = ((DiscordMessageEvent) @event).getDiscordEvent().getGuild();
						guild.getController().setNickname(guild.getMember(DiscordAdapter.getJda().getSelfUser()), GetArg(arg, 1)).queue();
					}
					else{
						_bot.sendIRC().changeNick(GetArg(arg, 1));
						_debug.CurrentNick = GetArg(arg, 1);
					}
				}
				else{ PermErrorchn(@event); }
			}

			// !SayAction - Makes the bot do a action
			else if(CommandChecker(@event, arg, "SayAction")){
				if(CheckPerm(@event.getUser(), 9001)){ @event.getChannel().send().action(ArgJoiner(arg, 1)); }
				else{ PermErrorchn(@event); }
			}

			// !jToggle - toggle joke COMMANDS
			else if(CommandChecker(@event, arg, "jToggle")){
				if(GetArg(arg, 1).equalsIgnoreCase("toggle")){
					if(CheckPerm(@event.getUser(), 2)){
						// ReSharper disable once AssignmentInConditionalExpression
						if(_bools[JokeCommands] = !_bools[JokeCommands]){ SendMessage(@event, "Joke COMMANDS are now enabled"); }
						else{ SendMessage(@event, "Joke COMMANDS are now disabled"); }
					}
					else{ PermErrorchn(@event); }
				}
				else{
					if(_bools[JokeCommands]){ SendMessage(@event, "Joke COMMANDS are currently enabled"); }
					else{ SendMessage(@event, "Joke COMMANDS are currently disabled"); }
				}
			}

			// !sudo/make me a sandwich - You should already know this joke
			else if(CommandChecker(@event, arg, "make me a sandwich")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){
					SendMessage(@event, "No, make one yourself", false);
					AddCooldown(@event.getUser());
				}
				else{ SendMessage(@event, " Sorry, Joke COMMANDS are disabled"); }
			}
			else if(CommandChecker(@event, arg, "sudo make me a sandwich")){
				if(CheckPerm(@event.getUser(), 9001)){
					SendMessage(@event, "Ok", false);
					AddCooldown(@event.getUser());
				}
				else{ SendMessage(@event, "This command requires root permissions"); }
			}

			// !Splatoon - Joke command - ask the splatoon question
			else if(CommandChecker(@event, arg, "Splatoon")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){
					SendMessage(@event, " YOU'RE A KID YOU'RE A SQUID");
					AddCooldown(@event.getUser());
				}
				else{ SendMessage(@event, " Sorry, Joke COMMANDS are disabled"); }
			}

			// !attempt - Joke command - NOT ATTEMPTED
			else if(CommandChecker(@event, arg, "attempt")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){
					SendMessage(@event, " NOT ATTEMPTED");
					AddCooldown(@event.getUser());
				}
				else{ SendMessage(@event, " Sorry, Joke COMMANDS are disabled"); }
			}

			// !potato - Joke command - say "i am potato" in Japanese
			else if(CommandChecker(@event, arg, "potato")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){ SendMessage(@event, "わたしわポタトデス"); }
				else SendMessage(@event, " Sorry, Joke COMMANDS are disabled");
			}

			// !WhatIs? - Joke command -
			else if(CommandChecker(@event, arg, "WhatIs?")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){
					var num = LilGUtil.randInt(0, Dictionary.Count() - 1);
					string comeback = java.lang.String.format(Dictionary[num], ArgJoiner(arg, 1));
					SendMessage(@event, comeback);
					AddCooldown(@event.getUser());
				}
				else SendMessage(@event, " Sorry, Joke COMMANDS are disabled");
			}

			// !rip - Joke command - never forgetti the spaghetti
			else if(CommandChecker(@event, arg, "rip")){
				if(_bools[JokeCommands] || CheckPerm(@event.getUser(), 1)){
					if(GetArg(arg, 1).equalsIgnoreCase(_currentUser.getNick())){
						SendMessage(@event, _currentUser.getNick() + " Will live forever!", false);
					}
					else if(GetArg(arg, 1).equalsIgnoreCase(_bot.getNick())){ SendMessage(@event, ">_>", false); }
					else{
						SendMessage(@event, "Rest in spaghetti, never forgetti. May the pasta be with " + ArgJoiner(arg, 1), false);
					}
				}
				else SendMessage(@event, " Sorry, Joke COMMANDS are disabled");
			}

			// s/*/*[/g] - sed
			else if(arg[0].toLowerCase().startsWith("s/")){
				if(channel == null){
					SendMessage(@event, "This is for channels");
					return;
				}
				var map = _allowedCommands[GetSeverName(@event, true)];
				List<string> commands;
				if(map != null){ commands = map[channel]; }
				else{ commands = null; }
				if(commands != null && commands.Contains("sed")){
					SendNotice(@event, @event.getUser().getNick(), "Sorry, you can't use that command here");
				}
				else{
					var msg = message.split("/");
					if(msg.Length > 2){
						if(!msg[1].isEmpty() || !msg[1].Equals(".")){
							var find = msg[1];
							var replace = msg[2];
							var replaceAll = msg.Length > 3 && msg[3].toLowerCase().startsWith("g");
							for(var i = _lastEvents.Count - 1; i >= 0; i--){
								MessageEvent last = _lastEvents;
								if(last.Equals(@event) || LilGUtil.wildCardMatch(last.getMessage(), "s/*/*")) continue;
								if(last.getChannel().Equals(@event.getChannel())){
									var lastMessage = last.getMessage();
									if(message.contains(find)){
										if(replaceAll){ lastMessage = lastMessage.replace(find, replace); }
										else{ lastMessage = lastMessage.replaceFirst(find, replace); }
										SendMessage(@event, "What " + last.getUser().getNick() + " meant to say was: " + lastMessage, false, false);
										AddCooldown(@event.getUser(), 15);
										return;
									}
								}
							}
						}
						else{ SendMessage(@event, "Sorry, we ain't having none of that spam stuff"); }
					}
				}
			}
			else if(message.startsWith(_bot.getNick())
			        || @event is DiscordMessageEvent
			        && ((DiscordMessageEvent) @event).getDiscordEvent()
			        .getMessage()
			        .isMentioned(DiscordAdapter.getJda().getSelfUser()
			        )
			){
				try{ SendMessage(@event, BotTalk("clever", message)); }
				catch(Exception e){ e.printStackTrace(); }
			}
		}


		private bool IsBot(MessageEvent @event){
			if(Network == Network.Discord && ((DiscordMessageEvent) @event).getDiscordEvent().getAuthor().isBot()){
				return true;
			}
			if(LilGUtil.equalsAnyIgnoreCase(@event.getUser().getNick(),
				"aqua-sama",
				"regume-chan",
				"Sylphy",
				"Dick-Cord",
				"Rakka" // list of known bots
			)){ return true; }
			return false;
		}

		private string DoChatFunctions(string message){
			if(!LilGUtil.wildCardMatch(message, "[$*(*)]")){ return message; }
			var chatFunctions = LilGUtil.splitMessage(message, 0, false);
			var returnStr = new StringBuilder();
			foreach(var possibleFunction in chatFunctions){
				var function = "";
				if(CheckChatFunction(possibleFunction, "char")){
					var sub = GetChatArgs(possibleFunction)[0];
					var charVal = Convert.ToInt32(sub);
					var character = (char) charVal;
					function = character + "";
				}
				else if(CheckChatFunction(possibleFunction, "size")){
					var sub = GetChatArgs(possibleFunction)[0];
					var longVal = Convert.ToInt64(sub);
					function = LilGUtil.formatFileSize(longVal);
				}
				returnStr.append(function).append(" ");
			}
			return returnStr.substring(0, returnStr.length() - 1);
		}


		public void OnPart(PartEvent part){ Log(part); }


		public void OnPrivateMessage(PrivateMessageEvent pm){
			var arg = LilGUtil.splitMessage(pm.getMessage());

			// !rps - Rock! Paper! ehh you know the rest
			/*if (CommandChecker(pm, arg, "rps")) {
			    //noinspection ConstantConditions
			    var nick = pm.getUser().getNick();
			    if (CheckPerm(pm.getUser(), 9001)) {
			        var found = false;
			        var isFirstPlayer = true;
			        RpsGame game;
			        var i = 0;
			        for (; i > RpsGames.Count; i++) {
			            if (RpsGames[i].isInGame(nick)) {
			                found = true;
			                game = RpsGames[i];
			                isFirstPlayer = game.isFirstPlayer(nick);
			                break;
			            }
			        }
			        if (found) {
			            if (GetArg(arg, 1) != null) {
			                switch (GetArg(arg, 1)) {
			                    case "r":
			                        if (isFirstPlayer) {
			                            game.setP1Choice(1);
			                        } else {
			                            game.setP2Choice(1);
			                        }
			                        break;
			                }
			            }
			        } else if (GetArg(arg, 1) != null && !(GetArg(arg, 1).equalsIgnoreCase("r") || GetArg(arg, 1).equalsIgnoreCase("p") || GetArg(arg, 1).equalsIgnoreCase("s"))) {
			            RpsGames.Add(new RpsGame(pm.getUser().getNick(), GetArg(arg, 1)));
			            pm.getUser().send().notice("Created a game with " + GetArg(arg, 1));
			        } else {
			            pm.getUser().send().notice("You aren't in a game!");
			        }
			    }
			}
	// !rejoin - Rejoins all channels
			else */
			if(pm.getMessage().equalsIgnoreCase(_prefix + "rejoin")){
				var autoChannels = _bot.getConfiguration().getAutoJoinChannels().keySet().asList().toList<string>();
				foreach(string t in autoChannels){ _bot.send().joinChannel(t); }
			}

			// !login - Sets the authed named to the new name ...if the password is right
			else if(CommandChecker(pm, arg, "login")){
				if(CryptoUtil.encrypt(GetArg(arg, 1)).Equals(FozConfig.Password)){
					_currentUser = pm.getUser();
					if(Network == Network.Discord){
						_currentUser = new DiscordUser(pm.getUserHostmask(),
							((DiscordPrivateMessageEvent) pm).getDiscordEvent().getAuthor(),
							null);
					}
					SendNotice(pm, "Welcome back Lil-G");
				}
				else{ SendNotice(pm, "password is incorrect."); }
			}
			//Only allow me (Lil-G) to use PM COMMANDS except for the login command
			else if(CheckPerm(pm.getUser(), 6)){
				// !SendTo - Tells the bot to say something on a channel
				if(CommandChecker(pm, arg, "SendTo")){ SendPrivateMessage(pm, GetArg(arg, 1), GetScramble(ArgJoiner(arg, 2))); }

				// !sendAction - Tells the bot to make a action on a channel
				else if(CommandChecker(pm, arg, "sendAction")){
					var actionTags = Network == Network.Discord ? "__" : "";
					SendPrivateMessage(pm, GetArg(arg, 1), actionTags + GetScramble(ArgJoiner(arg, 2)) + actionTags);
				}
				// !sendRaw - Tells the bot to say a raw line
				else if(CommandChecker(pm, arg, "sendRaw") && CheckPerm(pm.getUser(), 9001)){
					_bot.sendRaw().rawLineNow(ArgJoiner(arg, 1));
				}

				// !part - leaves a channel
				else if(CommandChecker(pm, arg, "part")){
					if(Network != Network.Discord){
						if(GetArg(arg, 2) != null){ _bot.sendRaw().rawLineNow("part " + GetArg(arg, 1) + " :" + ArgJoiner(arg, 2)); }
						else{ _bot.sendRaw().rawLineNow("part " + GetArg(arg, 1)); }
						SendNotice(pm, "Successfully Disconnected from " + GetArg(arg, 1));
					}
					else{ SendMessage(pm, "I can't leave channels in discord dumbass"); }
				}

				// !ChangeNick- Changes the nick of the bot
				else if(CommandChecker(pm, arg, "changeNick") && CheckPerm(pm.getUser(), 9001)){
					_bot.sendIRC().changeNick(ArgJoiner(arg, 1));
					_debug.CurrentNick = (ArgJoiner(arg, 1));
				}

				// !Connect - Tells the bot to connect to specified channel
				else if(CommandChecker(pm, arg, "connect")){
					if(GetArg(arg, 2) != null){ _bot.sendIRC().joinChannel(GetArg(arg, 1), GetArg(arg, 2)); }
					else{ _bot.sendIRC().joinChannel(GetArg(arg, 1)); }
					SendNotice(pm, "Successfully connected to " + GetArg(arg, 1));
				}

				// !QuitServ - Tells the bot to disconnect from server
				else if(CommandChecker(pm, arg, "QuitServ") && CheckPerm(pm.getUser(), int.MaxValue)){
					SendNotice(pm, "Disconnecting from server");
					_bot.sendIRC()
						.quitServer(GetArg(arg, 1) != null
							? ArgJoiner(arg, 1)
							: "I'm only a year old and have already wasted my entire life.");
					try{ LilGUtil.pause(10); }
					catch(Exception e){ e.printStackTrace(); }

					Environment.Exit(0);
				}
			}
			if(!pm.getMessage().contains(CryptoUtil.decrypt(FozConfig.Password))){ Log(pm); }
			if(pm.getMessage().startsWith(_prefix)
			   || pm.getMessage().startsWith(_consolePrefix)
			   || pm.getMessage().startsWith(pm.getBot().getNick())){
				DoCommand(new MessageEvent(_bot,
					new DiscordChannel(_bot, pm.getUser().getNick()),
					pm.getUser().getNick(),
					pm.getUserHostmask(),
					pm.getUser(),
					pm.getMessage(),
					null));
			}
			else{
				try{ pm.respondWith(BotTalk("clever", pm.getMessage())); }
				catch(Exception e){ e.printStackTrace(); }
			}
			_bools[ArrayOffsetSet] = false;
			_debug.UpdateBot = (_bot);
			CheckNote(pm, pm.getUser().getNick(), null);
			_debug.CurrentNick = (_currentUser.getHostmask());
		}

		public void OnNotice(NoticeEvent @event){
			var message = @event.getMessage();
			//noinspection ConstantConditions
			if(@event.getUser() == null){ return; }
			if(!@event.getUser().getNick().equalsIgnoreCase("NickServ")
			   || !@event.getUser().getNick().equalsIgnoreCase("irc.badnik.net")){
				//noinspection StatementWithEmptyBody
				if(message.contains("*** Found your hostname")
				   || message.contains("Password accepted - you are now recognized.")
				   || message.contains("This nickname is registered and protected.  If it is your")
				   || message.contains("*** You are connected using SSL cipher")
				   || message.contains("please choose a different nick.")
				   || message.contains("nick, type /msg NickServ IDENTIFY password.  Otherwise,")){ }
				else if(message.contains("\u0001AVATAR")){ @event.getUser().send().notice("\u0001AVATAR " + _avatar + "\u0001"); }
				else{
					_bot.sendIRC()
						.notice(_currentUser.getNick(),
							"Got notice from " + @event.getUser().getNick() + ". Notice was : " + @event.getMessage());
				}
			}
			CheckNote(@event, @event.getUser().getNick(), null);
			if(_bot.isConnected()){ _debug.CurrentNick = (_currentUser.getNick()); }
			_debug.UpdateBot = (_bot);
			Log(@event);
		}

		public void OnAction(ActionEvent action){
			//noinspection ConstantConditions
			OnMessage(new MessageEvent(_bot,
					action.getChannel(),
					action.getChannelSource(),
					action.getUserHostmask(),
					action.getUser(),
					action.getAction(),
					null),
				false);
			Log(action);
		}

		public void OnJoin(JoinEvent join){
			var hostmask = join.getUser().getHostmask();
			Logger.Debug("User Joined: " + (hostmask == null ? join.getUser().getNick() : hostmask));
			if(join is DiscordJoinEvent){
				GuildMemberJoinEvent discordJoin = ((DiscordJoinEvent) join).getJoinEvent();
				string channelToMessage = _checkJoinsAndQuits[discordJoin.getGuild().getId()];
				if(channelToMessage != null){
					List<TextChannel> channels = discordJoin.getGuild().getTextChannels().toList<TextChannel>();
					foreach(TextChannel channel in channels){
						if(channel.getId().Equals(channelToMessage)){
							if(discordJoin.getMember().getUser().isBot()){
								channel.sendMessage(
										"aw hell, we got another bot here"
									)
									.queue();
							}
							else{
								channel.sendMessage(
										discordJoin.getMember().getAsMention()
										+ ": Welcome to "
										+ discordJoin.getGuild().getName()
										+ ". Please make sure you check out the #help and #information channel"
									)
									.queue();
							}
						}
					}
				}
			}
			//noinspection ConstantConditions
			Log(join);
			if(join.getChannel() != null){
				if(CheckOp(join.getChannel())){
					//noinspection ConstantConditions
					if(CheckPerm(join.getUser(), 0)){ join.getChannel().send().voice(join.getUserHostmask()); }
				}
				CheckNote(join, join.getUser().getNick(), join.getChannel().getName());
			}
			else{ CheckNote(join, join.getUser().getNick(), null); }
			if(_debug != null){
				_debug.UpdateBot = (_bot);
				_debug.CurrentNick = (_currentUser.getHostmask());
			}
		}

		public void OnNickChange(NickChangeEvent nick){
			if(nick.getNewNick().equalsIgnoreCase(_currentUser.getNick())){
				_currentUser = _bot.getUserBot();
				Logger.Debug("resetting Authed nick");
				_debug.CurrentNick = (_currentUser.getNick());
			}

			/*if (nick.getOldNick().equalsIgnoreCase(currentNick)) {
			    currentNick = nick.getNewNick();
			    //noinspection ConstantConditions
			    currentUsername = nick.getUser().getLogin();
			    currentHost = nick.getUser().getHostname();
			    Logger.Debug("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostname());
			    debug.CurrentNick = (currentNick + "!" + currentUsername + "@" + currentHost);
			}*/
			CheckNote(nick, nick.getNewNick(), null);
			_debug.UpdateBot = (_bot);
			Log(nick);
		}

		public void OnNickAlreadyInUse(NickAlreadyInUseEvent nick){
			_bools[NickInUse] = true;
			nick.respond(nick.getUsedNick() + 1);
		}

		public void OnQuit(QuitEvent quit){
			if(quit.getReason().contains("RECOVER")
			   || quit.getReason().contains("GHOST")
			   || quit.getReason().contains("REGAIN")){ //Recover @event
				_bools[NickInUse] = true;
			}
			if(quit is DiscordQuitEvent){
				string channelToMessage = _checkJoinsAndQuits[((DiscordQuitEvent) quit).getLeaveEvent().getGuild().getId()];
				if(channelToMessage != null){
					List<TextChannel> channels = ((DiscordQuitEvent) quit).getLeaveEvent()
						.getGuild()
						.getTextChannels()
						.toList<TextChannel>();
					foreach(TextChannel channel in channels){
						if(channel.getId().Equals(channelToMessage)){
							channel.sendMessage("User "
							                    + ((DiscordQuitEvent) quit).getLeaveEvent().getMember().getAsMention()
							                    + " Has left the server")
								.queue();
						}
					}
				}
			}
			Log(quit);
		}

		public void OnBan(GuildBanEvent ban){
			string channelToMessage = _checkJoinsAndQuits[ban.getGuild().getId()];
			if(channelToMessage != null){
				List<TextChannel> channels = ban.getGuild().getTextChannels().toList<TextChannel>();
				foreach(TextChannel channel in channels){
					if(channel.getId().Equals(channelToMessage)){
						channel.sendMessage(ban.getUser().getAsMention()
						                    + " Has been b&, ripperoni in pepperoni http://gerbilsoft.soniccenter.org/lol/BAN.jpg")
							.queue();
					}
				}
			}
		}

		public void OnKick(KickEvent kick){
			//noinspection ConstantConditions
			if(kick.getRecipient().getNick().equalsIgnoreCase(_bot.getNick())){
				try{ LilGUtil.pause(5); }
				catch(Exception e){ e.printStackTrace(); }
				_bot.send().joinChannel(kick.getChannel().getName());
			}
			Log(kick);
		}

		public void OnUnknown(UnknownEvent @event){
			var line = @event.getLine();
			if(line.contains("\u0001AVATAR\u0001")){
				//noinspection ConstantConditions
				line = line.substring(line.indexOf(":") + 1, line.indexOf("!"));
				_bot.send().notice(line, "\u0001AVATAR " + _avatar + "\u0001");
			}
			Logger.Debug("Received unknown: " + @event.getLine());
			if(_debug != null){ _debug.CurrentNick = (_currentUser.getNick()); }
		}

		/**
		 * Checks if the user attempting to use the command is allowed
		 *
		 * @param user              User trying to use command
		 * @param requiredUserLevel Required permission level to access command
		 * @return Boolean true if allowed, false if not
		 */
		private bool CheckPerm(User user, int requiredUserLevel){
			if(user.Equals(_currentUser)){ return true; }
			if(_authedUser.Contains(user.getNick())){
				int index = _authedUser.IndexOf(user.getHostmask());
				if(index > -1){ if(_authedUserLevel[index] >= requiredUserLevel){ return true; } }
			}
			else if(user is DiscordUser){
				if(user.getHostname().Equals(_currentUser.getHostname())){ return true; }
				List<Role> roles = ((DiscordUser) user).getGuild()
					.getMember(((DiscordUser) user).getDiscordUser())
					.getRoles()
					.toList<Role>();
				var highestLevel = 0;
				foreach(Role role in roles){
					foreach(Permission perm in role.getPermissions().toList<Permission>()){
						var level = DiscordAdapter.getlevelFromPerm(perm);
						if(level > highestLevel){ highestLevel = level; }
					}
				}
				return highestLevel >= requiredUserLevel;
			}
			else{
				var index = _authedUser.Count - 1;
				while(index > -1){
					string ident = _authedUser[index];
					if(matchHostMask(user.getHostmask(), ident)){ return _authedUserLevel[index] >= requiredUserLevel; }
					index--;
				}
				List<UserLevel> levels = user.getUserLevels(((MessageEvent) _lastEvents).getChannel()).toList<UserLevel>();
				if(requiredUserLevel <= GetUserLevel(levels)){ return true; }
			}
			return false;
		}

		/**
		 * Checks if the user attempting to use the command is allowed
		 *
		 * @param user User trying to use command
		 * @param perm Required permission to access command
		 * @return Boolean true if allowed, false if not
		 */
		private bool CheckPerm(DiscordUser user, Permission perm){
			if(user.Equals(_currentUser)){ return true; }
			var guild = user.getGuild();
			if(guild != null){
				foreach(Role role in guild.getMember(user.getDiscordUser()).getRoles().toList<Role>()){
					if(role.getPermissions().contains(perm)){ return true; }
				}
			}
			return false;
		}

		private void LoadData(){ LoadData(true); }

		private void LoadData(bool writeOnce){
			if(writeOnce && _noteList == null) _noteList = SaveDataStore.getINSTANCE().getNoteList();

			if(writeOnce && _authedUser == null) _authedUser = SaveDataStore.getINSTANCE().getAuthedUser();

			if(writeOnce && _authedUserLevel == null) _authedUserLevel = SaveDataStore.getINSTANCE().getAuthedUserLevel();

			if(writeOnce && _avatar == null) _avatar = SaveDataStore.getINSTANCE().getAvatarLink();

			if(writeOnce && _memes == null) _memes = SaveDataStore.getINSTANCE().getMemes();

			if(writeOnce && _fcList == null) _fcList = SaveDataStore.getINSTANCE().getFCList();

			if(writeOnce && MarkovChain == null) MarkovChain = SaveDataStore.getINSTANCE().getMarkovChain();

			if(writeOnce && _allowedCommands == null) _allowedCommands = SaveDataStore.getINSTANCE().getAllowedCommands();

			if(writeOnce && _checkJoinsAndQuits == null)
				_checkJoinsAndQuits = SaveDataStore.getINSTANCE().getCheckJoinsAndQuits();

			if(writeOnce && _mutedServerList == null) _mutedServerList = SaveDataStore.getINSTANCE().getMutedServerList();

			if(writeOnce && DiscordData.wordFilter == null) DiscordData.wordFilter = SaveDataStore.getINSTANCE().getWordFilter();
		}

		private void CheckNote(Event @event, string user, string channel){
			System.Console.WriteLine("Debug: Starting checkNote -> ");
			try{
				for(var i = 0; i < _noteList.Count; i++){
					System.Console.WriteLine("Checking if " + _noteList[i].receiver + " matches " + user + " -> ");
					if(!wildCardMatch(user.toLowerCase(), _noteList[i].receiver.toLowerCase())) continue;
					System.Console.WriteLine("Found match! -> ");
					if(channel != null){
						try{ SendMessage((MessageEvent) @event, user + ": " + _noteList[i].displayMessage()); }
						catch(ClassCastException){
							//noinspection ConstantConditions
							SendPrivateMessage(((JoinEvent) @event).getUser().getNick(), user + ": " + _noteList[i].displayMessage());
						}
					}
					else{ SendNotice(@event, user, _noteList[i].displayMessage()); }
					_noteList.RemoveAt(i);
					i--;
				}
			}
			catch(Exception e){
				if(@event is JoinEvent){ e.printStackTrace(); }
				else{ SendError(_lastEvents, e); }
			}
			System.Console.WriteLine("| Ending checkNote");
		}

		private void SetDebugInfo(MessageEvent @event){
			_debug.UpdateBot = @event.getBot();
			_debug.CurrentNick = _currentUser.getHostmask();
		}

		public MessageEvent GetLastEvent(){ return _lastEvents; }

		private bool CommandChecker(GenericMessageEvent @event, string[] args, string command){
			return CommandChecker(@event, args, command, true);
		}

		private bool CommandChecker(GenericMessageEvent @event, string[] args, string command, bool printMsg){
			if(@event.getMessage() == null){ return false; }
			try{
				string chanName;
				if(@event is PrivateMessageEvent){ chanName = "PM"; }
				else{ chanName = ((MessageEvent) @event).getChannel().getName(); }
				var isCommand = false;
				if(args.Length < 1) return false;
				if(args[0].startsWith(_prefix)){
					if(_prefix.length() > 1 && !_prefix.endsWith(".")){ isCommand = GetArg(args, 0).equalsIgnoreCase(command); }
					else{ isCommand = args[0].equalsIgnoreCase(_prefix + command); }
				}
				else if(args[0].startsWith(_bot.getNick())){
					SetArrayOffset(args[0] + " ");
					isCommand = args[_arrayOffset].equalsIgnoreCase(command);
				}
				if(isCommand){
					var temp = _allowedCommands[GetSeverName((Event) @event, true)];
					List<string> commands;
					commands = temp != null ? temp[chanName] : null;
					if(commands != null && commands.Contains(command.toLowerCase())){
						if(printMsg){ SendNotice(@event, @event.getUser().getNick(), "Sorry, you can't use that command here"); }
					}
					else{
						var messageEvent = @event as DiscordMessageEvent;
						messageEvent?.getDiscordEvent().getTextChannel().sendTyping();
						Logger.Trace("Found command: " + command);
						return true;
					}
				}
			}
			catch(Exception e){ e.printStackTrace(); }
			return false;
		}

		private void GetHelp(GenericUserEvent @event, string command){
			switch(command.toLowerCase()){
				case "commands":
					SendNotice(@event, @event.getUser().getNick(), "Really? ಠ_ಠ");
					break;
				case "helpme":
					SendNotice(@event,
						@event.getUser().getNick(),
						"Changed to COMMANDS (Except you already know that since you just used it...)");
					break;
				case "time":
					SendNotice(@event, @event.getUser().getNick(), "Displays info from the Date class");
					break;
				case "hello":
					SendNotice(@event, @event.getUser().getNick(), "Just your average \"hello world!\" program");
					break;
				case "RandomInt":
					SendNotice(@event, @event.getUser().getNick(), "Creates a random number between the 2 integers");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage: first number sets the minimum number, second sets the maximum");
					break;
				case "version":
					SendNotice(@event, @event.getUser().getNick(), "Displays the version of the bot");
					break;
				case "stringtobytes":
					SendNotice(@event, @event.getUser().getNick(), "Converts a string into a Byte array");
					break;
				case "temp":
					SendNotice(@event, @event.getUser().getNick(), "Converts a temperature unit to another unit.");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
					break;
				case "chat":
					SendNotice(@event, @event.getUser().getNick(), "This command functions like ELIZA. Talk to it and it talks back.");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky. Second parameter is the Message to send.");
					break;
				case "calcJ":
					SendNotice(@event,
						@event.getUser().getNick(),
						"This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage 1: The simple way is to type out the expression without any VARIABLE_SET. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
					break;
				case "calcjs":
					SendNotice(@event, @event.getUser().getNick(), "Renamed to just \"JS\"");
					goto case "js";
				case "js":
					SendNotice(@event,
						@event.getUser().getNick(),
						"This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
					break;
				case "notej":
					SendNotice(@event, @event.getUser().getNick(), "Allows the user to leave notes");
					SendNotice(@event,
						@event.getUser().getNick(),
						"SubCommand add <Nick to leave note to> <message>: adds a note. SubCommand del <Given ID>: Deletes a set note Usage: . SubCommand list: Lists notes you've left");
					break;
				case "memes":
					SendNotice(@event,
						@event.getUser().getNick(),
						"Meme database. To get a meme you simply have to do \"Memes <meme name>\"");
					SendNotice(@event,
						@event.getUser().getNick(),
						"SubCommand set <Meme Name> <The Meme>: Sets up a meme. Note, When Setting a meme that already exists, you have to be the creator to edit it.  SubCommand list: Lists all the memes in the database");
					break;
				case "disasm":
					SendNotice(@event, @event.getUser().getNick(), "Disassembles bytes from different CPUs");
					SendNotice(@event,
						@event.getUser().getNick(),
						"Usage: 1st param is the CPU to read from. 2nd param is the bytes to assemble. You can use M68k as a shorthand instead of typing 68000. List of available CPUs https://www.hex-rays.com/products/ida/support/idadoc/618.shtml");
					break;
				case "attempt":
					SendNotice(@event,
						@event.getUser().getNick(),
						"Its a inside-joke with my friends in school. If i'm not away, ask me and i'll tell you about it.");
					break;
				case "reverselist":
					SendNotice(@event, @event.getUser().getNick(), "Reverses a list, pretty self explanatory");
					break;
				case "getdate":
					SendNotice(@event, @event.getUser().getNick(), "Gets the date");
					break;
				case "markov":
					SendNotice(@event, @event.getUser().getNick(), "Creates a markov chain from everything seen in chat");
					break;
				case "8ball":
					SendNotice(@event, @event.getUser().getNick(), "Rolls the magic 8Ball");
					break;
				case "checklink":
					SendNotice(@event, @event.getUser().getNick(), "Checks links, what else");
					break;
				case "calca":
					SendNotice(@event, @event.getUser().getNick(), "Currently broken: Calculates math using Wolfram Alpha");
					break;
				case "solvefor":
					SendNotice(@event, @event.getUser().getNick(), "Currently broken: Solves for a equation");
					break;
				case "count":
					SendNotice(@event, @event.getUser().getNick(), "");
					break;
				case "Lookupword":
					SendNotice(@event, @event.getUser().getNick(), "Looks up a word in the DICTIONARY");
					break;
				case "lookup":
					SendNotice(@event, @event.getUser().getNick(), "Looks up a word in the wikipedia");
					break;
				case "blockconv":
					SendNotice(@event, @event.getUser().getNick(), "Converts blocks to a actually known format");
					break;
				case "fc":
					SendNotice(@event, @event.getUser().getNick(), "Stores FC codes in a database so you can retrieve it");
					break;
				case "q":
					SendNotice(@event, @event.getUser().getNick(), "Allows from syncing up something such as sync watching a show");
					break;
				case "toscino":
					SendNotice(@event, @event.getUser().getNick(), "Converts a number to Scientific notation");
					break;
				case "dnd":
					SendNotice(@event, @event.getUser().getNick(), "");
					break;
				case "acc":
				case "68kcyc":
				case "asmcyclecounter":
					SendNotice(@event, @event.getUser().getNick(), "Counts cycles for 68k asm instructions. Use || to separate lines");
					break;
				case "trans":
					SendNotice(@event,
						@event.getUser().getNick(),
						"Translates between languages. -t is to, -f is from, and -d is detect");
					break;
				case "badtrans":
					SendNotice(@event, @event.getUser().getNick(), "Translates between languages... badly...");
					break;
				case "s1tcg":
					SendNotice(@event,
						@event.getUser().getNick(),
						"Generates Title card information for Sonic 1. Use \"-x xpos\" to specify X position, \"-y ypos\" to specify Y position, in hexadecimal. Use \"-l label\" to specify a label.");
					SendNotice(@event, @event.getUser().getNick(), "Example: !s1tcg -x F8 -y F8 -l GreenHillTitle RED HILL");
					break;
				default:
					SendNotice(@event,
						@event.getUser().getNick(),
						"That either isn't a command, or " + _currentUser.getNick() + " hasn't add that to the help yet.");
					break;
			}
		}

		public void SetAvatar(string avatar){
			_avatar = avatar;
			if(!(Network == Network.Discord || Network == Network.Twitch) && _currentUser.getNick() != null && _updateAvatar){
				SendNotice(_currentUser.getNick(), "\u0001AVATAR " + avatar + "\u0001");
			}
		}

		private void AddWords(string phrase){
			if(phrase.startsWith(_prefix) || phrase.startsWith(_consolePrefix)){ return; }
			// put each word into an array
			var words = phrase.split(" ");

			// Loop through each word, check if it's already added
			// if its added, then get the suffix List and add the word
			// if it hasn't been added then add the word to the list
			// if its the first or last word then select the _start / _end key

			for(var i = 0; i < words.Length; i++){
				if(words[i].isEmpty()){ break; }
				// Add the start and end words to their own
				if(i == 0){
					var startWords = MarkovChain["_start"];
					startWords.Add(words[i]);

					var suffix = MarkovChain[words[i]];
					if(suffix != null) continue;
					suffix = new List<string>();
					if(words.Length == 1){ return; }
					suffix.Add(words[i + 1]);
					MarkovChain[words[i]] = suffix;
				}
				else if(i == words.Length - 1){
					var endWords = MarkovChain["_end"];
					endWords.Add(words[i]);
				}
				else{
					var suffix = MarkovChain[words[i]];
					if(suffix == null){
						suffix = new List<string>();
						if(words.Length == 1){ return; }
						suffix.Add(words[i + 1]);
						MarkovChain[words[i]] = suffix;
					}
					else{
						if(words.Length == 1){ return; }
						suffix.Add(words[i + 1]);
						MarkovChain[words[i]] = suffix;
					}
				}
			}
		}

		/*private class Python /*: Thread * / {
		    private MessageEvent _event;
		    private Thread _thread;
		    private string _eval;
		    private int _radix = 10;
		    private ScriptEngine _engine;

		    public Python(){
		        _thread.Name = ("Python thread");
		        _engine = new ScriptEngineManager().getEngineByName("python");
		    }


		    public void Run() {
		        try {
		            var temp = _engine.eval(_eval);
		            if(temp == null) return;
		            var eval = temp.toString();
		            if (isNumeric(eval)) {
		                if (_radix == 10) {
		                    SendMessage(_event, eval);
		                    Logger.Debug("Outputting as decimal");
		                } else {
		                    var basePrefix = "";
		                    if(_radix == 2){ basePrefix = "0b"; }
		                    else if(_radix == 8){ basePrefix = "0"; }
		                    else if(_radix == 16) basePrefix = "0x";
		                    eval = Convert.ToString(Convert.ToInt64(eval), _radix);
		                    if (System.Math.Abs(eval.length()) % 2 == 1) {
		                        eval = "0" + eval;

		                    }
		                    SendMessage(_event, basePrefix + eval);
		                    Logger.Debug("Outputting as base " + _radix);
		                }
		            } else if (eval.length() < 470) {
		                SendMessage(_event, eval);
		            } else {
		                SendPage(_event, new[]{"!PY", this._eval, "" + _radix}, new List<string>(new[]{eval}));
		            }
		        } catch (Exception e) {
		            SendError(_event, e);
		        }
		    }

		    internal void RunNewPython(MessageEvent @event, string code, int @base) {
		        this._event = @event;
		        _eval = code;
		        _radix = @base;
		        Run();
		    }

		}

	public class JavaScript /*: Thread * / {
		    private readonly string[] _unsafeAttributes = {
		            "Java",
		            "JavaImporter",
		            "Packages",
		            "java",
		            "javax",
		            "javafx",
		            "org",
		            "com",
		            "net",
		            "edu",
		            "load",
		            "loadWithNewGlobal",
		            "exit",
		            "quit"
		    };
		    private readonly string[] _unsafeClasses = {
		            "java.lang.reflect",
		            "java.lang.invoke",
		    };

		    private string _factorialFunct = "function fact(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} " +
		            "function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} " +
		            "function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;} " +
		            "function solvefor(expr, solve){var eq = algebra.parse(expr); var ans = eq.solveFor(solve); return solve + \" = \" + ans.toString(); }  var life = 42; " +
		            "function roughSizeOf(e){for(var f=[],o=[e],t=0;o.length;){var n=o.pop();if(\"bool\"==typeof n)t+=4;else if(\"string\"==typeof n)t+=2*n.length;else if(\"number\"==typeof n)t+=8;else if(\"object\"==typeof n&&-1===f.indexOf(n)){f.push(n);for(var r in n)o.push(n[r])}}return t}" +
		            "fish = 4; eight = 6; triangle = 14; leet = 1337;";
		    private MessageEvent _event;
		    private string _arg;
		    private int _radix;
		    private ScriptEngine _botOpEngine;
		    private ScriptEngine _normalUserEngine;

		internal JavaScript( MessageEvent @event, string arg, int radix = 10) {
		        setName("JavaScript Thread");
		        this._event = @event;
		        this._arg = arg;
		        this._radix = radix;
		        _normalUserEngine = new NashornScriptEngineFactory().getScriptEngine(new JsClassFilter());
		        _botOpEngine = new NashornScriptEngineFactory().getScriptEngine();
		        using(InputStreamReader algebra = new InputStreamReader(new FileInputStream("algebra.min.js"))) {
		            _normalUserEngine.eval(_factorialFunct);
		            _normalUserEngine.eval(algebra);
		            ScriptContext context = _normalUserEngine.getContext();
		            int globalScope = context.getScopes().get(0);
		            foreach(string unsafeAttribute in _unsafeAttributes) {
		                context.removeAttribute(unsafeAttribute, globalScope);
		            }
		            _botOpEngine.eval(_factorialFunct);
		            UpdateVariables();
		        }
		    }

		    private void UpdateVariables() {
		        _botOpEngine["@event"] = _event;
		        _botOpEngine["message"] = _event.getMessage();
		        _botOpEngine["channel"] = _event.getChannel();
		        _botOpEngine["user"] = _event.getUser();
		        _botOpEngine["bot"] = _event.getBot();
		        _botOpEngine["hostmask"] = _event.getUserHostmask();
		        _botOpEngine["server"] = _event.getBot().getServerInfo();
		        _botOpEngine["jda"] = DiscordAdapter.getJda();
		        if (_event is DiscordMessageEvent) {
		            _botOpEngine["discordEvent"] = ((DiscordMessageEvent) _event).getDiscordEvent();
		            _botOpEngine["discordChannel"] = ((DiscordMessageEvent) _event).getDiscordEvent().getChannel();
		            _botOpEngine["discordType"] = ((DiscordMessageEvent) _event).getDiscordEvent().getChannelType();
		            _botOpEngine["discordGuild"] = ((DiscordMessageEvent) _event).getDiscordEvent().getGuild();
		            _botOpEngine["discordAuthor"] = ((DiscordMessageEvent) _event).getDiscordEvent().getAuthor();
		            _botOpEngine["discordMember"] = ((DiscordMessageEvent) _event).getDiscordEvent().getMember();
		            _botOpEngine["discordGroup"] = ((DiscordMessageEvent) _event).getDiscordEvent().getGroup();
		        } else {
		            _botOpEngine["discordEvent"] = null;
		            _botOpEngine["discordChannel"] = null;
		            _botOpEngine["discordType"] = null;
		            _botOpEngine["discordGuild"] = null;
		            _botOpEngine["discordAuthor"] = null;
		            _botOpEngine["discordMember"] = null;
		            _botOpEngine["discordGroup"] = null;
		        }
		    }

		internal void RunNewJavaScript(MessageEvent @event, string arg, int radix) {
		        this._event = @event;
		        this._arg = arg;
		        this._radix = radix;
		        UpdateVariables();
		        Run();
		    }


		    public void Run() {
		        try {
		            ScriptEngine engine;
		            if (CheckPerm(_event.getUser(), 9001)) {
		                engine = _botOpEngine;
		                Logger.Debug("Running as op");
		            } else {
		                engine = _normalUserEngine;
		                Logger.Debug("Running as normal user");
		            }

		            Object temp = engine.eval(_arg);
		            if (temp != null) {
		                var eval = temp.toString();
		                if (LilGUtil.isNumeric(eval)) {
		                    if (_radix == 10) {
		                        SendMessage(_event, eval);
		                        Logger.Debug("Outputting as decimal");
		                    } else {
		                        var basePrefix = "";
		                        switch (_radix) {
		                            case 2:
		                                basePrefix = "0b";
		                                break;
		                            case 8:
		                                basePrefix = "0";
		                                break;
		                            case 16:
		                                basePrefix = "0x";
		                                break;
		                        }
		                        eval = long.tostring(Convert.ToInt64(eval), _radix).toUpperCase();
		                        if (Math.abs(eval.length()) % 2 == 1) {
		                            eval = "0" + eval;

		                        }
		                        SendMessage(_event, basePrefix + eval);
		                        Logger.Debug("Outputting as base " + _radix);
		                    }
		                } else if (eval.length() < 470) {
		                    SendMessage(_event, eval);
		                } else {
		                    SendPage(_event, new string[]{"!JS", _arg, "" + _radix}, new List<>(Collections.singletonList(eval)));
		                }
		            }
		        } catch (Exception e) {
		            SendError(_event, e);
		        }
		    }

		    private class JsClassFilter implements ClassFilter {

		        public bool ExposeToScripts( string requestedClass) {
		            for (string unsafeClass : _unsafeClasses) {
		                if (requestedClass.Equals(unsafeClass)) return false;
		            }
		            return true;
		        }
		    }
		}*/
	}
}
