package com.LilG.Com;

import com.LilG.Com.utils.CryptoUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.LilG.Com.DiscordAdapter.avatarFile;

/**
 * Created by ggonz on 7/10/2016.
 */
public class DiscordAdapter extends ListenerAdapter {
    private final static Logger LOGGER = Logger.getLogger(DiscordAdapter.class);
    private static final String currentDiscordID = "131494148350935040";
    public static File avatarFile;
    private static FozruciX bot;
    private static PircBotX pircBotX;
    private static JDA jda;
    private static Thread game;
    private static Thread avatar;

    public DiscordAdapter(PircBotX pircBotX) {
        try {
            LOGGER.setLevel(Level.ALL);
            jda = new JDABuilder()
                    .setBotToken(CryptoUtil.decrypt(FozConfig.setPassword(FozConfig.Password.discord)))
                    .setAutoReconnect(true)
                    .setAudioEnabled(false)
                    .setEnableShutdownHook(true)
                    .buildBlocking();
            jda.addEventListener(this);
            jda.setAutoReconnect(true);
            DiscordAdapter.bot = new FozruciX(FozruciX.Network.discord, FozConfig.getManager(), FozConfig.loadData(new GsonBuilder().setPrettyPrinting().create()));
            DiscordAdapter.pircBotX = pircBotX;
            game = new GameThread(jda.getAccountManager());
            game.start();

            avatar = new AvatarThread(jda.getAccountManager(), bot);
            avatar.start();
            bot.onConnect(new ConnectEvent(pircBotX));
            LOGGER.trace("DiscordAdapter created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JDA getJda() {
        return jda;
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

    public DiscordMessageEvent(PircBotX bot, @NonNull Channel channel, @NonNull String channelSource, @NonNull UserHostmask userHostmask, User user, @NonNull String message, ImmutableMap<String, String> tags, MessageReceivedEvent discordEvent) {
        super(bot, channel, channelSource, userHostmask, user, message, tags);
        this.discordEvent = discordEvent;
    }

    public MessageReceivedEvent getDiscordEvent() {
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

    public DiscordPrivateMessageEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, @NonNull String message, MessageReceivedEvent discordEvent) {
        super(bot, userHostmask, user, message);
        this.discordEvent = discordEvent;
    }

    public MessageReceivedEvent getDiscordEvent() {
        return discordEvent;
    }


    @Override
    public void respond(String response) {
    }

    @Override
    public void respondWith(String fullLine) {
        discordEvent.getChannel().sendMessage(fullLine);
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

class DiscordUser extends User {
    private net.dv8tion.jda.entities.User discordUser;

    DiscordUser(UserHostmask hostmask) {
        super(hostmask);
    }

    public DiscordUser setUser(net.dv8tion.jda.entities.User user) {
        this.discordUser = user;
        return this;
    }
}

class DiscordUserHostmask extends UserHostmask {
    private net.dv8tion.jda.entities.User discordUser;

    DiscordUserHostmask(PircBotX bot, String rawHostmask) {
        super(bot, rawHostmask);
    }


}

class GameThread extends Thread {
    private AccountManager accountManager;

    private GameThread() {
    }

    public GameThread(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public void run() {
        String[] listOfGames = {"With bleach", "With fire", "With matches", "The Bleach Drinking Game", "The smallest violin", "In the blood of my enemies", "On top of the corpses of my enemies"};
        while (!Thread.currentThread().isInterrupted()) {
            try {
                accountManager.setGame(listOfGames[FozruciX.randInt(0, listOfGames.length - 1)]);
                FozruciX.pause(FozruciX.randInt(10, 30));
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
                FozruciX.pause(FozruciX.randInt(30, 120));
                File[] avatarList = avatarPath.listFiles();
                if (avatarList != null) {
                    avatarFile = avatarList[FozruciX.randInt(0, avatarList.length - 1)];
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