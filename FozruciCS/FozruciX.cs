using System;
using System = global::System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using Thread = System.Threading.Thread;
using com.google.code.chatterbotapi;
using com.jcraft.jsch;
using com.rmtheis.yandtran.language;
using FozruciCS.Misc;
using FozruciCS.Utils;
using ikvm.extensions;
using java.io;
using java.lang;
using java.net;
using java.nio.charset;
using java.util;
using java.util.regex;
using java.util.stream;
using javax.imageio;
using net.dv8tion.jda.core;
using net.dv8tion.jda.core.entities;
using net.dv8tion.jda.core.events.message;
using net.sourceforge.argparse4j;
using net.sourceforge.argparse4j.impl;
using net.sourceforge.argparse4j.inf;
using NLog;
using org.apache.commons.lang.time;
using org.apfloat;
using org.jsoup;
using org.pircbotx;
using org.pircbotx.hooks;
using org.pircbotx.hooks.events;
using org.pircbotx.hooks.types;
using Exception = java.lang.Exception;
using GenericMessageEvent = org.pircbotx.hooks.types.GenericMessageEvent;
using Logger = NLog.Logger;
using Object = java.lang.Object;
using Process = java.lang.Process;
using Random = System.Random;
using User = org.pircbotx.User;
using FozruciCS.Math;
using com.fathzer.soft.javaluator;
using com.joestelmach.natty;
using com.mysql.jdbc;
using com.rmtheis.yandtran.detect;
using com.rmtheis.yandtran.translate;
using com.wolfram.alpha;
using de.tudarmstadt.ukp.jwktl;
using de.tudarmstadt.ukp.jwktl.api;
using FozruciCS.DataStructs;
using FozruciCS.M68K;
using info.bliki.api;
using info.bliki.wiki.filter;
using info.bliki.wiki.model;
using javax.imageio.stream;
using net.didion.jwnl.data;
using org.jsoup.nodes;
using Byte = java.lang.Byte;
using Configuration = org.pircbotx.Configuration;
using Connection = org.jsoup.Connection;
using StringUtils = org.apache.commons.lang.StringUtils;

namespace FozruciCS {
    public enum Network {
        normal, twitch, discord
    }

    public enum EventType {
        [Description(" <%s> ")] message,
        [Description(" <%s> ")] privMessage,
        [Description(" *%s ")] action,
        [Description(" *%s* ")] notice,
        [Description(" %s ")] join,
        [Description(" %s ")] part,
        [Description(" %s ")] quit
    }

    public enum MessageModes {
        normal, reversed, wordReversed, scrambled, wordScrambled, CAPS
    }

    public class FozruciX : ListenerAdapter{
        public static readonly float VERSION = 2.7f;
        public static readonly string[] DICTIONARY = {"i don't know what \"%s\" is, do i look like a DICTIONARY?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck.", ">_>"};
        public static readonly string[] LIST_OF_NOES = {" It’s not a priority for me at this time.", "I’d rather stick needles in my eyes.", "My schedule is up in the air right now. SEE IT WAFTING GENTLY DOWN THE CORRIDOR.", "I don’t love it, which means I’m not the right person for it.", "I would prefer another option.", "I would be the absolute worst person to execute, are you on crack?!", "Life is too short TO DO THINGS YOU don’t LOVE.", "I no longer do things that make me want to kill myself", "You should do this yourself, you would be awesome sauce.", "I would love to say yes to everything, but that would be stupid", "Fuck no.", "Some things have come up that need my attention.", "There is a person who totally kicks ass at this. I AM NOT THAT PERSON.", "Shoot me now...", "It would cause the slow withering death of my soul.", "I’d rather remove my own gallbladder with an oyster fork.", "I'd love to but I did my own thing and now I've got to undo it."};
        public static readonly string[] COMMANDS = {"COMMANDS", " Time", " calcj", " RandomInt", " stringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " prefix", " SayThis", " ToSciNo", " Trans", " DebugVar", " cmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", " NoteJ", "Memes", " jToggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
        private static readonly File WIKTIONARY_DIRECTORY = new File("Data/Wiktionary");
        private static readonly int JOKE_COMMANDS = 0;
        private static readonly int ARRAY_OFFSET_SET = 1;
        private static readonly int CLEVER_BOT_INT = 2;
        private static readonly int PANDORA_BOT_INT = 3;
        private static readonly int JABBER_BOT_INT = 4;
        private static readonly int NICK_IN_USE = 5;
        private static readonly int COLOR = 6;
        private static readonly int RESPOND_TO_PMS = 7;
        private static readonly int DATA_LOADED = 8;
        private static readonly int CHECK_LINKS = 9;
        private static readonly ChatterBotFactory BOT_FACTORY = new ChatterBotFactory();
        private static readonly ArbitraryPrecisionEvaluator EVALUATOR = new ArbitraryPrecisionEvaluator();
        private static readonly StaticVariableSet VARIABLE_SET = new StaticVariableSet();
        private static readonly string APP_ID = "RGHHEP-HQU7HL67W9";
        private static readonly List<RpsGame> RPS_GAMES = new List<RpsGame>();
        private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();
        private static BitVector32 BOOLS = new BitVector32(0); // true, false, null, null, null, false, true, true, false, false
        private static readonly string M68kPath = "M68KSimulator";
        public static volatile Dictionary<string, LinkedList<string>> markovChain;
        private static volatile Random rnd = new Random();
        private static volatile ChatterBotSession chatterBotSession;
        private static volatile ChatterBotSession pandoraBotSession;
        private static volatile ChatterBotSession jabberBotSession;
        private static volatile FixedSizedQueue<MessageEvent> lastEvents = new FixedSizedQueue<MessageEvent>(30);
        private static volatile string lastLinkTitle = "";
        private static long lastLinkTime = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;

        private static volatile CMD singleCMD = null;
        //------------- save data -----------------------------------
        private static volatile LinkedList<Note> noteList = null;
        private static volatile LinkedList<string> authedUser = null;
        private static volatile LinkedList<int> authedUserLevel = null;
        private static volatile Dictionary<string, Dictionary<string, List<string>>> allowedCommands = null;
        private static volatile Dictionary<string, string> checkJoinsAndQuits = null;
        private static volatile LinkedList<string> mutedServerList = null;
        //-----------------------------------------------------------
        private static volatile int jokeCommandDebugVar = 30;

        private static volatile CommandLine terminal = new CommandLine();
        private static volatile string counter = "";
        private static volatile int counterCount = 0;

        private static volatile MessageModes messageMode = MessageModes.normal;
        private static volatile int arrayOffset = 0;

        private static volatile JavaScript js;

        private static volatile Python py;

        private static volatile string consolePrefix = ">";
        private static volatile string avatar;
        private static volatile Dictionary<string, Meme> memes;
        private static volatile Dictionary<string, string> FCList;
        private static volatile MultiBotManager manager;

        private static volatile HashSet<string> qList = new HashSet<string>();

        private static volatile Dictionary<User, long> commandCooldown = new Dictionary<User, long>();

        private static volatile StopWatch qTimer = new StopWatch();
        private static volatile DiscordAdapter discord;
        private static volatile bool updateAvatar = false;
        private static volatile int saveTime = 20;
        private static volatile int defaultCoolDownTime = 4;
        private static volatile FixedSizedQueue<Exception> lastExceptions = new FixedSizedQueue<>(30);
        private static volatile IM68KSim m68k;
        private static Thread saveThread = new Thread(() => {
            Thread thisThread = Thread.CurrentThread;
            thisThread.Name = "save Thread";
            while (true) {
                try{
                    LilGUtil.pause(LilGUtil.randInt(saveTime, saveTime + 10), false);
                    saveData();
                }
                catch(InterruptedException){
                    Logger.Info("Save thread interuppted");
                }
                catch(Exception){}
            }
        });

        static FozruciX(){
            saveThread.Start();
            try {
                //m68k = (M68kSim) Native.loadLibrary(M68kPath, M68kSim.class);
                global::System.Console.WriteLine(m68k);
                m68k.start();
                AppDomain.CurrentDomain.ProcessExit += m68k.exit;
                AppDomain.CurrentDomain.ProcessExit += saveData;
            } catch (UnsatisfiedLinkError e) {
                Logger.Error("JNA Error: {0}", e);
                //System.exit(1);
            }
        }

        public readonly Network network;

        private string prefix = "!";
        private DebugWindow debug;

        private User currentUser;
        private UserHostmask lastJsUser;
        private org.pircbotx.Channel lastJsChannel;
        private PircBotX bot;

        public FozruciX(MultiBotManager manager, Network network = Network.normal) {
            /*if (network == Network.twitch) {
                currentNick = "lilggamegenuis";
                currentUsername = currentNick;
                currentHost = currentUsername + ".tmi.network.tv";
            } else if (network == Network.discord) {
                currentNick = "Lil-G";
                currentUsername = currentNick;
                currentHost = "131494148350935040";
            }*/
            this.network = network;
            // true, false, null, null, null, false, true, true
            BOOLS[JOKE_COMMANDS] = true;
            BOOLS[COLOR] = true;
            BOOLS[RESPOND_TO_PMS] = true;
            BOOLS[CHECK_LINKS] = true;

            FozruciX.manager = manager;

            loadData(true);
            //Logger.setLevel(Level.ALL);
            Thread.CurrentThread.Name = "FozruciX: " + network.toString();
        }

        public static string getScramble( string msgToSend) {
            return getScramble(msgToSend, true);
        }


        public static string getScramble( string msgToSend, bool replaceNewLines) {
            if (replaceNewLines && (msgToSend.contains("\r") || msgToSend.contains("\n"))) {
                msgToSend = msgToSend.replace("\r", "").replace("\n", "");
            }
            switch (messageMode){
            case MessageModes.reversed:
                msgToSend = new StringBuilder(msgToSend).reverse().toString();
                break;
            case MessageModes.wordReversed:{
                var message = new List<string>(msgToSend.split("\\s+").ToList());
                msgToSend = "";
                for (var i = message.Count - 1; i >= 0; i--) {
                    msgToSend += message[i] + " ";
                }
            }
                break;
            case MessageModes.scrambled:
                var msgChars = msgToSend.toCharArray();
                var chars = new List<char>();
                foreach(var msgChar in msgChars) {
                    chars.Add(msgChar);
                }
                msgToSend = "";
                while (chars.Count != 0) {
                    int num = LilGUtil.randInt(0, chars.Count - 1);
                    msgToSend += chars[num] + "";
                    chars.RemoveAt(num);
                }
                break;
            case MessageModes.wordScrambled:{
                var message = new List<string>(msgToSend.split("\\s+").ToList());
                msgToSend = "";
                while (message.Count != 0) {
                    int num = LilGUtil.randInt(0, message.Count - 1);
                    msgToSend += message[num] + " ";
                    message.RemoveAt(num);
                }
            }
                break;
            case MessageModes.CAPS:
                msgToSend = msgToSend.toUpperCase();
                break;
            }
            return msgToSend;
        }

        private static int getUserLevel( List<UserLevel> levels) {
            var ret = 0;
            if (levels.Count == 0) {
                ret = 0;
            } else {
                foreach(var level in levels) {
                    var levelNum = level.ordinal();
                    ret = ret < levelNum ? levelNum : ret;
                }
            }
            return ret;
        }

        private static void sendFile(MessageEvent event_, File file, string message = null, bool discordUpload = true) {
            if (event_ is DiscordMessageEvent && discordUpload) {
                try {
                    ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel().sendFile(file, message != null ? new MessageBuilder().append(message).build() : null);
                } catch (IOException e) {
                    sendError(event_, e);
                }
            } else {
                uploadFile(event_, file, null, message);
            }
        }


        private static void uploadFile( GenericMessageEvent event_,  File file,  string folder,  string suffix) {
            Session session = null;
            com.jcraft.jsch.Channel channel = null;
            try {
                var ssh = new JSch();
                ssh.setKnownHosts(LilGUtil.IsLinux ? "~/.ssh/known_hosts" : "C:/Users/ggonz/AppData/Local/lxss/home/lil-g/.ssh/known_hosts");
                session = ssh.getSession("lil-g",
                        FozConfig.LilGNet
                        , 22);
                session.setPassword(CryptoUtil.decrypt(FozConfig.setPassword(Password.Normal)));
                UserInfo ui = new CommandLine.MyUserInfo() {
                    public bool promptYesNo(string message) {
                        return true;
                    };
                };
                session.setUserInfo(ui);
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                var sftp = (ChannelSftp) channel;
                folder = folder != null ? folder + "/" : "";
                sftp.put(file.getAbsolutePath(), "/var/www/html/upload/" + folder);
                sendMessage(event_, new URL("http://" + FozConfig.Location + "/upload/" + folder + file.getName() + (suffix == null ? "" : " " + suffix)).toExternalForm());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                channel?.disconnect();
                session?.disconnect();
            }
        }

        private static void sendMessage( GenericMessageEvent event_,  string msgToSend, bool addNick = true, bool splitMessage = false) {
            var textSizeLimit = 460; // irc limit
            if (event_ is DiscordMessageEvent) {
                textSizeLimit = 700; // discord
            }
            if (msgToSend == "\u0002\u0002") {
                return;
            }
            //msgToSend = msgToSend.replace("(Player)", event_.getUser().getNick()).replace("(items)", COMMANDS[LilGUtil.randInt(0, COMMANDS.length - 1)]).replace("(Pokémon)", event_.getBot().getNick()).replace("{random}", rnd.nextInt() + "");
            msgToSend = getScramble(msgToSend);

            if (!splitMessage && (msgToSend.length() > textSizeLimit)) {
                msgToSend = msgToSend.substring(0, textSizeLimit) + "-(snip)-";
            }

            if (((MessageEvent) event_).getChannel() != null) {
                if (addNick) {
                    event_.respond(msgToSend);
                } else {
                    event_.respondWith(msgToSend);
                }
            } else {
                event_.getUser().send().message(msgToSend);
            }
            log((MessageEvent) event_, msgToSend, true);
        }

        private static bool checkOP( org.pircbotx.Channel chn) {
            var bot = chn.getBot().getUserBot();
            return chn.isHalfOp(bot) || chn.isOp(bot) || chn.isSuperOp(bot) || chn.isOwner(bot);
        }

        /**
         * tells the user they don't have permission to use the command
         *
         * @param user User trying to use command
         */
        private static void permError( User user) {
            var num = LilGUtil.randInt(0, LIST_OF_NOES.Length - 1);
            var comeback = LIST_OF_NOES[num];
            user.send().notice(comeback);
        }

        /**
         * same as permError() except to be used in channels
         *
         * @param event_ Channel that the user used the command in
         */
        private static void permErrorchn( MessageEvent event_) {
            int num = LilGUtil.randInt(0, LIST_OF_NOES.Length - 1);
            var comeback = LIST_OF_NOES[num];
            sendMessage(event_, comeback);
        }

        private static string botTalk( string bot, string message){
            /*if (bot.equalsIgnoreCase("clever")) {
                if (chatterBotSession == null) {
                    chatterBotSession = BOT_FACTORY.create(ChatterBotType.CLEVERBOT).createSession();
                }
                return chatterBotSession.think(message);
            } else */
            if (bot.equalsIgnoreCase("pandora") || bot.equalsIgnoreCase("clever")) {
                if (pandoraBotSession == null) {
                    pandoraBotSession = BOT_FACTORY.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
                }
                return pandoraBotSession.think(message);
            }
            if (bot.equalsIgnoreCase("jabber") || bot.equalsIgnoreCase("jabberwacky")) {
                if (jabberBotSession == null) {
                    jabberBotSession = BOT_FACTORY.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
                }
                return jabberBotSession.think(message);
            }
            return "Error, not a valid bot";
        }

        private static string argJoiner( string[] args, int argToStartFrom){
            return argJoiner(args, argToStartFrom, arrayOffset);
        }

        private static string argJoiner( string[] args, int argToStartFrom, int arrayOffset){
            if ((args.Length - 1) == (argToStartFrom + arrayOffset)) {
                return getArg(args, argToStartFrom, arrayOffset);
            }
            var strToReturn = "";
            for (var length = args.Length; length > (argToStartFrom + arrayOffset); argToStartFrom++) {
                strToReturn += getArg(args, argToStartFrom, arrayOffset) + " ";
            }
            Logger.Debug("Argument joined to: " + strToReturn);
            return strToReturn.isEmpty() ? strToReturn : strToReturn.substring(0, strToReturn.length() - 1);
        }

        private static void addCooldown(User user) {
            addCooldown(user, defaultCoolDownTime);
        }

        private static void addCooldown(User user, int cooldownTime) {
            addCooldown(user, (long) cooldownTime * 1000);
        }

        private static void addCooldown(User user, long cooldownTime) {
            commandCooldown[user] = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) + cooldownTime;
        }


        private static string fullNameTostring( Language language) {
            return language.toString();
        }

        public static string getArg(string[] args, int index) {
            return getArg(args, index, arrayOffset);
        }

        public static string getArg(string[] args, int index, int arrayOffset) {
            try {
                return args[index + arrayOffset];
            } catch (Exception) {
                return null;
            }

        }

        private static void log(Event event_, bool botTalking) {
            log(event_, null, botTalking);
        }

        private static void log(Event event_, string messageOverride = null, bool botTalking = false) {
            string network = null;
            var channel = new List<string>();
            string user = null;
            string message = null;
            var eventType = EventType.message;

            var @event = event_ as MessageEvent;
            if (@event != null) {
                channel.Add(@event.getChannel().getName());
                user = @event.getUser().getHostmask();
                message = @event.getMessage();
                eventType = EventType.message;
            }
            var messageEvent = event_ as PrivateMessageEvent;
            if (messageEvent != null) {
                user = messageEvent.getUser().getHostmask();
                message = messageEvent.getMessage();
                eventType = EventType.privMessage;
            }
            var actionEvent = event_ as ActionEvent;
            if (actionEvent != null) {
                channel.Add(actionEvent.getChannel().getName());
                user = actionEvent.getUser().getHostmask();
                message = actionEvent.getMessage();
                eventType = EventType.action;
            }
            var noticeEvent = event_ as NoticeEvent;
            if (noticeEvent != null) {
                user = noticeEvent.getUser().getHostmask();
                message = noticeEvent.getMessage();
                eventType = EventType.notice;
            }
            var joinEvent = event_ as JoinEvent;
            if (joinEvent != null) {
                if (joinEvent.getChannel() != null) {
                    channel.Add(joinEvent.getChannel().getName());
                }
                user = joinEvent.getUser().getHostmask();
                message = "Joined " + channel;
                eventType = EventType.join;
            }
            var partEvent = event_ as PartEvent;
            if (partEvent != null) {
                channel.Add(partEvent.getChannel().getName());
                user = partEvent.getUser().getHostmask();
                message = "parted " + channel;
                if (partEvent.getReason() != null) {
                    message += " (" + partEvent.getReason() + ")";
                }
                eventType = EventType.part;
            }
            var quitEvent = event_ as QuitEvent;
            if (quitEvent != null) {
                if (event_ is DiscordQuitEvent) {
                    channel.AddRange(((DiscordQuitEvent)event_).getLeaveEvent().getGuild().getTextChannels().toList<TextChannel>().Select(chanName=> "#" +((MessageChannel)chanName).getName()));
                    user = ((DiscordQuitEvent) event_).getLeaveEvent().getMember().getUser().getName();
                    message = "Quit " + ((DiscordQuitEvent) event_).getLeaveEvent().getGuild().getName();
                } else {
                    channel.AddRange(quitEvent.getUser().getChannels().toList<org.pircbotx.Channel>().Select(chan=>chan.getName()));
                    user = quitEvent.getUser().getHostmask();
                    message = "Quit " + network;
                    if (quitEvent.getReason() != null) {
                        message += " (" + quitEvent.getReason() + ")";
                    }
                }
                eventType = EventType.quit;
            }
            if (event_ is KickEvent) {
                channel.Add(((KickEvent) event_).getChannel().getName());
                user = ((KickEvent) event_).getRecipient().getHostmask();
                message = "Kicked " + network + " by " + ((KickEvent) event_).getUser().getHostmask() + "(" + ((KickEvent) event_).getReason() + ")";
                eventType = EventType.part;
            }
            if (event_ is OutputEvent) {
                botTalking = true;
                var lines = ((OutputEvent) event_).getLineParsed().toList<string>();
                switch (lines[0]) {
                    case "PRIVMSG":
                        eventType = EventType.message;
                        break;
                    case "ACTION":
                        eventType = EventType.action;
                        break;
                    case "NOTICE":
                        eventType = EventType.notice;
                        break;
                    case "JOIN":
                        eventType = EventType.join;
                        break;
                    case "PONG":
                        return;
                }
                if (lines[1].contains("#")) {
                    channel.Add(lines[1]);
                } else {
                    if (event_.getBot() == DiscordAdapter.pircBotX) {
                        foreach(var guild in DiscordAdapter.getJda().getGuilds().toList<Guild>()) {
                            foreach(var discordUser in guild.getMembers().toList<Member>()) {
                                var nick = discordUser.getEffectiveName();
                                if (nick == lines[1]) {
                                    channel.Add(nick + "!" + discordUser.getUser().getName() + "@" + discordUser.getUser().getId());
                                }
                            }
                        }
                    } else {
                        foreach(org.pircbotx.Channel aChannel in event_.getBot().getUserBot().getChannels().toList<org.pircbotx.Channel>()) {
                            foreach(User aUser in aChannel.getUsers().toList<User>()) {
                                if (aUser.getNick() == lines[1]) {
                                    channel.Add(aUser.getHostmask());
                                }
                            }
                        }
                    }
                }
                message = lines[2];
            }
            if (botTalking) {
                user = event_.getBot().getUserBot().getHostmask();
            }
            if (messageOverride != null) {
                message = messageOverride;
            }
            network = getSeverName(event_);


            if (channel.Count == 0) {
                channel.Add(user);
            }
            foreach(var aChannel in channel) {
                try {
                    // APPEND MODE SET HERE
                    var today = Calendar.getInstance();
                    var parent = "logs/" + network + "/" + escapePath(aChannel) + "/" + today.get(Calendar.YEAR) + "/";
                    var parentDir = new File(parent);
                    if (!parentDir.mkdirs() && !parentDir.exists()) {
                        Logger.Error("Couldn't make dirs");
                    }
                    var path = java.lang.String.format("%02d", today.get(Calendar.MONTH) + 1) + "." + java.lang.String.format("%02d", today.get(Calendar.DATE)) + ".txt";
                    var file = new File(parent, path);
                    var minute = java.lang.String.format("%02d", today.get(Calendar.MINUTE));
                    if (minute.length() < 2) {
                        minute = "0" + minute;
                    }
                    PrintWriter pw;
                    var logFile = java.lang.String.format("%02d", today.get(Calendar.HOUR)) + ":" + minute + ":" + java.lang.String.format("%02d", today.get(Calendar.SECOND)) + java.lang.String.format("%1$12s", java.lang.String.format(eventType.toString(), user) + message);
                    if (file.exists() && !file.isDirectory()) {
                        pw = new PrintWriter(new FileOutputStream(file, true));
                        pw.append(logFile).append(java.lang.System.lineSeparator());
                        pw.close();
                    } else {
                        pw = new PrintWriter(parent + path);
                        pw.println(logFile);
                        pw.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        private static string getSeverName(Event event_, bool trimAddress = false) {
            var network = event_.getBot().getServerInfo().getNetwork();
            if (network == null) {
                network = event_.getBot().getServerHostname();
                if (trimAddress) {
                    network = network.substring(network.indexOf('.') + 1, network.lastIndexOf('.'));
                }
            }
            if (event_ is DiscordMessageEvent) {
                network = ((DiscordMessageEvent) event_).getDiscordEvent().getGuild().getName();
            }
            return network;
        }

        private static string escapePath(string path) {
            if (path == null) return null;
            var fileSep = '/'; // ... or do this portably.
            var escape = '%'; // ... or some other legal char.
            var len = path.length();
            var sb = new StringBuilder(len);
            for (var i = 0; i < len; i++) {
                var ch = path.charAt(i);
                if (ch < ' ' || ch >= 0x7F || ch == fileSep || ch == '|' // add other illegal chars
                    || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
                    || ch == escape) {
                    sb.append(escape);
                    if (ch < 0x10) {
                        sb.append('0');
                    }
                    sb.append(Convert.ToByte(ch).ToString("X2"));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }

        private static void sendError( MessageEvent event_, Throwable t) {
            sendError(event_, new Exception(t));
        }

        public static void sendError( MessageEvent event_,  Exception e) {
            Logger.Error("Error: {0}", e);
            var color = "";
            var discordFormatting = event_ is DiscordMessageEvent ? "`" : "";
            var cause = "";
            string from;
            if (BOOLS[COLOR] && discordFormatting.isEmpty()) {
                color = Colors.RED;
            }
            if (e.getCause() != null) {
                cause = "Error: " + discordFormatting + e.getCause() + discordFormatting;
            }
            if (cause.isEmpty()) {
                from = "Error: " + discordFormatting + e + discordFormatting;
            } else {
                from = ". From " + discordFormatting + e + discordFormatting;
            }
            if (cause.contains("jdk.nashorn.internal.runtime.ParserException") || from.contains("javax.script.ScriptException")) {
                if (cause.contains("TypeError: Cannot read property")) {
                    sendMessage(event_, color + "There was a type error, Cannot read property", false);
                } else {
                    if (cause.contains("\r") || cause.contains("\n")) {
                        sendMessage(event_, color + cause.substring(0, cause.indexOf("\r")), false);
                    } else {
                        sendMessage(event_, color + cause, false);
                    }
                }
            } else if (e is ArrayIndexOutOfBoundsException) {
                sendMessage(event_, color + "Not enough arguments, try doing the command \"COMMANDS <command>\" for help", false);
            } else {
                sendMessage(event_, color + cause + from, false);
            }
            e.printStackTrace();
            lastExceptions.Enqueue(e);
        }

        private static bool checkChatFunction(string args, string function) {
            return LilGUtil.wildCardMatch(args, "[$" + function + "(*)]");
        }

        private static string[] getChatArgs(string function) {
            var args = function.substring(function.indexOf('(') + 1, function.indexOf(')'));
            return args.split(",");
        }

        private static void saveData(object obj = null, EventArgs eventArgs = null) {
            /*if (!BOOLS[DATA_LOADED]) {
                Logger.Debug("Data save canceled because data hasn't been loaded yet");
                return;
            }*/
            try {
                FozConfig.saveData();
            } catch (ConcurrentModificationException e) {
                Logger.Debug("Data not saved", e);
            } catch (Exception e) {
                Logger.Error("Couldn't save data", e);
            }
        }

        private void sendCommandHelp(GenericEvent event_, ArgumentParser parser) {
            try {
                var stringWriter = new StringWriter();
                var writer = new PrintWriter(stringWriter);
                parser.printHelp(writer);
                var content = stringWriter.toString();
                Logger.Debug("Command help: " + content);
                if (event_ is DiscordMessageEvent || event_ is DiscordPrivateMessageEvent) {
                    sendNotice(event_, "```" + content + "```", false);
                } else {
                    sendNotice(event_, content, false);
                }
            } catch (Exception ex) {
                Logger.Error("Error sending command help", ex);
            }
        }

        private void removeFromCooldown() {
            var currentTime = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
            foreach(var user in commandCooldown) {
                if (currentTime >= user.Value) { // Check if we've waited long enough
                    commandCooldown.Remove(user.Key);
                }
                commandCooldown.Remove(currentUser);
            }
        }

        private bool checkCooldown(GenericUserEvent event_) {
            if (event_.getUser() == null ||
                !commandCooldown.ContainsKey(event_.getUser())) return false;
            var timeToWait = commandCooldown[event_.getUser()] - (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
            if (timeToWait < 0) { //wtf? this shouldn't happen
                removeFromCooldown();
                return false;
            }
            sendNotice(event_, event_.getUser().getNick(), "Sorry, you have to wait " + timeToWait + " Milliseconds for the cool down");
            return true;
        }

        private void sendNotice( GenericEvent event_, string msgToSend) {
            sendNotice(event_, ((GenericMessageEvent) event_).getUser().getNick(), msgToSend);
        }

        private void sendNotice( GenericEvent event_, string msgToSend, bool replaceNewLines) {
            sendNotice(event_, ((GenericMessageEvent) event_).getUser().getNick(), msgToSend, replaceNewLines);
        }

        private void sendNotice( GenericEvent event_, string userToSendTo, string msgToSend, bool replaceNewLines = true) {
            msgToSend = getScramble(msgToSend, replaceNewLines);
            if (network == Network.discord) {
                sendPrivateMessage(event_, userToSendTo, msgToSend, replaceNewLines);
            } else {
                foreach(var messagePart in msgToSend.split("\n")) {
                    if (!messagePart.isEmpty()) {
                        event_.getBot().send().notice(userToSendTo, messagePart);
                    }
                }
            }
        }

        private void sendNotice( string userToSendTo,  string msgToSend) {
            if (network == Network.discord || network == Network.twitch) {
                throw new RuntimeException("Not enough info to send to " + network);
            }
            msgToSend = getScramble(msgToSend);
            bot.send().notice(userToSendTo, msgToSend);
        }

        private void sendPrivateMessage( string userToSendTo,  string msgToSend) {
            if (network == Network.discord || network == Network.twitch) {
                throw new RuntimeException("Not enough info to send to " + network);
            }
            msgToSend = getScramble(msgToSend);
            bot.send().message(userToSendTo, msgToSend);

        }

        private void sendPrivateMessage( GenericEvent event_,  string msgToSend) {
            sendPrivateMessage(event_, ((PrivateMessageEvent) event_).getUser().getNick(), msgToSend);
        }

        private void sendPrivateMessage( GenericEvent event_,  string userToSendTo,  string msgToSend, bool removeNewLines = true) {
            msgToSend = getScramble(msgToSend, removeNewLines);
            if (event_ is DiscordPrivateMessageEvent || event_ is DiscordMessageEvent) {
                var users = DiscordAdapter.getJda().getUsersByName(userToSendTo, true).toList<net.dv8tion.jda.core.entities.User>();
                foreach(var name in users) {
                    if (!name.getName().equalsIgnoreCase(userToSendTo)) continue;
                    if (!name.hasPrivateChannel()) {
                        var str = msgToSend;
                        name.openPrivateChannel().complete();
                        name.getPrivateChannel().sendMessage(str).queue();
                    }
                    name.getPrivateChannel().sendMessage(msgToSend).queue();
                    return;
                }
                Logger.Warn("Couldn't find user with the name of " + userToSendTo);
            } else {
                event_.getBot().send().message(userToSendTo, msgToSend);
            }
        }

        private void makeDebug( ConnectEvent event_) {
            Logger.Debug("Creating Debug window");
            debug = new DebugWindow(event_, network, this);
            Logger.Debug("Debug window created");
            debug.setCurrentNick(currentUser.getHostmask());
        }

        private void makeDebug() {
            if ((debug == null) || (debug.getConnectEvent() == null)) {
                return;
            }
            makeDebug(debug.getConnectEvent());
        }

        private void makeDiscord() {
            if (discord == null && network != Network.discord) {
                discord = DiscordAdapter.makeDiscord(bot);
            }
        }

        public override void onDisconnect(DisconnectEvent DC) {
            if (debug != null) {
                debug.dispose();
            }
        }

        public override void onConnect( ConnectEvent event_) {
            bot = event_.getBot();
            bot.sendIRC().mode(bot.getNick(), "+BI");
            if (event_ is DiscordConnectEvent) {
                currentUser = new DiscordUser(new DiscordUserHostmask(bot, event_.getBot().getUserBot().getHostmask()), DiscordAdapter.getJda().getSelfUser(), null);
            } else {
                currentUser = event_.getBot().getUserBot();
            }

            Thread.CurrentThread.Name = ("FozruciX: " + getSeverName(event_);
            loadData(true);
            makeDebug(event_);
            makeDiscord();
        }

        private void sendPage( MessageEvent event_,  string[] arg,  List<string> messagesToSend) {
            try {
                Logger.Debug("Generating page...");
                var name = UUID.randomUUID();
                var dir = new File("Data/site/temp/");
                var f = new File(dir, name + ".htm");
                Logger.Debug("Result of making dirs: " + dir.mkdirs() + ". Result of create new file: " + f.createNewFile());
                var bw = new BufferedWriter(new FileWriter(f));
                bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
                bw.write("<html>");
                bw.write("<head>");
                bw.write("<link href=\"CommandStyles.css\" rel=\"stylesheet\" type=\"text/css\">");
                bw.write("<title>" + event_.getUser().getNick() + ": " + bot.getNick() + "'s Command output</title>");
                bw.write("</head>");
                bw.write("<body>");
                bw.write("<h1>" + event_.getUser().getNick() + ": " + argJoiner(arg, 0) + "</h1>");
                bw.write("<textarea cols=\"75\" rows=\"30\">");

                foreach(var aMessagesToSend in messagesToSend) {
                    bw.write(aMessagesToSend);
                    bw.newLine();
                }

                bw.write("</textarea>");
                bw.write("</body>");
                bw.write("</html>");

                bw.close();
                uploadFile(event_, f, "output", null);

            } catch (Exception e) {
                sendError(event_, e);
            }
        }

        // ReSharper disable once ParameterHidesMember
        private static void setArrayOffset( string prefix) {
            if(BOOLS[ARRAY_OFFSET_SET]) return;
            if ((prefix.length() > 1) && !prefix.endsWith(".")) {
                arrayOffset = StringUtils.countMatches(prefix, " ");
            } else {
                arrayOffset = 0;
            }
            Logger.Debug("Setting arrayOffset to " + arrayOffset + " based on string \"" + prefix + "\"");
            BOOLS[ARRAY_OFFSET_SET] = true;
        }

        private void setArrayOffset() {
            setArrayOffset(prefix);
        }


        private string[] formatstringArgs( string[] arg) {
            return trimFrontOfArray(arg, 1 + arrayOffset);
        }

        private static string[] trimFrontOfArray( string[] arg, int amount) {
            var ret = new string[arg.Length - amount];
            try {
                Array.Copy(arg, amount, ret, 0, ret.Length);
            } catch (Exception e) {
                sendError(lastEvents, e);
            }

            return ret;
        }


        public override void onMessage( MessageEvent event_) {
            onMessage(event_, true);
        }

        public void onMessage( MessageEvent event_, bool log) {
            if (log) {
                FozruciX.log(event_);
            }
            removeFromCooldown();
            checkNote(event_, event_.getUser().getNick(), event_.getChannel().getName());
            if (debug == null) {
                if (bot == null) {
                    bot = event_.getBot();
                }
                makeDebug();
            }
            lastEvents.Enqueue(event_);
            if (!BOOLS[DATA_LOADED]) {
                BOOLS[DATA_LOADED] = true;
                loadData();
            }
            if ((network == Network.normal) && BOOLS[NICK_IN_USE]) {
                if (!bot.getNick().equalsIgnoreCase(bot.getConfiguration().getName())) {
                    sendNotice(event_, currentUser.getNick(), "Ghost detected, recovering in 10 seconds");
                    new Thread(() => {
                        Thread.CurrentThread.Name = "ghost-thread";
                        try {
                            LilGUtil.pause(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bot.sendRaw().rawLineNow("ns recover " + bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.Password));
                        bot.sendRaw().rawLineNow("ns ghost " + bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.Password));
                        bot.sendIRC().changeNick(bot.getConfiguration().getName());
                    }).Start();
                }
            }
            BOOLS[NICK_IN_USE] = false;
            try {
                if (!(event_.getMessage().startsWith(prefix) && event_.getMessage().startsWith("."))) {
                    if (LilGUtil.endsWithAny(event_.getMessage(), ".", "?", "!")) {
                        addWords(event_.getMessage());
                    } else {
                        addWords(event_.getMessage() + ".");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            debug.setCurrentNick(currentUser.getHostmask());
            debug.setMessage(event_.getUser().getNick() + ": " + event_.getMessage());
            var server = network == Network.discord ? ((DiscordMessageEvent) event_).getDiscordEvent().getGuild().getId() : event_.getBot().getServerHostname();
            if (mutedServerList.Contains(server) && !checkPerm(event_.getUser(), 9001)) {
                Logger.Trace("Ignoring message from server " + server);
                return;
            }
            if (event_.getMessage() != null) {
                doCommand(event_);
            }

    // url checker - Checks if string contains a url and parses
            try {
                var channel = event_.getChannel().getName();
                string[] arg = LilGUtil.splitMessage(event_.getMessage());
                bool checklink = !commandChecker(event_, arg, "checkLink", false);
                bool isBot = this.isBot(event_);
                var isLinkShorterner = !event_.getMessage().contains("taglink: https://is.gd/");
                if (checklink && BOOLS[CHECK_LINKS] && isBot && isLinkShorterner) {
                    var channelContains = false;
                    var containsServer = false;
                    var containsChannel = false;
                    try {
                        containsServer = allowedCommands[getSeverName(event_, true)] != null;
                        containsChannel = allowedCommands[getSeverName(event_, true)][channel] != null;
                        channelContains = allowedCommands[getSeverName(event_, true)][channel].Contains("url checker");
                    } catch (NullReferenceException ignored) {}
                    Logger.Trace(getSeverName(event_, true) + ": containsServer: " + containsServer + " containsChannel: " + containsChannel + " channelContains: " + channelContains + " " + allowedCommands);
                    if (!channelContains) {
                        // NOTES:   1) \w includes 0-9, a-z, A-Z, _
                        //             2) The leading '-' is the '-' character. It must go first in character class expression
                        var VALID_CHARS = "-\\w+&@#/%=~()|";
                        var VALID_NON_TERMINAL = "?!:,.;";
                        // Notes on the expression:
                        //  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
                        //  2) s? ==> the s is optional so either [http, https] accepted as scheme
                        //  3) All valid chars accepted and then one or more
                        //  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
                        var URI_FINDER_PATTERN = Pattern.compile("\\(*https?://[" + VALID_CHARS + VALID_NON_TERMINAL + "]*[" + VALID_CHARS + "]", Pattern.CASE_INSENSITIVE);

                        /*
                         * <p>
                         * Finds all "URL"s in the given _rawText, wraps them in
                         * HTML link tags and returns the result (with the rest of the text
                         * html encoded).
                         * </p>
                         * <p>
                         * We employ the procedure described at:
                         * http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html
                         * which is a <b>must-read</b>.
                         * </p>
                         * Basically, we allow any number of left parenthesis (which will get stripped away)
                         * followed by http:// or https://.  Then any number of permitted URL characters
                         * (based on http://www.ietf.org/rfc/rfc1738.txt) followed by a single character
                         * of that set (basically, those minus typical punctuation).  We remove all sets of
                         * matching left & right parentheses which surround the URL.
                         *</p>
                         * <p>
                         * This method *must* be called from a tag/component which will NOT
                         * end up escaping the output.  For example:
                         * <PRE>
                         * <h:outputText ... escape="false" value="#{core:hyperlinkText(textThatMayHaveURLs, '_blank')}"/>
                         * </pre>
                         * </p>
                         * <p>
                         * Reason: we are adding <code>&lt;a href="..."&gt;</code> tags to the output *and*
                         * encoding the rest of the string.  So, encoding the output will result in
                         * double-encoding data which was already encoded - and encoding the <code>a href</code>
                         * (which will render it useless).
                         * </p>
                         * <p>
                         *
                         * @param   _rawText  - if <code>null</code>, returns <code>""</code> (empty string).
                         * @param   _target   - if not <code>null</code> or <code>""</code>, adds a target attributed to the generated link, using _target as the attribute value.
                         */
                        var _rawText = event_.getMessage();
                        var matcher = URI_FINDER_PATTERN.matcher(_rawText);

                        if (matcher.find()) {

                            // Counted 15 characters aside from the target + 2 of the URL (max if the whole string is URL)
                            // Rough guess, but should keep us from expanding the Builder too many times.

                            int currentStart;
                            int currentEnd;

                            string currentURL;

                            do {
                                currentStart = matcher.start();
                                currentEnd = matcher.end();
                                currentURL = matcher.group();

                                // Adjust for URLs wrapped in ()'s ... move start/end markers
                                //      and substring the _rawText for new URL value.
                                while (currentURL.startsWith("(") && currentURL.endsWith(")")) {
                                    currentStart = currentStart + 1;
                                    currentEnd = currentEnd - 1;

                                    currentURL = _rawText.substring(currentStart, currentEnd);
                                }

                                while (currentURL.startsWith("(")) {
                                    currentStart = currentStart + 1;

                                    currentURL = _rawText.substring(currentStart, currentEnd);
                                }

                            } while (matcher.find());
                            Logger.Debug("Found URL - " + currentURL);
                            try {
                                var title = Jsoup.connect(currentURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2783.4 Safari/537.36").timeout(5000).get().title();
                                if ((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) > lastLinkTime) {
                                    lastLinkTitle = "";
                                }
                                if (title.isEmpty()) {
                                    sendMessage(event_, "Title was empty", false);
                                } else if (title != lastLinkTitle) {
                                    sendMessage(event_, "Title: " + title, false);
                                    lastLinkTitle = title;
                                    lastLinkTime = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) + 30000;
                                }
                            } catch (UnsupportedMimeTypeException e) {
                                try {
                                    var fileURLConn = new URL(e.getUrl()).openConnection();
                                    if (e.getMimeType().split("/")[0] == "image") {
                                        var stream = fileURLConn.getInputStream();
                                        ImageInputStream obj = ImageIO.createImageInputStream(stream);
                                        ImageReader reader = ImageIO.getImageReaders(obj).next();
                                        reader.setInput(obj);
                                        long fileSize = fileURLConn.getContentLength();
                                        sendMessage(event_, "type: " + e.getMimeType() + " size: [Width = " + reader.getWidth(0) + ", Height = " + reader.getHeight(0) + "] File Size: " + (fileSize < 1 ? "Unknown" : LilGUtil.formatFileSize(fileSize)), false);
                                        stream.close();
                                    } else {
                                        sendMessage(event_, "type: " + e.getMimeType() + " File Size: " + LilGUtil.formatFileSize(fileURLConn.getContentLength()), false);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } catch (MalformedURLException e) {
                                sendMessage(event_, "Unsupported URL", false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void doCommand(MessageEvent event_) {
            string channel = null;
            if (event_.getChannel() != null) {
                channel = event_.getChannel().getName();
            }
            var message = event_.getMessage();
            message = doChatFunctions(message);
            string[] arg = LilGUtil.splitMessage(message);

            if (!LilGUtil.containsAny(message, prefix, consolePrefix, bot.getNick(), "s/")) return;
            setArrayOffset();
            BOOLS[ARRAY_OFFSET_SET] = false;
            if (checkCooldown(event_) || !checkPerm(event_.getUser(), 0) || isBot(event_)) {
                return;
            }

            // !getChannelName - Gets channel name, for debugging
            if (commandChecker(event_, arg, "GetChannelName")) {
                sendMessage(event_, channel ?? "This isn't a channel");
                addCooldown(event_.getUser());
            }

            // !checkLinks
            else if (commandChecker(event_, arg, "checkLinks")) {
                if (checkPerm(event_.getUser(), 4)) {
                    BOOLS.flip(CHECK_LINKS);
                    sendMessage(event_, BOOLS[CHECK_LINKS] ? "Link checking is on" : "Link checking is off");
                    addCooldown(event_.getUser());
                }
            }

            // !formatting - toggles COLOR (Mostly in the errors)
            else if (commandChecker(event_, arg, "formatting")) {
                if (checkPerm(event_.getUser(), 9001)) {
                    BOOLS[COLOR] = !BOOLS[COLOR];
                    if (BOOLS[COLOR]) {
                        sendMessage(event_, "Color formatting is now On");
                    } else {
                        sendMessage(event_, "Color formatting is now Off");
                    }
                } else {
                    permErrorchn(event_);
                }
            }

            // !HelpMe - redirect to !COMMANDS
            else if (commandChecker(event_, arg, "HelpMe")) {
                sendMessage(event_, "This command was changed to \"commands\".");
                addCooldown(event_.getUser());

            }

            // !setGuildChan - Sets what channel to announce joins an quits in
            else if (commandChecker(event_, arg, "setGuildChan")){
                if (!checkPerm(event_.getUser(), 5)) return;
                if (!(event_ is DiscordMessageEvent)) return;
                var guildID = ((DiscordMessageEvent) event_).getDiscordEvent().getGuild().getId();
                if (getArg(arg, 1) != null) {
                    if (getArg(arg, 1).toLowerCase().startsWith("rem")) {
                        checkJoinsAndQuits.Remove(guildID);
                        sendMessage(event_, "Removed Guild from Join and quit messages");

                    } else {
                        var channels = ((DiscordMessageEvent) event_).getDiscordEvent().getGuild().getTextChannels().toList<TextChannel>();
                        TextChannel textChannel = null;
                        foreach(var textChannel0 in channels) {
                            if (textChannel0.getId() == getArg(arg, 1) || ((MessageChannel)textChannel0).getName().equalsIgnoreCase(getArg(arg, 1))) {
                                textChannel = textChannel0;
                            }
                        }
                        if (textChannel == null) return;
                        checkJoinsAndQuits[guildID] = getArg(arg, 1);
                        sendMessage(event_, "set Join and quit message channel to #" + ((MessageChannel)textChannel).getName());
                    }
                } else {
                    checkJoinsAndQuits[guildID] = ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel().getId();
                    sendMessage(event_, "set Join and quit message channel to #" + ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel().getName());
                }
            }

            // !admin - contains most admin related commands - A super command
            else if (commandChecker(event_, arg, "admin")) {
                if (!checkPerm(event_.getUser(), 2)) return;
                var discord = network == Network.discord;
                var args = formatstringArgs(LilGUtil.splitMessage(message, 0, false));
                var parser = ArgumentParsers.newArgumentParser("admin")
                                            .description("contains most admin related commands")
                                            .defaultHelp(true);
                var subparsers = parser.addSubparsers()
                                       .title("Valid commands")
                                       .description("This is the list of valid admin commands you can use.")
                                       .dest("admin_command");
                var ban = subparsers.addParser("ban")
                                    .help("Bans a user");
                var kick = subparsers.addParser("kick")
                                     .help("kicks a user");
                ban.addArgument("users").nargs("*").type(typeof(string)).help("User to ban");
                kick.addArgument("users").nargs("*").type(typeof(string)).help("User to kick");
                ban.addArgument("-r", "--reason")
                   .type(typeof(string)).help("Sets the reason for the ban")
                    .setDefault("No reason given");
                kick.addArgument("-r", "--reason")
                    .type(typeof(string)).help("Sets the reason for the kick")
                    .setDefault("No reason given");
                if (discord) {
                    ban.addArgument("--remove-messages").nargs(1)
                       .type(typeof(int)).setDefault(0).help("Amount of messages, by days, to remove by the user, if any");
                    var delMsg = subparsers.addParser("delmsg")
                                           .help("Deletes a message, a span of messages, or a certain amount of messages from a certain user");

                    var delMsgGroup = delMsg.addMutuallyExclusiveGroup();
                    delMsgGroup.addArgument("-m", "--message-span").nargs(2)
                               .help("Specify 2 message IDs and any messages between (inclusive) will be deleted");
                    delMsgGroup.addArgument("-u", "--user").help("user to delete messages from with the metioned user infront and the amount of messages second").nargs(2);
                } else {
                    ban.addArgument("-k", "--kick-ban")
                       .type(typeof(bool)).help("Specify to also kick")
                        .action(Arguments.storeTrue());
                }
                var modeM = subparsers.addParser("+m")
                                      .help("Sets a channel so only certain users can speak")
                                      .defaultHelp(true);
                modeM.addArgument("-s", "--state").type(Arguments.booleanType("on", "off")).setDefault(null as object)
                     .help("Sets +m mode on or off. Otherwise toggle");
                modeM.addArgument("roles").nargs("*")
                     .help("Roles to white/black list");
                modeM.addArgument("-w", "--whitelist").type(typeof(bool)).action(Arguments.storeTrue())
                    .help("Sets the channel to white list mode");

                var modeG = subparsers.addParser("+g")
                                      .help("makes it so messages containing a certain string cannot be sent")
                                      .defaultHelp(true);
                modeG.addArgument("expression").type(typeof(string))
                    .help("what to check messages against");
                modeG.addArgument("-l", "--list").type(typeof(bool)).action(Arguments.storeTrue())
                    .help("List out all +g for the server");
                modeG.addArgument("-r", "--remove").type(typeof(bool)).action(Arguments.storeTrue())
                    .help("Remove expression instead of adding it");

                var logging = subparsers.addParser("logging")
                                        .help("Sets the logging channel")
                                        .defaultHelp(true);

                var topic = subparsers.addParser("topic")
                                      .help("Sets the topic")
                                      .defaultHelp(true);
                topic.addArgument("newTopic")
                     .nargs("*")
                     .help("New topic");
                //Subparser op; // TODO: 10/3/16 Add subparser for giving permissions
                Namespace ns;
                try {
                    ns = parser.parseArgs(args);
                    Logger.Debug(ns.toString());
                    var isBan = false;
                    switch (ns.getString("admin_command")) {
                    case "ban":
                        isBan = true;
                        goto kick;
                    case "kick":
                        kick:
                        var users = ns.getList("users").toList<string>();
                        if (discord) {
                            MessageReceivedEvent discordEvent = ((DiscordMessageEvent) event_).getDiscordEvent();
                            var permNeeded = isBan ? Permission.BAN_MEMBERS : Permission.KICK_MEMBERS;
                            if (checkPerm((DiscordUser) event_.getUser(), permNeeded)) {
                                var mentioned = discordEvent.getMessage().getMentionedUsers().toList<net.dv8tion.jda.core.entities.User>();
                                Logger.Trace($"Mentioned users: {mentioned.toString()}");
                                var guild = discordEvent.getGuild();
                                var controller = guild.getController();
                                if (mentioned.Count != 0) {
                                    foreach(var mentionedUser in mentioned) {
                                        var reason = ns.getString("reason");
                                        var readonlyBan = isBan;
                                        if (!mentionedUser.hasPrivateChannel()) {
                                            mentionedUser.openPrivateChannel().queue(privateChannel => sendMsg.run());
                                        } else {
                                            new Thread(() => {
                                                if (reason == null) return;
                                                var action = readonlyBan ? "Banned" : "Kicked";
                                                sendPrivateMessage(event_, mentionedUser.getName(), action + " by " +
                                                                                                    ((DiscordMessageEvent) event_).getDiscordEvent().getMember().getEffectiveName() +
                                                                                                    ". Reason: " + reason);
                                            }).Start();
                                        }
                                        if (isBan) {
                                            controller.ban(mentionedUser, ns.getInt("remove_messages").intValue()).queue();
                                            sendMessage(event_, "Banned user: " + mentionedUser.getName());
                                        } else {
                                            controller.kick(guild.getMember(mentionedUser)).queue();
                                            sendMessage(event_, "Kicked user: " + mentionedUser.getName());
                                        }
                                    }
                                } else {
                                    foreach(var user in users) {
                                        Member currentDiscordMember = null;
                                        var nick = "Error getting name";
                                        foreach(var discordUser in guild.getMembers().toList<Member>()) {
                                            nick = discordUser.getEffectiveName();
                                            if (!discordUser.getUser().getName().equalsIgnoreCase(user) &&
                                                !nick.equalsIgnoreCase(user) &&
                                                !discordUser.getUser().getId().equalsIgnoreCase(user)) continue;
                                            if (currentDiscordMember == null) {
                                                currentDiscordMember = discordUser;
                                            } else {
                                                if (isBan) {
                                                    sendMessage(event_, "Ambiguous ban, not banning user " + user);
                                                } else {
                                                    sendMessage(event_, "Ambiguous kick, not kicking user " + user);
                                                }
                                                return;
                                            }
                                        }
                                        if (currentDiscordMember != null) {
                                            var reason = ns.getString("reason");
                                            var currentDiscordUser = currentDiscordMember.getUser();
                                            var finalBan = isBan;
                                            Thread sendMsg = new Thread(() => {
                                                if (reason != null){
                                                    string action;
                                                    if (finalBan){ action = "Banned"; }
                                                    else{ action = "Kicked"; }
                                                    sendPrivateMessage(event_,
                                                                       currentDiscordUser.getName(),
                                                                       action + " by " +
                                                                       ((DiscordMessageEvent)event_)
                                                                       .getDiscordEvent()
                                                                       .getMember()
                                                                       .getEffectiveName() +
                                                                       ". Reason: " + reason);
                                                }
                                            });
                                            if (!currentDiscordUser.hasPrivateChannel()) {
                                                currentDiscordUser.openPrivateChannel().complete();
                                                sendMsg.Start();
                                            } else {
                                                sendMsg.Start();
                                            }
                                            if (isBan) {
                                                controller.ban(currentDiscordMember, ns.getInt("remove_messages")).queue();
                                                sendMessage(event_, "Banned user: " + nick);
                                            } else {
                                                controller.kick(currentDiscordMember);
                                                sendMessage(event_, "Kicked user: " + nick);
                                            }
                                        } else {
                                            if(user.matches("\\d+") && user.length() == 18){
                                                Logger.Info("Treating {0} as id to be hackbanned", user);
                                                if(isBan){
                                                    controller.ban(user, ns.getInt("remove_messages")).queue();
                                                    Logger.Info("Banned user {0}", user);
                                                } else {
                                                    sendMessage(event_, "you cannot \"hackkick\" a user (user: " + user + ")");
                                                }
                                            } else {
                                                sendMessage(event_, "user " + user + " could not be found");
                                            }
                                        }
                                    }
                                }
                            }
                        } else { // irc
                            foreach(User user in event_.getChannel().getUsers()) {
                                foreach(string userStr in users) {
                                    if (userStr.contains("@") && userStr.contains("!")) { // checks if given a hostmask
                                        if (LilGUtil.matchHostMask(user.getHostmask(), userStr)) {
                                            string reason;
                                                if (isBan) {
                                                    event_.getChannel().send().ban(userStr);
                                                        if (ns.getBoolean("kick_ban")) {
                                                            if ((reason = ns.getString("reason")) != null) {
                                                                event_.getChannel().send().kick(user, reason);
                                                            } else {
                                                                event_.getChannel().send().kick(user);
                                                            }
                                                        }
                                                    } else {
                                                        if ((reason = ns.getString("reason")) != null) {
                                                            event_.getChannel().send().kick(user, reason);
                                                        } else {
                                                            event_.getChannel().send().kick(user);
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (user.getNick().equalsIgnoreCase(userStr)) {
                                                    string reason;
                                                    if (isBan) {
                                                        event_.getChannel().send().ban("*!*@" + user.getHostname());
                                                        if (ns.getBoolean("kick_ban").booleanValue()) {
                                                            if ((reason = ns.getString("reason")) != null) {
                                                                event_.getChannel().send().kick(user, reason);
                                                            } else {
                                                                event_.getChannel().send().kick(user);
                                                            }
                                                        }
                                                    } else {
                                                        if ((reason = ns.getString("reason")) != null) {
                                                            event_.getChannel().send().kick(user, reason);
                                                        } else {
                                                            event_.getChannel().send().kick(user);
                                                        }
                                                    }
                                                }
                                            }
                                            }
                                        }
                                    }
                                    break;
                                case "delmsg": // only possible on discord so no need to check
                                    List<string> delMsgArgs;
                                    if ((delMsgArgs = ns.getList("message_span")) != null) {
                                        string firstMessage = delMsgArgs[0], secondMessage = delMsgArgs[1];
                                        TextChannel discordChannel =
                                                ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel();
                                        bool deleting = false;
                                        List<Message> messagesToDel = new List<Message>();
                                        foreach(Message msg in discordChannel.getHistory().getRetrievedHistory()) {
                                            if (deleting) {
                                                messagesToDel.Add(msg);
                                                if (msg.getId() == secondMessage) {
                                                    return;
                                                }
                                            } else {
                                                if (msg.getId() == firstMessage) {
                                                    deleting = true;
                                                    messagesToDel.Add(msg);
                                                }
                                            }
                                        }
                                        discordChannel.deleteMessages(messagesToDel);
                                    }
                                    break;

                                case "+m":
                                    bool state = ns.getBoolean("state"); // True = on, False = off, null = toggle
                                    bool whiteListMode = ns.getBoolean("whitelist");
                                    if (discord) { // ---------------------------Discord---------------------------
                                        string topicStr = "+m | ";
                                        TextChannel mChannel = ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel();
                                        Role publicRole = mChannel.getGuild().getPublicRole();
                                        List<string> roleArgs = ns.getList("roles");
                                        net.dv8tion.jda.core.entities.User currentDiscordUser = ((DiscordUser) currentUser).getDiscordUser();
                                        if (DiscordData.channelRoleMap.containsKey(mChannel)) {
                                            List<Role> roles = (List<Role>) DiscordData.channelRoleMap[mChannel];
                                            if (state == null || !state) { // disabling +m mode
                                                foreach(Role role in roles) {
                                                    PermissionOverride overRide = mChannel.getPermissionOverride(role);
                                                    overRide?.getManager().clear(Permission.MESSAGE_WRITE).queue();
                                                }
                                                DiscordData.channelRoleMap.Remove(mChannel);
                                                if (mChannel.getTopic().startsWith(topicStr)) {
                                                    mChannel.getManager().setTopic(mChannel.getTopic().substring(topicStr.length())).queue();
                                                }
                                                /*if (currentDiscordUser != null) { // commented out due to lib dev forgetting to re-add the delete function lol
                                                    PermissionOverride overRide = mChannel.getPermissionOverride(mChannel.getGuild().getMember(currentDiscordUser));
                                                    if(overRide != null){
                                                        overRide.delete();
                                                    }
                                                }*/
                                                sendMessage(event_, ((DiscordMessageEvent) event_).getDiscordEvent().getMember().getAsMention() +
                                                        " has set mode -m");
                                            } else { // overwriting role list?
                                                foreach(Role role in roles) {
                                                    PermissionOverride overRide = mChannel.getPermissionOverride(role);
                                                    if (overRide != null) {
                                                        overRide.getManager().clear(Permission.MESSAGE_WRITE).queue();
                                                    }
                                                }
                                                roles.Clear();
                                                roles.Add(publicRole);
                                                PermissionOverride overRide = mChannel.getPermissionOverride(publicRole);
                                                overRide?.getManager().clear(Permission.MESSAGE_WRITE).queue();
                                                List<Role> guildRoleList = mChannel.getGuild().getRoles();
                                                if (currentDiscordUser != null) {
                                                    Member currentDiscordMember = mChannel.getGuild().getMember(currentDiscordUser);
                                                    overRide = mChannel.getPermissionOverride(currentDiscordMember);
                                                    if (overRide == null) {
                                                        overRide = mChannel.createPermissionOverride(currentDiscordMember).complete(false);
                                                    }
                                                    overRide.getManager().grant(Permission.MESSAGE_WRITE).queue();

                                                }
                                                if (roleArgs.Count != 0)
                                                    foreach(Role role in guildRoleList) {
                                                        foreach(string roleArg in roleArgs) {
                                                            if (!roleArg.equalsIgnoreCase(role.getName())) continue;
                                                            overRide = mChannel.getPermissionOverride(role) ??
                                                                       mChannel.createPermissionOverride(role).complete(false);
                                                            if (whiteListMode) {
                                                                overRide.getManager().grant(Permission.MESSAGE_WRITE).queue();
                                                            } else {
                                                                overRide.getManager().deny(Permission.MESSAGE_WRITE).queue();
                                                            }
                                                            roles.Add(role);
                                                            roleArgs.Remove(roleArg);
                                                            break;
                                                        }
                                                    }
                                                sendMessage(event_, ((DiscordMessageEvent) event_).getDiscordEvent().getMember().getAsMention() +
                                                        " has updated the role list");
                                            }
                                        } else {
                                            List<Role> guildRoleList = mChannel.getGuild().getRoles();
                                            List<Role> roles = new List<Role>();

                                            PermissionOverride overRide = mChannel.getPermissionOverride(publicRole);
                                            overRide?.getManager().deny(Permission.MESSAGE_WRITE).queue();
                                            if (currentDiscordUser != null) {
                                                Member currentDiscordMember = mChannel.getGuild().getMember(currentDiscordUser);
                                                overRide = mChannel.getPermissionOverride(currentDiscordMember) ??
                                                           mChannel.createPermissionOverride(currentDiscordMember).complete(false);
                                                overRide.getManager().grant(Permission.MESSAGE_WRITE).queue();

                                            }
                                            roles.Add(publicRole);
                                            if (!roleArgs.isEmpty())
                                                foreach(Role role in guildRoleList) {
                                                    foreach(string roleArg in roleArgs) {
                                                        if (roleArg.equalsIgnoreCase(role.getName())) {
                                                            overRide = mChannel.getPermissionOverride(role);
                                                            if (overRide == null) {
                                                                overRide = mChannel.createPermissionOverride(role).complete(false);
                                                            }
                                                            if (whiteListMode) {
                                                                overRide.getManager().grant(Permission.MESSAGE_WRITE).queue();
                                                            } else {
                                                                overRide.getManager().deny(Permission.MESSAGE_WRITE).queue();
                                                            }
                                                            roles.Add(role);
                                                            roleArgs.Remove(roleArg);
                                                            break;
                                                        }
                                                    }
                                                }
                                            DiscordData.channelRoleMap[mChannel] = roles;
                                            mChannel.getManager().setTopic(topicStr + mChannel.getTopic()).queue();
                                            sendMessage(event_, ((DiscordMessageEvent) event_).getDiscordEvent().getMember().getAsMention() +
                                                    " has set mode +m");
                                        }
                                    } else { // ----------------------------------------------------IRC----------------------------------------------------
                                        org.pircbotx.Channel chan = event_.getChannel();
                                        if (chan.containsMode('m')) {
                                            chan.send().removeModerated();
                                        } else {
                                            chan.send().setModerated();
                                        }
                                    }
                                    break;
                                case "+g":
                                    if (discord) {
                                        TextChannel mChannel = ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel();
                                        List<string> expressions = (List<string>) DiscordData.wordFilter[mChannel];
                                        if (ns.getBoolean("list").booleanValue()) {
                                            if (expressions == null || expressions.isEmpty()) {
                                                sendMessage(event_, "+g list is empty");
                                            } else {
                                                sendMessage(event_, expressions.toString());
                                            }
                                        } else if (ns.getBoolean("remove").booleanValue()) {
                                            if (expressions == null || expressions.isEmpty()) {
                                                sendMessage(event_, "+g list is empty, nothing to remove");
                                            } else {
                                                if (expressions.Remove(ns.getString("expression"))) {
                                                    sendMessage(event_, "removed \"" + ns.getString("expression") + "\" from the +g list");
                                                } else {
                                                    sendMessage(event_, "that expression wasn't in the +g list");
                                                }
                                            }
                                        } else {
                                            if (expressions == null) {
                                                expressions = new List<>();
                                                DiscordData.wordFilter[mChannel] = expressions;
                                            }
                                            expressions.Add(ns.getString("expression"));
                                        }
                                    } else {
                                        org.pircbotx.Channel chan = event_.getChannel();
                                        if (ns.getBoolean("remove").booleanValue()) {
                                            chan.send().setMode("-g", chan.getName(), ns.getString("expression"));
                                        } else {
                                            chan.send().setMode("+g", chan.getName(), ns.getString("expression"));
                                        }
                                    }
                                    break;

                                case "topic":
                                    string topicStr = argJoiner(ns.getList("newTopic").toArray(new string[]{}), 0);
                                    if (discord) {
                                        ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel().getManager().setTopic(topicStr).queue();
                                    } else {
                                        event_.getChannel().send().setTopic(topicStr);
                                    }
                                    break;
                            }

                        } catch (ArgumentParserException e) {
                            sendCommandHelp(event_, parser);
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    }

    // !muteServer - mutes entire server
                else if (commandChecker(event_, arg, "muteServer")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        if (getArg(arg, 1) != null) {
                            if (getArg(arg, 1).equalsIgnoreCase("add")) {
                                mutedServerList.Add(getArg(arg, 2));
                            } else if (getArg(arg, 1).equalsIgnoreCase("del")) {
                                mutedServerList.Remove(getArg(arg, 2));
                            }
                        } else {
                            mutedServerList.Add(network == Network.discord ? ((DiscordMessageEvent) event_).getDiscordEvent().getGuild().getId() : event_.getBot().getServerHostname());
                        }
                    }
                }

    // !command - Sets what commands can be used where
                else if (commandChecker(event_, arg, "command")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        string[] commandArg = arg;
                        if (event_ is DiscordMessageEvent) {
                            commandArg = LilGUtil.splitMessage(((DiscordMessageEvent) event_).getDiscordEvent().getMessage().getStrippedContent());
                        }
                        Dictionary<string, List<string>> allowedCommands = FozruciX.allowedCommands[getSeverName(event_, true)];
                        if (allowedCommands == null) {
                            allowedCommands = new Dictionary<string, List<string>>();
                            FozruciX.allowedCommands[getSeverName(event_, true)] = allowedCommands;
                        }
                        if (getArg(commandArg, 2) != null) {
                            sbyte mode = 0;
                            string chan = getArg(commandArg, 1);
                            for (byte i = 2; getArg(commandArg, i) != null; i++) {
                                string command = getArg(commandArg, i);
                                if (command.startsWith("-")) {
                                    mode = -1;
                                    command = command.substring(1, command.length());
                                } else if (command.startsWith("+")) {
                                    mode = +1;
                                    command = command.substring(1, command.length());
                                }
                                if (mode == -1) {
                                    allowedCommands[chan].Remove(command);
                                    sendMessage(event_, "Removed command ban on " + command + " For channel " + chan);
                                } else if (mode == 1) {
                                    if (!allowedCommands.ContainsKey(chan)) {
                                        allowedCommands[chan] = new List<string>();
                                    }
                                    allowedCommands[chan].Add(command);
                                    sendMessage(event_, "Added command ban on " + command + " for channel " + chan);
                                } else {
                                    sendMessage(event_, "The command " + command + " is " + ((allowedCommands[chan]).Contains(command) ? "" : "not ") + "Banned from " + chan);
                                }
                            }
                        } else if (getArg(commandArg, 1) != null) {
                            sendMessage(event_, allowedCommands[getArg(commandArg, 1)].toString());
                        } else {
                            sendMessage(event_, allowedCommands.toString());
                        }
                        addCooldown(event_.getUser());
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !Commands - lists commands that can be used
                else if (commandChecker(event_, arg, "Commands")) {
                    if (getArg(arg, 1) == null) {
                        sendNotice(event_, event_.getUser().getNick(), "List of Commands so far. for more info on these Commands do " + prefix + "Commands. Commands with \"Joke: \" are joke Commands that can be disabled");
                        sendNotice(event_, event_.getUser().getNick(), Arrays.asList(COMMANDS).toString());
                    } else {
                        getHelp(event_, getArg(arg, 1));
                    }
                    addCooldown(event_.getUser());


                }

    // !getBots - gets all bots
                else if (commandChecker(event_, arg, "getBots")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        try {
                            object[] temp = manager.getBots().toArray();
                            string bots = "";
                            foreach(object aTemp in temp) {
                                string server = ((PircBotX) aTemp).getServerInfo().getNetwork();
                                if (server == null) {
                                    server = ((PircBotX) aTemp).getServerHostname();
                                }
                                string nick = ((PircBotX) aTemp).getNick();
                                bots += "Server: " + server + " Nick: " + nick + " | ";
                            }
                            sendMessage(event_, bots.substring(0, bots.lastIndexOf("|")));
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !addServer - adds a bot to a server
                else if (commandChecker(event_, arg, "addServer")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        string[] args = formatstringArgs(arg);
                        ArgumentParser parser = ArgumentParsers.newArgumentParser("addServer")
                                .description("Connects the bot to a server")
                                .defaultHelp(true);
                        parser.addArgument("address").type(typeof(string))
                                .help("The server to connect to");
                        parser.addArgument("channelList").type(typeof(string))
                                .help("List of channels to autoconnect to");
                        parser.addArgument("-k", "--key").type(typeof(string)).setDefault((Object) null)
                                .help("The server to connect to");
                        parser.addArgument("-p", "--port").type(typeof(int)).setDefault(6667)
                                .help("Sets what port to connect to");
                        parser.addArgument("-s", "--ssl").type(typeof(bool)).action(Arguments.storeTrue())
                                .help("Specifies if the server port is SSL");
                        Namespace ns;
                        try {
                            ns = parser.parseArgs(args);
                            Logger.Debug(ns.toString());
                            Configuration.Builder normal = null;
                            string server = ns.getString("address");
                            int port = ns.getInt("port");
                            if (LilGUtil.equalsAny(server.toLowerCase(), "badnik", "network", "caffie", "esper", "nova")) {
                                port = 6697;
                                if (FozConfig.Debug) {
                                    switch (server.toLowerCase()) {
                                        case "badnik":
                                            normal = FozConfig.DebugConfig;
                                            server = FozConfig.Badnik;
                                            break;
                                        case "network":
                                            //normal = FozConfig.twitchDebug;
                                            server = FozConfig.Twitch;
                                            break;
                                        case "caffie":
                                            normal = FozConfig.DebugConfigSmwc;
                                            server = FozConfig.Caffie;
                                            break;
                                        case "esper":
                                            normal = FozConfig.DebugConfigEsper;
                                            server = FozConfig.Esper;
                                            break;
                                        case "nova":
                                            normal = FozConfig.DebugConfigNova;
                                            server = FozConfig.Nova;
                                            break;
                                    }
                                } else {
                                    switch (ns.getString("address").toLowerCase()) {
                                        case "badnik":
                                            normal = FozConfig.Normal;
                                            server = FozConfig.Badnik;
                                            break;
                                        case "network":
                                            //normal = FozConfig.twitchNormal;
                                            server = FozConfig.Twitch;
                                            break;
                                        case "caffie":
                                            normal = FozConfig.NormalSmwc;
                                            server = FozConfig.Caffie;
                                            break;
                                        case "esper":
                                            normal = FozConfig.NormalEsper;
                                            server = FozConfig.Esper;
                                            break;
                                        case "nova":
                                            normal = FozConfig.NormalNova;
                                            server = FozConfig.Nova;
                                            break;
                                    }
                                }
                            } else if (ns.getBoolean("ssl").booleanValue()) {
                                normal = new Configuration.Builder()
                                        .setEncoding(Charset.forName("UTF-8"))
                                        .setAutoReconnect(true)
                                        .setAutoReconnectAttempts(5)
                                        .setNickservPassword(CryptoUtil.decrypt(FozConfig.Password))
                                        .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                                        .setLogin(bot.getConfiguration().getLogin())
                                        .setRealName(bot.getConfiguration().getRealName())
                                        .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                                        .addListener(new FozruciX(manager));
                            } else {
                                normal = new Configuration.Builder()
                                        .setEncoding(Charset.forName("UTF-8"))
                                        .setAutoReconnect(true)
                                        .setAutoReconnectAttempts(5)
                                        .setNickservPassword(CryptoUtil.decrypt(FozConfig.Password))
                                        .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                                        .setLogin(bot.getConfiguration().getLogin())
                                        .setRealName(bot.getConfiguration().getRealName())
                                        .addListener(new FozruciX(manager));
                            }
                            Debug.Assert(normal != null);
                            manager.addBot(normal.buildForServer(server, port, ns.getString("key")));
                            sendMessage(event_, "Connecting bot to " + ns.getString("address"), false);
                        } catch (ArgumentParserException e) {
                            sendCommandHelp(event_, parser);
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !serverHostName - Gets the Server Host Name
                else if (commandChecker(event_, arg, "serverHostName")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        sendMessage(event_, bot.getServerHostname(), false);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !clearLogin - Clears login info to test auth related thing
                else if (commandChecker(event_, arg, "clearLogin")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        currentUser = bot.getUserBot();
                        sendMessage(event_, "Logged out", false);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !RESPOND_TO_PMS - sets whether or not to respond to PMs
                else if (commandChecker(event_, arg, "RESPOND_TO_PMS")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        BOOLS[RESPOND_TO_PMS] = !BOOLS[RESPOND_TO_PMS];
                        sendMessage(event_, "Responding to PMs: " + BOOLS[RESPOND_TO_PMS], false);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !Connect - joins a channel
                else if (commandChecker(event_, arg, "Connect")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        bot.send().joinChannel(getArg(arg, 1));
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !setDebugLevel - sets debugging level
                else if (commandChecker(event_, arg, "setDebugLevel")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        Logger.setLevel(Level.toLevel(getArg(arg, 1).toUpperCase()));
                        sendMessage(event_, "Set debug level to " + Logger.getLevel().toString());
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !setAvatar - sets the avatar of the bot
                else if (commandChecker(event_, arg, "setAvatar")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        avatar = getArg(arg, 1);
                        sendMessage(event_, "Avatar set", false);
                        LinkedList<User> users = new LinkedList<>();
                        for (Channel channels : bot.getUserBot().getChannels()) {
                            channels.getUsers().stream().filter(curUser -> users.indexOf(curUser) == -1 && !curUser.getNick().equalsIgnoreCase(bot.getNick())).forEach(users::add);
                        }
                        for (byte i = 0; users.Count >= i; i++) {
                            if (users.get(i).getRealName().startsWith("\u0003")) {
                                bot.send().notice(users.get(i).getNick(), "\u0001AVATAR " + avatar + "\u0001");
                            }
                        }

                    } else {
                        permErrorchn(event_);
                    }
                }

    // !loadData - force a reload of the save data
                else if (commandChecker(event_, arg, "loadData")) {
                    if (checkPerm(event_.getUser(), 2)) {
                        loadData();
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !SkipLoad - skips loading save data
                else if (commandChecker(event_, arg, "SkipLoad")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        BOOLS.set(DATA_LOADED);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !reverseList - Reverses a list
                else if (commandChecker(event_, arg, "reverseList")) {
                    string[] list = Arrays.copyOfRange(arg, 1, arg.length);
                    string temp = "Uh oh, something broke";
                    int i;
                    for (i = list.length - 1 + arrayOffset; i > 0; i--) {
                        temp = list[0];
                        global::java.lang.System.arraycopy(list, 1, list, 0, i - 1);
                        list[i] = temp;
                    }
                    list[i] = temp;
                    string str = new LinkedList<>(Arrays.asList(list)).toString();
                    sendMessage(event_, str);
                    addCooldown(event_.getUser());

                }

    // !getDate - test get date
                else if (commandChecker(event_, arg, "getDate")) {
                    try {
                        Parser parser = new Parser();
                        /*
                        long time = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
                        WaitForQueue queue = new WaitForQueue(bot);
                        event_.getUser().send().ctcpCommand("TIME");

                        //Infinite loop since we might receive messages that aren't WaitTest's.
                        while ((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) < time+5000) {

                            //Use the waitFor() method to wait for a MessageEvent.
                            //This will block (wait) until a message event_ comes in, ignoring
                            //everything else
                            NoticeEvent currentEvent = queue.waitFor(NoticeEvent.class);
                            //Check if this message is the "ping" command
                            if (currentEvent.getMessage().toLowerCase().contains("time")) {
                                LinkedList<DateGroup> groups = parser.parse((currentEvent.getMessage()));
                                ZonedDateTime time2 = groups[0].getDates()[0];

                                TimeZone.setDefault(new SimpleTimeZone(time2.getTimezoneOffset(), new ZoneId()));
                            }
                        }
                        */


                        List<DateGroup> groups = parser.parse(argJoiner(arg, 1));
                        sendMessage(event_, groups.get(0).getDates().get(0).toString());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    Logger.Debug(ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    addCooldown(event_.getUser());

                }

    // !do nothing - does nothing
                else if (commandChecker(event_, arg, "do")) {
                    if (getArg(arg, 1).equalsIgnoreCase("nothing")) {
                        if (LilGUtil.randInt(0, 2) == 0) {
                            sendMessage(event_, "no");
                            addCooldown(event_.getUser());
                        }
                    }


                }

    // !markov - makes Markov chains
                else if (commandChecker(event_, arg, "markov")) {
                    if (markovChain == null) {
                        markovChain = new Dictionary<>();
                    }
                    bool loop = true;
                    string newPhrase = "";
                    try {
                        while (loop) {
                            // List to hold the phrase

                            // string for the next word
                            string nextWord = null;
                            //bool matches = getArg(arg, 1) != null;
                            bool matches = false;
                            int matchAttempts = 0;
                            do {
                                newPhrase = "";
                                for (int loops = 0; LilGUtil.randInt(0, 3) == 1 && loops < 3; loops++) {
                                    if (loops > 0) {
                                        newPhrase += " ";
                                    }
                                    // Select the first word
                                    LinkedList<string> startWords = (LinkedList<string>) markovChain["_start"];

                                    for (int i = 1 + arrayOffset; i < arg.length; i++) {
                                        if (startWords.contains(arg[i])) {
                                            matches = false;
                                            nextWord = startWords[startWords.indexOf(arg[i])];
                                        }
                                    }
                                    if (nextWord == null) {
                                        int startWordsLen = startWords.Count;
                                        nextWord = startWords[rnd.nextInt(startWordsLen)];
                                    }


                                    int greaterThanOne = nextWord.length() > 1 ? 2 : (nextWord.length() > 0 ? 1 : 0);
                                    newPhrase += nextWord.substring(0, greaterThanOne) + "\u200B" + nextWord.substring(greaterThanOne, nextWord.length());

                                    // Keep looping through the words until we've reached the end
                                    while (nextWord.charAt(nextWord.length() - 1) != '.') {
                                        List<string> wordSelection = (List<string>) markovChain[nextWord];
                                        nextWord = null;

                                        for (int i = 1; i < arg.length; i++) {
                                            if (startWords.contains(arg[i])) {
                                                matches = false;
                                                nextWord = startWords[startWords.indexOf(arg[i])];
                                            }
                                        }
                                        if (nextWord == null) {
                                            int wordSelectionLen = wordSelection.Count;
                                            nextWord = wordSelection[rnd.nextInt(wordSelectionLen)];
                                        }

                                        greaterThanOne = nextWord.length() > 1 ? 2 : (nextWord.length() > 0 ? 1 : 0);
                                        if (newPhrase.isEmpty()) {
                                            newPhrase = nextWord.substring(0, greaterThanOne) + "\u200B" + nextWord.substring(greaterThanOne, nextWord.length());
                                        } else {
                                            newPhrase += " " + nextWord.substring(0, greaterThanOne) + "\u200B" + nextWord.substring(greaterThanOne, nextWord.length());
                                        }
                                    }
                                    if (newPhrase.lastIndexOf(" ") != newPhrase.indexOf(" ") && !newPhrase.contains("!")) {
                                        loop = false;
                                    }
                                }
                                matchAttempts++;
                            } while (matches || matchAttempts > 50);
                        }
                        sendMessage(event_, "\u0002\u0002" + newPhrase, false);
                        Logger.Debug(newPhrase.replace('\u200B', '▮'));
                    } catch (IllegalArgumentException e) {
                        sendError(event_, new Exception("No words have been added to the database, Try saying something!"));
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());

                }

    // !8ball - ALL HAIL THE MAGIC 8-BALL
                else if (commandChecker(event_, arg, "8Ball")) {
                    int choice = LilGUtil.randInt(1, 20);
                    string response = "";

                    switch (choice) {
                        case 1:
                            response = "It is certain";
                            break;
                        case 2:
                            response = "It is decidedly so";
                            break;
                        case 3:
                            response = "Without a doubt";
                            break;
                        case 4:
                            response = "Yes - definitely";
                            break;
                        case 5:
                            response = "You may rely on it";
                            break;
                        case 6:
                            response = "As I see it, yes";
                            break;
                        case 7:
                            response = "Most likely";
                            break;
                        case 8:
                            response = "Outlook good";
                            break;
                        case 9:
                            response = "Signs point to yes";
                            break;
                        case 10:
                            response = "Yes";
                            break;
                        case 11:
                            response = "Reply hazy, try again";
                            break;
                        case 12:
                            response = "Ask again later";
                            break;
                        case 13:
                            response = "Better not tell you now";
                            break;
                        case 14:
                            response = "Cannot predict now";
                            break;
                        case 15:
                            response = "Concentrate and ask again";
                            break;
                        case 16:
                            response = "Don't count on it";
                            break;
                        case 17:
                            response = "My reply is no";
                            break;
                        case 18:
                            response = "My sources say no";
                            break;
                        case 19:
                            response = "Outlook not so good";
                            break;
                        case 20:
                            response = "Very doubtful";
                            break;
                    }
                    sendMessage(event_, response);
                    addCooldown(event_.getUser());

                }

    // !setMessage - Sets different message formats
                else if (commandChecker(event_, arg, "setMessage")) {
                    if (checkPerm(event_.getUser(), 8)) {
                        switch (getArg(arg, 1).toLowerCase()) {
                            case "normal":
                                messageMode = MessageModes.normal;
                                sendMessage(event_, "Message mode set back to normal");
                                break;
                            case "reverse":
                                messageMode = MessageModes.reversed;
                                sendMessage(event_, "Message is now reversed");
                                break;
                            case "wordreverse":
                                messageMode = MessageModes.wordReversed;
                                sendMessage(event_, "Message words reversed");
                                break;
                            case "scramble":
                                messageMode = MessageModes.scrambled;
                                sendMessage(event_, "Messages are scrambled");
                                break;
                            case "wordscramble":
                                messageMode = MessageModes.wordScrambled;
                                sendMessage(event_, "Message words are scrambled");
                                break;
                            case "caps":
                                messageMode = MessageModes.CAPS;
                                sendMessage(event_, "Messages are in all caps");
                                break;
                            default:
                                sendMessage(event_, "Not a message mode");
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }


    // !CheckLink - checks links, duh
                else if (commandChecker(event_, arg, "CheckLink")) {
                    try {
                        Document doc = Jsoup.connect(getArg(arg, 1)).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2783.4 Safari/537.36").timeout(5000).get();
                        sendMessage(event_, "Title: " + doc.title(), false);
                        addCooldown(event_.getUser());
                    } catch (UnsupportedMimeTypeException e) {
                        sendMessage(event_, "type: " + e.getMimeType());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                }

    // !comeback - gets one of the permission error statements
                else if (commandChecker(event_, arg, "comeback")) {
                    permErrorchn(event_);
                    addCooldown(event_.getUser());
                }


    // !Time - Tell the time
                else if (commandChecker(event_, arg, "time")) {
                    try {
                        string time = new Date().toString();
                        sendMessage(event_, " The time is now " + time);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());

                }

    // !perms - edit privileged users
                else if (commandChecker(event_, arg, "perms")) {
                    if (checkPerm(event_.getUser(), int.MAX_VALUE)) {
                        if (getArg(arg, 1).equalsIgnoreCase("set")) {
                            try {
                                if (authedUser.contains(getArg(arg, 2))) {
                                    try {
                                        authedUserLevel.set(authedUser.indexOf(getArg(arg, 2)), int.decode(getArg(arg, 3)));
                                    } catch (Exception e) {
                                        sendError(event_, e);
                                    }
                                    sendMessage(event_, "Set " + getArg(arg, 2) + " To level " + getArg(arg, 3));
                                } else {
                                    try {
                                        authedUser.Add(getArg(arg, 2));
                                        authedUserLevel.Add(int.decode(getArg(arg, 3)));
                                    } catch (Exception e) {
                                        sendError(event_, e);
                                    }
                                    sendMessage(event_, "Added " + getArg(arg, 2) + " To authed users with level " + getArg(arg, 3));
                                }
                            } catch (Exception e) {
                                sendError(event_, e);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("del")) {
                            try {
                                int index = authedUser.indexOf(getArg(arg, 2));
                                authedUserLevel.Remove(index);
                                authedUser.Remove(index);
                            } catch (Exception e) {
                                sendError(event_, e);
                            }
                            sendMessage(event_, "Removed " + getArg(arg, 2) + " from the authed user list");
                        } else if (getArg(arg, 1).equalsIgnoreCase("clear")) {
                            authedUser.clear();
                            authedUserLevel.clear();
                            sendMessage(event_, "Permission list cleared");
                        } else if (getArg(arg, 1).equalsIgnoreCase("List")) {
                            sendMessage(event_, authedUser.toString());
                        } else {
                            int place = -1;
                            try {
                                for (int i = 0; authedUser.Count >= i; i++) {
                                    if (((string) authedUser[i]).equalsIgnoreCase(getArg(arg, 1))) {
                                        place = i;
                                    }
                                }
                            } catch (IndexOutOfBoundsException e) {
                                sendMessage(event_, "That user wasn't found in the list of authed users", false);
                            }
                            if (place == -1) {
                                sendMessage(event_, "That user wasn't found in the list of authed users", false);
                            } else {
                                sendMessage(event_, "User " + (string) authedUser[place] + " Has permission level " + (int) authedUserLevel[place], false);
                            }

                        }

                    } else {
                        permErrorchn(event_);
                    }
                }

    // !CalcA - Calculates with Wolfram Alpha
                else if (commandChecker(event_, arg, "CalcA")) {
                    if (checkPerm(event_.getUser(), 0)) {
                        // The WAEngine is a BOT_FACTORY for creating WAQuery objects,
                        // and it also used to perform those queries. You can set properties of
                        // the WAEngine (such as the desired API output format types) that will
                        // be inherited by all WAQuery objects created from it. Most applications
                        // will only need to crete one WAEngine object, which is used throughout
                        // the life of the application.
                        WAEngine engine;
                        try {
                            engine = new WAEngine();
                        } catch (Exception e) {
                            sendError(event_, e);
                            return;
                        }

                        // These properties will be set in all the WAQuery objects created from this WAEngine.
                        engine.setAppID(APP_ID);
                        engine.addFormat("plaintext");

                        // Create the query.
                        WAQuery query = engine.createQuery();

                        // Set properties of the query.
                        query.setInput(argJoiner(arg, 1));

                        try {
                            // For educational purposes, print out the URL we are about to send:
                            Logger.Debug("Query URL:" + engine.toURL(query));

                            // This sends the URL to the Wolfram|Alpha server, gets the XML result
                            // and parses it into an object hierarchy held by the WAQueryResult object.
                            WAQueryResult queryResult = engine.performQuery(query);

                            if (queryResult.isError()) {
                                Logger.Error("Query error");
                                Logger.Error("  error code: " + queryResult.getErrorCode());
                                Logger.Error("  error message: " + queryResult.getErrorMessage());
                            } else if (!queryResult.isSuccess()) {
                                sendMessage(event_, "Query was not understood; no results available.");
                                Logger.Warn("Query was not understood; no results available.");
                            } else {
                                // Got a result.
                                Logger.Debug("Successful query. Pods follow:\n");
                                byte results = 0;
                                List<string> backupResults = new List<>();
                                for (WAPod pod : queryResult.getPods()) {
                                    if (!pod.isError()) {
                                        Logger.Debug("pod start: " + pod.getTitle());
                                        string solutions = "";
                                        for (WASubpod subPod : pod.getSubpods()) {
                                            for (Object element : subPod.getContents()) {
                                                if (element is WAPlainText) {
                                                    Logger.Debug("subpod start");
                                                    string elementResult = ((WAPlainText) element).getText().replace('\uF7D9', '=').replace("\uF74E", "\u001Di\u001D");
                                                    if (LilGUtil.containsAny(pod.getTitle(), "Result", "Exact result", "Decimal approximation")
                                                            && results < 2) {
                                                        sendMessage(event_, pod.getTitle() + ": " + elementResult);
                                                        results++;
                                                    } else if (LilGUtil.equalsAny(pod.getTitle(), "Solution", "Complex solution", "Roots", "Complex roots")) {
                                                        if (solutions.isEmpty()) {
                                                            solutions = pod.getTitle() + ": " + elementResult;
                                                        } else {
                                                            solutions += " or " + elementResult;
                                                        }
                                                    } else if (LilGUtil.containsAny(pod.getTitle(), "Alternate form assuming ", "Alternate form", "Alternative representations", "Input interpretation")) {
                                                        backupResults.Add(pod.getTitle() + ": " + elementResult);
                                                    } else {
                                                        Logger.Debug(pod.getTitle() + ": " + elementResult);
                                                    }
                                                    Logger.Debug("end of sub pod");
                                                }
                                            }
                                        }
                                        if (!solutions.isEmpty()) {
                                            sendMessage(event_, solutions);
                                            results++;
                                        }
                                        Logger.Debug("End of pod");
                                    }
                                }
                                if (results < 2) {
                                    if (results == 0 && backupResults.Count == 0) {
                                        sendMessage(event_, "Sorry, no result was found");
                                    } else {
                                        for (string backupResult : backupResults) {
                                            if (results < 2) {
                                                sendMessage(event_, backupResult);
                                                results++;
                                            } else break;
                                        }
                                    }
                                }
                                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
                            }
                        } catch (WAException e) {
                            sendError(event_, e);
                        }
                        addCooldown(event_.getUser());
                    }
                }

    // !CalcJ - calculate a expression
                else if (commandChecker(event_, arg, "CalcJ")) {
                    string[] args = formatstringArgs(arg);
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("CalcJ")
                            .description("Calculates an expression")
                            .defaultHelp(true);
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-v", "--Val").type(Double.class).setDefault(-1.0)
                            .help("Sets what the variable starts at");
                    parser.addArgument("-c", "--char").type(typeof(string)).setDefault("x")
                            .help("Sets what character the variable is");
                    parser.addArgument("-s", "--step").type(Double.class).setDefault(1.0)
                            .help("Sets How much to increase x at");
                    parser.addArgument("-a", "--amount").type(Byte.class).setDefault(3)
                            .help("Sets How many times to increase");
                    parser.addArgument("-p", "--precision").type(long.class).setDefault(64L)
                            .help("Sets what precision to calculate to");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        Logger.Debug(ns.toString());
                        EVALUATOR.setPrecision(ns.getlong("precision"));
                        if (LilGUtil.containsAny(message, "-v", "-c", "-s", "-a", "--Val", "--char", "--step", "--amount")) {
                            double x = ns.getDouble("Val");
                            double step = ns.getDouble("step");
                            byte calcAmount = ns.getByte("amount");
                            if (calcAmount > 5 && !checkPerm(event_.getUser(), 8)) {
                                calcAmount = 5;
                            }
                            int count = 0;
                            LinkedList<Apfloat> eval = new LinkedList<>();
                            while (count <= calcAmount) {
                                VARIABLE_SET.set((string) ns["char"], new Apfloat(x));
                                //noinspection SuspiciousToArrayCall
                                eval.Add(EVALUATOR.evaluate(argJoiner(ns.getList("expression").toArray(new string[]{}), 0).toLowerCase(), VARIABLE_SET));
                                x += step;
                                count++;
                            }
                            sendMessage(event_, eval.toString().replace("[", "").replace("]", "").replace(", ", " | "));
                        } else {
                            //noinspection SuspiciousToArrayCall
                            string[] expression = ns.getList("expression").toArray(new string[]{});
                            Apfloat eval = EVALUATOR.evaluate(argJoiner(expression, 0, 0));
                            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                            df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
                            df.setMaximumintDigits(340);
                            sendMessage(event_, df.format(eval));
                        }
                    } catch (ArgumentParserException e) {
                        sendCommandHelp(event_, parser);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());
                }

    // !pix - shows everyone the real face
                else if (commandChecker(event_, arg, "pix")) {
                    if (checkPerm(event_.getUser(), int.MAX_VALUE)) {
                        if (getArg(arg, 1) != null) {
                            char toggle = getArg(arg, 1).toLowerCase().charAt(0);
                            if (toggle == '0' || toggle == 'n') {
                                updateAvatar = false;
                                sendMessage(event_, "Avatar updates are now off");
                            } else if (toggle == '1' || toggle == 'y') {
                                updateAvatar = true;
                                sendMessage(event_, "Avatar updates are now on");
                            }
                        }
                        if (getArg(arg, 1).equalsIgnoreCase("upload") && checkPerm(event_.getUser(), 1)) {
                            sendFile(event_, DiscordAdapter.avatarFile, "[Me]");
                        }
                    } else {
                        sendMessage(event_, avatar + " [Me]");
                    }
                    addCooldown(event_.getUser());
                }

    // !Git - gets the link to source code
                else if (commandChecker(event_, arg, "Git")) {
                    sendMessage(event_, "Link to source code: https://github.com/lilggamegenuis/FozruciX");
                }

    // !Bugs - gets the link to issues
                else if (commandChecker(event_, arg, "Bugs")) {
                    sendMessage(event_, "Link to issue tracker: https://github.com/lilggamegenuis/FozruciX/issues");
                }

    // !vgm - links to my New mix tapes :V
                else if (commandChecker(event_, arg, "vgm")) {
                    sendMessage(event_, "Link to My smps music: https://drive.google.com/open?id=0B3aju_x5_V--ZjAyLWZEUnV1aHc");
                }

    // !cleanMarkov - cleans duplicates from markov chain list
                else if (commandChecker(event_, arg, "cleanMarkov")) {
                    int amountCleared = 0;
                    for (LinkedList<string> list : markovChain.values()) {
                        LilGUtil.removeDuplicates(list);
                        amountCleared++;
                    }
                    sendMessage(event_, "Cleared " + amountCleared);
                }

    // !GC - Runs the garbage collector
                else if (commandChecker(event_, arg, "GC")) {
                    int num = LilGUtil.gc();
                    if (num == 1) {
                        sendMessage(event_, "Took out the trash");
                    } else {
                        sendMessage(event_, "Took out " + num + " Trash bags");
                    }
                }

    // !JS - evaluates a expression in JavaScript
                else if (commandChecker(event_, arg, "JS")) {
                    string[] args = formatstringArgs(LilGUtil.splitMessage(message, 0, false));
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("JS")
                            .description("Calculates an expression")
                            .defaultHelp(true);
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-b", "--base", "-r", "--radix").type(typeof(int)).setDefault(10)
                            .help("Sets what radix to output to. Only applies if output is numeric");
                    parser.addArgument("-k", "--kill").type(typeof(bool)).action(Arguments.storeTrue())
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        Logger.Debug(ns.toString());
                        if (ns.getBoolean("kill")) {
                            //noinspection deprecation
                            js.interrupt();
                            js = null;
                            sendMessage(event_, "JavaScript Thread killed", false);
                        } else {
                            if (getArg(arg, 1) != null) {
                                Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
                                    if (ex is ThreadDeath) {
                                        lastJsChannel.send().message(event_.getUser(), "JavaScript thread killed by " + lastJsUser.getNick() + " in " + event_.getChannel().getName());
                                    } else {
                                        sendError(event_, ex);
                                    }
                                };
                                if (js == null) {
                                    //noinspection SuspiciousToArrayCall
                                    js = new JavaScript(event_, argJoiner(ns.getList("expression").toArray(new string[]{}), 0, 0), ns.getInt("base"));
                                    js.setUncaughtExceptionHandler(exceptionHandler);
                                    js.start();
                                } else {
                                    //noinspection SuspiciousToArrayCall
                                    js.runNewJavaScript(event_, argJoiner(ns.getList("expression").toArray(new string[]{}), 0, 0), ns.getInt("base"));
                                }
                                Logger.Debug(ns.getString("expression"));
                                lastJsUser = event_.getUserHostmask();
                                if (channel != null) {
                                    lastJsChannel = event_.getChannel();
                                }
                            } else {
                                sendMessage(event_, "Requires more arguments");
                            }
                        }
                    } catch (ArgumentParserException e) {
                        sendCommandHelp(event_, parser);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());

                }

    // !py - Evaluates python code
                else if (commandChecker(event_, arg, "py")) {
                    string[] args = formatstringArgs(LilGUtil.splitMessage(message, 0, false));
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("py")
                            .description("Calculates an expression")
                            .defaultHelp(true);
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-b", "--base", "-r", "--radix").type(typeof(int)).setDefault(10)
                            .help("Sets what radix to output to. Only applies if output is numeric");
                    parser.addArgument("-k", "--kill").type(typeof(bool)).action(Arguments.storeTrue())
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        Logger.Debug(ns.toString());
                        if (ns.getBoolean("kill")) {
                            //noinspection deprecation
                            py.interrupt();
                            py = null;
                            sendMessage(event_, "Python Thread killed", false);
                        } else {
                            if (getArg(arg, 1) != null) {
                                Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
                                    if (ex is ThreadDeath) {
                                        lastJsChannel.send().message(event_.getUser(), "Python thread killed by " + lastJsUser.getNick() + " in " + event_.getChannel().getName());
                                    } else {
                                        sendError(event_, ex);
                                    }
                                };
                                if (py == null) {
                                    //noinspection SuspiciousToArrayCall
                                    py = new Python();
                                    py.setUncaughtExceptionHandler(exceptionHandler);
                                    py.start();
                                }
                                //noinspection SuspiciousToArrayCall
                                py.runNewPython(event_, argJoiner(ns.getList("expression").toArray(new string[]{}), 0, 0), ns.getInt("base"));

                                Logger.Debug(ns.getString("expression"));
                                lastJsUser = event_.getUserHostmask();
                                if (channel != null) {
                                    lastJsChannel = event_.getChannel();
                                }
                            } else {
                                sendMessage(event_, "Requires more arguments");
                            }
                        }
                    } catch (ArgumentParserException e) {
                        sendCommandHelp(event_, parser);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());

                }

    // if someone tells the bot to "Go to hell" do this
                else if (message.contains(bot.getNick()) && message.toLowerCase().contains("go to hell")) {
                    if (!checkPerm(event_.getUser(), 9001)) {
                        sendMessage(event_, "I Can't go to hell, i'm all out of vacation days", false);
                    }
                }

    // !count - counts amount of something
                else if (commandChecker(event_, arg, "count")) {
                    if (checkPerm(event_.getUser(), 1)) {
                        if (getArg(arg, 1) != null && getArg(arg, 1).equalsIgnoreCase("setup")) {
                            counter = getArg(arg, 2);
                            if (getArg(arg, 3) != null) {
                                counterCount = int.decode(getArg(arg, 3));
                            }
                        }
                        if (commandChecker(event_, arg, "count")) {
                            counterCount++;
                            sendMessage(event_, "Number of times that " + counter + " is: " + counterCount, false);
                        }
                        addCooldown(event_.getUser());
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !stringToBytes - convert a string into a Byte array
                else if (commandChecker(event_, arg, "stringToBytes")) {
                    try {
                        sendMessage(event_, LilGUtil.getBytes(argJoiner(arg, 1)));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        sendMessage(event_, "Not enough args. Must provide a string");
                    }
                    addCooldown(event_.getUser());

                }

    // !LookUpWord - Looks up a word in the Wiktionary
                else if (commandChecker(event_, arg, "LookupWord")) {
                    try {
                        string lookedUpWord = "Null";
                        Logger.Debug("Looking up word");
                        // Connect to the Wiktionary database.
                        Logger.Debug("Opening DICTIONARY");
                        IWiktionaryEdition wkt = JWKTL.openEdition(WIKTIONARY_DIRECTORY);
                        Logger.Debug("Getting page for word");
                        IWiktionaryPage page = wkt.getPageForWord(getArg(arg, 1));
                        if (page != null) {
                            Logger.Debug("Getting entry");
                            IWiktionaryEntry entry;
                            if (getArg(arg, 2) != null && LilGUtil.isNumeric(getArg(arg, 2))) {
                                entry = page.getEntry(int.decode(getArg(arg, 2)));
                            } else {
                                entry = page.getEntry(0);
                            }
                            Logger.Debug("getting sense");
                            IWiktionarySense sense = entry.getSense(1);
                            Logger.Debug("getting Plain text");
                            if (getArg(arg, 2) != null) {
                                int subCommandNum = 2;
                                if (LilGUtil.isNumeric(getArg(arg, 2))) {
                                    subCommandNum++;
                                }
                                if (arg.length > subCommandNum + arrayOffset && arg[subCommandNum - 1].equalsIgnoreCase("Example")) {
                                    if (sense.getExamples().Count > 0) {
                                        lookedUpWord = ((IWikistring) sense.getExamples()[0]).getPlainText();
                                    } else {
                                        sendMessage(event_, "No examples found");
                                    }
                                } else {
                                    lookedUpWord = sense.getGloss().getPlainText();
                                }
                            } else {
                                lookedUpWord = sense.getGloss().getPlainText();
                            }
                            Logger.Debug("Sending message");
                            if (!lookedUpWord.isEmpty()) {
                                sendMessage(event_, lookedUpWord);
                            } else {
                                sendMessage(event_, "Empty response from Database");
                            }
                        } else {
                            sendMessage(event_, "That page couldn't be found.");
                        }

                        // Close the database connection.
                        wkt.close();
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());

                }

    //!lookup - Looks up something in Wikipedia
                else if (commandChecker(event_, arg, "Lookup")) {
                    if (getArg(arg, 1) != null) {
                        sendMessage(event_, "You forgot a param ya dingus");
                    } else try {
                        string[] listOfTitlestrings = {argJoiner(arg, 1)};
                        info.bliki.api.User user = new info.bliki.api.User("", "", "http://en.wikipedia.org/w/api.php");
                        user.login();
                        List<Page> pages = user.queryContent(listOfTitlestrings);
                        bool found = false;
                        while (pages.Count > 0) {
                            Page page = (Page) pages[0];
                            if (page.toString().contains("#REDIRECT")) {
                                Logger.Debug("Found redirect");
                                string link = page.toString();
                                link = link.substring(link.indexOf("[[") + 2, link.indexOf("]]"));
                                Logger.Debug("Going to " + link);
                                pages = user.queryContent(new string[]{link});
                                continue;
                            }
                            found = true;
                            WikiModel wikiModel = new WikiModel("${image}", "${title}");
                            string plainStr = page.toString();
                            LinkedList<string> related = null;
                            if (plainStr.contains(" may refer to:")) {
                                Logger.Debug("Found disambiguation page");
                                LinkedList<string> strings = new LinkedList<>(Arrays.asList(plainStr.split("[\n]")));
                                related = new LinkedList<>();
                                bool category = true;
                                for (int i = 0; strings.Count > i; i++) {
                                    Logger.Trace((string) strings[i]);
                                    if (LilGUtil.wildCardMatch((string) strings[i], "==*==")) {
                                        category = false;
                                    } else if (!category) {
                                        if (!((string) strings[i]).isEmpty()) {
                                            related.Add((string) strings[i]);
                                        } else if (((string) strings[i + 1]).isEmpty()) {
                                            category = true;
                                        }
                                    }
                                }
                            }
                            if (related != null) {
                                plainStr = page.getTitle() + " may refer to: ";
                                for (int i = 0; i < related.Count && i < 5; i++) {
                                    plainStr += ((string) related[i]).replace("* ", "").replace("*", "") + "; ";
                                }
                                int lastIndex = plainStr.lastIndexOf(",");
                                if (lastIndex != -1)
                                    plainStr = plainStr.substring(0, lastIndex);
                            } else {
                                plainStr = page.getCurrentContent();

                            }
                            plainStr = wikiModel.render(new PlainTextConverter(), plainStr);
                            if (related == null) {
                                Logger.Debug(plainStr);
                                int charIndex = stringUtils.ordinalIndexOf(plainStr, ".", 2);
                                if (charIndex == -1) {
                                    charIndex = plainStr.indexOf(".");
                                }
                                if (charIndex != -1) {
                                    plainStr = plainStr.substring(0, charIndex);
                                }
                            }
                            Pattern pattern = Pattern.compile("[^.]*");
                            Matcher matcher = pattern.matcher(plainStr);
                            if (matcher.find()) {
                                plainStr = plainStr.replaceAll("\\{\\{[^\\}]+\\}\\}", "");
                                if (plainStr.isEmpty()) {
                                    sendMessage(event_, "That page couldn't be found.");
                                } else {
                                    sendMessage(event_, plainStr + ".");
                                }
                            }
                            break;
                        }
                        if (!found) {
                            sendMessage(event_, "That page couldn't be found.");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        sendError(event_, new Exception("Error getting data, please try again"));
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                    addCooldown(event_.getUser());
                }

    // !chat - chat's with a internet conversation bot
                else if (commandChecker(event_, arg, "chat")) {
                    if (getArg(arg, 1).equalsIgnoreCase("clever")) {
                        if (!BOOLS[CLEVER_BOT_INT]) {
                            try {
                                chatterBotSession = BOT_FACTORY.create(ChatterBotType.CLEVERBOT).createSession();
                                BOOLS.set(CLEVER_BOT_INT);
                                //noinspection ConstantConditions
                                event_.getUser().send().notice("CleverBot started");
                            } catch (Exception e) {
                                sendMessage(event_, "Error: Could not create clever bot session. Error was: " + e);
                            }
                        }
                        try {
                            sendMessage(event_, " " + botTalk("clever", argJoiner(arg, 2)));
                        } catch (Exception e) {
                            sendMessage(event_, "Error: Problem with bot. Error was: " + e);
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("pandora")) {
                        if (!BOOLS[PANDORA_BOT_INT]) {
                            try {
                                pandoraBotSession = BOT_FACTORY.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
                                BOOLS.set(PANDORA_BOT_INT);
                                //noinspection ConstantConditions
                                event_.getUser().send().notice("PandoraBot started");
                            } catch (Exception e) {
                                sendMessage(event_, "Error: Could not create pandora bot session. Error was: " + e);
                            }
                        }
                        try {
                            sendMessage(event_, " " + botTalk("pandora", argJoiner(arg, 2)));
                        } catch (Exception e) {
                            sendMessage(event_, "Error: Problem with bot. Error was: " + e);
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("jabber")) {
                        if (!BOOLS[JABBER_BOT_INT]) {
                            try {
                                jabberBotSession = BOT_FACTORY.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
                                BOOLS.set(JABBER_BOT_INT);
                                //noinspection ConstantConditions
                                event_.getUser().send().notice("PandoraBot started");
                            } catch (Exception e) {
                                sendMessage(event_, "Error: Could not create pandora bot session. Error was: " + e);
                            }
                        }
                        try {
                            sendMessage(event_, " " + botTalk("clever", argJoiner(arg, 1)));
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    }
                    addCooldown(event_.getUser());
                }

    // !temp - Converts a unit of temperature to another
                else if (commandChecker(event_, arg, "temp")) {
                    int temp = int.decode(getArg(arg, 3));
                    double ans = 0;
                    string unit = "err";
                    if (getArg(arg, 1).equalsIgnoreCase("F")) {
                        if (getArg(arg, 2).equalsIgnoreCase("C")) {
                            ans = (temp - 32) * 5 / 9;
                            unit = "C";
                        } else if (getArg(arg, 2).equalsIgnoreCase("K")) {
                            ans = (temp - 32) * 5 / 9 + 273.15;
                            unit = "K";
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("C")) {
                        if (getArg(arg, 2).equalsIgnoreCase("F")) {
                            ans = (temp * 9 / 5) + 32;
                            unit = "F";
                        } else if (getArg(arg, 2).equalsIgnoreCase("K") && temp < 0) {
                            ans = temp + 273.15;
                            unit = "K";
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("K")) {
                        if (getArg(arg, 2).equalsIgnoreCase("F")) {
                            ans = (temp - 273.15) * 9 / 5 + 32;
                            unit = "F";
                        } else if (getArg(arg, 2).equalsIgnoreCase("C")) {
                            ans = temp - 273.15;
                            unit = "C";
                        }
                    }
                    if (unit.equalsIgnoreCase("err")) {
                        sendMessage(event_, "Incorrect arguments.");
                    } else {
                        sendMessage(event_, " " + ans + unit);
                    }
                    addCooldown(event_.getUser());

                }


    // !BlockConv - Converts blocks to bytes
                else if (commandChecker(event_, arg, "BlockConv")) {
                    int data = int.decode(getArg(arg, 3));
                    double ans = 0;
                    string unit = "err";
                    bool notify = true;
                    int BLOCKS = 128;
                    if (getArg(arg, 1).equalsIgnoreCase("blocks")) {
                        if (getArg(arg, 2).equalsIgnoreCase("kb")) {
                            ans = BLOCKS * data;
                            unit = "KB";
                            notify = false;
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("kb")) {
                        if (getArg(arg, 2).equalsIgnoreCase("blocks")) {
                            ans = data / BLOCKS;
                            unit = "Blocks";
                            notify = false;
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("mb")) {
                        if (getArg(arg, 2).equalsIgnoreCase("blocks")) {
                            int BLOCKS_MB = 8 * BLOCKS;
                            ans = data / BLOCKS_MB;
                            unit = "Blocks";
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("gb")) {
                        if (getArg(arg, 2).equalsIgnoreCase("blocks")) {
                            int BLOCKS_GB = 8192 * BLOCKS;
                            ans = data / BLOCKS_GB;
                            unit = "Blocks";
                        }
                    }
                    if (unit.equals("err")) {
                        sendMessage(event_, "Incorrect arguments.");
                    } else {
                        sendMessage(event_, " " + ans + unit);
                        if (notify)
                            sendMessage(event_, "NOTICE: this command currently doesn't work like it should. The only conversion that works is blocks to kb and kb to blocks");
                    }
                    addCooldown(event_.getUser());

                }


    // !FC - Friend code database
                else if (commandChecker(event_, arg, "FC")) {
                    try {
                        if (getArg(arg, 2) != null) {
                            if (getArg(arg, 1).equalsIgnoreCase("set")) {
                                if (FCList.containsKey(event_.getUser().getNick().toLowerCase())) {
                                    string fc = getArg(arg, 2).replaceAll("[^\\d]", "");
                                    if (fc.length() == 12) {
                                        FCList[event_.getUser().getNick().toLowerCase()] = fc;
                                        sendMessage(event_, "FC Edited");
                                    } else {
                                        sendMessage(event_, "Incorrect FC");
                                    }
                                } else {
                                    string fc = getArg(arg, 2).replaceAll("[^\\d]", "");
                                    if (fc.length() == 12) {
                                        FCList[event_.getUser().getNick().toLowerCase()] = getArg(arg, 2).replaceAll("[^\\d]", "");
                                        sendMessage(event_, "Added " + event_.getUser().getNick() + "'s FC to the DB as " + getArg(arg, 2).replaceAll("[^\\d]", ""));
                                    } else {
                                        sendMessage(event_, "Incorrect FC");
                                    }
                                }
                            }
                        } else {
                            if (getArg(arg, 1).equalsIgnoreCase("list")) {
                                sendMessage(event_, FCList.keySet().toString());

                            } else if (getArg(arg, 1).equalsIgnoreCase("del")) {
                                if (FCList.containsKey(event_.getUser().getNick().toLowerCase())) {
                                    FCList.Remove(event_.getUser().getNick().toLowerCase());
                                    sendMessage(event_, "Friend code removed");
                                } else {
                                    sendMessage(event_, "You haven't entered your Friend code yet");
                                }

                            } else if (FCList.containsKey(getArg(arg, 1).toLowerCase())) {
                                string fc = FCList[getArg(arg, 1).toLowerCase()];
                                string fcParts[] = new string[3];
                                fcParts[0] = fc.substring(0, 4);
                                fcParts[1] = fc.substring(4, 8);
                                fcParts[2] = fc.substring(8);
                                fc = fcParts[0] + "-" + fcParts[1] + "-" + fcParts[2];
                                sendMessage(event_, getArg(arg, 1) + ": " + fc, false);
                            } else {
                                sendMessage(event_, "That user hasn't entered their FC yet", false);
                            }
                        }
                        addCooldown(event_.getUser());
                    } catch (NullPointerException e) {
                        FCList = new Dictionary<>();
                        sendMessage(event_, "Try the command again");
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !sql - execute sql statements
                else if (commandChecker(event_, arg, "sql")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:mysql://10.0.0.63:3306/world?user=mysql&password=" + CryptoUtil.decrypt(FozConfig.PASSWORD));

                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(argJoiner(LilGUtil.splitMessage(message, 0, false), 1));

                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            LinkedList<string> results = new LinkedList<>();
                            bool getColumns = true;
                            while (getColumns || rs.next()) {
                                StringBuilder cols = new StringBuilder();
                                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                                    Object object;
                                    if (getColumns) {
                                        object = metaData.getColumnName(columnIndex);
                                    } else {
                                        object = rs.getObject(columnIndex);
                                    }
                                    if (columnIndex == columnCount) {
                                        cols.append(object == null ? "NULL" : object.toString());
                                    } else {
                                        cols.append(object == null ? "NULL" : object.toString()).append(" | ");
                                    }
                                }
                                results.Add(cols.toString());
                                getColumns = false;
                            }
                            Logger.Debug(results + " " + results.Count);
                            if (results.Count < 4) {
                                for (string result : results) {
                                    sendMessage(event_, result);
                                }
                            } else {
                                sendPage(event_, arg, results);
                            }
                            conn.close();
                        } catch (SQLException ex) {
                            // handle any errors
                            string exceptMsg = ex.getMessage();
                            sendMessage(event_, Colors.RED + "SQLException: " + exceptMsg.substring(0, exceptMsg.indexOf(": ") + 2) + "<SUPER SECRET PASSWORD>" + exceptMsg.substring(exceptMsg.indexOf(")"), exceptMsg.length()) + " SQLState: " + ex.getSQLState() + " VendorError: " + ex.getErrorCode(), false);
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !memes - Got all dem memes
                else if (commandChecker(event_, arg, "memes")) {
                    if (getArg(arg, 1) != null) {
                        try {
                            if (getArg(arg, 1).equalsIgnoreCase("set")) {
                                if (memes.containsKey(getArg(arg, 2).toLowerCase().replace("\u0001", ""))) {
                                    Meme meme = (Meme) memes[getArg(arg, 2).toLowerCase()];
                                    if (checkPerm(event_.getUser(), 9001) || meme.getCreator().equalsIgnoreCase(event_.getUser().getNick())) {
                                        if (getArg(arg, 3) == null) {
                                            memes.Remove(getArg(arg, 2).toLowerCase().replace("\u0001", ""));
                                            sendMessage(event_, "Meme " + getArg(arg, 2) + " Deleted!");
                                        } else {
                                            meme.setMeme(argJoiner(arg, 3));
                                            memes[getArg(arg, 2).toLowerCase().replace("\u0001", "")] = meme;
                                            sendMessage(event_, "Meme " + getArg(arg, 2) + " Edited!");
                                        }
                                    } else {
                                        sendMessage(event_, "Sorry, Only the creator of the meme can edit it");
                                    }
                                } else {
                                    memes[getArg(arg, 2).toLowerCase().replace("\u0001", "")] = new Meme(event_.getUser().getNick(), argJoiner(arg, 3).replace("\u0001", ""));
                                    sendMessage(event_, "Meme " + getArg(arg, 2) + " Created as " + argJoiner(arg, 3));
                                }
                            } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                                sendMessage(event_, memes.toString().replace("\u0001", ""));
                            } else {
                                if (memes.containsKey(getArg(arg, 1).toLowerCase())) {
                                    sendMessage(event_, getArg(arg, 1).replace("\u0001", "") + ": " + ((Meme) memes[getArg(arg, 1).toLowerCase()]).getMeme().replace("\u0001", ""), false);
                                } else {
                                    sendMessage(event_, "That Meme doesn't exist!");
                                }
                            }
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                        addCooldown(event_.getUser());
                    } else {
                        sendMessage(event_, "Missing arguments");
                    }

                }

    // !q - adds to q
                else if (commandChecker(event_, arg, "q")) {
                    try {
                        if (getArg(arg, 1).equalsIgnoreCase("del") || getArg(arg, 1).equalsIgnoreCase("pop") || getArg(arg, 1).equalsIgnoreCase("rem")) {
                            if (qList.Remove(event_.getUser().getNick())) {
                                sendMessage(event_, "Removed from Q");
                            } else {
                                sendMessage(event_, "You weren't in Q!");
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("add") || getArg(arg, 1).equalsIgnoreCase("push")) {
                            if (qList.Add(event_.getUser().getNick())) {
                                sendMessage(event_, "Added to Q");
                            } else {
                                sendMessage(event_, "You are already in Q!");
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                            sendMessage(event_, "Current people in Q: " + qList.toString().replace("[", "").replace("]", ""));
                        } else if (getArg(arg, 1).equalsIgnoreCase("clear")) {
                            qList.clear();
                            qTimer = new StopWatch();
                            sendMessage(event_, "Q has been qilled");
                        } else if (getArg(arg, 1).equalsIgnoreCase("start")) {
                            qTimer = new StopWatch();
                            int time = 10;
                            if (getArg(arg, 2) != null) {
                                time = int.decode(getArg(arg, 2));
                                time = time > 60 ? 30 : time;
                            }
                            sendMessage(event_, "READY?!?!1/1 " + (time != 10 ? "starting in " + time + " sec!!!! " : "") + qList.toString().replace("[", "").replace("]", ""), false);
                            LilGUtil.pause(time);
                            sendMessage(event_, "3", false);
                            LilGUtil.pause(1);
                            sendMessage(event_, "2", false);
                            LilGUtil.pause(1);
                            sendMessage(event_, "1", false);
                            LilGUtil.pause(1);
                            sendMessage(event_, "GO! " + qList.toString().replace("[", "").replace("]", ""), false);
                            qTimer = new StopWatch();
                            qTimer.start();
                        } else if (getArg(arg, 1).equalsIgnoreCase("time")) {
                            sendMessage(event_, "Current time: " + qTimer.toString());
                        }
                        addCooldown(event_.getUser());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !NoteJ - Leaves notes
                else if (commandChecker(event_, arg, "NoteJ")) {
                    try {
                        if (getArg(arg, 1).equalsIgnoreCase("del")) {
                            int i = 0;
                            int index = -1;
                            bool found = false;
                            while (i < noteList.Count && !found) {
                                if (((Note) noteList[i]).getId().toString().equals(getArg(arg, 2))) {
                                    found = true;
                                    index = i;
                                } else {
                                    i++;
                                }
                            }
                            if (found) {
                                if (event_.getUser().getNick().equalsIgnoreCase(noteList.get(index).getSender()) || checkPerm(event_.getUser(), 9001)) {
                                    noteList.Remove(index);
                                    sendMessage(event_, "Note " + getArg(arg, 2) + " Deleted");
                                } else {
                                    sendMessage(event_, "Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it");
                                }
                            } else {
                                sendMessage(event_, "That ID wasn't found.");
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                            int i = 0;
                            LinkedList<string> found = new LinkedList<>();
                            LinkedList<string> foundUUID = new LinkedList<>();
                            while (noteList.Count > i) {
                                if (noteList.get(i).getSender().equalsIgnoreCase(event_.getUser().getNick())) {
                                    found.Add(noteList.get(i).getMessageForList());
                                    foundUUID.Add(noteList.get(i).getUUIDForList());
                                }
                                i++;
                            }
                            sendMessage(event_, found.toString());
                            event_.getUser().send().notice(foundUUID.toString());
                        } else {
                            Note note = new Note(event_.getUser().getNick(), getArg(arg, 1), argJoiner(arg, 2), channel);
                            noteList.Add(note);
                            sendMessage(event_, "Left note \"" + argJoiner(arg, 2) + "\" for \"" + getArg(arg, 1) + "\".", false);
                            event_.getUser().send().notice("ID is \"" + noteList.get(noteList.indexOf(note)).getId().toString() + "\"");
                        }
                        addCooldown(event_.getUser());
                    } catch (stringIndexOutOfBoundsException e) {
                        sendMessage(event_, Colors.RED + "You need more parameters ya dingus");
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !Hello - Standard "Hello world" command
                else if (commandChecker(event_, arg, "hello")) {
                    sendMessage(event_, "Hello World!");
                    addCooldown(event_.getUser());

                }

    // !Bot - Explains that "yes this is a bot"
                else if (commandChecker(event_, arg, "bot")) {
                    sendMessage(event_, "Yes, this is " + currentUser.getNick() + "'s bot.");
                    addCooldown(event_.getUser());

                }

    // !getName - gets the name of the bot
                else if (commandChecker(event_, arg, "getName")) {
                    sendMessage(event_, bot.getUserBot().getRealName());
                    addCooldown(event_.getUser());

                }

    // !version - gets the version of the bot
                else if (commandChecker(event_, arg, "version")) {
                    string version = "PircBotX: " + PircBotX.VERSION + ". BotVersion: " + VERSION + ". Java version: " + global::java.lang.System.getProperty("java.version");
                    sendMessage(event_, "Version: " + version);
                    addCooldown(event_.getUser());

                }

    // !login - attempts to login to NickServ
                else if (commandChecker(event_, arg, "login")) {
                    bot.sendIRC().mode(bot.getNick(), "+B");
                    bot.sendIRC().identify(CryptoUtil.decrypt(FozConfig.PASSWORD));
                    bot.sendRaw().rawLineNow("cs op #Lil-G|bot " + bot.getNick());
                    bot.sendRaw().rawLineNow("cs op #Lil-G|bot Lil-G");
                    bot.sendRaw().rawLineNow("cs op #SSB " + bot.getNick());
                    bot.sendRaw().rawLineNow("cs op #SSB Lil-G");
                    bot.sendRaw().rawLineNow("ns recover FozruciX " + CryptoUtil.decrypt(FozConfig.PASSWORD));
                    addCooldown(event_.getUser());

                }

    // !getLogin - gets the login of the bot
                else if (commandChecker(event_, arg, "getLogin")) {
                    sendMessage(event_, bot.getUserBot().getLogin());
                    addCooldown(event_.getUser());

                }

    // !getID - gets the ID of the user
                else if (commandChecker(event_, arg, "getID")) {
                    sendMessage(event_, "You are :" + event_.getUser().getUserId());
                    addCooldown(event_.getUser());

                }

    // !RandomInt - Gives the user a random Number
                else if (commandChecker(event_, arg, "RandomInt")) {
                    int num1, num2;
                    if (getArg(arg, 1) != null && getArg(arg, 2) != null) {
                        num1 = int.decode(getArg(arg, 1));
                        num2 = int.decode(getArg(arg, 2));
                        sendMessage(event_, "" + LilGUtil.randInt(num1, num2));
                    }
                }

    // !RandomDec - Gives the user a random Number
                else if (commandChecker(event_, arg, "RandomDec")) {
                    double num1, num2;
                    if (getArg(arg, 1) != null && getArg(arg, 2) != null) {
                        num1 = Double.parseDouble(getArg(arg, 1));
                        num2 = Double.parseDouble(getArg(arg, 2));
                        sendMessage(event_, "" + LilGUtil.randDec(num1, num2));
                    }
                }

    // !getState - Displays what version the bot is on
                else if (commandChecker(event_, arg, "getState")) {
                    sendMessage(event_, "State is: " + bot.getState());
                    addCooldown(event_.getUser());

                }

    // !prefix - Changes the command prefix when it isn't the standard "!"
                else if (arg[0].equalsIgnoreCase("!prefix") && !prefix.equals("!")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        if (getArg(arg, 1) != null) {
                            prefix = argJoiner(arg, 1);
                            sendMessage(event_, "Command variable is now \"" + prefix + "\"");
                        } else {
                            sendMessage(event_, "Command variable is \"" + prefix + "\"");
                        }
                    } else {
                        permError(event_.getUser());
                    }
                }

    // !prefix - Changes the command prefix
                else if (commandChecker(event_, arg, "prefix")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        prefix = getArg(arg, 1);
                        if (prefix.length() > 1 && !endsWithAny(prefix, ".", "!", "`", "~", "@", "-", "/", "*", "&", "^", "%", "$", "#", "+", "_", "?", "\\", ";", ":", "|")) {
                            arrayOffset = 1;
                        } else {
                            arrayOffset = 0;
                        }
                        sendMessage(event_, "Command variable is now \"" + prefix + "\"");
                    } else {
                        permError(event_.getUser());
                    }
                }

    // !highlightAll - Highlights everyone
                else if (commandChecker(event_, arg, "highlightAll")) {
                    if (checkPerm(event_.getUser(), 6)) {
                        sendMessage(event_, argJoiner(arg, 1), false);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !upload - Uploads a file to discord
                else if (commandChecker(event_, arg, "upload")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        if (getArg(arg, 2) != null) {
                            sendFile(event_, new File(getArg(arg, 1)), argJoiner(arg, 2));
                        } else if (getArg(arg, 1) != null) {
                            sendFile(event_, new File(getArg(arg, 1)));
                        } else {
                            sendMessage(event_, "Fail");
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !SayThis - Tells the bot to say something
                else if (commandChecker(event_, arg, "SayThis")) {
                    if (checkPerm(event_.getUser(), 5)) {
                        sendMessage(event_, argJoiner(arg, 1), false);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !makeDebug - reCreates the debug Window
                else if (commandChecker(event_, arg, "makeDebug")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        makeDebug();
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !makeDiscord - reCreates the discord connection
                else if (commandChecker(event_, arg, "makeDiscord")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        try {
                            makeDiscord();
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !LoopSay - Tells the bot to say something and loop it
                else if (commandChecker(event_, arg, "LoopSay")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        int i = int.decode(getArg(arg, 1));
                        int loopCount = 0;
                        try {
                            while (i > loopCount) {
                                sendMessage(event_, argJoiner(arg, 2), false);
                                loopCount++;
                            }
                        } catch (Exception e) {
                            sendError(event_, e);
                        }

                    } else {
                        permErrorchn(event_);
                    }
                }

    // !ToSciNo - converts a number to scientific notation
                else if (commandChecker(event_, arg, "ToSciNo")) {
                    NumberFormat formatter = new DecimalFormat("0.######E0");

                    long num = long.parselong(getArg(arg, 1));
                    try {
                        sendMessage(event_, formatter.format(num));
                    } catch (Exception e) {
                        sendError(event_, e);
                        //log(e.toString());
                    }
                    addCooldown(event_.getUser());

                }

    // !S1TCG - Create Title card info
                else if (commandChecker(event_, arg, "S1TCG")) {
                    if (getArg(arg, 1) != null) {
                        try {
                            List<string> title = S1TCG.process(formatstringArgs(arg));
                            if (title.Count < 7) {
                                for (string aTitle : title) {
                                    sendNotice(event_, event_.getUser().getNick(), aTitle);
                                }
                            } else {
                                sendPage(event_, arg, new LinkedList<>(title));
                            }
                            addCooldown(event_.getUser());
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        sendNotice(event_, event_.getUser().getNick(), "Incorrect arguments");
                        getHelp(event_, "s1tcg");
                    }

                }

    // !68kTest - Tests 68k code
                else if (commandChecker(event_, arg, "68kTest")) {
                    if (getArg(arg, 1) == null) {
                        sendMessage(event_, "Missing args");
                        return;
                    }
                    string[] args = formatstringArgs(LilGUtil.splitMessage(message, 0, false));
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("68kTest")
                            .description("Simulates a M68k environment")
                            .defaultHelp(true);
                    parser.addArgument("expression").nargs("*")
                            .help("Code to execute");
                    parser.addArgument("-a", "--address").type(long.class)
                            .help("Sets what address to get");
                    parser.addArgument("-s", "--size").type(M68kSim.Size.class)
                            .help("Sets what size of value to get");
                    parser.addArgument("-c", "--count").type(typeof(int)).setDefault(1)
                            .help("sets the amount of bytes to return");
                    parser.addArgument("--clear").type(typeof(bool)).action(Arguments.storeTrue())
                            .help("clears the memory");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        Logger.Debug(ns.toString());
                        if (checkPerm(event_.getUser(), 5) && getArg(arg, 1).equalsIgnoreCase("debug")) {
                            if (getArg(arg, 2) == null) {
                                sendMessage(event_, m68k.toString());
                                return;
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("dump")) {
                                sendMessage(event_, "M68k memory dumped");
                            } else if (getArg(arg, 2).equalsIgnoreCase("clear")) {
                                long time = (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
                                m68k.clearMem();
                                Logger.Info("Took " + ((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) - time) + " ms to clear M68k memory");
                                sendMessage(event_, "M68K Memory cleared");
                            } else if (getArg(arg, 2).equalsIgnoreCase("Ramstart")) {
                                sendMessage(event_, "M68k ram starts at 0x" + string.format("%02X ", m68k.getRamStart()));
                            } else if (getArg(arg, 2).equalsIgnoreCase("start")) {
                                m68k.start();
                                sendMessage(event_, "M68K Initialized");
                            } else if (getArg(arg, 2).equalsIgnoreCase("unload")) {
                                NativeLibrary lib = NativeLibrary.getInstance(M68kPath);
                                m68k = null;
                                lib.dispose();
                                lib = null;
                                LilGUtil.gc();
                                sendMessage(event_, "DLL unloaded");
                                return;
                            } else if (getArg(arg, 2).equalsIgnoreCase("load")) {
                                m68k = (M68kSim) Native.loadLibrary(M68kPath, M68kSim.class);
                                sendMessage(event_, "DLL loaded as " + m68k);
                            } else if (getArg(arg, 2).toLowerCase().startsWith("move")) {
                                M68kSim.Size[] sizes = M68kSim.Size.values();
                                for (M68kSim.Size size : sizes) {
                                    if (size.getSymbol() == getArg(arg, 2).toLowerCase().charAt(getArg(arg, 2).length() - 1)) {
                                        m68k.move(size.ordinal(), int.decode(getArg(arg, 3)).shortValue(), int.decode(getArg(arg, 4)).shortValue());
                                        break;
                                    }
                                }
                            } else if (getArg(arg, 2).toLowerCase().startsWith("moveq")) {
                                m68k.moveq(Byte.decode(getArg(arg, 3)), int.decode(getArg(arg, 4)).shortValue());
                            } else if (getArg(arg, 2).toLowerCase().startsWith("lea")) {
                                m68k.lea(int.decode(getArg(arg, 3)).shortValue(), M68kSim.AddressRegister.valueOf(getArg(arg, 4).toLowerCase()).ordinal());
                            } else if (getArg(arg, 2).toLowerCase().startsWith("adda")) {
                                M68kSim.Size[] sizes = M68kSim.Size.values();
                                for (M68kSim.Size size : sizes) {
                                    if (size.getSymbol() == getArg(arg, 2).toLowerCase().charAt(getArg(arg, 2).length() - 1)) {
                                        m68k.adda(size, long.decode(getArg(arg, 3)).shortValue(), int.decode(getArg(arg, 4)));
                                        break;
                                    }
                                }
                            } else if (getArg(arg, 2).toLowerCase().startsWith("get")) {
                                M68kSim.Size[] sizes = M68kSim.Size.values();
                                for (M68kSim.Size size : sizes) {
                                    if (size.getSymbol() == getArg(arg, 2).toLowerCase().charAt(getArg(arg, 2).length() - 1)) {
                                        switch (size) {
                                            case Byte:
                                                sendMessage(event_, "0x" + string.format("%02x", m68k.getByte(int.decode(getArg(arg, 3)).shortValue())).toUpperCase());
                                                break;
                                            case Word:
                                                sendMessage(event_, "0x" + string.format("%02x", m68k.getWord(int.decode(getArg(arg, 3)).shortValue())).toUpperCase());
                                                break;
                                            case longWord:
                                                sendMessage(event_, "0x" + string.format("%02x", m68k.getlongWord(int.decode(getArg(arg, 3)).shortValue()).intValue()).toUpperCase());
                                        }
                                        break;
                                    }
                                }
                            }
                            m68k.memDump();
                        }
                        if (m68k == null) {
                            return;
                        }
                    } catch (ArgumentParserException e) {
                        sendCommandHelp(event_, parser);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                }

    // !acc/68kcyc/asmcyclecounter - counts asm cycles
                else if (commandChecker(event_, arg, "acc") || commandChecker(event_, arg, "68kcyc") || commandChecker(event_, arg, "asmcyclecounter")) {
                    try {
                        string asm = argJoiner(arg, 1).replace("||", "\r\n\t").replace("//", "\r\n\t");
                        Process process;
                        if (Platform.isWindows()) {
                            process = new ProcessBuilder("asmcyclecount/asmCycleCount.exe", "t", "t", "\t" + asm).start();
                        } else {
                            process = new ProcessBuilder("mono", "asmcyclecount/asmCycleCount.exe", "t", "t", "\t" + asm).start();
                        }
                        process.waitFor();
                        try (Scanner s = new Scanner(process.getInputStream())) {

                            LinkedList<string> output = new LinkedList<>();
                            while (s.hasNext()) {
                                output.Add(s.nextLine());
                            }
                            output = new LinkedList<>(output.subList(0, output.Count / 2));
                            Logger.Debug(output.toString());
                            if (output.Count > 3) {
                                sendPage(event_, arg, output);
                            } else {
                                for (string anOutput : output) {
                                    sendMessage(event_, anOutput.replace('\t', ' ').replace(";", "  ;"));
                                }
                            }
                            addCooldown(event_.getUser());
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } catch (Exception e2) {

                    }

                }

    // !disasm - disassembles machine code for the specified CPU
                else if (commandChecker(event_, arg, "disasm")) {
                    string byteStr = argJoiner(arg, 2).replace(" ", "");
                    try {
                        string processor = getArg(arg, 1).toLowerCase();
                        ProcessBuilder pb = new ProcessBuilder("rasm2", "-a", processor, "-d", byteStr);
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                        BufferedReader disasm = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        //noinspection StatementWithEmptyBody
                        process.waitFor();
                        string disasmTemp;
                        bool stdoutWasEmpty = true;
                        LinkedList<string> messagesToSend = new LinkedList<>();
                        while ((disasmTemp = disasm.readLine()) != null) {
                            Logger.Debug("disasm: %s", disasmTemp);
                            messagesToSend.Add(disasmTemp);
                            if (stdoutWasEmpty) {
                                stdoutWasEmpty = false;
                            }

                        }
                        disasm.close();
                        if (stdoutWasEmpty) {
                            sendMessage(event_, "Processor is either not supported or some other error has occurred: No Data in stdout");
                        } else {
                            if (messagesToSend.Count > 3) {
                                sendPage(event_, arg, messagesToSend);
                            } else {
                                for (string aMessagesToSend : messagesToSend) {
                                    sendMessage(event_, aMessagesToSend);
                                }
                            }
                        }
                        addCooldown(event_.getUser());
                    } catch (IllegalArgumentException e) {
                        sendMessage(event_, "Arguments have to be a Hexadecimal number: " + e.getCause());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !Trans - Translate from 1 language to another
                else if (commandChecker(event_, arg, "trans")) {
                    string text;
                    string[] args = formatstringArgs(arg);
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("Trans")
                            .description("Translates from one language to another")
                            .defaultHelp(true);
                    parser.addArgument("text").nargs("*")
                            .help("Text to translate");
                    parser.addArgument("-t", "--to").type(typeof(string)).setDefault("English")
                            .help("Sets language to translate to");
                    parser.addArgument("-f", "--from").type(typeof(string)).setDefault("detect")
                            .help("Sets language to translate from");
                    parser.addArgument("-d", "--detect").type(typeof(bool)).action(Arguments.storeTrue())
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        Logger.Debug(ns.toString());
                        if (ns.getBoolean("detect")) {
                            sendMessage(event_, fullNameTostring(Detect.execute(ns.getString("text"))));
                        } else {
                            //noinspection SuspiciousToArrayCall
                            string textToTrans = argJoiner(ns.getList("text").toArray(new string[]{}), 0);
                            Language to = ns.getString("to").toUpperCase();
                            Language from;
                            if (ns.getString("from").equals("detect")) {
                                from = Detect.execute(textToTrans);
                            } else {
                                from = ns.getString("from").toUpperCase();
                            }
                            text = Translate.execute(textToTrans, from, to);
                            Logger.Debug("Translating: " + text);
                            sendMessage(event_, text);
                        }
                        addCooldown(event_.getUser());
                    } catch (IllegalArgumentException e) {
                        sendError(event_, new Exception("That Language doesn't exist!"));
                    } catch (ArgumentParserException e) {
                        sendCommandHelp(event_, parser);
                    } catch (IOException e) {
                        sendError(event_, new Exception("IOException! try again"));
                        Logger.Error("Trans error", e);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                }

    // !BadTrans - Translate from english to.... english... badly
                else if (commandChecker(event_, arg, "BadTrans")) {
                    try {
                        if (getArg(arg, 1) != null) {
                            string text = argJoiner(arg, 1);
                            global::java.lang.System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.ENGLISH, Language.JAPANESE);
                            global::java.lang.System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.JAPANESE, Language.VIETNAMESE);
                            global::java.lang.System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.VIETNAMESE, Language.CHINESE);
                            global::java.lang.System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.CHINESE, Language.ENGLISH);
                            Logger.Debug("Translating: " + text);
                            sendMessage(event_, text);
                        } else {
                            sendMessage(event_, ">_>");
                        }
                        addCooldown(event_.getUser());
                    } catch (IllegalArgumentException e) {
                        sendError(event_, new Exception("That class doesn't exist!"));
                    } catch (IOException e) {
                        sendError(event_, new Exception("IOException! try again"));
                        Logger.Error("Trans error", e);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }
                }

    // !DebugVar - changes a variable to the value
                else if (commandChecker(event_, arg, "DebugVar")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        switch (getArg(arg, 1).toLowerCase()) { //Make sure strings are lowercase
                            case "i":
                                int i = int.decode(getArg(arg, 2));
                                sendMessage(event_, "DEBUG: Var \"i\" is now \"" + i + "\"");
                                break;
                            case "jokenum":
                                jokeCommandDebugVar = int.decode(getArg(arg, 2));
                                sendMessage(event_, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"");
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !cmd - Tells the bot to run a OS command
                else if (commandChecker(event_, arg, "cmd")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        try {
                            if (getArg(arg, 1).equalsIgnoreCase("stop")) {
                                sendMessage(event_, "Stopping");
                                singleCMD.interrupt();
                            } else {
                                try {
                                    singleCMD = new CMD(event_, arg);
                                    singleCMD.start();
                                } catch (Exception e) {
                                    sendError(event_, e);
                                }
                            }
                        } catch (Exception e) {
                            sendError(event_, e);
                        }
                    } else {
                        permError(event_.getUser());
                    }
                }

    // > - runs COMMANDS without closing at the end
                else if (arg[0].startsWith(consolePrefix)) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "start")) {
                            terminal.interrupt();
                            terminal = new CommandLine(event_, trimFrontOfArray(arg, 1));
                            terminal.start();
                            if (!terminal.isAlive()) {
                                sendMessage(event_, "Command line started", false);
                            }
                        } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "close")) {
                            terminal.doCommand(event_, "exit");
                        } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "stop")) {
                            terminal.interrupt();
                        } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "prefix")) {
                            consolePrefix = arg[1];
                            sendMessage(event_, "Console Prefix is now " + consolePrefix);
                        } else {
                            string command = message.substring(1);
                            terminal.doCommand(event_, command);
                            Logger.Debug("Running " + command);
                        }
                    }
                } else if (arg[0].equalsIgnoreCase("\\\\prefix")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        consolePrefix = arg[1];
                        sendMessage(event_, "Console Prefix is now " + consolePrefix);
                    }
                }

    // !SayRaw - Tells the bot to send a raw line
                else if (commandChecker(event_, arg, "SayRaw")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        bot.sendRaw().rawLineNow(argJoiner(arg, 1));
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !SayNotice - Tells the bot to send a notice
                else if (commandChecker(event_, arg, "SayNotice")) {
                    if (checkPerm(event_.getUser(), 6)) {
                        bot.sendIRC().notice(getArg(arg, 1), argJoiner(arg, 2));
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !SayCTCPCommand - Tells the bot to send a CTCP Command
                else if (commandChecker(event_, arg, "SayCTCPCommand")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        bot.sendIRC().ctcpCommand(getArg(arg, 1), argJoiner(arg, 2));
                    } else {
                        permErrorchn(event_);
                    }
                }

    //// !SayMethod - Tells the bot to run a method
    //		if (arguments[0].equalsIgnoreCase(prefix + "sayMethod")){
    //			if(checkPerm(event_.getUser())){
    //				sendRawLineViaQueue(arguments[1]);
    //			}
    //			else {
    //				permErrorchn(event_, "can use this command");
    //			}
    //		}

    // !leave - Tells the bot to leave the current channel
                else if (commandChecker(event_, arg, "leave")) {
                    if (checkPerm(event_.getUser(), 5)) {
                        if (!commandChecker(event_, arg, "leave")) {
                            event_.getChannel().send().part(argJoiner(arg, 2));
                        } else {
                            event_.getChannel().send().part("Ugh... Why do i always get the freaks...");
                        }
                    } else if (network != Network.twitch) {
                        permErrorchn(event_);
                    }
                }

    // !ReVoice - gives everyone voice if they didn't get it
                else if (commandChecker(event_, arg, "ReVoice")) {
                    for (User user1 : event_.getChannel().getUsers()) {
                        user1.send().mode("+v");
                    }
                }

    // !kill - Tells the bot to disconnect from server and exit
                else if (commandChecker(event_, arg, "kill")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        //noinspection ConstantConditions
                        saveData();
                        event_.getUser().send().notice("Disconnecting from server and exiting");
                        try {
                            Thread exit = new Thread(() -> {
                                if (getArg(arg, 1) != null) {
                                    manager.stop(argJoiner(arg, 1));
                                } else {
                                    manager.stop("I'm only a year old and have already wasted my entire life.");
                                }
                                try {
                                    LilGUtil.pause(1);
                                    global::java.lang.System.exit(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, "Exit-thread");
                            exit.start();
                            //noinspection StatementWithEmptyBody
                            LilGUtil.pause(5);
                        } catch (Exception ignored) {
                        }
                        global::java.lang.System.exit(0);
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !quitServ - Tells the bot to disconnect from server
                else if (commandChecker(event_, arg, "quitServ")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        //noinspection ConstantConditions
                        saveData();
                        event_.getUser().send().notice("Disconnecting from server");
                        if (getArg(arg, 1) != null) {
                            bot.sendIRC().quitServer(argJoiner(arg, 1));
                        } else {
                            bot.sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                        }
                        bot.stopBotReconnect();
                    } else {
                        permErrorchn(event_);
                    }
                }


    // !respawn - Tells the bot to restart and reconnect
                else if (commandChecker(event_, arg, "respawn")) {
                    if (checkPerm(event_.getUser(), 5)) {
                        saveData();
                        bot.sendIRC().quitServer("Died! Respawning in about 5 seconds");
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !recycle - Tells the bot to part and rejoin the channel
                else if (commandChecker(event_, arg, "recycle")) {
                    if (checkPerm(event_.getUser(), 2)) {
                        saveData();
                        event_.getChannel().send().cycle();
                        addCooldown(event_.getUser());
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !getUserLevels - gets the user levels of the user
                else if (commandChecker(event_, arg, "getUserLevels")) {
                    if (channel == null) {
                        return;
                    }
                    try {
                        List<UserLevel> userLevels = Lists.newList(event_.getUser().getUserLevels(event_.getChannel()).iterator());
                        sendMessage(event_, userLevels.toString());
                        addCooldown(event_.getUser());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !getCpu - Gets info about CPU
                else if (commandChecker(event_, arg, "getCpu")) {
                    try {
                        string processorTime = string.format("%03f", LilGUtil.getProcessCpuLoad());
                        sendMessage(event_, "Processor time: " + processorTime);
                        addCooldown(event_.getUser());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !getBat - Gets info about battery
                else if (commandChecker(event_, arg, "getBat")) {
                    try {
                        if (Platform.isWindows()) {
                            string statuses[] = {"discharging",
                                    "The system has access to AC so no battery is being discharged. However, the battery is not necessarily charging.",
                                    "fully charged",
                                    "low",
                                    "critical",
                                    "charging",
                                    "charging and high",
                                    "charging and low",
                                    "charging and critical",
                                    "UNDEFINED",
                                    "partially charged"};
                            int batteryStatus = int.decode(getWMIValue("Select BatteryStatus from Win32_Battery", "BatteryStatus"));
                            string batteryPercentRemaining = getWMIValue("Select EstimatedChargeRemaining from Win32_Battery", "EstimatedChargeRemaining");
                            sendMessage(event_, "Remaining battery: " + batteryPercentRemaining + "% Battery status: " + statuses[batteryStatus]);
                        } else if (LilGUtil.IsLinux) {

                        }
                        addCooldown(event_.getUser());
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !getMem - Gets various info about memory
                else if (commandChecker(event_, arg, "getMem")) {
                    Runtime runtime = Runtime.getRuntime();
                    string send = "Current memory usage: " + LilGUtil.formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + LilGUtil.formatFileSize(runtime.totalMemory()) + ". Total memory that can be used: " + LilGUtil.formatFileSize(runtime.maxMemory()) + ".  Active Threads: " + Thread.activeCount() + "/" + ManagementFactory.getThreadMXBean().getThreadCount() + ".  Available Processors: " + runtime.availableProcessors();
                    sendMessage(event_, send, false);
                    addCooldown(event_.getUser());

                }

    // !formatBytes -
                else if (commandChecker(event_, arg, "formatBytes")) {
                    sendMessage(event_, LilGUtil.formatFileSize(long.decode(getArg(arg, 1))));
                    addCooldown(event_.getUser());
                }

    // !getDiscordStatus - does what it says
                else if (commandChecker(event_, arg, "getDiscordStatus")) {
                    try {
                        sendMessage(event_, DiscordAdapter.getJda().getStatus().toString(), false);
                    } catch (Exception e) {
                        sendError(event_, e);
                    }

                }

    // !ChangeNick - Changes the nick of the bot
                else if (commandChecker(event_, arg, "changeNick")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        if (event_ is DiscordMessageEvent) {
                            Guild guild = ((DiscordMessageEvent) event_).getDiscordEvent().getGuild();
                            guild.getController().setNickname(guild.getMember(DiscordAdapter.getJda().getSelfUser()), getArg(arg, 1)).queue();
                        } else {
                            bot.sendIRC().changeNick(getArg(arg, 1));
                            debug.setNick(getArg(arg, 1));
                        }
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !SayAction - Makes the bot do a action
                else if (commandChecker(event_, arg, "SayAction")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        event_.getChannel().send().action(argJoiner(arg, 1));
                    } else {
                        permErrorchn(event_);
                    }
                }

    // !jToggle - toggle joke COMMANDS
                else if (commandChecker(event_, arg, "jToggle")) {
                    if (getArg(arg, 1).equalsIgnoreCase("toggle")) {
                        if (checkPerm(event_.getUser(), 2)) {
                            BOOLS.flip(JOKE_COMMANDS);
                            if (BOOLS.get(JOKE_COMMANDS)) {
                                sendMessage(event_, "Joke COMMANDS are now enabled");
                            } else {
                                sendMessage(event_, "Joke COMMANDS are now disabled");
                            }
                        } else {
                            permErrorchn(event_);
                        }
                    } else {
                        if (BOOLS.get(JOKE_COMMANDS)) {
                            sendMessage(event_, "Joke COMMANDS are currently enabled");
                        } else {
                            sendMessage(event_, "Joke COMMANDS are currently disabled");
                        }

                    }
                }

    // !sudo/make me a sandwich - You should already know this joke
                else if (commandChecker(event_, arg, "make me a sandwich")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        sendMessage(event_, "No, make one yourself", false);
                        addCooldown(event_.getUser());
                    } else {
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");
                    }

                } else if (commandChecker(event_, arg, "sudo make me a sandwich")) {
                    if (checkPerm(event_.getUser(), 9001)) {
                        sendMessage(event_, "Ok", false);
                        addCooldown(event_.getUser());
                    } else {
                        sendMessage(event_, "This command requires root permissions");
                    }
                }

    // !Splatoon - Joke command - ask the splatoon question
                else if (commandChecker(event_, arg, "Splatoon")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        sendMessage(event_, " YOU'RE A KID YOU'RE A SQUID");
                        addCooldown(event_.getUser());
                    } else {
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");
                    }

                }

    // !attempt - Joke command - NOT ATTEMPTED
                else if (commandChecker(event_, arg, "attempt")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        sendMessage(event_, " NOT ATTEMPTED");
                        addCooldown(event_.getUser());
                    } else {
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");
                    }

                }

    // !potato - Joke command - say "i am potato" in Japanese
                else if (commandChecker(event_, arg, "potato")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        byte[] bytes = "わたしわポタトデス".getBytes(Charset.forName("UTF-8"));
                        string v = new string(bytes, Charset.forName("UTF-8"));
                        sendMessage(event_, v);
                    } else
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");

                }

    // !WhatIs? - Joke command -
                else if (commandChecker(event_, arg, "WhatIs?")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        int num = LilGUtil.randInt(0, DICTIONARY.length - 1);
                        string comeback = string.format(DICTIONARY[num], argJoiner(arg, 1));
                        sendMessage(event_, comeback);
                        addCooldown(event_.getUser());
                    } else
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");

                }

    // !rip - Joke command - never forgetti the spaghetti
                else if (commandChecker(event_, arg, "rip")) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event_.getUser(), 1)) {
                        if (getArg(arg, 1).equalsIgnoreCase(currentUser.getNick())) {
                            sendMessage(event_, currentUser.getNick() + " Will live forever!", false);
                        } else if (getArg(arg, 1).equalsIgnoreCase(bot.getNick())) {
                            sendMessage(event_, ">_>", false);
                        } else {
                            sendMessage(event_, "Rest in spaghetti, never forgetti. May the pasta be with " + argJoiner(arg, 1), false);
                        }
                    } else
                        sendMessage(event_, " Sorry, Joke COMMANDS are disabled");

                }

    // s/*/*[/g] - sed
                else if (arg[0].toLowerCase().startsWith("s/")) {
                    if (channel == null) {
                        sendMessage(event_, "This is for channels");
                        return;
                    }
                    Dictionary<string, List<string>> map = allowedCommands[getSeverName(event_, true)];
                    List<string> commands;
                    if (map != null) {
                        commands = map[channel];
                    } else {
                        commands = null;
                    }
                    if (commands != null && commands.Contains("sed")) {
                        sendNotice(event_, event_.getUser().getNick(), "Sorry, you can't use that command here");
                    } else {
                        string[] msg = message.split("/");
                        if (msg.Length > 2) {
                            if (!msg[1].isEmpty() || !msg[1].Equals(".")) {
                                string find = msg[1];
                                string replace = msg[2];
                                bool replaceAll = msg.Length > 3 && msg[3].toLowerCase().startsWith("g");
                                for (int i = lastEvents.Count - 1; i >= 0; i--) {
                                    MessageEvent last = lastEvents[i];
                                    if (last.equals(event_) || LilGUtil.wildCardMatch(last.getMessage(), "s/*/*")) continue;
                                    if (last.getChannel().equals(event_.getChannel())) {
                                        string lastMessage = last.getMessage();
                                        if (message.contains(find)) {
                                            if (replaceAll) {
                                                lastMessage = lastMessage.replace(find, replace);
                                            } else {
                                                lastMessage = lastMessage.replaceFirst(find, replace);
                                            }
                                            sendMessage(event_, "What " + last.getUser().getNick() + " meant to say was: " + lastMessage, false, false);
                                            addCooldown(event_.getUser(), 15);
                                            return;
                                        }
                                    }
                                }
                            } else {
                                sendMessage(event_, "Sorry, we ain't having none of that spam stuff");
                            }
                        }
                    }
                } else if (message.startsWith(bot.getNick()) ||
                        event_ is DiscordMessageEvent &&
                                ((DiscordMessageEvent) event_).getDiscordEvent().getMessage()
                                        .isMentioned(DiscordAdapter.getJda().getSelfUser()
                                        )
                        ) {
                    try {
                        sendMessage(event_, botTalk("clever", message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        private bool isBot(MessageEvent event_) {
            if (network == Network.discord && ((DiscordMessageEvent) event_).getDiscordEvent().getAuthor().isBot()) {
                return true;
            } else if (equalsAnyIgnoreCase(event_.getUser().getNick(),
                    "aqua-sama", "regume-chan", "Sylphy", "Dick-Cord", "Rakka" // list of known bots
            )) {
                return true;
            }
            return false;
        }
    
        private string doChatFunctions(string message) {
            if (!LilGUtil.wildCardMatch(message, "[$*(*)]")) {
                return message;
            }
            string[] chatFunctions = LilGUtil.splitMessage(message, 0, false);
            StringBuilder returnStr = new StringBuilder();
            for (string possibleFunction : chatFunctions) {
                if (checkChatFunction(possibleFunction, "char")) {
                    string sub = getChatArgs(possibleFunction)[0];
                    int charVal = int.decode(sub);
                    char character = (char) charVal;
                    possibleFunction = character + "";
                } else if (checkChatFunction(possibleFunction, "size")) {
                    string sub = getChatArgs(possibleFunction)[0];
                    long longVal = long.parselong(sub);
                    possibleFunction = LilGUtil.formatFileSize(longVal);
                }
                returnStr.append(possibleFunction).append(" ");
            }
            return returnStr.substring(0, returnStr.length() - 1);
        }
    
        
        public void onPart(PartEvent part) {
            log(part);
        }
    
        
        public void onPrivateMessage( PrivateMessageEvent PM) {
            string[] arg = LilGUtil.splitMessage(PM.getMessage());
    
    // !rps - Rock! Paper! ehh you know the rest
            if (commandChecker(PM, arg, "rps")) {
                //noinspection ConstantConditions
                string nick = PM.getUser().getNick();
                if (checkPerm(PM.getUser(), 9001)) {
                    bool found = false;
                    bool isFirstPlayer = true;
                    RPSGame game = null;
                    int i = 0;
                    for (; i > RPS_GAMES.Count; i++) {
                        if (RPS_GAMES.get(i).isInGame(nick)) {
                            found = true;
                            game = RPS_GAMES.get(i);
                            isFirstPlayer = game.isFirstPlayer(nick);
                            break;
                        }
                    }
                    if (found) {
                        if (getArg(arg, 1) != null) {
                            switch (getArg(arg, 1)) {
                                case "r":
                                    if (isFirstPlayer) {
                                        game.setP1Choice(1);
                                    } else {
                                        game.setP2Choice(1);
                                    }
                            }
                        }
                    } else if (getArg(arg, 1) != null && !(getArg(arg, 1).equalsIgnoreCase("r") || getArg(arg, 1).equalsIgnoreCase("p") || getArg(arg, 1).equalsIgnoreCase("s"))) {
                        RPS_GAMES.Add(new RPSGame(PM.getUser().getNick(), getArg(arg, 1)));
                        PM.getUser().send().notice("Created a game with " + getArg(arg, 1));
                    } else {
                        PM.getUser().send().notice("You aren't in a game!");
                    }
                }
            }
    // !rejoin - Rejoins all channels
            else if (PM.getMessage().equalsIgnoreCase(prefix + "rejoin")) {
                ImmutableList<string> autoChannels = bot.getConfiguration().getAutoJoinChannels().keySet().asList();
                for (int i = 0; i < autoChannels.Count; i++) {
                    bot.send().joinChannel(autoChannels.get(i));
                }
            }
    
    // !login - Sets the authed named to the new name ...if the password is right
            else if (commandChecker(PM, arg, "login")) {
                if (CryptoUtil.encrypt(getArg(arg, 1)).equals(FozConfig.PASSWORD)) {
                    currentUser = PM.getUser();
                    if (network == Network.discord) {
                        currentUser = new DiscordUser(PM.getUserHostmask(), ((DiscordPrivateMessageEvent) PM).getDiscordEvent().getAuthor(), null);
                    }
                    sendNotice(PM, "Welcome back Lil-G");
                } else {
                    sendNotice(PM, "password is incorrect.");
                }
            }
            //Only allow me (Lil-G) to use PM COMMANDS except for the login command
            else if (checkPerm(PM.getUser(), 6)) {
    
    
    // !SendTo - Tells the bot to say something on a channel
                if (commandChecker(PM, arg, "SendTo")) {
                    sendPrivateMessage(PM, getArg(arg, 1), getScramble(argJoiner(arg, 2)));
                }
    
    // !sendAction - Tells the bot to make a action on a channel
                else if (commandChecker(PM, arg, "sendAction")) {
                    string actionTags = network == Network.discord ? "__" : "";
                    sendPrivateMessage(PM, getArg(arg, 1), actionTags + getScramble(argJoiner(arg, 2)) + actionTags);
                }
    // !sendRaw - Tells the bot to say a raw line
                else if (commandChecker(PM, arg, "sendRaw") && checkPerm(PM.getUser(), 9001)) {
                    bot.sendRaw().rawLineNow(argJoiner(arg, 1));
                }
    
    // !part - leaves a channel
                else if (commandChecker(PM, arg, "part")) {
                    if (network != Network.discord) {
                        if (getArg(arg, 2) != null) {
                            bot.sendRaw().rawLineNow("part " + getArg(arg, 1) + " :" + argJoiner(arg, 2));
                        } else {
                            bot.sendRaw().rawLineNow("part " + getArg(arg, 1));
                        }
                        sendNotice(PM, "Successfully Disconnected from " + getArg(arg, 1));
                    } else {
                        sendMessage(PM, "I can't leave channels in discord dumbass");
                    }
                }
    
    // !ChangeNick- Changes the nick of the bot
                else if (commandChecker(PM, arg, "changeNick") && checkPerm(PM.getUser(), 9001)) {
                    bot.sendIRC().changeNick(argJoiner(arg, 1));
                    debug.setNick(argJoiner(arg, 1));
                }
    
    
    // !Connect - Tells the bot to connect to specified channel
                else if (commandChecker(PM, arg, "connect")) {
                    if (getArg(arg, 2) != null) {
                        bot.sendIRC().joinChannel(getArg(arg, 1), getArg(arg, 2));
                    } else {
                        bot.sendIRC().joinChannel(getArg(arg, 1));
                    }
                    sendNotice(PM, "Successfully connected to " + getArg(arg, 1));
                }
    
    // !QuitServ - Tells the bot to disconnect from server
                else if (commandChecker(PM, arg, "QuitServ") && checkPerm(PM.getUser(), int.MAX_VALUE)) {
                    sendNotice(PM, "Disconnecting from server");
                    if (getArg(arg, 1) != null) {
                        bot.sendIRC().quitServer(argJoiner(arg, 1));
                    } else {
                        bot.sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                    }
                    try {
                        LilGUtil.pause(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
    
                    System.exit(0);
                }
            }
            if (!PM.getMessage().contains(CryptoUtil.decrypt(FozConfig.PASSWORD))) {
                log(PM);
            }
            if (PM.getMessage().startsWith(prefix) || PM.getMessage().startsWith(consolePrefix) || PM.getMessage().startsWith(PM.getBot().getNick())) {
                doCommand(new MessageEvent(bot, new DiscordChannel(bot, PM.getUser().getNick()), PM.getUser().getNick(), PM.getUserHostmask(), PM.getUser(), PM.getMessage(), null));
            } else {
                try {
                    PM.respondWith(botTalk("clever", PM.getMessage()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            BOOLS.clear(ARRAY_OFFSET_SET);
            debug.updateBot(bot);
            checkNote(PM, PM.getUser().getNick(), null);
            debug.setCurrentNick(currentUser.getHostmask());
        }
    
        public void onNotice( NoticeEvent event_) {
            string message = event_.getMessage();
            //noinspection ConstantConditions
            if (event_.getUser() == null) {
                return;
            }
            if (!event_.getUser().getNick().equalsIgnoreCase("NickServ") || !event_.getUser().getNick().equalsIgnoreCase("irc.badnik.net")) {
                //noinspection StatementWithEmptyBody
                if (message.contains("*** Found your hostname") ||
                        message.contains("Password accepted - you are now recognized.") ||
                        message.contains("This nickname is registered and protected.  If it is your") ||
                        message.contains("*** You are connected using SSL cipher") ||
                        message.contains("please choose a different nick.") ||
                        message.contains("nick, type /msg NickServ IDENTIFY password.  Otherwise,")) {
    
                } else if (message.contains("\u0001AVATAR")) {
                    event_.getUser().send().notice("\u0001AVATAR " + avatar + "\u0001");
                } else {
                    bot.sendIRC().notice(currentUser.getNick(), "Got notice from " + event_.getUser().getNick() + ". Notice was : " + event_.getMessage());
                }
            }
            checkNote(event_, event_.getUser().getNick(), null);
            if (bot.isConnected()) {
                debug.setCurrentNick(currentUser.getNick());
            }
            debug.updateBot(bot);
            log(event_);
        }
    
        public void onAction( ActionEvent action) {
            //noinspection ConstantConditions
            onMessage(new MessageEvent(bot, action.getChannel(), action.getChannelSource(), action.getUserHostmask(), action.getUser(), action.getAction(), null), false);
            log(action);
        }
    
        public void onJoin( JoinEvent join) {
            string hostmask = join.getUser().getHostmask();
            Logger.Debug("User Joined: " + (hostmask == null ? join.getUser().getNick() : hostmask));
            if (join is DiscordJoinEvent) {
                GuildMemberJoinEvent discordJoin = ((DiscordJoinEvent) join).getJoinEvent();
                string channelToMessage = checkJoinsAndQuits.get(discordJoin.getGuild().getId());
                if (channelToMessage != null) {
                    List<TextChannel> channels = discordJoin.getGuild().getTextChannels();
                    for (TextChannel channel : channels) {
                        if (channel.getId().equals(channelToMessage)) {
                            if (discordJoin.getMember().getUser().isBot()) {
                                channel.sendMessage(
                                        "aw hell, we got another bot here"
                                ).queue();
                            } else {
                                channel.sendMessage(
                                        discordJoin.getMember().getAsMention() +
                                                ": Welcome to " +
                                                discordJoin.getGuild().getName() +
                                                ". Please make sure you check out the #help and #information channel"
                                ).queue();
                            }
                        }
                    }
                }
            }
            //noinspection ConstantConditions
            log(join);
            if (join.getChannel() != null) {
                if (checkOP(join.getChannel())) {
                    //noinspection ConstantConditions
                    if (checkPerm(join.getUser(), 0)) {
                        join.getChannel().send().voice(join.getUserHostmask());
                    }
                }
                checkNote(join, join.getUser().getNick(), join.getChannel().getName());
            } else {
                checkNote(join, join.getUser().getNick(), null);
            }
            if (debug != null) {
                debug.updateBot(bot);
                debug.setCurrentNick(currentUser.getHostmask());
            }
        }
    
        public void onNickChange( NickChangeEvent nick) {
            if (nick.getNewNick().equalsIgnoreCase(currentUser.getNick())) {
                currentUser = bot.getUserBot();
                Logger.Debug("resetting Authed nick");
                debug.setCurrentNick(currentUser.getNick());
            }
    
            /*if (nick.getOldNick().equalsIgnoreCase(currentNick)) {
                currentNick = nick.getNewNick();
                //noinspection ConstantConditions
                currentUsername = nick.getUser().getLogin();
                currentHost = nick.getUser().getHostname();
                Logger.Debug("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostname());
                debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
            }*/
            checkNote(nick, nick.getNewNick(), null);
            debug.updateBot(bot);
            log(nick);
        }
    
        public void onNickAlreadyInUse( NickAlreadyInUseEvent nick) {
    
            BOOLS.set(NICK_IN_USE);
            nick.respond(nick.getUsedNick() + 1);
        }
    
        public void onQuit( QuitEvent quit) {
            if (quit.getReason().contains("RECOVER") || quit.getReason().contains("GHOST") || quit.getReason().contains("REGAIN")) { //Recover event_
                BOOLS.set(NICK_IN_USE);
            }
            if (quit is DiscordQuitEvent) {
                string channelToMessage = checkJoinsAndQuits.get(((DiscordQuitEvent) quit).getLeaveEvent().getGuild().getId());
                if (channelToMessage != null) {
                    List<TextChannel> channels = ((DiscordQuitEvent) quit).getLeaveEvent().getGuild().getTextChannels();
                    for (TextChannel channel : channels) {
                        if (channel.getId().equals(channelToMessage)) {
                            channel.sendMessage("User " + ((DiscordQuitEvent) quit).getLeaveEvent().getMember().getAsMention() + " Has left the server").queue();
                        }
                    }
                }
            }
            log(quit);
        }
    
        public void onBan(GuildBanEvent ban) {
            string channelToMessage = checkJoinsAndQuits.get(ban.getGuild().getId());
            if (channelToMessage != null) {
                List<TextChannel> channels = ban.getGuild().getTextChannels();
                for (TextChannel channel : channels) {
                    if (channel.getId().equals(channelToMessage)) {
                        channel.sendMessage(ban.getUser().getAsMention() + " Has been b&, ripperoni in pepperoni http://gerbilsoft.soniccenter.org/lol/BAN.jpg").queue();
                    }
                }
            }
    
        }
    
        public void onKick( KickEvent kick) {
            //noinspection ConstantConditions
            if (kick.getRecipient().getNick().equalsIgnoreCase(bot.getNick())) {
                try {
                    LilGUtil.pause(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bot.send().joinChannel(kick.getChannel().getName());
            }
            log(kick);
        }
    
        public void onUnknown( UnknownEvent event_) {
            string line = event_.getLine();
            if (line.contains("\u0001AVATAR\u0001")) {
                //noinspection ConstantConditions
                line = line.substring(line.indexOf(":") + 1, line.indexOf("!"));
                bot.send().notice(line, "\u0001AVATAR " + avatar + "\u0001");
            }
            Logger.Debug("Received unknown: " + event_.getLine());
            if (debug != null) {
                debug.setCurrentNick(currentUser.getNick());
            }
        }
    
        /**
         * Checks if the user attempting to use the command is allowed
         *
         * @param user              User trying to use command
         * @param requiredUserLevel Required permission level to access command
         * @return Boolean true if allowed, false if not
         */
        private bool checkPerm( User user, int requiredUserLevel) {
            if (user.equals(currentUser)) {
                return true;
            } else if (authedUser.contains(user.getNick())) {
                int index = authedUser.indexOf(user.getHostmask());
                if (index > -1) {
                    if (authedUserLevel.get(index) >= requiredUserLevel) {
                        return true;
                    }
                }
            } else if (user is DiscordUser) {
                if (user.getHostname().equals(currentUser.getHostname())) {
                    return true;
                }
                List<Role> roles = ((DiscordUser) user).getGuild().getMember(((DiscordUser) user).getDiscordUser()).getRoles();
                int highestLevel = 0;
                for (Role role : roles) {
                    for (Permission perm : role.getPermissions()) {
                        int level = DiscordAdapter.getlevelFromPerm(perm);
                        if (level > highestLevel) {
                            highestLevel = level;
                        }
                    }
                }
                return highestLevel >= requiredUserLevel;
            } else {
                int index = authedUser.Count - 1;
                while (index > -1) {
                    string ident = authedUser.get(index);
                    if (LilGUtil.matchHostMask(user.getHostmask(), ident)) {
                        return authedUserLevel.get(index) >= requiredUserLevel;
                    }
                    index--;
                }
                List<UserLevel> levels = Lists.newList(user.getUserLevels(lastEvents.get().getChannel()).iterator());
                if (requiredUserLevel <= getUserLevel(levels)) {
                    return true;
                }
            }
            return false;
        }
    
        /**
         * Checks if the user attempting to use the command is allowed
         *
         * @param user User trying to use command
         * @param perm Required permission to access command
         * @return Boolean true if allowed, false if not
         */
        private bool checkPerm( DiscordUser user, Permission perm) {
            if (user.equals(currentUser)) {
                return true;
            }
            Guild guild = user.getGuild();
            if (guild != null) {
                for (Role role : guild.getMember(user.getDiscordUser()).getRoles()) {
                    if (role.getPermissions().contains(perm)) {
                        return true;
                    }
                }
            }
            return false;
        }
    
        private void loadData() {
            loadData(true);
        }
    
        private void loadData(bool writeOnce) {
            if (writeOnce && noteList == null)
                noteList = SaveDataStore.getINSTANCE().getNoteList();
    
            if (writeOnce && authedUser == null)
                authedUser = SaveDataStore.getINSTANCE().getAuthedUser();
    
            if (writeOnce && authedUserLevel == null)
                authedUserLevel = SaveDataStore.getINSTANCE().getAuthedUserLevel();
    
            if (writeOnce && avatar == null)
                avatar = SaveDataStore.getINSTANCE().getAvatarLink();
    
            if (writeOnce && memes == null)
                memes = SaveDataStore.getINSTANCE().getMemes();
    
            if (writeOnce && FCList == null)
                FCList = SaveDataStore.getINSTANCE().getFCList();
    
            if (writeOnce && markovChain == null)
                markovChain = SaveDataStore.getINSTANCE().getMarkovChain();
    
            if (writeOnce && allowedCommands == null)
                allowedCommands = SaveDataStore.getINSTANCE().getAllowedCommands();
    
            if (writeOnce && checkJoinsAndQuits == null)
                checkJoinsAndQuits = SaveDataStore.getINSTANCE().getCheckJoinsAndQuits();
    
            if (writeOnce && mutedServerList == null)
                mutedServerList = SaveDataStore.getINSTANCE().getMutedServerList();
    
            if (writeOnce && DiscordData.wordFilter == null)
                DiscordData.wordFilter = SaveDataStore.getINSTANCE().getWordFilter();
        }
    
        private void checkNote( Event event_,  string user,  string channel) {
            System.out.print("Debug: Starting checkNote -> ");
            try {
                for (int i = 0; i < noteList.Count; i++) {
                    System.out.print("Checking if " + noteList.get(i).getReceiver() + " matches " + user + " -> ");
                    if (LilGUtil.wildCardMatch(user.toLowerCase(), noteList.get(i).getReceiver().toLowerCase())) {
                        System.out.print("Found match! -> ");
                        if (channel != null) {
                            try {
                                sendMessage((MessageEvent) event_, user + ": " + noteList.get(i).displayMessage());
                            } catch (ClassCastException e) {
                                //noinspection ConstantConditions
                                sendPrivateMessage(((JoinEvent) event_).getUser().getNick(), user + ": " + noteList.get(i).displayMessage());
                            }
                        } else {
                            sendNotice(event_, user, noteList.get(i).displayMessage());
                        }
                        noteList.Remove(i);
                        i--;
                    }
                }
            } catch (Exception e) {
                if (event_ is JoinEvent) {
                    e.printStackTrace();
                } else {
                    sendError(lastEvents.get(), e);
                }
            }
            System.out.println("| Ending checkNote");
        }
    
        private void setDebugInfo( MessageEvent event_) {
            debug.updateBot(bot);
            debug.setCurrentNick(currentUser.getHostmask());
        }
    
        public MessageEvent getLastEvent() {
            return lastEvents.get();
        }
    
        private bool commandChecker(GenericMessageEvent event_, string[] args, string command) {
            return commandChecker(event_, args, command, true);
        }
    
        private bool commandChecker(GenericMessageEvent event_, string[] args, string command, bool printMsg) {
            if (event_.getMessage() == null) {
                return false;
            }
            try {
                string chanName;
                if (event_ is PrivateMessageEvent) {
                    chanName = "PM";
                } else {
                    chanName = ((MessageEvent) event_).getChannel().getName();
                }
                bool isCommand = false;
                if(args.length < 1) return false;
                if (args[0].startsWith(prefix)) {
                    if (prefix.length() > 1 && !prefix.endsWith(".")) {
                        isCommand = getArg(args, 0).equalsIgnoreCase(command);
                    } else {
                        isCommand = args[0].equalsIgnoreCase(prefix + command);
                    }
                } else if (args[0].startsWith(bot.getNick())) {
                    setArrayOffset(args[0] + " ");
                    isCommand = args[arrayOffset].equalsIgnoreCase(command);
                }
                if (isCommand) {
                    Dictionary<string, List<string>> temp = allowedCommands.get(getSeverName((Event) event_, true));
                    List<string> commands;
                    if (temp != null) {
                        commands = temp.get(chanName);
                    } else {
                        commands = null;
                    }
                    if (commands != null && commands.contains(command.toLowerCase())) {
                        if (printMsg) {
                            sendNotice(event_, event_.getUser().getNick(), "Sorry, you can't use that command here");
                        }
                    } else {
                        if (event_ is DiscordMessageEvent) /*then*/
                            ((DiscordMessageEvent) event_).getDiscordEvent().getTextChannel().sendTyping();
                        Logger.Trace("Found command: " + command);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    
        private void getHelp(GenericMessageEvent event_, string command) {
            switch (command.toLowerCase()) {
                case "commands":
                    sendNotice(event_, event_.getUser().getNick(), "Really? ಠ_ಠ");
                    break;
                case "helpme":
                    sendNotice(event_, event_.getUser().getNick(), "Changed to COMMANDS (Except you already know that since you just used it...)");
                    break;
                case "time":
                    sendNotice(event_, event_.getUser().getNick(), "Displays info from the Date class");
                    break;
                case "hello":
                    sendNotice(event_, event_.getUser().getNick(), "Just your average \"hello world!\" program");
                    break;
                case "RandomInt":
                    sendNotice(event_, event_.getUser().getNick(), "Creates a random number between the 2 integers");
                    sendNotice(event_, event_.getUser().getNick(), "Usage: first number sets the minimum number, second sets the maximum");
                    break;
                case "version":
                    sendNotice(event_, event_.getUser().getNick(), "Displays the version of the bot");
                    break;
                case "stringtobytes":
                    sendNotice(event_, event_.getUser().getNick(), "Converts a string into a Byte array");
                    break;
                case "temp":
                    sendNotice(event_, event_.getUser().getNick(), "Converts a temperature unit to another unit.");
                    sendNotice(event_, event_.getUser().getNick(), "Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
                    break;
                case "chat":
                    sendNotice(event_, event_.getUser().getNick(), "This command functions like ELIZA. Talk to it and it talks back.");
                    sendNotice(event_, event_.getUser().getNick(), "Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky. Second parameter is the Message to send.");
                    break;
                case "calcJ":
                    sendNotice(event_, event_.getUser().getNick(), "This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
                    sendNotice(event_, event_.getUser().getNick(), "Usage 1: The simple way is to type out the expression without any VARIABLE_SET. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
                    break;
                case "calcjs":
                    sendNotice(event_, event_.getUser().getNick(), "Renamed to just \"JS\"");
                case "js":
                    sendNotice(event_, event_.getUser().getNick(), "This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
                    sendNotice(event_, event_.getUser().getNick(), "Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
                    break;
                case "notej":
                    sendNotice(event_, event_.getUser().getNick(), "Allows the user to leave notes");
                    sendNotice(event_, event_.getUser().getNick(), "SubCommand add <Nick to leave note to> <message>: adds a note. SubCommand del <Given ID>: Deletes a set note Usage: . SubCommand list: Lists notes you've left");
                    break;
                case "memes":
                    sendNotice(event_, event_.getUser().getNick(), "Meme database. To get a meme you simply have to do \"Memes <meme name>\"");
                    sendNotice(event_, event_.getUser().getNick(), "SubCommand set <Meme Name> <The Meme>: Sets up a meme. Note, When Setting a meme that already exists, you have to be the creator to edit it.  SubCommand list: Lists all the memes in the database");
                    break;
                case "disasm":
                    sendNotice(event_, event_.getUser().getNick(), "Disassembles bytes from different CPUs");
                    sendNotice(event_, event_.getUser().getNick(), "Usage: 1st param is the CPU to read from. 2nd param is the bytes to assemble. You can use M68k as a shorthand instead of typing 68000. List of available CPUs https://www.hex-rays.com/products/ida/support/idadoc/618.shtml");
                    break;
                case "attempt":
                    sendNotice(event_, event_.getUser().getNick(), "Its a inside-joke with my friends in school. If i'm not away, ask me and i'll tell you about it.");
                    break;
                case "reverselist":
                    sendNotice(event_, event_.getUser().getNick(), "Reverses a list, pretty self explanatory");
                    break;
                case "getdate":
                    sendNotice(event_, event_.getUser().getNick(), "Gets the date");
                    break;
                case "markov":
                    sendNotice(event_, event_.getUser().getNick(), "Creates a markov chain from everything seen in chat");
                    break;
                case "8ball":
                    sendNotice(event_, event_.getUser().getNick(), "Rolls the magic 8Ball");
                    break;
                case "checklink":
                    sendNotice(event_, event_.getUser().getNick(), "Checks links, what else");
                    break;
                case "calca":
                    sendNotice(event_, event_.getUser().getNick(), "Currently broken: Calculates math using Wolfram Alpha");
                    break;
                case "solvefor":
                    sendNotice(event_, event_.getUser().getNick(), "Currently broken: Solves for a equation");
                    break;
                case "count":
                    sendNotice(event_, event_.getUser().getNick(), "");
                    break;
                case "Lookupword":
                    sendNotice(event_, event_.getUser().getNick(), "Looks up a word in the DICTIONARY");
                    break;
                case "lookup":
                    sendNotice(event_, event_.getUser().getNick(), "Looks up a word in the wikipedia");
                    break;
                case "blockconv":
                    sendNotice(event_, event_.getUser().getNick(), "Converts blocks to a actually known format");
                    break;
                case "fc":
                    sendNotice(event_, event_.getUser().getNick(), "Stores FC codes in a database so you can retrieve it");
                    break;
                case "q":
                    sendNotice(event_, event_.getUser().getNick(), "Allows from syncing up something such as sync watching a show");
                    break;
                case "toscino":
                    sendNotice(event_, event_.getUser().getNick(), "Converts a number to Scientific notation");
                    break;
                case "dnd":
                    sendNotice(event_, event_.getUser().getNick(), "");
                    break;
                case "acc":
                case "68kcyc":
                case "asmcyclecounter":
                    sendNotice(event_, event_.getUser().getNick(), "Counts cycles for 68k asm instructions. Use || to separate lines");
                    break;
                case "trans":
                    sendNotice(event_, event_.getUser().getNick(), "Translates between languages. -t is to, -f is from, and -d is detect");
                    break;
                case "badtrans":
                    sendNotice(event_, event_.getUser().getNick(), "Translates between languages... badly...");
                    break;
                case "s1tcg":
                    sendNotice(event_, event_.getUser().getNick(), "Generates Title card information for Sonic 1. Use \"-x xpos\" to specify X position, \"-y ypos\" to specify Y position, in hexadecimal. Use \"-l label\" to specify a label.");
                    sendNotice(event_, event_.getUser().getNick(), "Example: !s1tcg -x F8 -y F8 -l GreenHillTitle RED HILL");
                    break;
                default:
                    sendNotice(event_, event_.getUser().getNick(), "That either isn't a command, or " + currentUser.getNick() + " hasn't add that to the help yet.");
            }
        }
    
        public void setAvatar(string avatar) {
            FozruciX.avatar = avatar;
            if (!(network == Network.discord || network == Network.twitch) && currentUser.getNick() != null && updateAvatar) {
                sendNotice(currentUser.getNick(), "\u0001AVATAR " + avatar + "\u0001");
            }
        }
    
        private void addWords( string phrase) {
            if (phrase.startsWith(prefix) ||
                    phrase.startsWith(consolePrefix)) {
                return;
            }
            // put each word into an array
            string[] words = phrase.split(" ");
    
            // Loop through each word, check if it's already added
            // if its added, then get the suffix List and add the word
            // if it hasn't been added then add the word to the list
            // if its the first or last word then select the _start / _end key
    
            for (int i = 0; i < words.length; i++) {
                if (words[i].isEmpty()) {
                    break;
                }
                // Add the start and end words to their own
                if (i == 0) {
                    LinkedList<string> startWords = markovChain.get("_start");
                    startWords.Add(words[i]);
    
                    LinkedList<string> suffix = markovChain.get(words[i]);
                    if (suffix == null) {
                        suffix = new LinkedList<>();
                        if (words.length == 1) {
                            return;
                        } else {
                            suffix.Add(words[i + 1]);
                        }
                        markovChain[words[i]] = suffix;
                    }
    
                } else if (i == words.length - 1) {
                    LinkedList<string> endWords = markovChain.get("_end");
                    endWords.Add(words[i]);
    
                } else {
                    LinkedList<string> suffix = markovChain.get(words[i]);
                    if (suffix == null) {
                        suffix = new LinkedList<>();
                        if (words.length == 1) {
                            return;
                        } else {
                            suffix.Add(words[i + 1]);
                        }
                        markovChain[words[i]] = suffix;
                    } else {
                        if (words.length == 1) {
                            return;
                        } else {
                            suffix.Add(words[i + 1]);
                        }
                        markovChain[words[i]] = suffix;
                    }
                }
            }
        }
    
        private class Python extends Thread {
            private MessageEvent event_;
            private string eval;
            private int radix = 10;
            private ScriptEngine engine;
    
            public Python() {
                this.setName("Python thread");
                engine = new ScriptEngineManager().getEngineByName("python");
            }
    
            
            public void run() {
                try {
                    Object temp = engine.eval(eval);
                    if (temp != null) {
                        string eval = temp.toString();
                        if (LilGUtil.isNumeric(eval)) {
                            if (radix == 10) {
                                sendMessage(event_, eval);
                                Logger.Debug("Outputting as decimal");
                            } else {
                                string basePrefix = "";
                                switch (radix) {
                                    case 2:
                                        basePrefix = "0b";
                                        break;
                                    case 8:
                                        basePrefix = "0";
                                        break;
                                    case 16:
                                        basePrefix = "0x";
                                        break;
                                }
                                eval = long.tostring(long.parselong(eval), radix).toUpperCase();
                                if (Math.abs(eval.length()) % 2 == 1) {
                                    eval = "0" + eval;
    
                                }
                                sendMessage(event_, basePrefix + eval);
                                Logger.Debug("Outputting as base " + radix);
                            }
                        } else if (eval.length() < 470) {
                            sendMessage(event_, eval);
                        } else {
                            sendPage(event_, new string[]{"!PY", this.eval, "" + radix}, new LinkedList<>(Collections.singletonList(eval)));
                        }
                    }
                } catch (Exception e) {
                    sendError(event_, e);
                }
            }
    
            void runNewPython(MessageEvent event_, string code, int base) {
                this.event_ = event_;
                eval = code;
                radix = base;
                run();
            }
    
        }
    
        private class JavaScript extends Thread {
            private readonly string[] unsafeAttributes = {
                    "Java",
                    "JavaImporter",
                    "Packages",
                    "java",
                    "javax",
                    "javafx",
                    "org",
                    "com",
                    "net",
                    "edu",
                    "load",
                    "loadWithNewGlobal",
                    "exit",
                    "quit"
            };
            private readonly string[] unsafeClasses = {
                    "java.lang.reflect",
                    "java.lang.invoke",
            };
            
            private string factorialFunct = "function fact(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} " +
                    "function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} " +
                    "function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;} " +
                    "function solvefor(expr, solve){var eq = algebra.parse(expr); var ans = eq.solveFor(solve); return solve + \" = \" + ans.toString(); }  var life = 42; " +
                    "function roughSizeOf(e){for(var f=[],o=[e],t=0;o.length;){var n=o.pop();if(\"bool\"==typeof n)t+=4;else if(\"string\"==typeof n)t+=2*n.length;else if(\"number\"==typeof n)t+=8;else if(\"object\"==typeof n&&-1===f.indexOf(n)){f.push(n);for(var r in n)o.push(n[r])}}return t}" +
                    "fish = 4; eight = 6; triangle = 14; leet = 1337;";
            private MessageEvent event_;
            private string arg;
            private int radix = 10;
            private ScriptEngine botOPEngine;
            private ScriptEngine normalUserEngine;
    
            JavaScript( MessageEvent event_, string arg, int radix) {
                setName("JavaScript Thread");
                this.event_ = event_;
                this.arg = arg;
                this.radix = radix;
                normalUserEngine = new NashornScriptEngineFactory().getScriptEngine(new JSClassFilter());
                botOPEngine = new NashornScriptEngineFactory().getScriptEngine();
                try (InputStreamReader algebra = new InputStreamReader(new FileInputStream("algebra.min.js"))) {
                    normalUserEngine.eval(factorialFunct);
                    normalUserEngine.eval(algebra);
                    ScriptContext context = normalUserEngine.getContext();
                    int globalScope = context.getScopes().get(0);
                    for (string unsafeAttribute : unsafeAttributes) {
                        context.removeAttribute(unsafeAttribute, globalScope);
                    }
                    botOPEngine.eval(factorialFunct);
                    updateVariables();
                } catch (Exception e) {
                    sendError(event_, e);
                }
            }
    
            private void updateVariables() {
                botOPEngine["event_"] = event_;
                botOPEngine["message"] = event_.getMessage();
                botOPEngine["channel"] = event_.getChannel();
                botOPEngine["user"] = event_.getUser();
                botOPEngine["bot"] = event_.getBot();
                botOPEngine["hostmask"] = event_.getUserHostmask();
                botOPEngine["server"] = event_.getBot().getServerInfo();
                botOPEngine["jda"] = DiscordAdapter.getJda();
                if (event_ is DiscordMessageEvent) {
                    botOPEngine["discordEvent"] = ((DiscordMessageEvent) event_).getDiscordEvent();
                    botOPEngine["discordChannel"] = ((DiscordMessageEvent) event_).getDiscordEvent().getChannel();
                    botOPEngine["discordType"] = ((DiscordMessageEvent) event_).getDiscordEvent().getChannelType();
                    botOPEngine["discordGuild"] = ((DiscordMessageEvent) event_).getDiscordEvent().getGuild();
                    botOPEngine["discordAuthor"] = ((DiscordMessageEvent) event_).getDiscordEvent().getAuthor();
                    botOPEngine["discordMember"] = ((DiscordMessageEvent) event_).getDiscordEvent().getMember();
                    botOPEngine["discordGroup"] = ((DiscordMessageEvent) event_).getDiscordEvent().getGroup();
                } else {
                    botOPEngine["discordEvent"] = null;
                    botOPEngine["discordChannel"] = null;
                    botOPEngine["discordType"] = null;
                    botOPEngine["discordGuild"] = null;
                    botOPEngine["discordAuthor"] = null;
                    botOPEngine["discordMember"] = null;
                    botOPEngine["discordGroup"] = null;
                }
            }
    
            void runNewJavaScript(MessageEvent event_, string arg, int radix) {
                this.event_ = event_;
                this.arg = arg;
                this.radix = radix;
                updateVariables();
                run();
            }
    
            
            public void run() {
                try {
                    ScriptEngine engine;
                    if (checkPerm(event_.getUser(), 9001)) {
                        engine = botOPEngine;
                        Logger.Debug("Running as op");
                    } else {
                        engine = normalUserEngine;
                        Logger.Debug("Running as normal user");
                    }
    
                    Object temp = engine.eval(arg);
                    if (temp != null) {
                        string eval = temp.toString();
                        if (LilGUtil.isNumeric(eval)) {
                            if (radix == 10) {
                                sendMessage(event_, eval);
                                Logger.Debug("Outputting as decimal");
                            } else {
                                string basePrefix = "";
                                switch (radix) {
                                    case 2:
                                        basePrefix = "0b";
                                        break;
                                    case 8:
                                        basePrefix = "0";
                                        break;
                                    case 16:
                                        basePrefix = "0x";
                                        break;
                                }
                                eval = long.tostring(long.parselong(eval), radix).toUpperCase();
                                if (Math.abs(eval.length()) % 2 == 1) {
                                    eval = "0" + eval;
    
                                }
                                sendMessage(event_, basePrefix + eval);
                                Logger.Debug("Outputting as base " + radix);
                            }
                        } else if (eval.length() < 470) {
                            sendMessage(event_, eval);
                        } else {
                            sendPage(event_, new string[]{"!JS", arg, "" + radix}, new LinkedList<>(Collections.singletonList(eval)));
                        }
                    }
                } catch (Exception e) {
                    sendError(event_, e);
                }
            }
    
            private class JSClassFilter implements ClassFilter {
                
                public bool exposeToScripts( string requestedClass) {
                    for (string unsafeClass : unsafeClasses) {
                        if (requestedClass.equals(unsafeClass)) return false;
                    }
                    return true;
                }
            }
        }
    }
}