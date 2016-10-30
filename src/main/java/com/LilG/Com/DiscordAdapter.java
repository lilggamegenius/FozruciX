package com.LilG.Com;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.LilG.Com.utils.CryptoUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.mashape.unirest.http.Unirest;
import com.sun.jna.Platform;
import lombok.NonNull;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.channel.text.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.AccountManager;
import net.dv8tion.jda.utils.AvatarUtil;
import org.apache.commons.httpclient.util.URIUtil;
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

import static com.LilG.Com.DiscordAdapter.avatarFile;
import static com.LilG.Com.utils.LilGUtil.pause;
import static com.LilG.Com.utils.LilGUtil.randInt;

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

    private DiscordAdapter(PircBotX pircBotX) throws LoginException, InterruptedException {
            LOGGER.trace("Calling JDA Builder");
            jda = new JDABuilder()
                    .setBotToken(CryptoUtil.decrypt(FozConfig.setPassword(FozConfig.Password.discord)))
                    .setAutoReconnect(true)
                    .setAudioEnabled(true)
                    .setEnableShutdownHook(true)
                    .addListener(this)
                    .buildBlocking();
        DiscordAdapter.bot = new FozruciX(FozruciX.Network.discord, FozConfig.getManager());
            DiscordAdapter.pircBotX = pircBotX;
            game = new GameThread(jda.getAccountManager());
        game.setName("Game Setter thread");
            game.start();

            avatar = new AvatarThread(jda.getAccountManager(), bot);
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
        String discordNick = join.getGuild().getNicknameForUser(join.getUser());
        String discordUsername = join.getUser().getUsername();
        String discordHostmask = join.getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", join.getGuild().getName(), discordUserHostmask.getHostmask(), "Joined"));
        bot.onJoin(new DiscordJoinEvent(pircBotX, new DiscordChannel(pircBotX, '#' + join.getGuild().getPublicChannel().getName()), discordUserHostmask, new DiscordUser(discordUserHostmask, join.getUser(), join.getGuild()), join));
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent leave) {
        String discordNick = leave.getGuild().getNicknameForUser(leave.getUser());
        String discordUsername = leave.getUser().getUsername();
        String discordHostmask = leave.getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", leave.getGuild().getName(), discordUserHostmask.getHostmask(), "Left"));
        bot.onQuit(new DiscordQuitEvent(pircBotX, null, discordUserHostmask, new UserSnapshot(new DiscordUser(discordUserHostmask, leave.getUser(), leave.getGuild())), "", leave));
    }

    public void onGuildMemberBan(GuildMemberBanEvent ban) {
        String discordNick = ban.getGuild().getNicknameForUser(ban.getUser());
        String discordUsername = ban.getUser().getUsername();
        String discordHostmask = ban.getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[%s] %s: %s", ban.getGuild().getName(), discordUserHostmask.getHostmask(), "Banned"));
        bot.onBan(ban);
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent nick){
        String discordNick = nick.getGuild().getNicknameForUser(nick.getUser());
        String discordUsername = nick.getUser().getUsername();
        String discordHostmask = nick.getUser().getId();
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, (discordNick == null ? discordUsername : discordNick) + "!" + discordUsername + "@" + discordHostmask);
        LOGGER.info(String.format("[PM] %s: %s %s %s %s %s", nick.getGuild().getName(),discordUserHostmask.getHostmask(), "Changed nick from", nick.getPrevNick(), "to", nick.getNewNick()));
        bot.onNickChange(new NickChangeEvent(pircBotX, nick.getPrevNick(), nick.getNewNick(), discordUserHostmask, new DiscordUser(discordUserHostmask, nick.getUser(), nick.getGuild())));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String discordNick = event.getAuthorName();
        String discordUsername = event.getAuthor().getUsername();
        String discordHostmask = event.getAuthor().getId(); //misnomer but it acts as the same thing
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);

        if (event.isPrivate()) {
            LOGGER.info(String.format("[PM] %s: %s", discordUserHostmask.getHostmask(), event.getMessage().getRawContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfInfo().getId())) {
                bot.onPrivateMessage(new DiscordPrivateMessageEvent(pircBotX, discordUserHostmask, new DiscordUser(discordUserHostmask, event.getAuthor(), null), event.getMessage().getRawContent(), event));
            }
        } else {
            LOGGER.info(String.format("[%s][%s] %s: %s", event.getGuild().getName(),
                    event.getTextChannel().getName(), discordUserHostmask.getHostmask(),
                    event.getMessage().getRawContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfInfo().getId())) {
                bot.onMessage(new DiscordMessageEvent(pircBotX, new DiscordChannel(pircBotX, event.getTextChannel().getName()).setChannel(event.getTextChannel()), event.getTextChannel().getName(), discordUserHostmask, new DiscordUser(discordUserHostmask, event.getAuthor(), event.getGuild()), event.getMessage().getRawContent(), null, event));
            }
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

    DiscordMessageEvent(PircBotX bot, @NonNull Channel channel, @NonNull String channelSource, @NonNull UserHostmask userHostmask, User user, @NonNull String message, ImmutableMap<String, String> tags, MessageReceivedEvent discordEvent) {
        super(bot, channel, channelSource, userHostmask, user, message, tags);
        this.discordEvent = discordEvent;
    }

    MessageReceivedEvent getDiscordEvent() {
        return discordEvent;
    }


    @Override
    public void respond(String response) {
        discordEvent.getChannel().sendMessage(discordEvent.getAuthorName() + ": " + response);
    }

    @Override
    public void respondWith(String fullLine) {
        discordEvent.getChannel().sendMessage(fullLine);
    }
}

class DiscordPrivateMessageEvent extends PrivateMessageEvent {
    private MessageReceivedEvent discordEvent;

    DiscordPrivateMessageEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, @NonNull String message, MessageReceivedEvent discordEvent) {
        super(bot, userHostmask, user, message);
        this.discordEvent = discordEvent;
    }

    public MessageReceivedEvent getDiscordEvent() {
        return discordEvent;
    }


    @Override
    public void respond(String response) {
        discordEvent.getAuthor().getPrivateChannel().sendMessage(discordEvent.getAuthorName() + ": " + response);
    }

    @Override
    public void respondWith(String fullLine) {
        discordEvent.getAuthor().getPrivateChannel().sendMessage(fullLine);
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
        List<net.dv8tion.jda.entities.User> discordUsers = channel.getUsers();
        List<User> users = new ArrayList<>();
        for (net.dv8tion.jda.entities.User user : discordUsers) {
            String discordNick = user.getUsername();
            String discordUsername = user.getUsername();
            String discordHostmask = user.getId(); //misnomer but it acts as the same thing
            users.add(new DiscordUser(new DiscordUserHostmask(this.bot, discordNick + "!" + discordUsername + "@" + discordHostmask), user, channel.getGuild()));
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
    private net.dv8tion.jda.entities.User discordUser;
    private Guild guild = null; //null if PM

    DiscordUser(UserHostmask hostmask, net.dv8tion.jda.entities.User user, Guild guild) {
        super(hostmask);
        discordUser = user;
        this.guild = guild;
    }

    public net.dv8tion.jda.entities.User getDiscordUser() {
        return discordUser;
    }

    public Guild getGuild() {
        return guild;
    }
}

class DiscordQuitEvent extends QuitEvent {

    private GuildMemberLeaveEvent leaveEvent;

    public DiscordQuitEvent(PircBotX bot, UserChannelDaoSnapshot userChannelDaoSnapshot, @NonNull UserHostmask userHostmask, UserSnapshot user, @NonNull String reason, GuildMemberLeaveEvent leave) {
        super(bot, userChannelDaoSnapshot, userHostmask, user, reason);
        leaveEvent = leave;
    }

    public GuildMemberLeaveEvent getLeaveEvent() {
        return leaveEvent;
    }
}

class DiscordJoinEvent extends JoinEvent {

    GuildMemberJoinEvent joinEvent;

    public DiscordJoinEvent(PircBotX bot, @NonNull Channel channel, @NonNull UserHostmask userHostmask, User user, GuildMemberJoinEvent joinEvent) {
        super(bot, channel, userHostmask, user);
        this.joinEvent = joinEvent;
    }

    public GuildMemberJoinEvent getJoinEvent() {
        return joinEvent;
    }
}

class DiscordUserHostmask extends UserHostmask {
    private net.dv8tion.jda.entities.User discordUser;

    DiscordUserHostmask(PircBotX bot, String rawHostmask) {
        super(bot, rawHostmask);
    }

    public net.dv8tion.jda.entities.User getDiscordUser() {
        return discordUser;
    }

}

class DiscordNickChangeEvent extends NickChangeEvent {

    GuildMemberNickChangeEvent nickChangeEvent;

    public DiscordNickChangeEvent(PircBotX bot, @NonNull String oldNick, @NonNull String newNick, @NonNull UserHostmask userHostmask, User user, GuildMemberNickChangeEvent nickChangeEvent) {
        super(bot, oldNick, newNick, userHostmask, user);
        this.nickChangeEvent = nickChangeEvent;
    }
}

class DiscordTopicEvent extends TopicEvent {

    public DiscordTopicEvent(PircBotX bot, @NonNull Channel channel, String oldTopic, @NonNull String topic, @NonNull UserHostmask user, long date, boolean changed) {
        super(bot, channel, oldTopic, topic, user, date, changed);
    }
}

class GameThread extends Thread {
    private AccountManager accountManager;

    private GameThread() {
    }

    GameThread(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public void run() {
        String[] listOfGames = {"With bleach", "With fire", "With matches", "The Bleach Drinking Game", "The smallest violin", "In the blood of my enemies", "On top of the corpses of my enemies", "In the trash can where i belong", "The waiting game", "The game of life", "baseball with the head of my enemies", "basketball with the head of my enemies", "football with the head of my enemies"};
        while (!Thread.currentThread().isInterrupted()) {
            try {
                accountManager.setGame(listOfGames[randInt(0, listOfGames.length - 1)]);
                pause(randInt(10, 30));
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
    private AccountManager accountManager;
    private FozruciX bot;

    public AvatarThread(AccountManager accountManager, FozruciX bot) {
        this.accountManager = accountManager;
        this.bot = bot;
    }

    @Override
    public void run() {
        File avatarPath = new File(Platform.isLinux() ? "/media/lil-g/OS/Users/ggonz/Pictures/avatar/burgerpants" : "C:/Users/ggonz/Pictures/avatar/burgerpants/");
        File tempImage = new File("./data/temp.png");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                pause(randInt(30, 120));
                File[] avatarList = avatarPath.listFiles();
                StringBuilder pathArray = new StringBuilder();
                if (avatarList != null) {
                    for (File anAvatarList : avatarList) {
                        pathArray.append(anAvatarList);
                    }
                    LOGGER.info("File list: " + pathArray);
                    avatarFile = avatarList[randInt(0, avatarList.length - 1)];
                    LOGGER.info("Picked file: " + avatarFile);
                    try {
                        accountManager.setAvatar(AvatarUtil.getAvatar(avatarFile));
                    } catch (UnsupportedEncodingException e) {
                        try {
                            ImageIO.write(ImageIO.read(avatarFile), "png", tempImage);
                            accountManager.setAvatar(AvatarUtil.getAvatar(tempImage));
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
                            bot.setAvatar(URIUtil.encodeQuery("https://lilggamegenuis.tk/burgerpants/" + avatarFile.getName()));
                    }

                }
                accountManager.update();
                LOGGER.debug("Set Avatar");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}