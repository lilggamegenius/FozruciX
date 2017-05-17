using System;
using System.Collections.Generic;
using com.google.common.collect;
using com.mashape.unirest.http;
using FozruciCS.Utils;
using java.io;
using java.lang;
using java.util;
using javax.imageio;
using lombok;
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
using org.apache.log4j.spi;
using org.pircbotx;
using org.pircbotx.hooks.events;
using org.pircbotx.snapshot;
using Channel = org.pircbotx.Channel;
using Exception = java.lang.Exception;
using Object = java.lang.Object;
using String = java.lang.String;
using User = org.pircbotx.User;

namespace FozruciCS{
	public class DiscordAdapter : ListenerAdapter{
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();
		public static File avatarFile;
		static FozruciX bot;
		internal static PircBotX pircBotX;
		private static DiscordAdapter discordAdapter = null;
		private static ReadyEvent readyEvent;
		private static JDA jda;
		private static GameThread game;
		private static AvatarThread avatar;

		private DiscordAdapter(PircBotX pircBotX){
			var token = CryptoUtil.decrypt(FozConfig.setPassword(Password.Discord));
			Logger.Trace("Calling JDA Builder with token: " + token);
			jda = new JDABuilder(AccountType.BOT)
				.setToken(token)
				.setAutoReconnect(true)
				.setAudioEnabled(true)
				.setEnableShutdownHook(true)
				.addEventListener(this)
				.buildBlocking();
			bot = new FozruciX(FozConfig.getManager(), Network.discord);
			FozruciCS.DiscordAdapter.pircBotX = pircBotX;
			game = new GameThread(jda.getPresence());
			game.setName("Game Setter thread");
			game.start();

			avatar = new AvatarThread(jda.getSelfUser().getManagerUpdatable(), bot);
			avatar.setName("Avatar Setter thread");
			avatar.start();
			Logger.Trace("DiscordAdapter created");
			Logger.Trace("Calling onConnect() method");
			lock(readyEvent){ bot.onConnect(new DiscordConnectEvent(pircBotX).setReadyEvent(readyEvent)); }
		}

		internal static DiscordAdapter makeDiscord(PircBotX pircBotX){
			//Logger.setLevel(Level.ALL);
			try{
				Logger.Trace("Making Discord connection");
				if(discordAdapter == null){
					Logger.Trace("Constructing...");
					Unirest.setTimeouts(10 * 1000, 10 * 1000);
					discordAdapter = new DiscordAdapter(pircBotX);
				}
			}
			catch(Exception e){ e.printStackTrace(); }
			return discordAdapter;
		}

		public static JDA getJda(){
			return jda;
		}

		public static int getlevelFromPerm(Permission perm){
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

		public void onReady(ReadyEvent event_){
			try{
				lock(event_){
					readyEvent = event_;
					Logger.Info("Discord is ready");
				}
			}
			catch(Exception e){ e.printStackTrace(); }
		}

		public void onGuildMemberJoin(GuildMemberJoinEvent join){
			var discordNick = join.getMember().getEffectiveName();
			var discordUsername = join.getMember().getUser().getName();
			var discordHostmask = join.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(pircBotX,
				                        (discordNick ?? discordUsername) + "!" + discordUsername + "@" +
				                        discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", join.getGuild().getName(), discordUserHostmask.getHostmask(), "Joined"));
			bot.onJoin(new DiscordJoinEvent(pircBotX,
			                                new DiscordChannel(pircBotX, '#' + (join.getGuild().getPublicChannel() as MessageChannel).getName()),
			                                discordUserHostmask,
			                                new DiscordUser(discordUserHostmask, join.getMember().getUser(), join.getGuild()),
			                                join));
		}

		public void onGuildMemberLeave(GuildMemberLeaveEvent leave){
			var discordNick = leave.getMember().getEffectiveName();
			var discordUsername = leave.getMember().getUser().getName();
			var discordHostmask = leave.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(pircBotX, (discordNick ?? discordUsername) + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", leave.getGuild().getName(), discordUserHostmask.getHostmask(), "Left"));
			bot.onQuit(new DiscordQuitEvent(pircBotX,
			                                null,
			                                discordUserHostmask,
			                                new UserSnapshot(new DiscordUser(discordUserHostmask,
			                                                                 leave.getMember().getUser(),
			                                                                 leave.getGuild())),
			                                "",
			                                leave));
		}

		public void onGuildBan(GuildBanEvent ban){
			var discordNick = ban.getUser().getName();
			var discordUsername = discordNick;
			var discordHostmask = ban.getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(pircBotX, (discordNick ?? discordUsername) + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s] %s: %s", ban.getGuild().getName(), discordUserHostmask.getHostmask(), "Banned"));
			bot.onBan(ban);
		}

		public void onGuildMemberNickChange(GuildMemberNickChangeEvent nick){
			var discordNick = nick.getMember().getEffectiveName();
			var discordUsername = nick.getMember().getUser().getName();
			var discordOldNick = nick.getPrevNick();
			discordOldNick = discordOldNick != null ? discordOldNick : discordUsername;
			var discordHostmask = nick.getMember().getUser().getId();
			var discordUserHostmask =
				new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);
			Logger.Info(String.format("[%s]: %s %s %s %s %s",
			                          nick.getGuild().getName(),
			                          discordUserHostmask.getHostmask(),
			                          "Changed nick from",
			                          discordOldNick,
			                          "to",
			                          discordNick));
			bot.onNickChange(new NickChangeEvent(pircBotX,
			                                     discordOldNick,
			                                     discordNick,
			                                     discordUserHostmask,
			                                     new DiscordUser(discordUserHostmask,
			                                                     nick.getMember().getUser(),
			                                                     nick.getGuild())));
		}

		public void onMessageReceived(MessageReceivedEvent event_){
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
				new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);

			if(event_.isFromType(ChannelType.PRIVATE)){
				Logger.Info(String.format("[PM] %s: %s", discordUserHostmask.getHostmask(), event_.getMessage().getRawContent()));
				if(!event_.getAuthor().getId().Equals(jda.getSelfUser().getId())){
					bot.onPrivateMessage(new DiscordPrivateMessageEvent(pircBotX,
					                                                    discordUserHostmask,
					                                                    new DiscordUser(discordUserHostmask, event_.getAuthor(), null),
					                                                    event_.getMessage().getRawContent(),
					                                                    event_));
				}
			}
			else if(event_.isFromType(ChannelType.TEXT)){
				Logger.Info(String.format("[%s][%s] %s: %s",
				                          event_.getGuild().getName(),
				                          event_.getTextChannel().getName(),
				                          discordUserHostmask.getHostmask(),
				                          event_.getMessage().getRawContent()));
				if(!event_.getAuthor().getId().Equals(jda.getSelfUser().getId())){
					bot.onMessage(new DiscordMessageEvent(pircBotX,
					                                      new DiscordChannel(pircBotX, event_.getTextChannel().getName())
						                                      .setChannel(event_.getTextChannel()),
					                                      event_.getTextChannel().getName(),
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

		public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event_){
			try{
				bot.onTopic(new DiscordTopicEvent(pircBotX,
				                                  new DiscordChannel(pircBotX, event_.getChannel().getName()),
				                                  event_.getOldTopic(),
				                                  event_.getChannel().getTopic(),
				                                  new DiscordUserHostmask(pircBotX, ""),
				                                  DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond,
				                                  true));
			}
			catch(Exception e){ Logger.Error("Oh shit, something broke", e); }
		}
	}

	class DiscordMessageEvent : MessageEvent{
		private MessageReceivedEvent discordEvent;

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
			this.discordEvent = discordEvent;
		}

		public MessageReceivedEvent getDiscordEvent(){
			return discordEvent;
		}

		public void respond(string response){
			discordEvent.getChannel().sendMessage(discordEvent.getAuthor().getAsMention() + ": " + response).queue();
		}

		public void respondWith(string fullLine){
			discordEvent.getChannel().sendMessage(fullLine).queue();
		}
	}

	class DiscordPrivateMessageEvent : PrivateMessageEvent{
		private MessageReceivedEvent discordEvent;

		internal DiscordPrivateMessageEvent(PircBotX bot,
		                                    UserHostmask userHostmask,
		                                    User user,
		                                    string message,
		                                    MessageReceivedEvent discordEvent) : base(bot, userHostmask, user, message){
			this.discordEvent = discordEvent;
		}

		public MessageReceivedEvent getDiscordEvent(){
			return discordEvent;
		}

		public void respond(string response){
			var user = discordEvent.getAuthor();
			var messageToSend = discordEvent.getAuthor().getAsMention() + ": " + response;
			if(user.hasPrivateChannel()){ user.getPrivateChannel().sendMessage(messageToSend).queue(); }
			else{ user.openPrivateChannel().queue(privateChannel->user.getPrivateChannel().sendMessage(messageToSend).queue()); }
		}

		public void respondWith(string fullLine){
			var user = discordEvent.getAuthor();
			if(user.hasPrivateChannel()){ user.getPrivateChannel().sendMessage(fullLine).queue(); }
			else{ user.openPrivateChannel().queue(privateChannel->user.getPrivateChannel().sendMessage(fullLine).queue()); }
		}
	}

	class DiscordChannel : Channel{
		private TextChannel channel;

		internal DiscordChannel(PircBotX bot, string name) : base(bot, "#" + name){}

		public DiscordChannel setChannel(TextChannel channel){
			this.channel = channel;
			return this;
		}

		public ImmutableSortedSet getUsers(){
			var discordMembers = channel.getMembers();
			var users = new ArrayList();
			foreach(Member member in discordMembers){
				var discordNick = member.getEffectiveName();
				var discordUsername = member.getUser().getName();
				var discordHostmask = member.getUser().getId(); //misnomer but it acts as the same thing
				users.add(new DiscordUser(new DiscordUserHostmask(bot,
				                                                  discordNick + "!" + discordUsername + "@" + discordHostmask),
				                          member.getUser(),
				                          channel.getGuild()));
			}
			return ImmutableSortedSet.copyOf(users);
		}
	}

	class DiscordConnectEvent : ConnectEvent{
		private ReadyEvent ready;

		/**
		 * Default constructor to setup object. Timestamp is automatically set to
		 * current time as reported by {@link System#currentTimeMillis() }
		 *
		 * @param bot
		 */
		internal DiscordConnectEvent(PircBotX bot) : base(bot){}

		ReadyEvent getReadyEvent(){
			return ready;
		}

		internal DiscordConnectEvent setReadyEvent(ReadyEvent ready){
			this.ready = ready;
			return this;
		}
	}

	class DiscordUser : User{
		private net.dv8tion.jda.core.entities.User discordUser;
		private Guild guild = null; //null if PM

		internal DiscordUser(UserHostmask hostmask, net.dv8tion.jda.core.entities.User user, Guild guild) : base(hostmask){
			discordUser = user;
			this.guild = guild;
		}

		public net.dv8tion.jda.core.entities.User getDiscordUser(){
			return discordUser;
		}

		public Guild getGuild(){
			return guild;
		}

		public bool isAway(){
			return (guild != null) && (guild.getMember(discordUser).getOnlineStatus() != OnlineStatus.ONLINE);
		}
	}

	class DiscordQuitEvent : QuitEvent{
		private GuildMemberLeaveEvent leaveEvent;

		public DiscordQuitEvent(PircBotX bot,
		                        UserChannelDaoSnapshot userChannelDaoSnapshot,
		                        UserHostmask userHostmask,
		                        UserSnapshot user,
		                        string reason,
		                        GuildMemberLeaveEvent leave) : base(bot, userChannelDaoSnapshot, userHostmask, user, reason){
			leaveEvent = leave;
		}

		public GuildMemberLeaveEvent getLeaveEvent(){
			return leaveEvent;
		}
	}

	class DiscordJoinEvent : JoinEvent{
		GuildMemberJoinEvent joinEvent;

		public DiscordJoinEvent(PircBotX bot,
		                        Channel channel,
		                        UserHostmask userHostmask,
		                        User user,
		                        GuildMemberJoinEvent joinEvent) : base(bot, channel, userHostmask, user){
			;
			this.joinEvent = joinEvent;
		}

		public GuildMemberJoinEvent getJoinEvent(){
			return joinEvent;
		}
	}

	class DiscordUserHostmask : UserHostmask{
		private net.dv8tion.jda.core.entities.User discordUser;

		internal DiscordUserHostmask(PircBotX bot, string rawHostmask) : base(bot, rawHostmask){}

		public net.dv8tion.jda.core.entities.User getDiscordUser(){
			return discordUser;
		}
	}

	class DiscordNickChangeEvent : NickChangeEvent{
		GuildMemberNickChangeEvent nickChangeEvent;

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
			this.nickChangeEvent = nickChangeEvent;
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

	class GameThread : Thread{
		private Presence presence;

		private GameThread(){}

		internal GameThread(Presence presence){
			this.presence = presence;
		}

		public void run(){
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
			while(!currentThread().isInterrupted()){
				try{
					presence.setGame(Game.of((listOfGames[LilGUtil.randInt(0, listOfGames.Length - 1)])));
					LilGUtil.pause(LilGUtil.randInt(10, 30));
				}
				catch(InterruptedException e){ currentThread().interrupt(); }
				catch(Exception e){ e.printStackTrace(); }
			}
		}
	}

	class AvatarThread : Thread{
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();

		private AccountManagerUpdatable accountManager;
		private FozruciX bot;

		public AvatarThread(AccountManagerUpdatable accountManager, FozruciX bot){
			this.accountManager = accountManager;
			this.bot = bot;
		}

		public void run(){
			var avatarPath = new File(LilGUtil.IsLinux
				                          ? "/media/lil-g/OS/Users/ggonz/Pictures/avatar/burgerpants"
				                          : "C:/Users/ggonz/Pictures/avatar/burgerpants/");
			var tempImage = new File("./data/temp.png");
			while(!Thread.currentThread().isInterrupted()){
				try{
					LilGUtil.pause(LilGUtil.randInt(30, 120));
					var avatarList = avatarPath.listFiles();
					var pathArray = new StringBuilder();
					if(avatarList != null){
						foreach(var anAvatarList in
							avatarList){ if(!(anAvatarList.isDirectory() || anAvatarList.canRead())){ pathArray.append(anAvatarList); } }
						Logger.Info("File list: " + pathArray);
						avatarFile = avatarList[LilGUtil.randInt(0, avatarList.Length - 1)];
						Logger.Info("Picked file: " + avatarFile);
						var field = accountManager.getAvatarField();
						try{
							field.shouldUpdate();
							field.setValue(Icon.from(avatarFile));
						}
						catch(UnsupportedEncodingException e){
							try{
								ImageIO.write(ImageIO.read(avatarFile), "png", tempImage);
								field.setValue(Icon.from(tempImage));
							}
							catch(ArrayIndexOutOfBoundsException arrayE){
								arrayE.printStackTrace();
								Logger.Error(avatarFile.getAbsolutePath());
							}
						}
						foreach(var pircBotObj in
							FozConfig.getManager().getBots().toArray()){
							var temp = ((PircBotX)pircBotObj).getConfiguration().getListenerManager().getListeners().toArray();
							FozruciX bot = null;
							var index = 0;
							while(index < temp.Length){
								if(temp[index] is FozruciX){
									bot = (FozruciX)temp[index];
									break;
								}
								index++;
							}
							if(bot != null)
								bot.setAvatar(URIUtil.encodeQuery("https://lilggamegenius.ml/burgerpants/" + avatarFile.getName()));
						}
					}
					accountManager.update(null).queue();
					Logger.debug("Set Avatar");
				}
				catch(InterruptedException){ currentThread().interrupt(); }
				catch(Exception e){ e.printStackTrace(); }
			}
		}
	}
	
}