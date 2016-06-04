package com.LilG.Com;

import com.LilG.Com.CMD.CommandLine;
import com.LilG.Com.DataClasses.Meme;
import com.LilG.Com.DataClasses.Note;
import org.pircbotx.Configuration;
import org.pircbotx.IdentServer;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

public class FozConfig {

    private final static boolean debug = false;
    //Configure what we want our bot to do
    private final static String nick = "FozruciX";
    private final static String login = "SmugLeaf";
    private final static String realName = "\u00034\u000F* What can I do for you, little buddy?";
    private final static String badnickNET = "irc.badnik.net";
    private final static String twitch = "irc.twitch.tv";
    private final static String caffie = "irc.caffie.net";
    private final static String esper = "irc.esper.net";
    private final static int attempts = Integer.MAX_VALUE;

    //Bot universal variables
    private final static List<Note> noteList = new ArrayList<>();
    private final static CommandLine terminal = new CommandLine();
    private final static String avatar = "http://puu.sh/oiLYR.gif";
    private final static HashMap<String, Meme> memes = new HashMap<>();
    private final static Thread js = new Thread();
    private final static HashMap<String, String> FCList = new HashMap<>();

    //Create our bot with the configuration
    private final static MultiBotManager manager = new MultiBotManager();

    private final static Configuration.Builder debugConfig = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#SSB")
            .addAutoJoinChannel("#FozruciX")
            .addAutoJoinChannel("#discordBotTest")
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder debugConfigSmwc = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#botTest")
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder twitchDebug = new Configuration.Builder()
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(new FozruciX(true, manager, noteList, avatar, memes, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder debugConfigEsper = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            .setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normal = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#pokemon")
            .addAutoJoinChannel("#retro")
            .addAutoJoinChannel("#retrotech")
            .addAutoJoinChannel("#SSB")
            .addAutoJoinChannel("#origami64")
            .addAutoJoinChannel("#FozruciX")
            .addAutoJoinChannel("#discordBotTest")
            .addAutoJoinChannel("#idkwtf")
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normalSmwc = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#pmd")
            .addAutoJoinChannel("#botTest")
            .addAutoJoinChannel("#unix")
            .addAutoJoinChannel("#smashbros")
            .addAutoJoinChannel("#homebrew")
            .addAutoJoinChannel("#radbusiness")
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder twitchNormal = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setAutoNickChange(false) //Twitch doesn't support multiple users
            .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
            .setCapEnabled(true)
            .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
            .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
            .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addAutoJoinChannel("#deltasmash")
            .addListener(new FozruciX(true, manager, noteList, avatar, memes, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normalEsper = new Configuration.Builder()
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            .addAutoJoinChannel("#ducks")
            .setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        //Before anything else
        IdentServer.startServer();

        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnickNET, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697));
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
        } else {
            manager.addBot(normal.buildForServer(badnickNET, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(normalEsper.buildForServer(esper, 6697));
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
        }
        //Connect to the server
        manager.start();
    }


}
