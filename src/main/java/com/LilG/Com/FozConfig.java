package com.LilG.Com;

import ch.qos.logback.classic.Logger;
import com.LilG.Com.DataClasses.SaveDataStore;
import com.LilG.Com.utils.CryptoUtil;
import com.rmtheis.yandtran.ApiKeys;
import com.rmtheis.yandtran.YandexTranslatorAPI;
import com.rmtheis.yandtran.detect.Detect;
import com.rmtheis.yandtran.translate.Translate;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Inet4Address;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

public class FozConfig {
    public final static boolean debug = false;
    public final static String badnik = "irc.badnik.zone";
    public final static String twitch = "irc.twitch.tv";
    public final static String caffie = "irc.caffie.net";
    public final static String esper = "irc.esper.net";
    public final static String nova = "irc.novasquirrel.com";
    public final static String rizon = "irc.rizon.io";
    public final static String Lil_G_Net;
    //Configure what we want our bot to do
    public final static String nick = "FozruciX";
    public final static String login = "SmugLeaf";
    public final static String kvircFlags = "\u00034\u000F";
    public final static String realName = kvircFlags + "* Why do i always get the freaks...";
    public final static MultiBotManager manager = new MultiBotManager();
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FozConfig.class);
    transient final static String PASSWORD = setPassword(Password.normal);
    private final static File bak = new File("Data/DataBak.xml");
    private final static File saveFile = new File("Data/Data.xml");
    private final static LocationRelativeToServer location;
    private final static int attempts = Integer.MAX_VALUE;
    private final static int connectDelay = 5 * 1000; //5 seconds
    public final static Configuration.Builder debugConfig = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#SSB")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigSmwc = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#botTest")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    /*public final static Configuration.Builder twitchDebug = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(new FozruciX(FozruciX.Network.twitch, manager)); //Add our listener that will be called on Events
    */public final static Configuration.Builder debugConfigEsper = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigNova = new Configuration.Builder() //same as normal for now
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugLil_G_NetConfig = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            //.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#FozruciX")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder normal = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#retro")
            .addAutoJoinChannel("#pokemon")
            .addAutoJoinChannel("#retrotech")
            .addAutoJoinChannel("#SSB")
            .addAutoJoinChannel("#idkwtf")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalSmwc = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#pmd")
            .addAutoJoinChannel("#botTest")
            .addAutoJoinChannel("#unix")
            .addAutoJoinChannel("#smashbros")
            .addAutoJoinChannel("#undertale")
            .addAutoJoinChannel("#homebrew")
            .addAutoJoinChannel("#radbusiness")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    /*public final static Configuration.Builder twitchNormal = new Configuration.Builder()
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
            .addListener(new FozruciX(FozruciX.Network.twitch, manager)); //Add our listener that will be called on Events
    */public final static Configuration.Builder normalEsper = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            .addAutoJoinChannel("#ducks")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalNova = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalRizon = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#origami64")
            .addAutoJoinChannel("#FozruciX")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder normalLil_G_NetConfig = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            //.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#FozruciX")
            .addAutoJoinChannel("#chat")
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events
    public final static Configuration.Builder debugConfigRizon = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#FozruciX")
            //.setIdentServerEnabled(true)
            .addListener(new FozruciX(manager)); //Add our listener that will be called on Events

    static {
        XStream xStream = new XStream();
        xStream.ignoreUnknownElements();
        loadData(xStream);
        LocationRelativeToServer locationTemp = null;
        try {
            System.setProperty("jna.library.path", "jni");
            System.setProperty("jna.debug_load", "true");
            System.setProperty("jna.debug_load.jna", "true");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String address = Inet4Address.getLocalHost().getHostAddress();
            LOGGER.debug("Address is " + address);
            if (address.startsWith("10.0.0.")) {
                if (address.equalsIgnoreCase("10.0.0.63")) {
                    locationTemp = LocationRelativeToServer.self;
                } else {
                    locationTemp = LocationRelativeToServer.local;
                }
            } else {
                locationTemp = LocationRelativeToServer.global;
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("SQL Driver not found", e);
        } catch (Exception e2) {
            LOGGER.error("Error", e2);
        }
        location = locationTemp;
        switch (location) {
            case self:
                Lil_G_Net = "localhost";
                break;
            case local:
                Lil_G_Net = "10.0.0.63";
                break;
            case global:
                Lil_G_Net = "irc.lilggamegenuis.tk";
                break;
            default:
                Lil_G_Net = "you fucking broke it";
        }
    }
    //Create our bot with the configuration

    public static void main(String[] args) throws Exception {

        //Before anything else
        //IdentServer.startServer();
        LOGGER.debug("Setting key");
        YandexTranslatorAPI.setKey(ApiKeys.YANDEX_API_KEY);
        Translate.setKey(ApiKeys.YANDEX_API_KEY);
        Detect.setKey(ApiKeys.YANDEX_API_KEY);

        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnik, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697));
            //manager.addBot(twitchDebug.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
            manager.addBot(debugConfigNova.buildForServer(nova, 6697));
            manager.addBot(debugConfigRizon.buildForServer(rizon, 9999));
            manager.addBot(debugLil_G_NetConfig.buildForServer(Lil_G_Net, 6667));
        } else {
            manager.addBot(normal.buildForServer(badnik, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(normalEsper.buildForServer(esper, 6697));
            //manager.addBot(twitchNormal.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
            manager.addBot(normalNova.buildForServer(nova, 6697));
            manager.addBot(normalRizon.buildForServer(rizon, 9999));
            manager.addBot(normalLil_G_NetConfig.buildForServer(Lil_G_Net, 6667));
        }
        //Connect to the server
        manager.start();
    }

    @NotNull
    public static String setPassword(Password password) {
        @NotNull File file;
        if (password == Password.normal) {
            file = new File("pass.bin");
        } else if (password == Password.twitch) {
            file = new File("twitch.bin");
        } else if (password == Password.discord) {
            file = new File("discord.bin");
        } else if (password == Password.key) {
            file = new File("key.bin");
        } else if (password == Password.salt) {
            file = new File("salt.bin");
        } else if (password == Password.ssh) {
            file = new File("ssh.bin");
        } else {
            throw new RuntimeException("Can't find file specified");
        }
        FileInputStream fin = null;
        String ret = " ";
        try {
            // create FileInputStream object
            fin = new FileInputStream(file);

            byte fileContent[] = new byte[(int) file.length()];

            // Reads up to certain bytes of data from this input stream into an array of bytes.
            //noinspection ResultOfMethodCallIgnored
            fin.read(fileContent);
            //create string from byte array
            ret = new String(fileContent);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (IOException ioe) {
            LOGGER.error("Exception while reading file", ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                LOGGER.error("Error while closing stream", ioe);
            }
        }
        return ret;
    }

    public static synchronized void loadData(XStream xstream) {
        LOGGER.info("Starting to loadData");
        xstream.ignoreUnknownElements();
        if (!saveFile.exists()) {
            LOGGER.info("Save file doesn't exist. Attempting to load backup");
            try {
                //noinspection StatementWithEmptyBody
                //while(!bak.canWrite() || !saveFile.canWrite()){}
                Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Backup file moved");
            } catch (java.nio.file.FileSystemException e) {
                LOGGER.error("file in use", e);
                return;
            } catch (Exception e) {
                LOGGER.error("failed renaming backup file", e);
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
            LOGGER.info("Attempting to load data");
            SaveDataStore.setINSTANCE((SaveDataStore) xstream.fromXML(br));
            if (SaveDataStore.getINSTANCE() != null) {
                LOGGER.info("Loaded data");
            }
        } catch (Exception e) {
            LOGGER.error("failed loading data, Attempting to save empty copy", e);
            try (FileWriter writer = new FileWriter(new File("Data/DataEmpty.xml"))) {
                xstream.ignoreUnknownElements();
                xstream.toXML(new SaveDataStore(), writer);
            } catch (Exception e1) {
                LOGGER.error("Couldn't save data", e1);
            }
            System.exit(1);
        }
    }

    public static synchronized void saveData(XStream xstream) throws IOException {
        try (FileWriter writer = new FileWriter(bak)) {
            xstream.ignoreUnknownElements();
            xstream.toXML(SaveDataStore.getINSTANCE(), writer);
        } catch (Exception e) {
            LOGGER.error("Couldn't save data", e);
        }
        Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("Data saved");
    }

    public static MultiBotManager getManager() {
        return manager;
    }

    public enum Password {
        normal, twitch, discord, key, salt, ssh
    }

    private enum LocationRelativeToServer {
        self, local, global
    }

}
