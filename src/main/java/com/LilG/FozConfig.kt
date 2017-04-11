package com.LilG

import ch.qos.logback.classic.Logger
import com.LilG.DataClasses.SaveDataStore
import com.LilG.utils.CryptoUtil
import com.google.gson.GsonBuilder
import com.rmtheis.yandtran.ApiKeys
import com.rmtheis.yandtran.YandexTranslatorAPI
import com.rmtheis.yandtran.detect.Detect
import com.rmtheis.yandtran.translate.Translate
import org.pircbotx.Configuration
import org.pircbotx.MultiBotManager
import org.pircbotx.UtilSSLSocketFactory
import org.pircbotx.cap.EnableCapHandler
import org.slf4j.LoggerFactory
import java.io.*
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

object FozConfig {
    val debug = false
    val badnik = "irc.badnik.zone"
    val twitch = "irc.twitch.tv"
    val caffie = "irc.caffie.net"
    val esper = "irc.esper.net"
    val nova = "irc.novasquirrel.com"
    val rizon = "irc.rizon.io"
    val Lil_G_Net: String
    //Configure what we want our bot to do
    val nick = "FozruciX"
    val login = "SmugLeaf"
    val kvircFlags = "\u00034\u000F"
    val realName = kvircFlags + "* Why do i always get the freaks..."
    val manager = MultiBotManager()
    val location: LocationRelativeToServer
    private val LOGGER = LoggerFactory.getLogger(FozConfig::class.java) as Logger
    @Transient internal val PASSWORD = setPassword(Password.normal)
    private val bak = File("Data/DataBak.json")
    private val saveFile = File("Data/Data.json")
    private val attempts = 10
    private val connectDelay = 15 * 1000
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        loadData()
        var locationTemp = LocationRelativeToServer.local
        try {
            System.setProperty("jna.library.path", "M68k")
            System.setProperty("jna.debug_load", "true")
            System.setProperty("jna.debug_load.jna", "true")
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            val n = NetworkInterface.getNetworkInterfaces()
            getAddress@ while (n.hasMoreElements()) {
                val inetAddresses = n.nextElement().inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (inetAddress.isLoopbackAddress || inetAddress.isLinkLocalAddress || inetAddress.isMulticastAddress)
                        continue
                    val address = inetAddress.hostAddress
                    LOGGER.debug("Address is " + address)
                    if (address.startsWith("10.0.0.")) {
                        if (address.equals("10.0.0.63", ignoreCase = true)) {
                            locationTemp = LocationRelativeToServer.self
                            break@getAddress
                        } else {
                            locationTemp = LocationRelativeToServer.local
                            break@getAddress
                        }
                    } else {
                        locationTemp = LocationRelativeToServer.global
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            LOGGER.error("SQL Driver not found", e)
        } catch (e2: Exception) {
            LOGGER.error("Error", e2)
        }

        location = locationTemp
        if (location == LocationRelativeToServer.global) {
            Lil_G_Net = "irc." + location.address
        } else {
            Lil_G_Net = location.address
        }
    }

    val debugLil_G_NetConfig: Configuration.Builder = Configuration.Builder()
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
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val normalLil_G_NetConfig: Configuration.Builder = Configuration.Builder()
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
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val debugConfig: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#SSB")
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val debugConfigSmwc: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#botTest")
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val twitchDebug: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setEncoding(Charset.forName("UTF-8"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addListener(FozruciX(FozruciX.Network.twitch, manager)) //Add our listener that will be called on Events
    val debugConfigEsper: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val debugConfigNova: Configuration.Builder = Configuration.Builder() //same as normal for now
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val normal: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
            .addAutoJoinChannel("#retro")
            .addAutoJoinChannel("#pokemon")
            .addAutoJoinChannel("#retrotech")
            .addAutoJoinChannel("#SSB")
            .addAutoJoinChannel("#idkwtf")
            .addAutoJoinChannel("#ducks")
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val normalSmwc: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#sm64")
            .addAutoJoinChannel("#pmd")
            .addAutoJoinChannel("#botTest")
            .addAutoJoinChannel("#unix")
            .addAutoJoinChannel("#smashbros")
            .addAutoJoinChannel("#undertale")
            .addAutoJoinChannel("#homebrew")
            .addAutoJoinChannel("#radbusiness")
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val twitchNormal: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setAutoNickChange(false) //Twitch doesn't support multiple users
            .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
            .setCapEnabled(true)
            .addCapHandler(EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
            .addCapHandler(EnableCapHandler("twitch.tv/commands"))
            .addCapHandler(EnableCapHandler("twitch.tv/tags"))
            .setName(nick.toLowerCase()) //Set the nick of the bot.
            .setLogin(nick.toLowerCase())
            .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
            .addAutoJoinChannel("#deltasmash")
            .addListener(FozruciX(FozruciX.Network.twitch, manager)) //Add our listener that will be called on Events
    val normalEsper: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#savespam")
            .addAutoJoinChannel("#ducks")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val normalNova: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#bots")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val normalRizon: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#origami64")
            .addAutoJoinChannel("#FozruciX")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events
    val debugConfigRizon: Configuration.Builder = Configuration.Builder()
            .setAutoReconnectDelay(connectDelay)
            .setEncoding(Charset.forName("UTF-8"))
            .setAutoReconnect(true)
            .setAutoReconnectAttempts(attempts)
            .setNickservPassword(CryptoUtil.decrypt(PASSWORD))
            .setName(nick) //Set the nick of the bot.
            .setLogin(login)
            .setRealName(realName)
            .setSocketFactory(UtilSSLSocketFactory().trustAllCertificates())
            .addAutoJoinChannel("#FozruciX")
            //.setIdentServerEnabled(true)
            .addListener(FozruciX(manager)) //Add our listener that will be called on Events


    //Create our bot with the configuration

    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {

        //Before anything else
        //IdentServer.startServer();
        LOGGER.debug("Setting key")
        YandexTranslatorAPI.setKey(ApiKeys.YANDEX_API_KEY)
        Translate.setKey(ApiKeys.YANDEX_API_KEY)
        Detect.setKey(ApiKeys.YANDEX_API_KEY)

        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnik, 6697))
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697))
            manager.addBot(debugConfigEsper.buildForServer(esper, 6697))
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))))
            manager.addBot(debugConfigNova.buildForServer(nova, 6697))
            manager.addBot(debugConfigRizon.buildForServer(rizon, 9999))
            manager.addBot(debugLil_G_NetConfig.buildForServer(Lil_G_Net, 6667))
        } else {
            manager.addBot(normal.buildForServer(badnik, 6697))
            manager.addBot(normalSmwc.buildForServer(caffie, 6697))
            manager.addBot(normalEsper.buildForServer(esper, 6697))
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))))
            manager.addBot(normalNova.buildForServer(nova, 6697))
            manager.addBot(normalRizon.buildForServer(rizon, 9999))
            manager.addBot(normalLil_G_NetConfig.buildForServer(Lil_G_Net, 6667))
        }
        //Connect to the server
        manager.start()
    }

    fun setPassword(password: Password): String {
        val file: File
        if (password == Password.normal) {
            file = File("pass.bin")
        } else if (password == Password.twitch) {
            file = File("twitch.bin")
        } else if (password == Password.discord) {
            file = File("discord.bin")
        } else if (password == Password.key) {
            file = File("key.bin")
        } else if (password == Password.salt) {
            file = File("salt.bin")
        } else if (password == Password.ssh) {
            file = File("ssh.bin")
        } else {
            throw RuntimeException("Can't find file specified")
        }
        var ret = " "
        try {
            FileInputStream(file).use { fin ->
                val fileContent = ByteArray(file.length().toInt())

                // Reads up to certain bytes of data from this input stream into an array of bytes.

                fin.read(fileContent)
                //create string from byte array
                ret = String(fileContent)
            }
        } catch (e: FileNotFoundException) {
            LOGGER.error("File not found", e)
        } catch (ioe: IOException) {
            LOGGER.error("Exception while reading file", ioe)
        }

        return ret
    }

    @Synchronized fun loadData() {
        LOGGER.info("Starting to loadData")
        if (!saveFile.exists()) {
            LOGGER.info("Save file doesn't exist. Attempting to load backup")
            try {

                //while(!bak.canWrite() || !saveFile.canWrite()){}
                Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                LOGGER.info("Backup file moved")
            } catch (e: java.nio.file.FileSystemException) {
                LOGGER.error("file in use", e)
                return
            } catch (e: Exception) {
                LOGGER.error("failed renaming backup file", e)
            }

        }
        try {
            BufferedReader(FileReader(saveFile)).use { br ->
                LOGGER.info("Attempting to load data")
                SaveDataStore.instance = gson.fromJson(br, SaveDataStore::class.java)
                if (SaveDataStore.instance != null) {
                    LOGGER.info("Loaded data")
                }
            }
        } catch (e: Exception) {
            LOGGER.error("failed loading data, Attempting to save empty copy", e)
            try {
                FileWriter(File("Data/DataEmpty.json")).use { writer -> writer.write(gson.toJson(SaveDataStore())) }
            } catch (e1: Exception) {
                LOGGER.error("Couldn't save data", e1)
            }

            System.exit(1)
        }

    }

    @Synchronized @Throws(IOException::class)
    fun saveData() {
        try {
            FileWriter(bak).use { writer -> writer.write(gson.toJson(SaveDataStore.instance)) }
        } catch (e: Exception) {
            LOGGER.error("Couldn't save data", e)
        }

        Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        LOGGER.info("Data saved")
    }

    enum class Password {
        normal, twitch, discord, key, salt, ssh
    }

    enum class LocationRelativeToServer private constructor(val address: String) {
        self("localhost"),
        local("10.0.0.63"),
        global("lilggamegenius.ml")
    }

}
