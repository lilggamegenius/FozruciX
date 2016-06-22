package com.LilG.Com;

import com.LilG.Com.CMD.CommandLine;
import com.LilG.Com.DataClasses.Meme;
import com.LilG.Com.DataClasses.Note;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.TreeMap;

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
    private final static String badnickNET = "irc.tcrf.net"; //TL;DR Shit went down
    private final static String twitch = "irc.twitch.tv";
    private final static String caffie = "irc.caffie.net";
    private final static String esper = "irc.esper.net";
    private final static String nova = "irc.novasquirrel.net";
    private final static int attempts = 20;
    private final static int connectDelay = 5; //5 seconds

    //Bot universal variables
    private final static LinkedList<Note> noteList = new LinkedList<>();
    private final static CommandLine terminal = new CommandLine();
    private final static String avatar = "http://puu.sh/oiLYR.gif";
    private final static TreeMap<String, Meme> memes = new TreeMap<>();
    private final static Thread js = new Thread();
    private final static TreeMap<String, String> FCList = new TreeMap<>();

    //Create our bot with the configuration
    private final static MultiBotManager manager = new MultiBotManager();

    private final static Configuration.Builder debugConfig = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
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
            .setAutoReconnectDelay(connectDelay)
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
            .setAutoReconnectDelay(connectDelay)
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(new FozruciX(true, manager, noteList, avatar, memes, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder debugConfigEsper = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder debugConfigNova = new Configuration.Builder() //same as normal for now
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normal = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#retro")
            .addAutoJoinChannel("#pokemon")
            .addAutoJoinChannel("#retrotech")
            .addAutoJoinChannel("#SSB")
            .addAutoJoinChannel("#origami64")
            .addAutoJoinChannel("#FozruciX")
            .addAutoJoinChannel("#discordBotTest")
            .addAutoJoinChannel("#idkwtf")
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normalSmwc = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
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
            .setAutoReconnectDelay(connectDelay)
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
            .setAutoReconnectDelay(connectDelay)
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
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events

    private final static Configuration.Builder normalNova = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(FozruciX.setPassword(true))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager, noteList, terminal, avatar, memes, js, FCList)); //Add our listener that will be called on Events


    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        //Before anything else
        //IdentServer.startServer();


        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnickNET, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697));
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
            manager.addBot(debugConfigNova.buildForServer(nova, 6697));
        } else {
            manager.addBot(normal.buildForServer(badnickNET, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(normalEsper.buildForServer(esper, 6697));
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
            manager.addBot(normalNova.buildForServer(nova, 6697));
        }
        //Connect to the server
        manager.start();
    }


}
