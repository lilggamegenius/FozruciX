using System;
using System.Net;
using System.Text;
using System.Windows.Forms;
using com.google.gson;
using com.rmtheis.yandtran;
using FozruciCS.DataStructs;
using FozruciCS.Utils;
using ikvm.extensions;
using java.lang;
using java.nio.charset;
using System.IO;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using NLog;
using org.pircbotx;
using org.pircbotx.cap;
using Exception = java.lang.Exception;

namespace FozruciCS {
	public enum Password {
		Normal,
		Twitch,
		Discord,
		Key,
		Salt,
		Ssh
	}

	public static class LocationRelativeToServer {
		public enum Locations {
			Self,
			Local,
			Global
		}

		public static readonly string[] Address = {
			"localhost",
			"10.0.0.63",
			"lilggamegenius.ml"
		};

		public static string getAddr(Locations locations){ return Address[(int) locations]; }
	}

	public static class FozConfig {
		public const bool Debug = false;
		public const string Badnik = "irc.badnik.zone";
		public const string Twitch = "irc.twitch.tv";
		public const string Caffie = "irc.caffie.net";
		public const string Esper = "irc.esper.net";
		public const string Nova = "irc.novasquirrel.com";
		public const string Rizon = "irc.rizon.io";

		public static readonly string LilGNet;

		//Configure what we want our bot to do
		public const string Nick = "FozruciX";

		public const string Login = "SmugLeaf";
		public const string KvircFlags = "\u00034\u000F";
		public const string RealName = KvircFlags + "* Why do i always get the freaks...";
		public static readonly MultiBotManager Manager = new MultiBotManager();
		public static readonly LocationRelativeToServer.Locations Location;
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();
		[JsonIgnore] public static readonly string Password = setPassword(FozruciCS.Password.Normal);
		private const string Bak = "Data/DataBak.json";
		private const string SaveFile = "Data/Data.json";
		private const int Attempts = 10;
		private const int ConnectDelay = 15 * 1000;
		private static readonly Newtonsoft.Json.JsonSerializer serializer = new Newtonsoft.Json.JsonSerializer();

		static FozConfig(){
			Application.EnableVisualStyles();
			Application.SetCompatibleTextRenderingDefault(false);


			serializer.Converters.Add(new JavaScriptDateTimeConverter());
			serializer.NullValueHandling = NullValueHandling.Ignore;

			loadData();
			var locationTemp = LocationRelativeToServer.Locations.Local;
			try{
				// Getting Ip address of local machine...
				// First get the host name of local machine.
				// Then using host name, get the IP address list..
				var ipEntry = Dns.GetHostEntry(Dns.GetHostName());
				var addrs = ipEntry.AddressList;
				foreach(var addr in addrs){
					Logger.Debug("Address is " + addr);
					if(addr.toString().startsWith("10.0.0.")){
						if(addr.toString().equalsIgnoreCase("10.0.0.63")){
							locationTemp = LocationRelativeToServer.Locations.Self;
							break;
						} //else
						locationTemp = LocationRelativeToServer.Locations.Local;
						break;
					} //else
					locationTemp = LocationRelativeToServer.Locations.Global;
				}
			}
			catch(ClassNotFoundException e){ Logger.Error("SQL Driver not found: {0}", e); }
			catch(Exception e2){ Logger.Error("Error: {0}", e2); }
			Location = locationTemp;
			if(Location == LocationRelativeToServer.Locations.Global){
				LilGNet = "irc." + LocationRelativeToServer.getAddr(Location);
			}
			else{ LilGNet = LocationRelativeToServer.getAddr(Location); }
		}

		public static readonly Configuration.Builder DebugLilGNetConfig = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			//.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#FozruciX")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder NormalLilGNetConfig = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			//.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#FozruciX")
			.addAutoJoinChannel("#chat")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder DebugConfig = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
			.addAutoJoinChannel("#SSB")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder DebugConfigSmwc = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#sm64")
			.addAutoJoinChannel("#botTest")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder TwitchDebug = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setEncoding(Charset.forName("UTF-8"))
			.setName(Nick.toLowerCase()) //Set the nick of the bot.
			.setLogin(Nick.toLowerCase())
			.addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
			.addListener(new FozruciX(Manager, Network.Twitch)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder DebugConfigEsper = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#savespam")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder DebugConfigNova = new Configuration.Builder() //same as normal for now
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#bots")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder Normal = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
			.addAutoJoinChannel("#retro")
			.addAutoJoinChannel("#pokemon")
			.addAutoJoinChannel("#retrotech")
			.addAutoJoinChannel("#SSB")
			.addAutoJoinChannel("#idkwtf")
			.addAutoJoinChannel("#ducks")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder NormalSmwc = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#sm64")
			.addAutoJoinChannel("#pmd")
			.addAutoJoinChannel("#botTest")
			.addAutoJoinChannel("#unix")
			.addAutoJoinChannel("#smashbros")
			.addAutoJoinChannel("#undertale")
			.addAutoJoinChannel("#homebrew")
			.addAutoJoinChannel("#radbusiness")
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder TwitchNormal = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setAutoNickChange(false) //Twitch doesn't support multiple users
			.setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
			.setCapEnabled(true)
			.addCapHandler(
				new EnableCapHandler(
					"twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
			.addCapHandler(new EnableCapHandler("twitch.tv/commands"))
			.addCapHandler(new EnableCapHandler("twitch.tv/tags"))
			.setName(Nick.toLowerCase()) //Set the nick of the bot.
			.setLogin(Nick.toLowerCase())
			.addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
			.addAutoJoinChannel("#deltasmash")
			.addListener(new FozruciX(Manager, Network.Twitch)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder NormalEsper = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#savespam")
			.addAutoJoinChannel("#ducks")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder NormalNova = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#bots")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder NormalRizon = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#origami64")
			.addAutoJoinChannel("#FozruciX")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events

		public static readonly Configuration.Builder DebugConfigRizon = new Configuration.Builder()
			.setAutoReconnectDelay(ConnectDelay)
			.setEncoding(Charset.forName("UTF-8"))
			.setAutoReconnect(true)
			.setAutoReconnectAttempts(Attempts)
			.setNickservPassword(CryptoUtil.decrypt(Password))
			.setName(Nick) //Set the nick of the bot.
			.setLogin(Login)
			.setRealName(RealName)
			.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
			.addAutoJoinChannel("#FozruciX")
			//.setIdentServerEnabled(true)
			.addListener(new FozruciX(Manager)); //Add our listener that will be called on Events


		//Create our bot with the configuration
		[STAThread]
		public static void main(string[] args){
			//Before anything else
			//IdentServer.startServer();
			Logger.Debug("Setting key");
			YandexTranslatorAPI.setKey(ApiKeys.YANDEX_API_KEY);

#pragma warning disable 162
#if DEBUG
				Manager.addBot(DebugConfig.buildForServer(Badnik, 6697));
				Manager.addBot(DebugConfigSmwc.buildForServer(Caffie, 6697));
				Manager.addBot(DebugConfigEsper.buildForServer(Esper, 6697));
				Manager.addBot(TwitchDebug.buildForServer(Twitch,
					6667,
					CryptoUtil.decrypt(setPassword(FozruciCS.Password.Twitch))));
				Manager.addBot(DebugConfigNova.buildForServer(Nova, 6697));
				Manager.addBot(DebugConfigRizon.buildForServer(Rizon, 9999));
				Manager.addBot(DebugLilGNetConfig.buildForServer(LilGNet, 6667));
#else
				Manager.addBot(Normal.buildForServer(Badnik, 6697));
				Manager.addBot(NormalSmwc.buildForServer(Caffie, 6697));
				Manager.addBot(NormalEsper.buildForServer(Esper, 6697));
				Manager.addBot(TwitchNormal.buildForServer(Twitch,
					6667,
					CryptoUtil.decrypt(setPassword(FozruciCS.Password.Twitch))));
				Manager.addBot(NormalNova.buildForServer(Nova, 6697));
				Manager.addBot(NormalRizon.buildForServer(Rizon, 9999));
				Manager.addBot(NormalLilGNetConfig.buildForServer(LilGNet, 6667));
#endif
#pragma warning restore 162
			//Connect to the server
			Manager.start();
		}

		public static string setPassword(Password password){
			string file;
			switch(password){
				case FozruciCS.Password.Normal:
					file = "pass.bin";
					break;
				case FozruciCS.Password.Twitch:
					file = "twitch.bin";
					break;
				case FozruciCS.Password.Discord:
					file = "discord.bin";
					break;
				case FozruciCS.Password.Key:
					file = "key.bin";
					break;
				case FozruciCS.Password.Salt:
					file = "salt.bin";
					break;
				case FozruciCS.Password.Ssh:
					file = "ssh.bin";
					break;
				default: throw new FileNotFoundException();
			}
			string ret;

			var fileContent = File.ReadAllBytes(file);
			ret = new string(Encoding.UTF8.GetString(fileContent).ToCharArray());
			return ret;
		}

		public static void loadData(){
			Logger.Info("Starting to loadData");
			if(!File.Exists(SaveFile)){
				Logger.Info("Save file doesn't exist. Attempting to load backup");
				try{
					//noinspection StatementWithEmptyBody
					//while(!bak.canWrite() || !saveFile.canWrite()){}
					File.Move(Bak, SaveFile);
					Logger.Info("Backup file moved");
				}
				catch(System.Exception e){
					Logger.Error("file in use: {0}", e);
					return;
				}
			}
			try{
				using(var jr = new JsonTextReader (new StreamReader(SaveFile))){
					Logger.Info("Attempting to load data");
					serializer.Deserialize<SaveDataStore>(jr);
				}
			}
			catch(Exception e){
				Logger.Error("failed loading data, Attempting to save empty copy: {0}", e);
				try{
					using(var writer = new JsonTextWriter(new StreamWriter("Data/DataEmpty.json"))){
						serializer.Serialize(writer, SaveDataStore);
					}
				}
				catch(Exception e1){ Logger.Error("Couldn't save data: {0}", e1); }
				Environment.Exit(1);
			}
		}

		public static void saveData(){
			try{ using(FileWriter writer = new FileWriter(Bak)){ writer.write(Gson.toJson(SaveDataStore.getINSTANCE())); } }
			catch(Exception e){ Logger.Error("Couldn't save data", e); }
			Files.move(Bak.toPath(), SaveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.Info("Data saved");
		}

		public static MultiBotManager getManager(){ return Manager; }
	}
}
