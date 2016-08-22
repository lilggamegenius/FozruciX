package com.LilG.Com;

import ch.qos.logback.classic.Logger;
import com.LilG.Com.DataClasses.SaveDataStore;
import com.LilG.Com.utils.CryptoUtil;
import com.LilG.Com.utils.LilGUtil;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

public class FozConfig {
    public final static boolean debug = true;
    public final static String badnik = "irc.badnik.zone"; //TL;DR Shit went down
    public final static String twitch = "irc.twitch.tv";
    public final static String caffie = "irc.caffie.net";
    public final static String esper = "irc.esper.net";
    public final static String nova = "irc.novasquirrel.com";
    //Configure what we want our bot to do
    private final static String nick = "FozruciX";
    private final static String login = "SmugLeaf";
    private final static String realName = "\u00034\u000F* What can I do for you, little buddy?";
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FozConfig.class);
    transient final static String PASSWORD = setPassword(Password.normal);
    private final static int attempts = Integer.MAX_VALUE;
    private final static int connectDelay = 5 * 1000; //5 seconds
    //Create our bot with the configuration
    private final static File bak = new File("Data/DataBak.xml");
    private final static File saveFile = new File("Data/Data.xml");
    private final static MultiBotManager manager = new MultiBotManager();
    private final static SaveDataStore save = loadData(new XStream());

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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
    /*public final static Configuration.Builder twitchDebug = new Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(new FozruciX(FozruciX.Network.twitch, manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addAutoJoinChannel("#origami64")
            .addAutoJoinChannel("#idkwtf")
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(FozruciX.Network.twitch, manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events
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
            .addListener(new FozruciX(manager, save)); //Add our listener that will be called on Events

    static {
        try {
            LilGUtil.loadLib("JNIThing");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException e) {
            LOGGER.error("SQL Driver not found", e);
        } catch (Exception e2) {
            LOGGER.error("Error", e2);
        }
    }

    public static void main(String[] args) throws Exception {

        //Before anything else
        //IdentServer.startServer();


        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnik, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697));
            //manager.addBot(twitchDebug.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
            manager.addBot(debugConfigNova.buildForServer(nova, 6697));
        } else {
            manager.addBot(normal.buildForServer(badnik, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(normalEsper.buildForServer(esper, 6697));
            //manager.addBot(twitchNormal.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
            manager.addBot(normalNova.buildForServer(nova, 6697));
        }
        //Connect to the server
        manager.start();
    }

    @NotNull
    public static String setPassword(Password password) {
        @SuppressWarnings("ConstantConditions") @NotNull
        File file = null;
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

    public static synchronized SaveDataStore loadData(XStream xstream) {
        LOGGER.info("Starting to loadData");
        if (!saveFile.exists()) {
            LOGGER.info("Save file doesn't exist. Attempting to load backup");
            try {
                //noinspection StatementWithEmptyBody
                //while(!bak.canWrite() || !saveFile.canWrite()){}
                Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Backup file moved");
            } catch (java.nio.file.FileSystemException e) {
                LOGGER.error("file in use", e);
                return null;
            } catch (Exception e) {
                LOGGER.error("failed renaming backup file", e);
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
            LOGGER.info("Attempting to load data");
            SaveDataStore save = (SaveDataStore) xstream.fromXML(br);
            if (save == null) throw new FileNotFoundException("Couldn't find save data");
            save = new SaveDataStore(save.getAuthedUser(), save.getAuthedUserLevel(), save.getDNDJoined(), save.getDNDList(), save.getNoteList(), save.getAvatarLink(), save.getMemes(), save.getFCList(), save.getMarkovChain(), save.getAllowedCommands(), save.getCheckJoinsAndQuits());
            LOGGER.info("Loaded data");
            return save;
        } catch (Exception e) {
            LOGGER.error("failed loading data", e);
            System.exit(1);
        }
        return null;
    }

    public static synchronized void saveData(@NotNull SaveDataStore save, XStream xstream) throws IOException {
        try (FileWriter writer = new FileWriter(bak)) {
            xstream.toXML(save, writer);
        } catch (Exception e) {
            LOGGER.error("Couldn't save data", e);
        }
        Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static MultiBotManager getManager() {
        return manager;
    }

    public enum Password {
        normal, twitch, discord, key, salt
    }

}
