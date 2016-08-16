package com.LilG.Com;

import com.LilG.Com.utils.CryptoUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.mashape.unirest.http.Unirest;
import com.thoughtworks.xstream.XStream;
import lombok.NonNull;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.AccountManager;
import net.dv8tion.jda.utils.AvatarUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.LilG.Com.DiscordAdapter.avatarFile;
import static com.LilG.Com.utils.LilGUtil.pause;
import static com.LilG.Com.utils.LilGUtil.randInt;

/**
 * Created by ggonz on 7/10/2016.
 */
public class DiscordAdapter extends ListenerAdapter {
    private static final Logger LOGGER = Logger.getLogger(DiscordAdapter.class);
    public static File avatarFile;
    private static DiscordAdapter discordAdapter = null;
    private static FozruciX bot;
    private static PircBotX pircBotX;
    private static JDA jda;
    private static Thread game;
    private static Thread avatar;

    private DiscordAdapter(PircBotX pircBotX) throws LoginException, InterruptedException {
            LOGGER.trace("Calling JDA Builder");
            jda = new JDABuilder()
                    .setBotToken(CryptoUtil.decrypt(FozConfig.setPassword(FozConfig.Password.discord)))
                    .setAutoReconnect(true)
                    .setAudioEnabled(false)
                    .setEnableShutdownHook(true)
                    .addListener(this)
                    .buildBlocking();
            DiscordAdapter.bot = new FozruciX(FozruciX.Network.discord, FozConfig.getManager(), FozConfig.loadData(new XStream()));
            DiscordAdapter.pircBotX = pircBotX;
            game = new GameThread(jda.getAccountManager());
            game.start();

            avatar = new AvatarThread(jda.getAccountManager(), bot);
            avatar.start();
            LOGGER.trace("Calling onConnect() method");
            bot.onConnect(new ConnectEvent(pircBotX));
            LOGGER.trace("DiscordAdapter created");
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

    static JDA getJda() {
        return jda;
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            bot.onConnect(new DiscordConnectEvent(pircBotX).setReadyEvent(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String discordNick = event.getAuthorName();
        String discordUsername = event.getAuthor().getUsername();
        String discordHostmask = event.getAuthor().getId(); //misnomer but it acts as the same thing
        DiscordUserHostmask discordUserHostmask = new DiscordUserHostmask(pircBotX, discordNick + "!" + discordUsername + "@" + discordHostmask);

        if (event.isPrivate()) {
            LOGGER.info(String.format("[PM] %s: %s", discordUserHostmask.getHostmask(), event.getMessage().getContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfInfo().getId())) {
                bot.onPrivateMessage(new DiscordPrivateMessageEvent(pircBotX, discordUserHostmask, new DiscordUser(discordUserHostmask), event.getMessage().getContent(), event));
            }
        } else {
            LOGGER.info(String.format("[%s][%s] %s: %s", event.getGuild().getName(),
                    event.getTextChannel().getName(), discordUserHostmask.getHostmask(),
                    event.getMessage().getContent()));
            if (!event.getAuthor().getId().equals(jda.getSelfInfo().getId())) {
                bot.onMessage(new DiscordMessageEvent(pircBotX, new DiscordChannel(pircBotX, event.getTextChannel().getName()).setChannel(event.getTextChannel()), event.getTextChannel().getName(), discordUserHostmask, new DiscordUser(discordUserHostmask), event.getMessage().getContent(), null, event));
            }
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
            users.add(new DiscordUser(new DiscordUserHostmask(this.bot, discordNick + "!" + discordUsername + "@" + discordHostmask)));
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

    DiscordUser(UserHostmask hostmask) {
        super(hostmask);
    }

    public DiscordUser setUser(net.dv8tion.jda.entities.User user) {
        this.discordUser = user;
        return this;
    }

    public net.dv8tion.jda.entities.User getDiscordUser() {
        return discordUser;
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
    private final static Logger LOGGER = Logger.getLogger(AvatarThread.class);
    private AccountManager accountManager;
    private FozruciX bot;

    public AvatarThread(AccountManager accountManager, FozruciX bot) {
        this.accountManager = accountManager;
        this.bot = bot;
    }

    @Override
    public void run() {
        File avatarPath = new File("C:\\Users\\ggonz\\Pictures\\avatar\\burgerpants\\");
        File tempImage = new File("data\\temp.png");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                pause(randInt(30, 120));
                File[] avatarList = avatarPath.listFiles();
                if (avatarList != null) {
                    avatarFile = avatarList[randInt(0, avatarList.length - 1)];
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
                            bot.setAvatar("https://lilggamegenuis.tk/burgerpants/" + new URI(avatarFile.getName()).getPath());
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