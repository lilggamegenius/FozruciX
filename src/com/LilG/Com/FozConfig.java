package com.LilG.Com;

import com.LilG.Com.DataClasses.SaveDataStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

public class FozConfig {
    public final static boolean debug = false;
    public final static String badnik = "irc.badnik.zone"; //TL;DR Shit went down
    public final static String twitch = "irc.twitch.tv";
    public final static String caffie = "irc.caffie.net";
    public final static String esper = "irc.esper.net";
    public final static String nova = "irc.novasquirrel.net";
    //Configure what we want our bot to do
    private final static String nick = "FozruciX";
    private final static String login = "SmugLeaf";
    private final static String realName = "\u00034\u000F* What can I do for you, little buddy?";
    private final static int attempts = 20;
    private final static int connectDelay = 5; //5 seconds
    //Create our bot with the configuration
    private final static MultiBotManager manager = new MultiBotManager();
    private final static SaveDataStore save = loadData(new GsonBuilder().setPrettyPrinting().create());

    public final static Configuration.Builder debugConfig = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigSmwc = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder twitchDebug = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(new FozruciX(true, manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigEsper = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigNova = new Configuration.Builder() //same as normal for now
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder normal = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalSmwc = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder twitchNormal = new Configuration.Builder()
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
            .addListener(new FozruciX(true, manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalEsper = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalNova = new Configuration.Builder()
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events

    static {
        System.loadLibrary("JNIThing");
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        //Before anything else
        //IdentServer.startServer();


        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnik, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697));
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
            manager.addBot(debugConfigNova.buildForServer(nova, 6697));
        } else {
            manager.addBot(normal.buildForServer(badnik, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(normalEsper.buildForServer(esper, 6697));
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, FozruciX.setPassword(false)));
            manager.addBot(normalNova.buildForServer(nova, 6697));
        }
        //Connect to the server
        manager.start();
    }

    public static synchronized SaveDataStore loadData(Gson GSON) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("Data/Data.json"));
            return GSON.fromJson(br, SaveDataStore.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void saveData(@NotNull SaveDataStore save, Gson GSON) throws IOException {
        FileWriter writer = new FileWriter("Data/Data.json");
        writer.write(GSON.toJson(save));
        writer.close();
    }

}
