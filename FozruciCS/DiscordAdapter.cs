using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using com.google.common.collect;
using com.mashape.unirest.http;
using FozruciCS.Utils;
using java.lang;
using java.util;
using net.dv8tion.jda.core;
using net.dv8tion.jda.core.entities;
using net.dv8tion.jda.core.events;
using net.dv8tion.jda.core.events.channel.text.update;
using net.dv8tion.jda.core.events.guild;
using net.dv8tion.jda.core.events.guild.member;
using net.dv8tion.jda.core.events.message;
using net.dv8tion.jda.core.hooks;
using net.dv8tion.jda.core.managers;
using NLog;
using org.apache.commons.httpclient.util;
using org.pircbotx;
using org.pircbotx.hooks.events;
using org.pircbotx.snapshot;
using Channel = org.pircbotx.Channel;
using Exception = System.Exception;
using Icon = net.dv8tion.jda.core.entities.Icon;
using String = java.lang.String;
using User = org.pircbotx.User;
using Thread = System.Threading.Thread;

namespace FozruciCS{
	public class DiscordAdapter : ListenerAdapter{
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();
		public static string AvatarFile;
		static FozruciX _bot;
		internal static PircBotX PircBotX;
		private static DiscordAdapter _discordAdapter = null;
		private static ReadyEvent _readyEvent;
		private static JDA _jda;
		private static GameThread _game;
		private static AvatarThread _avatar;

		private DiscordAdapter(PircBotX pircBotX){
			var token = CryptoUtil.decrypt(FozConfig.setPassword(Password.Discord));
			Logger.Trace("Calling JDA Builder with token: " + token);
			_jda = new JDABuilder(AccountType.BOT)
				.setToken(token)
				.setAutoReconnect(true)
				.setAudioEnabled(true)
				.setEnableShutdownHook(true)
				.addEventListener(this)
				.buildBlocking();
			_bot = new FozruciX(FozConfig.Manager, Network.Discord);
			PircBotX = pircBotX;
			_game = new GameThread(_jda.getPresence());
			_game.SetName("Game Setter thread");
			_game.Start();

			_avatar = new AvatarThread(_jda.getSelfUser().getManagerUpdatable(), _bot);
			_avatar.SetName("Avatar Setter thread");
			_avatar.Start();
			Logger.Trace("DiscordAdapter created");
			Logger.Trace("Calling onConnect() method");
			lock(_readyEvent){ _bot.onConnect(new DiscordConnectEvent(pircBotX).SetReadyEvent(_readyEvent)); }
		}

		internal static DiscordAdapter MakeDiscord(PircBotX pircBotX){
			//Logger.setLevel(Level.ALL);
			try{
				Logger.Trace("Making Discord connection");
				if(_discordAdapter == null){
					Logger.Trace("Constructing...");
					Unirest.setTimeouts(10 * 1000, 10 * 1000);
					_discordAdapter = new DiscordAdapter(pircBotX);
				}
			}
			catch(Exception e){ Logger.Error(e, "Error making Discord connection"); }
			return _discordAdapter;
		}

		public static JDA GetJda(){
			return _jda;
		}

		public static int GetlevelFromPerm(Permission perm){
			if(perm == Permission.CREATE_INSTANT_INVITE){ return 0; }
			if(perm == Permission.KICK_MEMBERS){ return 3; }
			if(perm == Permission.BAN_MEMBERS){ return 3; }
			if(perm == Permission.ADMINISTRATOR){ return 3; }
			if(perm == Permission.MANAGE_CHANNEL){ return 4; }
			if(perm == Permission.MANAGE_SERVER){ return 5; }
			if(perm == Permission.MESSAGE_READ){ return 0; }
			if(perm == Permission.MESSAGE_WRITE){ return 0; }
			if(perm == Permission.MESSAGE_TTS){ return 0; }
			if(perm == Permission.MESSAGE_MANAGE){ return 3; }
			if(perm == Permission.MESSAGE_EMBED_LINKS){ return 0; }
			if(perm == Permission.MESSAGE_ATTACH_FILES){ return 0; }
			if(perm == Permission.MESSAGE_HISTORY){ return 0; }
			if(perm == Permission.MESSAGE_MENTION_EVERYONE){ return 0; }
			if(perm == Permission.MESSAGE_EXT_EMOJI){ return 0; }
			if(perm == Permission.VOICE_CONNECT){ return 0; }
			if(perm == Permission.VOICE_SPEAK){ return 0; }
			if(perm == Permission.VOICE_MUTE_OTHERS){ return 3; }
			if(perm == Permission.VOICE_DEAF_OTHERS){ return 3; }
			if(perm == Permission.VOICE_MOVE_OTHERS){ return 3; }
			if(perm == Permission.VOICE_USE_VAD){ return 0; }
			if(perm == Permission.NICKNAME_CHANGE){ return 0; }
			if(perm == Permission.NICKNAME_MANAGE){ return 2; }
			if(perm == Permission.MANAGE_ROLES){ return 5; }
			if(perm == Permission.MANAGE_PERMISSIONS){ return 5; }
			return 0;
		}

		public override void onReady(ReadyEvent event_){
			try{
				lock(event_){
					_readyEvent = event_;
					Logger.Info("Discord is ready");
				}
			}
			catch(Exception e){ Logger.Error(e, "Error OnReady()"); }
		}

		public override void onGuildMemberJoin(GuildMemberJoinEvent join){
			var discordNick = join.getMember().getEffectiveName();
			var discordUsername = join.getMember().getUser().getName();
			var discordHostmask = join.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(PircBotX,
				                        (discordNick ?? discordUsername) + "!" + discordUsername + "@" +
				                        discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", join.getGuild().getName(), discordUserHostmask.getHostmask(), "Joined"));
			_bot.onJoin(new DiscordJoinEvent(PircBotX,
			                                new DiscordChannel(PircBotX, '#' + (join.getGuild().getPublicChannel() as MessageChannel).getName()),
			                                discordUserHostmask,
			                                new DiscordUser(discordUserHostmask, join.getMember().getUser(), join.getGuild()),
			                                join));
		}

		public override void onGuildMemberLeave(GuildMemberLeaveEvent leave){
			var discordNick = leave.getMember().getEffectiveName();
			var discordUsername = leave.getMember().getUser().getName();
			var discordHostmask = leave.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(PircBotX, (discordNick ?? discordUsername) + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", leave.getGuild().getName(), discordUserHostmask.getHostmask(), "Left"));
			_bot.onQuit(new DiscordQuitEvent(PircBotX,
			                                null,
			                                discordUserHostmask,
			                                new UserSnapshot(new DiscordUser(discordUserHostmask,
			                                                                 leave.getMember().getUser(),
			                                                                 leave.getGuild())),
			                                "",
			                                leave));
		}

		public override void onGuildBan(GuildBanEvent ban){
			var discordNick = ban.getUser().getName();
			var discordUsername = discordNick;
			var discordHostmask = ban.getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(PircBotX, (discordNick ?? discordUsername) + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", ban.getGuild().getName(), discordUserHostmask.getHostmask(), "Banned"));
			_bot.onBan(ban);
		}

		public override void onGuildMemberNickChange(GuildMemberNickChangeEvent nick){
			var discordNick = nick.getMember().getEffectiveName();
			var discordUsername = nick.getMember().getUser().getName();
			var discordOldNick = nick.getPrevNick();
			discordOldNick = discordOldNick ?? discordUsername;
			var discordHostmask = nick.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(PircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s]: %s %s %s %s %s",
			                          nick.getGuild().getName(),
			                          discordUserHostmask.getHostmask(),
			                          "Changed nick from",
			                          discordOldNick,
			                          "to",
			                          discordNick));
			_bot.onNickChange(new NickChangeEvent(PircBotX,
			                                     discordOldNick,
			                                     discordNick,
			                                     discordUserHostmask,
			                                     new DiscordUser(discordUserHostmask,
			                                                     nick.getMember().getUser(),
			                                                     nick.getGuild())));
		}

		public override void onMessageReceived(MessageReceivedEvent event_){
			var discordNick = "Error nick";
			var discordUsername = "Error UserName";
			var discordHostmask = "Error Hostmask";
			try{
				discordNick = event_.getMember().getEffectiveName();
				discordUsername = event_.getMember().getUser().getName();
				discordHostmask = event_.getMember().getUser().getId();
			}
			catch(NullPointerException e){
				discordNick = event_.getAuthor().getName();
				discordUsername = discordNick;
				discordHostmask = event_.getAuthor().getId();
			}
			catch(Exception e){ Logger.Error("Error receiving message", e); }
			var discordUserHostmask =
				new DiscordUserHostmask(PircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);

			if(event_.isFromType(ChannelType.PRIVATE)){
				Logger.Info(String.format("[PM] %s: %s", discordUserHostmask.getHostmask(), event_.getMessage().getRawContent()));
				if(!event_.getAuthor().getId().Equals(_jda.getSelfUser().getId())){
					_bot.onPrivateMessage(new DiscordPrivateMessageEvent(PircBotX,
					                                                    discordUserHostmask,
					                                                    new DiscordUser(discordUserHostmask, event_.getAuthor(), null),
					                                                    event_.getMessage().getRawContent(),
					                                                    event_));
				}
			}
			else if(event_.isFromType(ChannelType.TEXT)){
				Logger.Info(String.format("[%s][%s] %s: %s",
				                          event_.getGuild().getName(),
				                          ((MessageChannel)event_.getTextChannel()).getName(),
				                          discordUserHostmask.getHostmask(),
				                          event_.getMessage().getRawContent()));
				if(!event_.getAuthor().getId().Equals(_jda.getSelfUser().getId())){
					_bot.onMessage(new DiscordMessageEvent(PircBotX,
					                                      new DiscordChannel(PircBotX, ((MessageChannel)event_.getTextChannel()).getName())
						                                      .SetChannel(event_.getTextChannel()),
															  ((MessageChannel)event_.getTextChannel()).getName(),
					                                      discordUserHostmask,
					                                      new DiscordUser(discordUserHostmask, event_.getAuthor(), event_.getGuild()),
					                                      event_.getMessage().getRawContent(),
					                                      null,
					                                      event_));
				}
			}
			else{
				//// TODO: 11/13/2016 add settings for group message
			}
		}

		public override void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event_){
			try{
				_bot.onTopic(new DiscordTopicEvent(PircBotX,
				                                  new DiscordChannel(PircBotX, ((MessageChannel)event_.getChannel()).getName()),
				                                  event_.getOldTopic(),
				                                  event_.getChannel().getTopic(),
				                                  new DiscordUserHostmask(PircBotX, ""),
				                                  DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond,
				                                  true));
			}
			catch(Exception e){ Logger.Error("Oh shit, something broke", e); }
		}
	}

	class DiscordMessageEvent : MessageEvent{
		private MessageReceivedEvent _discordEvent;

		internal DiscordMessageEvent(PircBotX bot,
		                             Channel channel,
		                             string channelSource,
		                             UserHostmask userHostmask,
		                             User user,
		                             string message,
		                             ImmutableMap tags,
		                             MessageReceivedEvent discordEvent) : base(bot,
		                                                                       channel,
		                                                                       channelSource,
		                                                                       userHostmask,
		                                                                       user,
		                                                                       message,
		                                                                       tags){
			_discordEvent = discordEvent;
		}

		public MessageReceivedEvent GetDiscordEvent(){
			return _discordEvent;
		}

		public void Respond(string response){
			_discordEvent.getChannel().sendMessage(_discordEvent.getAuthor().getAsMention() + ": " + response).queue();
		}

		public void RespondWith(string fullLine){
			_discordEvent.getChannel().sendMessage(fullLine).queue();
		}
	}

	class DiscordPrivateMessageEvent : PrivateMessageEvent{
		private MessageReceivedEvent _discordEvent;

		internal DiscordPrivateMessageEvent(PircBotX bot,
		                                    UserHostmask userHostmask,
		                                    User user,
		                                    string message,
		                                    MessageReceivedEvent discordEvent) : base(bot, userHostmask, user, message){
			_discordEvent = discordEvent;
		}

		public MessageReceivedEvent GetDiscordEvent(){
			return _discordEvent;
		}

		public void Respond(string response){
			var user = _discordEvent.getAuthor();
			var messageToSend = _discordEvent.getAuthor().getAsMention() + ": " + response;
			if(user.hasPrivateChannel()){ user.getPrivateChannel().sendMessage(messageToSend).queue(); }
			else{
				user.openPrivateChannel().complete();
				user.getPrivateChannel().sendMessage(messageToSend).queue();
			}
		}

		public void RespondWith(string fullLine){
			var user = _discordEvent.getAuthor();
			if(user.hasPrivateChannel()){ user.getPrivateChannel().sendMessage(fullLine).queue(); }
			else{
				user.openPrivateChannel().complete();
				user.getPrivateChannel().sendMessage(fullLine).queue();
			}
		}
	}

	class DiscordChannel : Channel{
		private TextChannel _channel;

		internal DiscordChannel(PircBotX bot, string name) : base(bot, "#" + name){}

		public DiscordChannel SetChannel(TextChannel channel){
			_channel = channel;
			return this;
		}

		public ImmutableSortedSet GetUsers(){
			var discordMembers = _channel.getMembers();
			var users = new ArrayList();
			foreach(var member in discordMembers.toList<Member>()){
				var discordNick = member.getEffectiveName();
				var discordUsername = member.getUser().getName();
				var discordHostmask = member.getUser().getId(); //misnomer but it acts as the same thing
				users.add(new DiscordUser(new DiscordUserHostmask(bot,
				                                                  discordNick + "!" + discordUsername + "@" + discordHostmask),
				                          member.getUser(),
				                          _channel.getGuild()));
			}
			return ImmutableSortedSet.copyOf(users);
		}
	}

	class DiscordConnectEvent : ConnectEvent{
		private ReadyEvent _ready;

		/**
		 * Default constructor to setup object. Timestamp is automatically set to
		 * current time as reported by {@link System#currentTimeMillis() }
		 *
		 * @param bot
		 */
		internal DiscordConnectEvent(PircBotX bot) : base(bot){}

		ReadyEvent GetReadyEvent(){
			return _ready;
		}

		internal DiscordConnectEvent SetReadyEvent(ReadyEvent ready){
			_ready = ready;
			return this;
		}
	}

	class DiscordUser : User{
		private net.dv8tion.jda.core.entities.User _discordUser;
		private Guild _guild = null; //null if PM

		internal DiscordUser(UserHostmask hostmask, net.dv8tion.jda.core.entities.User user, Guild guild) : base(hostmask){
			_discordUser = user;
			_guild = guild;
		}

		public net.dv8tion.jda.core.entities.User GetDiscordUser(){
			return _discordUser;
		}

		public Guild GetGuild(){
			return _guild;
		}

		public bool IsAway(){
			return (_guild != null) && (_guild.getMember(_discordUser).getOnlineStatus() != OnlineStatus.ONLINE);
		}
	}

	class DiscordQuitEvent : QuitEvent{
		private GuildMemberLeaveEvent _leaveEvent;

		public DiscordQuitEvent(PircBotX bot,
		                        UserChannelDaoSnapshot userChannelDaoSnapshot,
		                        UserHostmask userHostmask,
		                        UserSnapshot user,
		                        string reason,
		                        GuildMemberLeaveEvent leave) : base(bot, userChannelDaoSnapshot, userHostmask, user, reason){
			_leaveEvent = leave;
		}

		public GuildMemberLeaveEvent GetLeaveEvent(){
			return _leaveEvent;
		}
	}

	class DiscordJoinEvent : JoinEvent{
		GuildMemberJoinEvent _joinEvent;

		public DiscordJoinEvent(PircBotX bot,
		                        Channel channel,
		                        UserHostmask userHostmask,
		                        User user,
		                        GuildMemberJoinEvent joinEvent) : base(bot, channel, userHostmask, user){
			;
			_joinEvent = joinEvent;
		}

		public GuildMemberJoinEvent GetJoinEvent(){
			return _joinEvent;
		}
	}

	class DiscordUserHostmask : UserHostmask{
		private net.dv8tion.jda.core.entities.User _discordUser;

		internal DiscordUserHostmask(PircBotX bot, string rawHostmask) : base(bot, rawHostmask){}

		public net.dv8tion.jda.core.entities.User GetDiscordUser(){
			return _discordUser;
		}
	}

	class DiscordNickChangeEvent : NickChangeEvent{
		GuildMemberNickChangeEvent _nickChangeEvent;

		public DiscordNickChangeEvent(PircBotX bot,
		                              string oldNick,
		                              string newNick,
		                              UserHostmask userHostmask,
		                              User user,
		                              GuildMemberNickChangeEvent nickChangeEvent) : base(bot,
		                                                                                 oldNick,
		                                                                                 newNick,
		                                                                                 userHostmask,
		                                                                                 user){
			_nickChangeEvent = nickChangeEvent;
		}
	}

	class DiscordTopicEvent : TopicEvent{
		public DiscordTopicEvent(PircBotX bot,
		                         Channel channel,
		                         string oldTopic,
		                         string topic,
		                         UserHostmask user,
		                         long date,
		                         bool changed) : base(bot, channel, oldTopic, topic, user, date, changed){}
	}

	class GameThread{
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();
		private Presence _presence;

		private Thread _thread;

		private GameThread(){}

		internal GameThread(Presence presence){
			_presence = presence;
		}

		public void Run(){
			string[] listOfGames = {
				"With bleach",
				"With fire",
				"With matches",
				"The Bleach Drinking Game",
				"The smallest violin",
				"In the blood of my enemies",
				"On top of the corpses of my enemies",
				"In the trash can where i belong",
				"The waiting game",
				"The game of life",
				"baseball with the head of my enemies",
				"basketball with the head of my enemies",
				"football with the head of my enemies"
			};
			while(true){
				try{
					_presence.setGame(Game.of((listOfGames[LilGUtil.randInt(0, listOfGames.Length - 1)])));
					LilGUtil.pause(LilGUtil.randInt(10, 30));
				}
				//catch(InterruptedException e){ currentThread().interrupt(); }
				catch(Exception e){ Logger.Error(e, "Error in game setter thread"); }
			}
		}

		public void Start(){
			_thread = new Thread(Run);
			_thread.Start();
		}
		public void SetName(string avatarSetterThread){
			if(_thread != null){
				_thread.Name = avatarSetterThread;
			}
		}
	}

	class AvatarThread{
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();

		private Thread _thread;

		private AccountManagerUpdatable _accountManager;
		private FozruciX _bot;

		public AvatarThread(AccountManagerUpdatable accountManager, FozruciX bot){
			_accountManager = accountManager;
			_bot = bot;
		}

		public void Run(){
			var avatarPath = LilGUtil.IsLinux
				                          ? "/media/lil-g/OS/Users/ggonz/Pictures/avatar/burgerpants"
				                          : "C:/Users/ggonz/Pictures/avatar/burgerpants/";
			var tempImage = "./data/temp.png";
			while(true){
				try{
					LilGUtil.pause(LilGUtil.randInt(30, 120));
					var avatarList = Directory.GetFiles(avatarPath);
					var pathArray = new StringBuilder();
						foreach(var anAvatarList in avatarList){
							if(!Directory.Exists(anAvatarList)){
								pathArray.append(anAvatarList);
							}
						}
						Logger.Info("File list: " + pathArray);
						DiscordAdapter.AvatarFile = avatarList[LilGUtil.randInt(0, avatarList.Length - 1)];
						Logger.Info("Picked file: " + DiscordAdapter.AvatarFile);
						var field = _accountManager.getAvatarField();
						try{
							field.shouldUpdate();
							field.setValue(Icon.from(new java.io.File(DiscordAdapter.AvatarFile)));
						}
						catch(Exception e){
							try{
								Image.FromFile(DiscordAdapter.AvatarFile).Save(tempImage, ImageFormat.Png);
								field.setValue(Icon.from(new java.io.File(tempImage)));
							}
							catch(ArrayIndexOutOfBoundsException arrayE){
								arrayE.printStackTrace();
								Logger.Error(Path.GetFullPath(DiscordAdapter.AvatarFile));
							}
						}
						foreach(var pircBotObj in
							FozConfig.Manager.getBots().toArray()){
							var temp = ((PircBotX)pircBotObj).getConfiguration().getListenerManager().getListeners().toArray();
							FozruciX bot = null;
							var index = 0;
							while(index < temp.Length){
								var x = temp[index] as FozruciX;
								if(x != null){
									bot = x;
									break;
								}
								index++;
							}
							bot?.SetAvatar(URIUtil.encodeQuery("https://lilggamegenius.ml/burgerpants/" + new java.io.File(DiscordAdapter.AvatarFile).getName()));
						}
					_accountManager.update(null).queue();
					Logger.Debug("Set Avatar");
				}
				//catch(InterruptedException){ currentThread().interrupt(); }
				catch(Exception e){ Logger.Error(e, "Error in avatar setter thread"); }
			}
			// ReSharper disable once FunctionNeverReturns
		}

		public void SetName(string avatarSetterThread){
			if(_thread != null){
				_thread.Name = avatarSetterThread;
			}
		}

		public void Start(){
			_thread = new Thread(Run);
			_thread.Start();
		}
	}

}
