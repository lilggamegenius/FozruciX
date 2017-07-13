package com.LilG;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.LilG.utils.CryptoUtil;
import com.LilG.utils.LilGUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.mashape.unirest.http.Unirest;
import com.sun.jna.Platform;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AccountManagerUpdatable;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.core.managers.fields.AccountField;
import org.apache.commons.httpclient.util.URIUtil;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.*;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.LilG.DiscordAdapter.avatarFile;

/**
 * Created by ggonz on 7/10/2016.
 */
public class DiscordAdapter extends ListenerAdapter {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DiscordAdapter.class);
    public static File avatarFile;
    static FozruciX bot;
    static PircBotX pircBotX;
    private static DiscordAdapter discordAdapter = null;
    private static ReadyEvent readyEvent;
    private static JDA jda;
    private static Thread game;
    private static Thread avatar;

    private DiscordAdapter(PircBotX pircBotX) throws LoginException, InterruptedException, RateLimitedException {
        String token = CryptoUtil.decrypt(FozConfig.setPassword(FozConfig.Password.discord));
        LOGGER.trace("Calling JDA Builder with token: " + token);
        jda = new JDABuilder(AccountType.BOT)
                .setToken(token)
                    .setAutoReconnect(true)
                    .setAudioEnabled(true)
                    .setEnableShutdownHook(true)
		        .addEventListener(this)
		        .buildBlocking();
	    DiscordAdapter.bot = new FozruciX(FozruciX.Network.discord, FozConfig.getManager());
            DiscordAdapter.pircBotX = pircBotX;
        game = new GameThread(jda.getPresence());
        game.setName("Game Setter thread");
            game.start();

        avatar = new AvatarThread(jda.getSelfUser().getManagerUpdatable(), bot);
        avatar.setName("Avatar Setter thread");
            avatar.start();
            LOGGER.trace("DiscordAdapter created");
        LOGGER.trace("Calling onConnect() method");
        synchronized (readyEvent) {
            bot.onConnect(new DiscordConnectEvent(pircBotX).setReadyEvent(readyEvent));
        }
    }

    static synchronized DiscordAdapter makeDiscord(PircBotX pircBotX) {
        LOGGER.setLevel(Level.ALL);
        try {
            LOGGER.trace("Making Discord connection");
            if (discordAdapter == null) {
                LOGGER.trace("Constructing...");
                Unirest.setTimeouts(10 * 1000, 10 * 1000);
                discordAdapter = new DiscordAdapter(pircBotX);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return discordAdapter;
    }

    public static JDA getJda() {
        return jda;
    }

    public static int getlevelFromPerm(Permission perm) {
        switch (perm) {
            case CREATE_INSTANT_INVITE:
                return 0;
            case KICK_MEMBERS:
                return 3;
            case BAN_MEMBERS:
                return 3;
            case ADMINISTRATOR:
                return 3;
            case MANAGE_CHANNEL:
                return 4;
            case MANAGE_SERVER:
                return 5;

            case MESSAGE_READ:
                return 0;
            case MESSAGE_WRITE:
                return 0;
            case MESSAGE_TTS:
                return 0;
            case MESSAGE_MANAGE:
                return 3;
            case MESSAGE_EMBED_LINKS:
                return 0;
            case MESSAGE_ATTACH_FILES:
                return 0;
            case MESSAGE_HISTORY:
                return 0;
            case MESSAGE_MENTION_EVERYONE:
                return 0;
            case MESSAGE_EXT_EMOJI:
                return 0;

            case VOICE_CONNECT:
                return 0;
            case VOICE_SPEAK:
                return 0;
            case VOICE_MUTE_OTHERS:
                return 3;
            case VOICE_DEAF_OTHERS:
                return 3;
            case VOICE_MOVE_OTHERS:
                return 3;
            case VOICE_USE_VAD:
                return 0;

            case NICKNAME_CHANGE:
                return 0;
            case NICKNAME_MANAGE:
                return 2;

            case MANAGE_ROLES:
                return 5;
            case MANAGE_PERMISSIONS:
                return 5;
        }

        return 0;
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            synchronized (event) {
                readyEvent = event;
                LOGGER.info("Discord is ready");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent join) {
        String discordNick = join.getMember().getEffectiveName();
        String discordUsername = join.getMember().getUser().getName();
        String discordHostmask = join.getMember().getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", join.getGuild().getName(), discordUserHostmask.getHostmask(), "Joined"));
        bot.onJoin(new DiscordJoinEvent(pircBotX, new DiscordChannel(pircBotX, '#' + join.getGuild().getPublicChannel().getName()), discordUserHostmask, new DiscordUser(discordUserHostmask, join.getMember().getUser(), join.getGuild()), join));
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent leave) {
        String discordNick = leave.getMember().getEffectiveName();
        String discordUsername = leave.getMember().getUser().getName();
        String discordHostmask = leave.getMember().getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", leave.getGuild().getName(), discordUserHostmask.getHostmask(), "Left"));
        bot.onQuit(new DiscordQuitEvent(pircBotX, null, discordUserHostmask, new UserSnapshot(new DiscordUser(discordUserHostmask, leave.getMember().getUser(), leave.getGuild())), "", leave));
    }

    public void onGuildBan(GuildBanEvent ban) {
        String discordNick = ban.getUser().getName();
        String discordUsername = discordNick;
        String discordHostmask = ban.getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", ban.getGuild().getName(), discordUserHostmask.getHostmask(), "Banned"));
        bot.onBan(ban);
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent nick){
        String discordNick = nick.getMember().getEffectiveName();
        String discordUsername = nick.getMember().getUser().getName();
        String discordOldNick = nick.getPrevNick();
        discordOldNick = discordOldNick != null ? discordOldNick : discordUsername;
        String discordHostmask = nick.getMember().getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s]: %s %s %s %s %s", nick.getGuild().getName(), discordUserHostmask.getHostmask(), "Changed nick from", discordOldNick, "to", discordNick));
        bot.onNickChange(new NickChangeEvent(pircBotX, discordOldNick, discordNick, discordUserHostmask, new DiscordUser(discordUserHostmask, nick.getMember().getUser(), nick.getGuild())));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String discordNick = "Error nick";
        String discordUsername = "Error UserName";
        String discordHostmask = "Error Hostmask";
        try {
            discordNick = event.getMember().getEffectiveName();
            discordUsername = event.getMember().getUser().getName();
            discordHostmask = event.getMember().getUser().getId();
        } catch (NullPointerException e) {
            discordNick = event.getAuthor().getName();
            discordUsername = discordNick;
            discordHostmask = event.getAuthor().getId();
        } catch (Exception e) {
            LOGGER.error("Error receiving message", e);
        }
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);

        if (event.isFromType(ChannelType.PRIVATE)) {
            LOGGER.info(String.format("[PM] %s: %s", discordUserHostmask.getHostmask(), event.getMessage().getRawContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfUser().getId())) {
                bot.onPrivateMessage(new DiscordPrivateMessageEvent(pircBotX, discordUserHostmask, new DiscordUser(discordUserHostmask, event.getAuthor(), null), event.getMessage().getRawContent(), event));
            }
        } else if (event.isFromType(ChannelType.TEXT)) {
            LOGGER.info(String.format("[%s][%s] %s: %s", event.getGuild().getName(),
                    event.getTextChannel().getName(), discordUserHostmask.getHostmask(),
                    event.getMessage().getRawContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfUser().getId())) {
                bot.onMessage(new DiscordMessageEvent(pircBotX, new DiscordChannel(pircBotX, event.getTextChannel().getName()).setChannel(event.getTextChannel()), event.getTextChannel().getName(), discordUserHostmask, new DiscordUser(discordUserHostmask, event.getAuthor(), event.getGuild()), event.getMessage().getRawContent(), null, event));
            }
        } else {
            //// TODO: 11/13/2016 add settings for group message
        }

    }

    @Override
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {
        try {
            bot.onTopic(new DiscordTopicEvent(pircBotX, new DiscordChannel(pircBotX, event.getChannel().getName()), event.getOldTopic(), event.getChannel().getTopic(), new DiscordUserHostmask(pircBotX, ""), System.currentTimeMillis(), true));
        } catch (Exception e) {
            LOGGER.error("Oh shit, something broke", e);
        }
    }
}

class DiscordMessageEvent extends MessageEvent {
    private MessageReceivedEvent discordEvent;

    DiscordMessageEvent(PircBotX bot, @NotNull Channel channel, @NotNull String channelSource, @NotNull UserHostmask userHostmask, User user, @NotNull String message, ImmutableMap<String, String> tags, MessageReceivedEvent discordEvent) {
        super(bot, channel, channelSource, userHostmask, user, message, tags);
        this.discordEvent = discordEvent;
    }

    MessageReceivedEvent getDiscordEvent() {
        return discordEvent;
    }


    @Override
    public void respond(String response) {
        discordEvent.getChannel().sendMessage(discordEvent.getAuthor().getAsMention() + ": " + response).queue();
    }

    @Override
    public void respondWith(String fullLine) {
        discordEvent.getChannel().sendMessage(fullLine).queue();
    }
}

class DiscordPrivateMessageEvent extends PrivateMessageEvent {
    private MessageReceivedEvent discordEvent;

    DiscordPrivateMessageEvent(PircBotX bot, @NotNull UserHostmask userHostmask, User user, @NotNull String message, MessageReceivedEvent discordEvent) {
        super(bot, userHostmask, user, message);
        this.discordEvent = discordEvent;
    }

    public MessageReceivedEvent getDiscordEvent() {
        return discordEvent;
    }


    @Override
    public void respond(String response) {
        net.dv8tion.jda.core.entities.User user = discordEvent.getAuthor();
        String messageToSend = discordEvent.getAuthor().getAsMention() + ": " + response;
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageToSend).queue());
    }

    @Override
    public void respondWith(String fullLine) {
        net.dv8tion.jda.core.entities.User user = discordEvent.getAuthor();
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(fullLine).queue());
    }
}

class DiscordChannel extends Channel {
    private TextChannel channel;

    DiscordChannel(PircBotX bot, String name) {
        super(bot, "#" + name);
    }

    public DiscordChannel setChannel(TextChannel channel) {
        this.channel = channel;
        return this;
    }

    public ImmutableSortedSet<User> getUsers() {
        List<net.dv8tion.jda.core.entities.Member> discordMembers = channel.getMembers();
        List<User> users = new ArrayList<>();
        for (net.dv8tion.jda.core.entities.Member member : discordMembers) {
            String discordNick = member.getEffectiveName();
            String discordUsername = member.getUser().getName();
            String discordHostmask = member.getUser().getId(); //misnomer but it acts as the same thing
            users.add(new DiscordUser(new DiscordUserHostmask(this.bot, discordNick + "!" + discordUsername + "@" + discordHostmask), member.getUser(), channel.getGuild()));
        }
        return ImmutableSortedSet.copyOf(users);
    }
}

class DiscordConnectEvent extends ConnectEvent {

    private ReadyEvent ready;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param bot
     */
    DiscordConnectEvent(PircBotX bot) {
        super(bot);
    }

    ReadyEvent getReadyEvent() {
        return ready;
    }

    DiscordConnectEvent setReadyEvent(ReadyEvent ready) {
        this.ready = ready;
        return this;
    }
}

class DiscordUser extends User {
    private net.dv8tion.jda.core.entities.User discordUser;
    private Guild guild = null; //null if PM

    DiscordUser(UserHostmask hostmask, net.dv8tion.jda.core.entities.User user, Guild guild) {
        super(hostmask);
        discordUser = user;
        this.guild = guild;
    }

    public net.dv8tion.jda.core.entities.User getDiscordUser() {
        return discordUser;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public boolean isAway() {
        return guild != null && guild.getMember(discordUser).getOnlineStatus() != OnlineStatus.ONLINE;
    }

    @Override
    public boolean isVerified() {
    	return !discordUser.isFake();
    }

	@Override
	public String getRealName() {
		return discordUser.getName();
	}

	@Override
	public String getAwayMessage() {
    	if(guild!=null){
    		return guild.getMember(discordUser).getOnlineStatus().getKey();
	    }
		return null;
	}

	@Override
	public boolean isIrcop() {
		return guild != null && guild.getMember(discordUser).isOwner();
	}

	@Override
	public String getHostmask() {
		if(guild!=null){
			return String.format("%s!%s@%s", guild.getMember(discordUser).getEffectiveName(), discordUser.getName(), discordUser.getId());
		}
		return String.format("%s!%s@%s", discordUser.getName(), discordUser.getName(), discordUser.getId());
	}
}

class DiscordQuitEvent extends QuitEvent {

    private GuildMemberLeaveEvent leaveEvent;

    public DiscordQuitEvent(PircBotX bot, UserChannelDaoSnapshot userChannelDaoSnapshot, @NotNull UserHostmask userHostmask, UserSnapshot user, @NotNull String reason, GuildMemberLeaveEvent leave) {
        super(bot, userChannelDaoSnapshot, userHostmask, user, reason);
        leaveEvent = leave;
    }

    public GuildMemberLeaveEvent getLeaveEvent() {
        return leaveEvent;
    }
}

class DiscordJoinEvent extends JoinEvent {

    GuildMemberJoinEvent joinEvent;

    public DiscordJoinEvent(PircBotX bot, @NotNull Channel channel, @NotNull UserHostmask userHostmask, User user, GuildMemberJoinEvent joinEvent) {
        super(bot, channel, userHostmask, user);
        this.joinEvent = joinEvent;
    }

    public GuildMemberJoinEvent getJoinEvent() {
        return joinEvent;
    }
}

class DiscordUserHostmask extends UserHostmask {
    private net.dv8tion.jda.core.entities.User discordUser;

    DiscordUserHostmask(PircBotX bot, String rawHostmask) {
        super(bot, rawHostmask);
    }

    public net.dv8tion.jda.core.entities.User getDiscordUser() {
        return discordUser;
    }

}

class DiscordNickChangeEvent extends NickChangeEvent {

    GuildMemberNickChangeEvent nickChangeEvent;

    public DiscordNickChangeEvent(PircBotX bot, @NotNull String oldNick, @NotNull String newNick, @NotNull UserHostmask userHostmask, User user, GuildMemberNickChangeEvent nickChangeEvent) {
        super(bot, oldNick, newNick, userHostmask, user);
        this.nickChangeEvent = nickChangeEvent;
    }
}

class DiscordTopicEvent extends TopicEvent {

    public DiscordTopicEvent(PircBotX bot, @NotNull Channel channel, String oldTopic, @NotNull String topic, @NotNull UserHostmask user, long date, boolean changed) {
        super(bot, channel, oldTopic, topic, user, date, changed);
    }
}

class GameThread extends Thread {
    private Presence presence;

    private GameThread() {
    }

    GameThread(Presence presence) {
        this.presence = presence;
    }

    @Override
    public void run() {
        String[] listOfGames = {"With bleach", "With fire", "With matches", "The Bleach Drinking Game", "The smallest violin", "In the blood of my enemies", "On top of the corpses of my enemies", "In the trash can where i belong", "The waiting game", "The game of life", "baseball with the head of my enemies", "basketball with the head of my enemies", "football with the head of my enemies"};
        while (!Thread.currentThread().isInterrupted()) {
            try {
                presence.setGame(Game.of((listOfGames[LilGUtil.randInt(0, listOfGames.length - 1)])));
                LilGUtil.pause(LilGUtil.randInt(10, 30));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class AvatarThread extends Thread {
    private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(AvatarThread.class);
    private AccountManagerUpdatable accountManager;
    private FozruciX bot;

    public AvatarThread(AccountManagerUpdatable accountManager, FozruciX bot) {
        this.accountManager = accountManager;
        this.bot = bot;
    }

    @Override
    public void run() {
        File avatarPath = new File(Platform.isLinux() ? "/media/lil-g/OS/Users/ggonz/OneDrive/Pictures/avatar/burgerpants" : "C:/Users/ggonz/OneDrive/Pictures/avatar/burgerpants");
        File tempImage = new File("./data/temp.png");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LilGUtil.pause(LilGUtil.randInt(30, 120));
                File[] avatarList = avatarPath.listFiles();
                StringBuilder pathArray = new StringBuilder();
                if (avatarList != null) {
                    for (File anAvatarList : avatarList) {
                        if (!(anAvatarList.isDirectory() || anAvatarList.canRead())) {
                            pathArray.append(anAvatarList);
                        }
                    }
                    LOGGER.info("File list: " + pathArray);
                    avatarFile = avatarList[LilGUtil.randInt(0, avatarList.length - 1)];
                    LOGGER.info("Picked file: " + avatarFile);
                    AccountField<Icon> field = accountManager.getAvatarField();
                    try {
                        field.shouldUpdate();
                        field.setValue(Icon.from(avatarFile));
                    } catch (UnsupportedEncodingException e) {
                        try {
                            ImageIO.write(ImageIO.read(avatarFile), "png", tempImage);
                            field.setValue(Icon.from(tempImage));
                        } catch (ArrayIndexOutOfBoundsException arrayE) {
                            arrayE.printStackTrace();
                            LOGGER.error(avatarFile.getAbsolutePath());
                        }
                    }
                    for (Object pircBotObj : FozConfig.getManager().getBots().toArray()) {
                        Object[] temp = ((PircBotX) pircBotObj).getConfiguration().getListenerManager().getListeners().toArray();
                        FozruciX bot = null;
                        int index = 0;
                        while (index < temp.length) {
                            if (temp[index] instanceof FozruciX) {
                                bot = (FozruciX) temp[index];
                                break;
                            }
                            index++;
                        }
                        if (bot != null)
                            bot.setAvatar(URIUtil.encodeQuery("https://lilggamegenius.ml/burgerpants/" + avatarFile.getName()));
                    }

                }
                accountManager.update(null).queue();
                LOGGER.debug("Set Avatar");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}