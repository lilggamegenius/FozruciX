using System;
using System.Net;
using System.Text;
using com.google.gson;
using com.rmtheis.yandtran;
using FozruciCS.Utils;
using ikvm.extensions;
using java.io;
using java.lang;
using java.nio.charset;
using java.nio.file;
using Newtonsoft.Json;
using NLog;
using org.pircbotx;
using org.pircbotx.cap;
using Exception = java.lang.Exception;

namespace FozruciCS {

    public enum Password {
        Normal, Twitch, Discord, Key, Salt, Ssh
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

        public static string getAddr(Locations locations){
            return Address[(int)locations];
        }
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
        private static readonly File Bak = new File("Data/DataBak.json");
        private static readonly File SaveFile = new File("Data/Data.json");
        private const int Attempts = 10;
        private const int ConnectDelay = 15 * 1000;
        private static readonly Gson Gson = new GsonBuilder().setPrettyPrinting().create();

        static FozConfig(){
            loadData();
            LocationRelativeToServer.Locations locationTemp = LocationRelativeToServer.Locations.Local;
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                // Getting Ip address of local machine...
                // First get the host name of local machine.
                // Then using host name, get the IP address list..
                IPHostEntry ipEntry = Dns.GetHostEntry(Dns.GetHostName());
                IPAddress[] addrs = ipEntry.AddressList;
                foreach (IPAddress addr in addrs){
                    Logger.Debug("Address is " + addr);
                    if (addr.toString().startsWith("10.0.0.")) {
                        if (addr.toString().equalsIgnoreCase("10.0.0.63")) {
                            locationTemp = LocationRelativeToServer.Locations.Self;
                            break;
                        } //else
                        locationTemp = LocationRelativeToServer.Locations.Local;
                        break;
                    } //else
                    locationTemp = LocationRelativeToServer.Locations.Global;
                }
            } catch (ClassNotFoundException e) {
                Logger.Error("SQL Driver not found: {0}", e);
            } catch (Exception e2) {
                Logger.Error("Error: {0}", e2);
            }
            Location = locationTemp;
            if (Location == LocationRelativeToServer.Locations.Global) {
                LilGNet = "irc." + LocationRelativeToServer.getAddr(Location);
            } else {
                LilGNet = LocationRelativeToServer.getAddr(Location);
            }
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
                .addListener(new FozruciX(Manager, Network.twitch)); //Add our listener that will be called on Events
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
                .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
                .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
                .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
                .setName(Nick.toLowerCase()) //Set the nick of the bot.
                .setLogin(Nick.toLowerCase())
                .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
                .addAutoJoinChannel("#deltasmash")
                .addListener(new FozruciX(Manager, Network.twitch)); //Add our listener that will be called on Events
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

        public static void main(string[] args) {

            //Before anything else
            //IdentServer.startServer();
            Logger.Debug("Setting key");
            YandexTranslatorAPI.setKey(ApiKeys.YANDEX_API_KEY);

#pragma warning disable 162
            // ReSharper disable once HeuristicUnreachableCode
            // ReSharper disable once ConditionIsAlwaysTrueOrFalse
            if(Debug){
                // ReSharper disable once HeuristicUnreachableCode
                Manager.addBot(DebugConfig.buildForServer(Badnik, 6697));
                Manager.addBot(DebugConfigSmwc.buildForServer(Caffie, 6697));
                Manager.addBot(DebugConfigEsper.buildForServer(Esper, 6697));
                Manager.addBot(TwitchDebug.buildForServer(Twitch,
                                                          6667,
                                                          CryptoUtil.decrypt(setPassword(FozruciCS.Password.Twitch))));
                Manager.addBot(DebugConfigNova.buildForServer(Nova, 6697));
                Manager.addBot(DebugConfigRizon.buildForServer(Rizon, 9999));
                Manager.addBot(DebugLilGNetConfig.buildForServer(LilGNet, 6667));
            }
            else{
                Manager.addBot(Normal.buildForServer(Badnik, 6697));
                Manager.addBot(NormalSmwc.buildForServer(Caffie, 6697));
                Manager.addBot(NormalEsper.buildForServer(Esper, 6697));
                Manager.addBot(TwitchNormal.buildForServer(Twitch,
                                                           6667,
                                                           CryptoUtil.decrypt(setPassword(FozruciCS.Password.Twitch))));
                Manager.addBot(NormalNova.buildForServer(Nova, 6697));
                Manager.addBot(NormalRizon.buildForServer(Rizon, 9999));
                Manager.addBot(NormalLilGNetConfig.buildForServer(LilGNet, 6667));
            }
#pragma warning restore 162
            //Connect to the server
            Manager.start();
        }

        public static string setPassword(Password password) {
            File file;
            switch (password){
            case FozruciCS.Password.Normal:
                file = new File("pass.bin");
                break;
            case FozruciCS.Password.Twitch:
                file = new File("twitch.bin");
                break;
            case FozruciCS.Password.Discord:
                file = new File("discord.bin");
                break;
            case FozruciCS.Password.Key:
                file = new File("key.bin");
                break;
            case FozruciCS.Password.Salt:
                file = new File("salt.bin");
                break;
            case FozruciCS.Password.Ssh:
                file = new File("ssh.bin");
                break;
            default:
                throw new RuntimeException("Can't find file specified");
            }
            string ret;
            using (var fin = new FileInputStream(file)){
                var fileContent = new byte[(int)file.length()];

                // Reads up to certain bytes of data from this input stream into an array of bytes.
                //noinspection ResultOfMethodCallIgnored
                fin.read(fileContent);
                //create string from byte array
                ret = new string(Encoding.UTF8.GetString(fileContent).ToCharArray());
            }
            return ret;
        }

        public static void loadData() {
            Logger.Info("Starting to loadData");
            if (!SaveFile.exists()) {
                Logger.Info("Save file doesn't exist. Attempting to load backup");
                try {
                    //noinspection StatementWithEmptyBody
                    //while(!bak.canWrite() || !saveFile.canWrite()){}
                    Files.move(Bak.toPath(), SaveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Logger.Info("Backup file moved");
                } catch (FileSystemException e) {
                    Logger.Error("file in use: {0}", e);
                    return;
                } catch (Exception e) {
                    Logger.Error("failed renaming backup file: {0}", e);
                }
            }
            try {
                using (var br = new BufferedReader(new FileReader(SaveFile))){
                    Logger.Info("Attempting to load data");
                    SaveDataStore.setINSTANCE(Gson.fromJson(br, SaveDataStore.class));
                    if (SaveDataStore.getINSTANCE() != null){ Logger.Info("Loaded data"); }
                }
            } catch (Exception e) {
                Logger.Error("failed loading data, Attempting to save empty copy: {0}", e);
                try {
                    using (var writer = new FileWriter(new File("Data/DataEmpty.json"))){
                        writer.write(Gson.toJson(new SaveDataStore()));
                    }
                } catch (Exception e1) {
                    Logger.Error("Couldn't save data: {0}", e1);
                }
                Environment.Exit(1);
            }
        }

        public static void saveData() {
            try{
                using (FileWriter writer = new FileWriter(Bak)){
                    writer.write(Gson.toJson(SaveDataStore.getINSTANCE()));
                }
            } catch (Exception e) {
                Logger.Error("Couldn't save data", e);
            }
            Files.move(Bak.toPath(), SaveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Logger.Info("Data saved");
        }

        public static MultiBotManager getManager() {
            return Manager;
        }
    }
}