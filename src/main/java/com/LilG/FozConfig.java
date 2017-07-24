package com.LilG;

import ch.qos.logback.classic.Logger;
import com.LilG.DataClasses.Meme;
import com.LilG.DataClasses.Note;
import com.LilG.DataClasses.SaveDataStore;
import com.LilG.Serialization.GuildKeyDeserializer;
import com.LilG.Serialization.ISnowflakeSerializer;
import com.LilG.Serialization.MixInModule;
import com.LilG.Serialization.TextChannelKeyDeserializer;
import com.LilG.utils.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.rmtheis.yandtran.ApiKeys;
import com.rmtheis.yandtran.YandexTranslatorAPI;
import com.rmtheis.yandtran.detect.Detect;
import com.rmtheis.yandtran.translate.Translate;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */

public class FozConfig {
	public final static boolean debug = true;
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
	public final static LocationRelativeToServer location;
	public final static edu.cmu.sphinx.api.Configuration configuration = new edu.cmu.sphinx.api.Configuration();
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FozConfig.class);
	transient final static String PASSWORD = setPassword(Password.normal);
	private final static File bak = new File("Data/DataBak.json");
	private final static File saveFile = new File("Data/Data.json");
	private final static int attempts = 10;
	private final static int connectDelay = 15 * 1000;
	//private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final static ObjectMapper objectMapper = new ObjectMapper();
	public final static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	public static JDA jda;
	static Thread game;
	static Thread avatar;

	static {
		String token = CryptoUtil.decrypt(FozConfig.setPassword(FozConfig.Password.discord));
		LOGGER.trace("Calling JDA Builder with token: " + token);
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(token)
					.setAutoReconnect(true)
					.setAudioEnabled(true)
					.setEnableShutdownHook(true)
					.buildAsync();
			game = new GameThread(jda.getPresence());
			game.setName("Game Setter thread");
			game.start();
		} catch (LoginException | RateLimitedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		SimpleModule module = new SimpleModule();
		module.addKeyDeserializer(Guild.class, new GuildKeyDeserializer());
		module.addKeyDeserializer(TextChannel.class, new TextChannelKeyDeserializer());

		module.addSerializer(ISnowflake.class, new ISnowflakeSerializer());
		//module.addSerializer(Guild.class, new ISnowflakeSerializer());
		//module.addSerializer(TextChannel.class, new ISnowflakeSerializer());
		objectMapper.registerModule(module);
		objectMapper.registerModule(new MixInModule());
		loadData();
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		AudioSourceManagers.registerRemoteSources(playerManager);
		playerManager.registerSourceManager(new LocalAudioSourceManager());
		LocationRelativeToServer locationTemp = LocationRelativeToServer.local;
		try {
			System.setProperty("jna.library.path", "M68k");
			System.setProperty("jna.debug_load", "true");
			System.setProperty("jna.debug_load.jna", "true");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			getAddress:
			for (; n.hasMoreElements(); ) {
				Enumeration<InetAddress> inetAddresses = n.nextElement().getInetAddresses();
				for (; inetAddresses.hasMoreElements(); ) {
					InetAddress inetAddress = inetAddresses.nextElement();
					if (inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress())
						continue;
					String address = inetAddress.getHostAddress();
					LOGGER.debug("Address is " + address);
					if (address.startsWith("192.168.1.")) {
						if (address.equalsIgnoreCase("192.168.1.178")) {
							locationTemp = LocationRelativeToServer.self;
							break getAddress;
						} else {
							locationTemp = LocationRelativeToServer.local;
							break getAddress;
						}
					} else {
						locationTemp = LocationRelativeToServer.global;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("SQL Driver not found", e);
		} catch (Exception e2) {
			LOGGER.error("Error", e2);
		}
		location = locationTemp;
		if (location == LocationRelativeToServer.global) {
			Lil_G_Net = "irc." + location.address;
		} else {
			Lil_G_Net = location.address;
		}
	}

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
	public final static Configuration.Builder twitchDebug = new Configuration.Builder()
			.setAutoReconnectDelay(connectDelay)
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(attempts)
			.setEncoding(Charset.forName("UTF-8"))
			.setName(nick.toLowerCase()) //Set the nick of the bot.
			.setLogin(nick.toLowerCase())
			.addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
			.addListener(new FozruciX(FozruciX.Network.twitch, manager)); //Add our listener that will be called on Events
	public final static Configuration.Builder debugConfigEsper = new Configuration.Builder()
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
			.addAutoJoinChannel("#ducks")
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
			.addListener(new FozruciX(FozruciX.Network.twitch, manager)); //Add our listener that will be called on Events
	public final static Configuration.Builder normalEsper = new Configuration.Builder()
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
			manager.addBot(twitchDebug.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
			manager.addBot(debugConfigNova.buildForServer(nova, 6697));
			manager.addBot(debugConfigRizon.buildForServer(rizon, 9999));
			manager.addBot(debugLil_G_NetConfig.buildForServer(Lil_G_Net, 6667));
		} else {
			manager.addBot(normal.buildForServer(badnik, 6697));
			manager.addBot(normalSmwc.buildForServer(caffie, 6697));
			manager.addBot(normalEsper.buildForServer(esper, 6697));
			manager.addBot(twitchNormal.buildForServer(twitch, 6667, CryptoUtil.decrypt(setPassword(Password.twitch))));
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
		String ret = " ";
		try (FileInputStream fin = new FileInputStream(file)) {
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
		}
		return ret;
	}

	public static synchronized void loadData() {
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
				return;
			} catch (Exception e) {
				LOGGER.error("failed renaming backup file", e);
			}
		}
		try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
			LOGGER.info("Attempting to load data");
			//SaveDataStore.setINSTANCE(gson.fromJson(br, SaveDataStore.class));
			SaveDataStore.setINSTANCE(objectMapper.readValue(br, SaveDataStore.class));
			if (SaveDataStore.getINSTANCE() != null) {
				LOGGER.info("Loaded data");
			}
		} catch (Exception e) {
			LOGGER.error("failed loading data, Attempting to save empty copy", e);
			try (FileWriter writer = new FileWriter(new File("Data/DataEmpty.json"))) {
				SaveDataStore temp = new SaveDataStore();
				temp.getNoteList().add(new Note("sender", "receiver", "message", "channel"));
				temp.getAuthedUser().put("name", 0);
				temp.getMemes().put("meme", new Meme("creator", "meme"));
				temp.getFCList().put("name", "fc");
				Map<String, List<String>> tempMap = new HashMap<>();
				List<String> tempArray = new ArrayList<>();
				tempArray.add("command");
				tempMap.put("channel", tempArray);
				temp.getAllowedCommands().put("server", tempMap);
				temp.getCheckJoinsAndQuits().put("server", "channel");
				temp.getMutedServerList().add("server");
				tempArray.clear();
				tempArray.add("filter");
				temp.getWordFilter().put(jda.getTextChannels().get(0).getId(), tempArray);
				//temp.getGuildGuildEXMap().put(jda.getGuilds().get(0), GuildEX.getGuildEX(jda.getGuilds().get(0)));
				tempArray.clear();
				tempArray.add("word");
				temp.getMarkovChain().put("key", tempArray);
				//writer.write(gson.toJson(temp));
				objectMapper.writeValue(writer, temp);
			} catch (Exception e1) {
				LOGGER.error("Couldn't save empty data", e1);
			}
			System.exit(1);
		}
	}

	public static synchronized void saveData() throws IOException {
		try (FileWriter writer = new FileWriter(bak)) {
			//writer.write(gson.toJson(SaveDataStore.getINSTANCE()));
			objectMapper.writeValue(writer, SaveDataStore.getINSTANCE());
			Files.move(bak.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Data saved");
		} catch (Exception e) {
			LOGGER.error("Couldn't save data", e);
		}
	}

	public static MultiBotManager getManager() {
		return manager;
	}

	public enum Password {
		normal, twitch, discord, key, salt, ssh
	}

	private enum LocationRelativeToServer {
		self("localhost"),
		local("192.168.1.192"),
		global("lilggamegenius.ml");

		public final String address;

		LocationRelativeToServer(String address) {
			this.address = address;
		}
	}

}
