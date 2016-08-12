package com.LilG.Com;

import com.LilG.Com.CMD.CMD;
import com.LilG.Com.CMD.CommandLine;
import com.LilG.Com.DND.DNDPlayer;
import com.LilG.Com.DND.Dungeon;
import com.LilG.Com.DataClasses.Meme;
import com.LilG.Com.DataClasses.Note;
import com.LilG.Com.DataClasses.SaveDataStore;
import com.LilG.Com.utils.CryptoUtil;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.rmtheis.yandtran.YandexTranslatorAPI;
import com.rmtheis.yandtran.detect.Detect;
import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;
import com.wolfram.alpha.*;
import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import info.bliki.api.Page;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.dv8tion.jda.MessageBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.pircbotx.*;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import s1tcg.S1TCG;
import sun.misc.Unsafe;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.citumpe.ctpTools.jWMI.getWMIValue;

/**
 * Created by Lil-G on 10/11/2015.
 * Main bot class
 */
public class FozruciX extends ListenerAdapter {
    private final static double VERSION = 2.2;
    private final static File WIKTIONARY_DIRECTORY = new File("Data\\Wiktionary");
    private final static int JOKE_COMMANDS = 0;
    private final static int ARRAY_OFFSET_SET = 1;
    private final static int CLEVER_BOT_INT = 2;
    private final static int PANDORA_BOT_INT = 3;
    private final static int JABBER_BOT_INT = 4;
    private final static int NICK_IN_USE = 5;
    private final static int COLOR = 6;
    private final static int RESPOND_TO_PMS = 7;
    private final static int DATA_LOADED = 8;
    private final static int CHECK_LINKS = 9;
    private final static String[] DICTIONARY = {"i don't know what \"%s\" is, do i look like a DICTIONARY?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck.", ">_>"};
    private final static String[] LIST_OF_NOES = {" It’s not a priority for me at this time.", "I’d rather stick needles in my eyes.", "My schedule is up in the air right now. SEE IT WAFTING GENTLY DOWN THE CORRIDOR.", "I don’t love it, which means I’m not the right person for it.", "I would prefer another option.", "I would be the absolute worst person to execute, are you on crack?!", "Life is too short TO DO THINGS YOU don’t LOVE.", "I no longer do things that make me want to kill myself", "You should do this yourself, you would be awesome sauce.", "I would love to say yes to everything, but that would be stupid", "Fuck no.", "Some things have come up that need my attention.", "There is a person who totally kicks ass at this. I AM NOT THAT PERSON.", "Shoot me now...", "It would cause the slow withering death of my soul.", "I’d rather remove my own gallbladder with an oyster fork.", "I'd love to but I did my own thing and now I've got to undo it."};
    private final static String[] COMMANDS = {"COMMANDS", " Time", " calcj", " randomNum", " StringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " prefix", " SayThis", " ToSciNo", " Trans", " DebugVar", " cmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", " NoteJ", "Memes", " jToggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
    private final static ChatterBotFactory BOT_FACTORY = new ChatterBotFactory();
    private final static ArbitraryPrecisionEvaluator EVALUATOR = new ArbitraryPrecisionEvaluator();
    private final static StaticVariableSet<Double> VARIABLE_SET = new StaticVariableSet<>();
    private final static String APP_ID = "RGHHEP-HQU7HL67W9";
    private final static LinkedList<RPSGame> RPS_GAMES = new LinkedList<>();
    private final static Logger LOGGER = Logger.getLogger(FozruciX.class);
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final static BitSet BOOLS = new BitSet(10); // true, false, null, null, null, false, true, true, false, false
    public static volatile ConcurrentHashMap<String, LinkedList<String>> markovChain = null;
    @NotNull
    private static volatile Random rnd = new Random();
    private static volatile ChatterBotSession chatterBotSession;
    private static volatile ChatterBotSession pandoraBotSession;
    private static volatile ChatterBotSession jabberBotSession;
    @SuppressWarnings("ConstantConditions")
    @NotNull
    private static volatile SizedArray<MessageEvent> lastEvents = new SizedArray<>(30);
    private static volatile String lastLinkTitle = "";
    @Nullable
    private static volatile CMD singleCMD = null;
    private static volatile LinkedList<Note> noteList = null;
    private static volatile LinkedList<String> authedUser = null;
    private static volatile LinkedList<Integer> authedUserLevel = null;
    private static volatile LinkedList<String> DNDJoined = null;
    private static volatile LinkedList<DNDPlayer> DNDList = null;
    private static volatile HashMap<String, HashMap<String, ArrayList<String>>> allowedCommands = null;
    @NotNull
    private static volatile Dungeon DNDDungeon = new Dungeon();
    @NotNull
    private static volatile String DNDDungeonMaster = "Null";
    private static volatile int jokeCommandDebugVar = 30;
    @NotNull
    private static volatile CommandLine terminal = new CommandLine();
    private static volatile String counter = "";
    private static volatile int counterCount = 0;
    @NotNull
    private static volatile JFrame frame = new JFrame();
    @NotNull
    private static volatile MessageModes messageMode = MessageModes.normal;
    private static volatile int arrayOffset = 0;
    @Nullable
    private static volatile JavaScript js;
    @Nullable
    private static volatile Python py;
    @NotNull
    private static volatile String consolePrefix = ">";
    private static volatile String avatar;
    private static volatile TreeMap<String, Meme> memes;
    private static volatile TreeMap<String, String> FCList;
    private static volatile MultiBotManager manager;
    @NotNull
    private static volatile TreeSet<String> qList = new TreeSet<>();
    @NotNull
    private static volatile TreeMap<User, Long> commandCooldown = new TreeMap<>();
    @NotNull
    private static volatile StopWatch qTimer = new StopWatch();
    private static volatile DiscordAdapter discord;
    private static volatile boolean updateAvatar = false;
    @NotNull
    private String prefix = "!";
    private DebugWindow debug;
    @Nullable
    private String currentNick = "Lil-G";
    @Nullable
    private String currentUsername = "GameGenuis";
    @Nullable
    private String currentHost = "friendly.local.noob";
    private UserHostmask lastJsUser;
    private Channel lastJsChannel;
    private Network network = Network.normal;
    private PircBotX bot;

    @SuppressWarnings("unused")
    public FozruciX(MultiBotManager manager, SaveDataStore save) {
        this(Network.normal, manager, save);
    }

    @SuppressWarnings("unused")
    public FozruciX(Network network, MultiBotManager manager, SaveDataStore save) {
        if (network == Network.twitch) {
            currentNick = "lilggamegenuis";
            currentUsername = currentNick;
            currentHost = currentUsername + ".tmi.network.tv";
        } else if (network == Network.discord) {
            currentNick = "Lil-G";
            currentUsername = currentNick;
            currentHost = "131494148350935040";
        }
        this.network = network;
        // true, false, null, null, null, false, true, true
        BOOLS.set(JOKE_COMMANDS);
        BOOLS.set(COLOR);
        BOOLS.set(RESPOND_TO_PMS);
        BOOLS.set(CHECK_LINKS);

        FozruciX.manager = manager;
        loadData(save, true);
        AnsiConsole.systemInstall();
        LOGGER.setLevel(Level.ALL);
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveData, "Shutdown-thread"));
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    private static String getBytes(@NotNull String bytes) {
        byte[] Bytes = bytes.getBytes();
        return Arrays.toString(Bytes);
    }

    static String formatFileSize(long size) {
        String hrSize;

        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format((double) size).concat(" B");
        }

        return hrSize;
    }

    private static boolean isNumeric(@NotNull String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    private static int gc() {
        int timesRan = 0;
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        //noinspection UnusedAssignment
        obj = null;
        while (ref.get() != null) {
            System.gc();
            timesRan++;
        }
        return timesRan;
    }

    @SuppressWarnings("unused")
    public static Unsafe getUnsafe() throws SecurityException {
        return Unsafe.getUnsafe();
    }

    @NotNull
    static String getScramble(@NotNull String msgToSend) {
        if (msgToSend.contains("\r") || msgToSend.contains("\n")) {
            msgToSend = msgToSend.replace("\r", "").replace("\n", "");
        }
        if (messageMode == MessageModes.reversed) {
            msgToSend = new StringBuilder(msgToSend).reverse().toString();
        } else if (messageMode == MessageModes.wordReversed) {
            LinkedList<String> message = new LinkedList<>(Arrays.asList(msgToSend.split("\\s+")));
            msgToSend = "";
            for (int i = message.size() - 1; i >= 0; i--) {
                msgToSend += message.get(i) + " ";
            }
        } else if (messageMode == MessageModes.scrambled) {
            char[] msgChars = msgToSend.toCharArray();
            LinkedList<Character> chars = new LinkedList<>();
            for (char msgChar : msgChars) {
                chars.add(msgChar);
            }
            msgToSend = "";
            while (chars.size() != 0) {
                int num = randInt(0, chars.size() - 1);
                msgToSend += chars.get(num) + "";
                chars.remove(num);
            }
        } else if (messageMode == MessageModes.wordScrambled) {
            LinkedList<String> message = new LinkedList<>(Arrays.asList(msgToSend.split("\\s+")));
            msgToSend = "";
            while (message.size() != 0) {
                int num = randInt(0, message.size() - 1);
                msgToSend += message.get(num) + " ";
                message.remove(num);
            }
        }
        return msgToSend;
    }

    private static int getUserLevel(@NotNull ArrayList<UserLevel> levels) {
        int ret;
        if (levels.size() == 0) {
            ret = 0;
        } else {
            UserLevel level = levels.get(levels.size() - 1);
            switch (level) {
                case VOICE:
                    ret = 1;
                    break;
                case HALFOP:
                    ret = 2;
                    break;
                case OP:
                    ret = 3;
                    break;
                case SUPEROP:
                    ret = 4;
                    break;
                case OWNER:
                    ret = 5;
                    break;
                default:
                    ret = 0; // how it can not be these, i don't know
            }
        }
        return ret;
    }

    synchronized static void sendFile(MessageEvent event, File file) {
        sendFile(event, file, null);
    }

    synchronized static void sendFile(MessageEvent event, File file, String message) {
        if (event instanceof DiscordMessageEvent) {
            if (message == null) {
                ((DiscordMessageEvent) event).getDiscordEvent().getTextChannel().sendFile(file, null);
            } else {
                ((DiscordMessageEvent) event).getDiscordEvent().getTextChannel().sendFile(file, new MessageBuilder().appendString(message).build());
            }
        } else {
            //todo DCC support
        }
    }

    private synchronized static void sendMessage(@NotNull GenericMessageEvent event, @NotNull String msgToSend, boolean addNick) {
        sendMessage(event, msgToSend, addNick, false);
    }

    private synchronized static void sendMessage(@NotNull GenericMessageEvent event, @NotNull String msgToSend, boolean addNick, boolean splitMessage) {
        int textSizeLimit = 460; // irc limit
        if (event instanceof DiscordMessageEvent) {
            textSizeLimit = 700; // discord
        }
        if (msgToSend.equals("\u0002\u0002")) {
            return;
        }
        msgToSend = msgToSend.replace("(Player)", event.getUser().getNick()).replace("(items)", COMMANDS[randInt(0, COMMANDS.length - 1)]).replace("(Pokémon)", event.getBot().getNick()).replace("{random}", rnd.nextInt() + "");
        msgToSend = getScramble(msgToSend);

        if (!splitMessage && msgToSend.length() > textSizeLimit) {
            msgToSend = msgToSend.substring(0, textSizeLimit) + "-(snip)-";
        }

        if (((MessageEvent) event).getChannel() != null) {
            if (addNick) {
                event.respond(msgToSend);
            } else {
                event.respondWith(msgToSend);
            }
        } else {
            event.getUser().send().message(msgToSend);
        }
        log((MessageEvent) event, msgToSend, true);
    }

    private static boolean checkOP(@NotNull Channel chn) {
        User bot = chn.getBot().getUserBot();
        return chn.isHalfOp(bot) || chn.isOp(bot) || chn.isSuperOp(bot) || chn.isOwner(bot);
    }

    /**
     * tells the user they don't have permission to use the command
     *
     * @param user User trying to use command
     */
    private synchronized static void permError(@NotNull User user) {
        int num = randInt(0, LIST_OF_NOES.length - 1);
        String comeback = LIST_OF_NOES[num];
        user.send().notice(comeback);
    }

    /**
     * same as permError() except to be used in channels
     *
     * @param event Channel that the user used the command in
     */
    private synchronized static void permErrorchn(@NotNull MessageEvent event) {
        int num = randInt(0, LIST_OF_NOES.length - 1);
        String comeback = LIST_OF_NOES[num];
        sendMessage(event, comeback, true);
    }

    @NotNull
    private static String[] splitMessage(String stringToSplit) {
        return splitMessage(stringToSplit, 0);
    }

    @NotNull
    private static String[] splitMessage(String stringToSplit, int amountToSplit) {
        return splitMessage(stringToSplit, amountToSplit, true);
    }

    @NotNull
    private static String[] splitMessage(@Nullable String stringToSplit, int amountToSplit, boolean removeQuotes) {
        if (stringToSplit == null)
            return new String[0];

        LinkedList<String> list = new LinkedList<>();
        Matcher argSep = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(stringToSplit);
        while (argSep.find())
            list.add(argSep.group(1));

        if (removeQuotes) {
            if (amountToSplit != 0) {
                for (int i = 0; list.size() > i; i++) { // go through all of the
                    list.set(i, list.get(i).replaceAll("\"", "")); // remove quotes left in the string
                    list.set(i, list.get(i).replaceAll("''", "\"")); // replace double ' to quotes
                    // go to next string
                }
            } else {
                for (int i = 0; list.size() > i || amountToSplit > i; i++) { // go through all of the
                    list.set(i, list.get(i).replaceAll("\"", "")); // remove quotes left in the string
                    list.set(i, list.get(i).replaceAll("''", "\"")); // replace double ' to quotes
                    // go to next string
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private synchronized static String botTalk(@NotNull String bot, String message) throws Exception {
        if (bot.equalsIgnoreCase("clever")) {
            if (chatterBotSession == null) {
                chatterBotSession = BOT_FACTORY.create(ChatterBotType.CLEVERBOT).createSession();
            }
            return chatterBotSession.think(message);
        } else if (bot.equalsIgnoreCase("pandora")) {
            if (pandoraBotSession == null) {
                pandoraBotSession = BOT_FACTORY.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
            }
            return pandoraBotSession.think(message);
        } else if (bot.equalsIgnoreCase("jabber") || bot.equalsIgnoreCase("jabberwacky")) {
            if (jabberBotSession == null) {
                jabberBotSession = BOT_FACTORY.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
            }
            return jabberBotSession.think(message);
        } else {
            return "Error, not a valid bot";
        }
    }

    private static String argJoiner(@NotNull String[] args, int argToStartFrom) throws ArrayIndexOutOfBoundsException {
        return argJoiner(args, argToStartFrom, arrayOffset);
    }

    private static String argJoiner(@NotNull String[] args, int argToStartFrom, int arrayOffset) throws ArrayIndexOutOfBoundsException {
        if (args.length - 1 == argToStartFrom + arrayOffset) {
            return getArg(args, argToStartFrom, arrayOffset);
        }
        String strToReturn = "";
        for (int length = args.length; length > argToStartFrom + arrayOffset; argToStartFrom++) {
            strToReturn += getArg(args, argToStartFrom, arrayOffset) + " ";
        }
        LOGGER.debug("Argument joined to: " + strToReturn);
        if (strToReturn.isEmpty()) {
            return strToReturn;
        } else {
            return strToReturn.substring(0, strToReturn.length() - 1);
        }
    }

    private static void addCooldown(User user) {
        addCooldown(user, 7L);
    }

    private static void addCooldown(User user, long cooldownTime) {
        commandCooldown.put(user, System.currentTimeMillis() + (cooldownTime * 1000));
    }

    private static void removeFromCooldown() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<User, Long> user : commandCooldown.entrySet()) {
            if (currentTime >= (user.getValue())) { // Check if we've waited long enough
                commandCooldown.remove(user.getKey());
            }
        }
    }

    private static boolean containsAny(@NotNull String check, @NotNull String... contain) {
        for (String aContain : contain) {
            if (check.contains(aContain)) {
                return true;
            }
        }
        return false;
    }

    private static boolean equalsAny(@NotNull String check, @NotNull String... equal) {
        for (String aEqual : equal) {
            if (check.contains(aEqual)) {
                return true;
            }
        }
        return false;
    }

    private synchronized static native void jniTest();

    private static int hash(@NotNull String string, int maxNum) {
        int hash = 0;
        for (int i = 0; i < string.length(); i++) {
            int charCode = string.charAt(i);
            hash += charCode;
        }
        return hash % maxNum;
    }

    @NotNull
    private static String fullNameToString(@NotNull Language language) {
        String lang = "Null".toLowerCase();
        switch (language) {
            case ALBANIAN:
                lang = "ALBANIAN".toLowerCase();
                break;
            case ARABIC:
                lang = "ARABIC".toLowerCase();
                break;
            case ARMENIAN:
                lang = "ARMENIAN".toLowerCase();
                break;
            case AFRIKAANS:
                lang = "AFRIKAANS".toLowerCase();
                break;
            case AZERBAIJANI:
                lang = "AZERBAIJANI".toLowerCase();
                break;
            case BASQUE:
                lang = "BASQUE".toLowerCase();
                break;
            case BELARUSIAN:
                lang = "BELARUSIAN".toLowerCase();
                break;
            case BULGARIAN:
                lang = "BULGARIAN".toLowerCase();
                break;
            case BOSNIAN:
                lang = "BOSNIAN".toLowerCase();
                break;
            case CATALAN:
                lang = "CATALAN".toLowerCase();
                break;
            case CROATIAN:
                lang = "CROATIAN".toLowerCase();
                break;
            case CZECH:
                lang = "CZECH".toLowerCase();
                break;
            case DANISH:
                lang = "DANISH".toLowerCase();
                break;
            case DUTCH:
                lang = "DUTCH".toLowerCase();
                break;
            case ENGLISH:
                lang = "ENGLISH".toLowerCase();
                break;
            case ESTONIAN:
                lang = "ESTONIAN".toLowerCase();
                break;
            case FINNISH:
                lang = "FINNISH".toLowerCase();
                break;
            case FRENCH:
                lang = "FRENCH".toLowerCase();
                break;
            case GERMAN:
                lang = "GERMAN".toLowerCase();
                break;
            case GEORGIAN:
                lang = "GEORGIAN".toLowerCase();
                break;
            case GREEK:
                lang = "GREEK".toLowerCase();
                break;
            case HAITIAN:
                lang = "HAITIAN".toLowerCase();
                break;
            case CREOLE:
                lang = "CREOLE".toLowerCase();
                break;
            case HUNGARIAN:
                lang = "HUNGARIAN".toLowerCase();
                break;
            case ICELANDIC:
                lang = "ICELANDIC".toLowerCase();
                break;
            case INDONESIAN:
                lang = "INDONESIAN".toLowerCase();
                break;
            case IRISH:
                lang = "IRISH".toLowerCase();
                break;
            case ITALIAN:
                lang = "ITALIAN".toLowerCase();
                break;
            case JAPANESE:
                lang = "JAPANESE".toLowerCase();
                break;
            case KAZAKH:
                lang = "KAZAKH".toLowerCase();
                break;
            case KYRGYZ:
                lang = "KYRGYZ".toLowerCase();
                break;
            case LATIN:
                lang = "LATIN".toLowerCase();
                break;
            case LATVIAN:
                lang = "LATVIAN".toLowerCase();
                break;
            case LITHUANIAN:
                lang = "LITHUANIAN".toLowerCase();
                break;
            case MACEDONIAN:
                lang = "MACEDONIAN".toLowerCase();
                break;
            case MALAGASY:
                lang = "MALAGASY".toLowerCase();
                break;
            case MALAY:
                lang = "MALAY".toLowerCase();
                break;
            case MALTESE:
                lang = "MALTESE".toLowerCase();
                break;
            case MONGOLIAN:
                lang = "MONGOLIAN".toLowerCase();
                break;
            case NORWEGIAN:
                lang = "NORWEGIAN".toLowerCase();
                break;
            case PERSIAN:
                lang = "PERSIAN".toLowerCase();
                break;
            case POLISH:
                lang = "POLISH".toLowerCase();
                break;
            case PORTUGUESE:
                lang = "PORTUGUESE".toLowerCase();
                break;
            case ROMANIAN:
                lang = "ROMANIAN".toLowerCase();
                break;
            case RUSSIAN:
                lang = "RUSSIAN".toLowerCase();
                break;
            case SERBIAN:
                lang = "SERBIAN".toLowerCase();
                break;
            case SLOVAK:
                lang = "SLOVAK".toLowerCase();
                break;
            case SLOVENIAN:
                lang = "SLOVENIAN".toLowerCase();
                break;
            case SPANISH:
                lang = "SPANISH".toLowerCase();
                break;
            case SWAHILI:
                lang = "SWAHILI".toLowerCase();
                break;
            case SWEDISH:
                lang = "SWEDISH".toLowerCase();
                break;
            case TATAR:
                lang = "TATAR".toLowerCase();
                break;
            case TAJIK:
                lang = "TAJIK".toLowerCase();
                break;
            case THAI:
                lang = "THAI".toLowerCase();
                break;
            case TAGALOG:
                lang = "TAGALOG".toLowerCase();
                break;
            case TURKISH:
                lang = "TURKISH".toLowerCase();
                break;
            case UZBEK:
                lang = "UZBEK".toLowerCase();
                break;
            case UKRAINIAN:
                lang = "UKRAINIAN".toLowerCase();
                break;
            case WELSH:
                lang = "WELSH".toLowerCase();
                break;
            case VIETNAMESE:
                lang = "VIETNAMESE".toLowerCase();
                break;
            case YIDDISH:
                lang = "YIDDISH".toLowerCase();
                break;
        }
        return lang;
    }

    public static String getArg(String[] args, int index) {
        return getArg(args, index, arrayOffset);
    }

    public static String getArg(String[] args, int index, int arrayOffset) {
        try {
            return args[index + arrayOffset];
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Performs a wildcard matching for the text and pattern
     * provided.
     *
     * @param text    the text to be tested for matches.
     * @param pattern the pattern to be matched for.
     *                This can contain the wildcard character '*' (asterisk).
     * @return <tt>true</tt> if a match is found, <tt>false</tt>
     * otherwise.
     */
    public static boolean wildCardMatch(@NotNull String text, @NotNull String pattern) {
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx == -1) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }

        return true;
    }

    public static boolean matchHostMask(@NotNull String hostmask, @NotNull String pattern) {
        String nick = hostmask.substring(0, hostmask.indexOf("!"));
        String userName = hostmask.substring(hostmask.indexOf("!") + 1, hostmask.indexOf("@"));
        String hostname = hostmask.substring(hostmask.indexOf("@") + 1);

        String patternNick = pattern.substring(0, pattern.indexOf("!"));
        String patternUserName = pattern.substring(pattern.indexOf("!") + 1, pattern.indexOf("@"));
        String patternHostname = pattern.substring(pattern.indexOf("@") + 1);
        if (wildCardMatch(nick, patternNick)) {
            if (wildCardMatch(userName, patternUserName)) {
                if (wildCardMatch(hostname, patternHostname)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void log(Event event) {
        log(event, null);
    }

    private static void log(Event event, String messageOverride) {
        log(event, messageOverride, false);
    }

    private static void log(Event event, String messageOverride, boolean botTalking) {
        String network = null;
        ArrayList<String> channel = new ArrayList<>();
        String user = null;
        String message = null;
        EventType eventType = EventType.message;

        if (event instanceof MessageEvent) {
            channel.add(((MessageEvent) event).getChannel().getName());
            user = ((MessageEvent) event).getUser().getHostmask();
            message = ((MessageEvent) event).getMessage();
            eventType = EventType.message;
        }
        if (event instanceof PrivateMessageEvent) {
            user = ((PrivateMessageEvent) event).getUser().getHostmask();
            message = ((PrivateMessageEvent) event).getMessage();
            eventType = EventType.privMessage;
        }
        if (event instanceof ActionEvent) {
            channel.add(((ActionEvent) event).getChannel().getName());
            user = ((ActionEvent) event).getUser().getHostmask();
            message = ((ActionEvent) event).getMessage();
            eventType = EventType.action;
        }
        if (event instanceof NoticeEvent) {
            user = ((NoticeEvent) event).getUser().getHostmask();
            message = ((NoticeEvent) event).getMessage();
            eventType = EventType.notice;
        }
        if (event instanceof JoinEvent) {
            channel.add(((JoinEvent) event).getChannel().getName());
            user = ((JoinEvent) event).getUser().getHostmask();
            message = "Joined " + channel;
            eventType = EventType.join;
        }
        if (event instanceof PartEvent) {
            channel.add(((PartEvent) event).getChannel().getName());
            user = ((PartEvent) event).getUser().getHostmask();
            message = "parted " + channel;
            if (((PartEvent) event).getReason() != null) {
                message += " (" + ((PartEvent) event).getReason() + ")";
            }
            eventType = EventType.part;
        }
        if (event instanceof QuitEvent) {
            channel.addAll(event.getBot().getUserBot().getChannels().stream().map(Channel::getName).collect(Collectors.toList())); //get all channels user was in
            user = ((QuitEvent) event).getUser().getHostmask();
            message = "Quit " + network;
            if (((QuitEvent) event).getReason() != null) {
                message += " (" + ((QuitEvent) event).getReason() + ")";
            }
            eventType = EventType.quit;
        }
        if (event instanceof KickEvent) {
            user = ((KickEvent) event).getRecipient().getHostmask();
            message = "Kicked " + network + " by " + ((KickEvent) event).getUser().getHostmask() + "(" + ((KickEvent) event).getReason() + ")";
            eventType = EventType.part;
        }
        if (botTalking) {
            user = event.getBot().getUserBot().getHostmask();
        }
        if (messageOverride != null) {
            message = messageOverride;
        }
        network = getSeverName(event);


        if (channel.isEmpty()) {
            channel.add(user);
        }
        for (String aChannel : channel) {
            try {
                // APPEND MODE SET HERE
                String parent;
                String path;
                Calendar today = Calendar.getInstance();
                parent = "logs\\" + network + "\\" + escapePath(aChannel) + "\\" + today.get(Calendar.YEAR) + "\\";
                File parentDir = new File(parent);
                if (!parentDir.mkdirs() && !parentDir.exists()) {
                    LOGGER.error("Couldn't make dirs");
                }
                path = String.format("%02d", (today.get(Calendar.MONTH) + 1)) + "." + String.format("%02d", today.get(Calendar.DATE)) + ".txt";
                File file = new File(parent, path);
                String minute = String.format("%02d", today.get(Calendar.MINUTE));
                if (minute.length() < 2) {
                    minute = "0" + minute;
                }
                PrintWriter out;
                String logFile = String.format("%02d", today.get(Calendar.HOUR)) + ":" + minute + ":" + String.format("%02d", today.get(Calendar.SECOND)) + String.format("%1$" + 12 + "s", String.format(eventType.getVal(), user) + message);
                if (file.exists() && !file.isDirectory()) {
                    out = new PrintWriter(new FileOutputStream(file, true));
                    out.append(logFile).append(System.lineSeparator());
                    out.close();
                } else {
                    out = new PrintWriter(parent + path);
                    out.println(logFile);
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static String getSeverName(Event event) {
        return getSeverName(event, false);
    }

    public static String getSeverName(Event event, boolean trimAddress) {
        String network = event.getBot().getServerInfo().getNetwork();
        if (network == null) {
            if (event instanceof DiscordMessageEvent) {
                network = ((DiscordMessageEvent) event).getDiscordEvent().getGuild().getName();
            } else {
                network = event.getBot().getServerHostname();
                if (trimAddress) {
                    network = network.substring(network.indexOf('.') + 1, network.lastIndexOf('.'));
                }
            }
        }
        return network;
    }

    public static String escapePath(String path) {
        if (path != null) {
            char fileSep = '/'; // ... or do this portably.
            char escape = '%'; // ... or some other legal char.
            int len = path.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char ch = path.charAt(i);
                if (ch < ' ' || ch >= 0x7F || ch == fileSep || ch == '|' // add other illegal chars
                        || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
                        || ch == escape) {
                    sb.append(escape);
                    if (ch < 0x10) {
                        sb.append('0');
                    }
                    sb.append(Integer.toHexString(ch));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static void pause(int time) throws InterruptedException {
        LOGGER.debug("Sleeping for " + time + " seconds");
        Thread.sleep(time * 1000);
    }

    public synchronized static void sendError(@NotNull MessageEvent event, Throwable t) {
        sendError(event, new Exception(t));
    }

    public synchronized static void sendError(@NotNull MessageEvent event, @NotNull Exception e) {
        LOGGER.error("Error: " + e.getCause());
        String color = "";
        String cause = "";
        String from;
        if (BOOLS.get(COLOR)) {
            color = Colors.RED;
        }
        if (e.getCause() != null) {
            cause = "Error: " + e.getCause();
        }
        if (cause.isEmpty()) {
            from = "Error: " + e;
        } else {
            from = ". From " + e;
        }
        if (cause.contains("jdk.nashorn.internal.runtime.ParserException") || from.contains("javax.script.ScriptException")) {
            if (cause.contains("TypeError: Cannot read property")) {
                sendMessage(event, color + "There was a type error, Cannot read property", false);
            } else {
                if (cause.contains("\r") || cause.contains("\n")) {
                    sendMessage(event, color + cause.substring(0, cause.indexOf("\r")), false);
                } else {
                    sendMessage(event, color + cause, false);
                }
            }
        } else if (e instanceof ArrayIndexOutOfBoundsException) {
            sendMessage(event, color + "Not enough arguments, try doing the command \"COMMANDS <command>\" for help", false);
        } else {
            sendMessage(event, color + cause + from, false);
        }
        e.printStackTrace();
    }

    @SuppressWarnings("SameParameterValue")
    private synchronized void sendNotice(@NotNull GenericEvent event, String userToSendTo, String msgToSend) {
        msgToSend = getScramble(msgToSend);
        if (network == Network.discord) {
            sendMessage((DiscordMessageEvent) event, msgToSend, true);
        } else {
            event.getBot().send().notice(userToSendTo, msgToSend);
        }
    }

    private boolean checkCooldown(MessageEvent event) {
        if (commandCooldown.containsKey(event.getUser())) {
            sendNotice(event, event.getUser().getNick(), "Sorry, you have to wait " + (commandCooldown.get(event.getUser()) - System.currentTimeMillis()) + " Milliseconds for the cool down");
            return true;
        }
        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private synchronized void sendNotice(@NotNull String userToSendTo, @NotNull String msgToSend) {
        msgToSend = getScramble(msgToSend);
        bot.send().notice(userToSendTo, msgToSend);
    }

    @SuppressWarnings("SameParameterValue")
    private synchronized void sendMessage(@NotNull String userToSendTo, @NotNull String msgToSend) {
        msgToSend = getScramble(msgToSend);
        bot.send().message(userToSendTo, msgToSend);
    }

    private synchronized void makeDebug(@NotNull ConnectEvent event) {
        LOGGER.debug("Creating Debug window");
        debug = new DebugWindow(event);
        LOGGER.debug("Debug window created");
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    private synchronized void makeDebug() {
        makeDebug(debug.getConnectEvent());
    }

    private synchronized void makeDiscord() {
        if (discord == null && network != Network.discord) {
            discord = DiscordAdapter.makeDiscord(bot);
        }
    }

    public synchronized void onDisconnect(DisconnectEvent DC) {
        if (debug != null) {
            debug.dispose();
        }
    }

    public synchronized void onConnect(@NotNull ConnectEvent event) throws Exception {
        bot = event.getBot();
        bot.sendIRC().mode(bot.getNick(), "+BI");


        loadData(true);
        makeDebug(event);
        makeDiscord();
        /*boolean drawDungeon = false;
        if (drawDungeon) {
            SwingUtilities.invokeLater(() -> {
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setSize(frameWidth, frameHeight);
                frame.setVisible(true);
                frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
                frame.paintAll(frame.getGraphics());
            });
        }*/
    }

    private synchronized void sendPage(@NotNull MessageEvent event, @NotNull String[] arg, @NotNull LinkedList<String> messagesToSend) {
        try {
            LOGGER.debug("Generating page...");
            UUID name = UUID.randomUUID();
            File f = new File("Data/site/temp/" + name + ".htm");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
            bw.write("<html>");
            bw.write("<head>");
            bw.write("<link href=\"CommandStyles.css\" rel=\"stylesheet\" type=\"text/css\">");
            bw.write("<title>" + event.getUser().getNick() + ": " + bot.getNick() + "'s Command output</title>");
            bw.write("</head>");
            bw.write("<body>");
            bw.write("<h1>" + event.getUser().getNick() + ": " + argJoiner(arg, 0) + "</h1>");
            bw.write("<textarea cols=\"75\" rows=\"30\">");

            for (String aMessagesToSend : messagesToSend) {
                bw.write(aMessagesToSend);
                bw.newLine();
            }

            bw.write("</textarea>");
            bw.write("</body>");
            bw.write("</html>");

            bw.close();
            sendMessage(event, "http://lilggamegenuis.noip.me/Misc/temp/" + name + ".htm", true);
        } catch (Exception e) {
            sendError(event, e);
        }
    }

    private void setArrayOffset(@NotNull String prefix) {
        if (!BOOLS.get(ARRAY_OFFSET_SET)) {
            if (prefix.length() > 1 && !prefix.endsWith(".")) {
                arrayOffset = StringUtils.countMatches(prefix, " ");
            } else {
                arrayOffset = 0;
            }
            LOGGER.debug("Setting arrayOffset to " + arrayOffset + " based on string \"" + prefix + "\"");
            BOOLS.set(ARRAY_OFFSET_SET);
        }
    }

    private void setArrayOffset() {
        setArrayOffset(prefix);
    }

    @NotNull
    private String[] formatStringArgs(@NotNull String[] arg) {
        return trimFrontOfArray(arg, 1 + arrayOffset);
    }

    private String[] trimFrontOfArray(@NotNull String[] arg, int amount) {
        String[] ret = new String[arg.length - amount];
        try {
            System.arraycopy(arg, amount, ret, 0, ret.length);
        } catch (Exception e) {
            sendError(lastEvents.get(), e);
        }

        return ret;
    }

    @SuppressWarnings({"StatementWithEmptyBody", "ConstantConditions"})
    @Override
    public synchronized void onMessage(@NotNull MessageEvent event) {
        onMessage(event, true);
    }

    public synchronized void onMessage(@NotNull MessageEvent event, boolean log) {
        if (log) {
            log(event);
        }
        removeFromCooldown();
        checkNote(event, event.getUser().getNick(), event.getChannel().getName());
        if (debug == null) {
            if (bot == null) {
                bot = event.getBot();
            }
            makeDebug();

        }
        lastEvents.add(event);
        if (!BOOLS.get(DATA_LOADED)) {
            loadData();
        }
        if (network == Network.normal && BOOLS.get(NICK_IN_USE)) {
            if (!bot.getNick().equalsIgnoreCase(bot.getConfiguration().getName())) {
                sendNotice(event, currentNick, "Ghost detected, recovering in 10 seconds");
                new Thread(() -> {
                    try {
                        pause(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bot.sendRaw().rawLineNow("ns recover " + bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.PASSWORD));
                    bot.sendRaw().rawLineNow("ns ghost " + bot.getConfiguration().getName() + " " + CryptoUtil.decrypt(FozConfig.PASSWORD));
                    bot.sendIRC().changeNick(bot.getConfiguration().getName());
                }, "ghost-thread").start();
            }
        }
        BOOLS.clear(NICK_IN_USE);
        try {
            if (!(event.getMessage().startsWith(prefix) && event.getMessage().startsWith("."))) {
                if (event.getMessage().endsWith(".")) {
                    addWords(event.getMessage());
                } else {
                    addWords(event.getMessage() + ".");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        doCommand(event);
        saveData();
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        debug.setCurrDM(DNDDungeonMaster);
        debug.setMessage(event.getUser().getNick() + ": " + event.getMessage());

// url checker - Checks if string contains a url and parses
        try {
            String channel = event.getChannel().getName();
            String[] arg = splitMessage(event.getMessage());
            if (!commandChecker(event, arg, "checkLink", false) && BOOLS.get(CHECK_LINKS) && !((event.getUser().getNick().equalsIgnoreCase("aqua-sama") && event.getMessage().contains("taglink: https://is.gd/")))) {
                boolean containsServer = allowedCommands.get(getSeverName(event, true)) != null;
                boolean containsChannel = containsServer && allowedCommands.get(getSeverName(event, true)).get(channel) != null;
                boolean channelContains = containsChannel && allowedCommands.get(getSeverName(event, true)).get(channel).contains("url checker");
                LOGGER.trace(getSeverName(event, true) + ": containsServer: " + containsServer + " containsChannel: " + containsChannel + " channelContains: " + channelContains + " " + allowedCommands);
                if (!channelContains) {
                    // NOTES:   1) \w includes 0-9, a-z, A-Z, _
                    //             2) The leading '-' is the '-' character. It must go first in character class expression
                    final String VALID_CHARS = "-\\w+&@#/%=~()|";
                    final String VALID_NON_TERMINAL = "?!:,.;";
                    // Notes on the expression:
                    //  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
                    //  2) s? ==> the s is optional so either [http, https] accepted as scheme
                    //  3) All valid chars accepted and then one or more
                    //  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
                    final Pattern URI_FINDER_PATTERN = Pattern.compile("\\(*https?://[" + VALID_CHARS + VALID_NON_TERMINAL + "]*[" + VALID_CHARS + "]", Pattern.CASE_INSENSITIVE);

                    /**
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
                    String _rawText = event.getMessage();
                    final Matcher matcher = URI_FINDER_PATTERN.matcher(_rawText);

                    if (matcher.find()) {

                        // Counted 15 characters aside from the target + 2 of the URL (max if the whole string is URL)
                        // Rough guess, but should keep us from expanding the Builder too many times.

                        int currentStart;
                        int currentEnd;

                        String currentURL;

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
                        LOGGER.debug("Found URL - " + currentURL);
                        try {
                            String title = Jsoup.connect(currentURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2783.4 Safari/537.36").timeout(5000).get().title();
                            if (title.isEmpty()) {
                                sendMessage(event, "Title was empty", false);
                            } else if (!title.equals(lastLinkTitle)) {
                                sendMessage(event, "Title: " + title, false);
                                lastLinkTitle = title;
                            }
                        } catch (UnsupportedMimeTypeException e) {
                            try {
                                URLConnection fileURLConn = new URL(e.getUrl()).openConnection();
                                if (e.getMimeType().split("/")[0].equals("image")) {
                                    InputStream stream = fileURLConn.getInputStream();
                                    Object obj = ImageIO.createImageInputStream(stream);
                                    ImageReader reader = ImageIO.getImageReaders(obj).next();
                                    reader.setInput(obj);
                                    long fileSize = fileURLConn.getContentLength();
                                    sendMessage(event, "type: " + e.getMimeType() + " size: [Width = " + reader.getWidth(0) + ", Height = " + reader.getHeight(0) + "] File Size: " + (fileSize < 1 ? "Unknown" : formatFileSize(fileSize)), false);
                                    stream.close();
                                } else {
                                    sendMessage(event, "type: " + e.getMimeType() + " File Size: " + formatFileSize(fileURLConn.getContentLength()), false);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            sendMessage(event, "Unsupported URL", false);
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

    private synchronized void doCommand(MessageEvent event) {
        String channel = null;
        if (event.getChannel() != null) {
            channel = event.getChannel().getName();
        }
        String message = event.getMessage();
        String[] arg = splitMessage(message);

        if (message.contains(prefix) || message.contains(consolePrefix) || message.contains(bot.getNick())) {
            setArrayOffset();
            BOOLS.clear(ARRAY_OFFSET_SET);
            if (checkCooldown(event)) {
                return;
            }

            message = doChatFunctions(message);

// !getChannelName - Gets channel name, for debugging
            if (commandChecker(event, arg, "GetChannelName")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (channel != null) {
                        sendMessage(event, channel, true);
                    } else {
                        sendMessage(event, "This isn't a channel", true);
                    }
                    addCooldown(event.getUser());
                }
            }

// !checkLink
            else if (commandChecker(event, arg, "checkLink")) {
                if (checkPerm(event.getUser(), 4)) {
                    BOOLS.flip(CHECK_LINKS);
                    if (BOOLS.get(CHECK_LINKS)) {
                        sendMessage(event, "Link checking is on", false);
                    } else {
                        sendMessage(event, "Link checking is off", false);
                    }
                    addCooldown(event.getUser());
                }
            }

// !formatting - toggles COLOR (Mostly in the errors)
            else if (commandChecker(event, arg, "formatting")) {
                if (checkPerm(event.getUser(), 9001)) {
                    BOOLS.flip(COLOR);
                    if (BOOLS.get(COLOR)) {
                        sendMessage(event, "Color formatting is now On", true);
                    } else {
                        sendMessage(event, "Color formatting is now Off", true);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !HelpMe - redirect to !COMMANDS
            else if (commandChecker(event, arg, "HelpMe")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, "This command was changed to COMMANDS.", true);
                    addCooldown(event.getUser());
                }
            }

// !command - Sets what commands can be used where
            else if (commandChecker(event, arg, "command")) {
                if (checkPerm(event.getUser(), 9001)) {
                    HashMap<String, ArrayList<String>> allowedCommands = FozruciX.allowedCommands.get(getSeverName(event, true));
                    if (allowedCommands == null) {
                        allowedCommands = new HashMap<>();
                        FozruciX.allowedCommands.put(getSeverName(event, true), allowedCommands);
                    }
                    if (getArg(arg, 2) != null) {
                        byte mode = 0;
                        String chan = getArg(arg, 1);
                        for (byte i = 2; getArg(arg, i) != null; i++) {
                            String command = getArg(arg, i);
                            if (command.startsWith("-")) {
                                mode = -1;
                                command = command.substring(1, command.length());
                            } else if (command.startsWith("+")) {
                                mode = +1;
                                command = command.substring(1, command.length());
                            }
                            if (mode == -1) {
                                allowedCommands.get(chan).remove(command);
                                sendMessage(event, "Removed command ban on " + command + " For channel " + chan, true);
                            } else if (mode == 1) {
                                if (!allowedCommands.containsKey(chan)) {
                                    allowedCommands.put(chan, new ArrayList<>());
                                }
                                allowedCommands.get(chan).add(command);
                                sendMessage(event, "Added command ban on " + command + " for channel " + chan, true);
                            } else {
                                sendMessage(event, "The command " + command + " is " + (allowedCommands.get(chan).contains(command) ? "" : "not ") + "Banned from " + chan, true);
                            }
                        }
                    } else if (getArg(arg, 1) != null) {
                        sendMessage(event, allowedCommands.get(getArg(arg, 1)).toString(), true);
                    } else {
                        sendMessage(event, allowedCommands.toString(), true);
                    }
                    addCooldown(event.getUser());
                } else {
                    permErrorchn(event);
                }
            }

// !Commands - lists commands that can be used
            else if (commandChecker(event, arg, "Commands")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1) == null) {
                        sendNotice(event, event.getUser().getNick(), "List of Commands so far. for more info on these Commands do " + prefix + "Commands. Commands with \"Joke: \" are joke Commands that can be disabled");
                        sendNotice(event, event.getUser().getNick(), Arrays.asList(COMMANDS).toString());
                    } else {
                        getHelp(event, getArg(arg, 1));
                    }
                    addCooldown(event.getUser());
                }

            }

// !getBots - gets all bots
            else if (commandChecker(event, arg, "getBots")) {
                if (checkPerm(event.getUser(), 9001)) {
                    try {
                        Object[] temp = manager.getBots().toArray();
                        String bots = "";
                        for (Object aTemp : temp) {
                            String server = ((PircBotX) aTemp).getServerInfo().getNetwork();
                            if (server == null) {
                                server = ((PircBotX) aTemp).getServerHostname();
                            }
                            String nick = ((PircBotX) aTemp).getNick();
                            bots += "Server: " + server + " Nick: " + nick + " | ";
                        }
                        sendMessage(event, bots.substring(0, bots.lastIndexOf("|")), true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !addServer - adds a bot to a server
            else if (commandChecker(event, arg, "addServer")) {
                if (checkPerm(event.getUser(), 9001)) {
                    String[] args = formatStringArgs(arg);
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("addServer")
                            .description("Connects the bot to a server");
                    parser.addArgument("address").type(String.class)
                            .help("The server to connect to");
                    parser.addArgument("-k", "--key").type(String.class).setDefault((Object) null)
                            .help("The server to connect to");
                    parser.addArgument("-p", "--port").type(Integer.class).setDefault(6667)
                            .help("Sets what port to connect to");
                    parser.addArgument("-s", "--ssl").type(Boolean.class).action(Arguments.storeTrue()).setDefault(false)
                            .help("Specifies if the server port is SSL");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        LOGGER.debug(ns.toString());
                        Configuration.Builder normal = null;
                        String server = ns.getString("address");
                        int port = ns.getInt("port");
                        if (equalsAny(server.toLowerCase(), "badnik", "network", "caffie", "esper", "nova")) {
                            port = 6697;
                            if (FozConfig.debug) {
                                switch (server.toLowerCase()) {
                                    case "badnik":
                                        normal = FozConfig.debugConfig;
                                        server = FozConfig.badnik;
                                        break;
                                    case "network":
                                        normal = FozConfig.twitchDebug;
                                        server = FozConfig.twitch;
                                        break;
                                    case "caffie":
                                        normal = FozConfig.debugConfigSmwc;
                                        server = FozConfig.caffie;
                                        break;
                                    case "esper":
                                        normal = FozConfig.debugConfigEsper;
                                        server = FozConfig.esper;
                                        break;
                                    case "nova":
                                        normal = FozConfig.debugConfigNova;
                                        server = FozConfig.nova;
                                        break;
                                }
                            } else {
                                switch (ns.getString("address").toLowerCase()) {
                                    case "badnik":
                                        normal = FozConfig.normal;
                                        server = FozConfig.badnik;
                                        break;
                                    case "network":
                                        normal = FozConfig.twitchNormal;
                                        server = FozConfig.twitch;
                                        break;
                                    case "caffie":
                                        normal = FozConfig.normalSmwc;
                                        server = FozConfig.caffie;
                                        break;
                                    case "esper":
                                        normal = FozConfig.normalEsper;
                                        server = FozConfig.esper;
                                        break;
                                    case "nova":
                                        normal = FozConfig.normalNova;
                                        server = FozConfig.nova;
                                        break;
                                }
                            }
                        } else if (ns.getBoolean("ssl")) {
                            normal = new Configuration.Builder()
                                    .setEncoding(Charset.forName("UTF-8"))
                                    .setAutoReconnect(true)
                                    .setAutoReconnectAttempts(5)
                                    .setNickservPassword(CryptoUtil.decrypt(FozConfig.PASSWORD))
                                    .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                                    .setLogin(bot.getConfiguration().getLogin())
                                    .setRealName(bot.getConfiguration().getRealName())
                                    .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                                    .addListener(new FozruciX(manager, FozConfig.loadData(GSON)));
                        } else {
                            normal = new Configuration.Builder()
                                    .setEncoding(Charset.forName("UTF-8"))
                                    .setAutoReconnect(true)
                                    .setAutoReconnectAttempts(5)
                                    .setNickservPassword(CryptoUtil.decrypt(FozConfig.PASSWORD))
                                    .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                                    .setLogin(bot.getConfiguration().getLogin())
                                    .setRealName(bot.getConfiguration().getRealName())
                                    .addListener(new FozruciX(manager, FozConfig.loadData(GSON)));
                        }
                        assert normal != null;
                        manager.addBot(normal.buildForServer(server, port, ns.getString("key")));
                        sendMessage(event, "Connecting bot to " + ns.getString("address"), false);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !serverHostName - Gets the Server Host Name
            else if (commandChecker(event, arg, "serverHostName")) {
                if (checkPerm(event.getUser(), 9001)) {
                    sendMessage(event, bot.getServerHostname(), false);
                } else {
                    permErrorchn(event);
                }
            }

// !clearLogin - Clears login info to test auth related thing
            else if (commandChecker(event, arg, "clearLogin")) {
                if (checkPerm(event.getUser(), 9001)) {
                    currentNick = "Null";
                    currentUsername = "Null";
                    currentHost = "Null";
                    sendMessage(event, "Logged out", false);
                } else {
                    permErrorchn(event);
                }
            }

// !RESPOND_TO_PMS - sets whether or not to respond to PMs
            else if (commandChecker(event, arg, "RESPOND_TO_PMS")) {
                if (checkPerm(event.getUser(), 9001)) {
                    BOOLS.flip(RESPOND_TO_PMS);
                    sendMessage(event, "Responding to PMs: " + BOOLS.get(RESPOND_TO_PMS), false);
                } else {
                    permErrorchn(event);
                }
            }

// !Connect - joins a channel
            else if (commandChecker(event, arg, "Connect")) {
                if (checkPerm(event.getUser(), 9001)) {
                    bot.send().joinChannel(getArg(arg, 1));
                } else {
                    permErrorchn(event);
                }
            }

// !setDebugLevel - sets debugging level
            else if (commandChecker(event, arg, "setDebugLevel")) {
                if (checkPerm(event.getUser(), 9001)) {
                    LOGGER.setLevel(Level.toLevel(getArg(arg, 1).toUpperCase()));
                    sendMessage(event, "Set debug level to " + LOGGER.getLevel().toString(), true);
                } else {
                    permErrorchn(event);
                }
            }

// !setAvatar - sets the avatar of the bot
            else if (commandChecker(event, arg, "setAvatar")) {
                if (checkPerm(event.getUser(), 9001)) {
                    avatar = getArg(arg, 1);
                    sendMessage(event, "Avatar set", false);
                    LinkedList<User> users = new LinkedList<>();
                    for (Channel channels : bot.getUserBot().getChannels()) {
                        channels.getUsers().stream().filter(curUser -> users.indexOf(curUser) == -1 && !curUser.getNick().equalsIgnoreCase(bot.getNick())).forEach(users::add);
                    }
                    for (byte i = 0; users.size() >= i; i++) {
                        if (users.get(i).getRealName().startsWith("\u0003")) {
                            bot.send().notice(users.get(i).getNick(), "\u0001AVATAR " + avatar + "\u0001");
                        }
                    }

                } else {
                    permErrorchn(event);
                }
            }

// !loadData - force a reload of the save data
            else if (commandChecker(event, arg, "loadData")) {
                if (checkPerm(event.getUser(), 2)) {
                    loadData();
                } else {
                    permErrorchn(event);
                }
            }

// !SkipLoad - skips loading save data
            else if (commandChecker(event, arg, "SkipLoad")) {
                if (checkPerm(event.getUser(), 9001)) {
                    BOOLS.set(DATA_LOADED);
                } else {
                    permErrorchn(event);
                }
            }

// !reverseList - Reverses a list
            else if (commandChecker(event, arg, "reverseList")) {
                if (checkPerm(event.getUser(), 0)) {
                    String[] list = Arrays.copyOfRange(arg, 1, arg.length);
                    String temp = "Uh oh, something broke";
                    int i;
                    for (i = list.length - 1 + arrayOffset; i > 0; i--) {
                        temp = list[0];
                        System.arraycopy(list, 1, list, 0, i - 1);
                        list[i] = temp;
                    }
                    list[i] = temp;
                    String str = new LinkedList<>(Arrays.asList(list)).toString();
                    sendMessage(event, str, true);
                    addCooldown(event.getUser());
                }
            }

// !getDate - test get date
            else if (commandChecker(event, arg, "getDate")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        Parser parser = new Parser();
                    /*
                    long time = System.currentTimeMillis();
                    WaitForQueue queue = new WaitForQueue(bot);
                    event.getUser().send().ctcpCommand("TIME");

                    //Infinite loop since we might receive messages that aren't WaitTest's.
                    while (System.currentTimeMillis() < time+5000) {

                        //Use the waitFor() method to wait for a MessageEvent.
                        //This will block (wait) until a message event comes in, ignoring
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
                        sendMessage(event, groups.get(0).getDates().get(0).toString(), true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    LOGGER.debug(ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    addCooldown(event.getUser());
                }
            }

// !do nothing - does nothing
            else if (commandChecker(event, arg, "do")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1).equalsIgnoreCase("nothing")) {
                        if (randInt(0, 2) == 0) {
                            sendMessage(event, "no", true);
                        }
                    }
                    addCooldown(event.getUser());
                }
            }

// !markov - makes Markov chains
            else if (commandChecker(event, arg, "markov")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (markovChain == null) {
                        markovChain = new ConcurrentHashMap<>();
                    }
                    boolean loop = true;
                    String newPhrase = "";
                    try {
                        while (loop) {
                            // ArrayList to hold the phrase

                            // String for the next word
                            String nextWord = null;
                            //boolean matches = getArg(arg, 1) != null;
                            boolean matches = false;
                            int matchAttempts = 0;
                            do {
                                newPhrase = "";
                                for (int loops = 0; randInt(0, 3) == 1 && loops < 3; loops++) {
                                    if (loops > 0) {
                                        newPhrase += " ";
                                    }
                                    // Select the first word
                                    LinkedList<String> startWords = markovChain.get("_start");

                                    for (int i = 1 + arrayOffset; i < arg.length; i++) {
                                        if (startWords.contains(arg[i])) {
                                            matches = false;
                                            nextWord = startWords.get(startWords.indexOf(arg[i]));
                                        }
                                    }
                                    if (nextWord == null) {
                                        int startWordsLen = startWords.size();
                                        nextWord = startWords.get(rnd.nextInt(startWordsLen));
                                    }


                                    int greaterThanOne = nextWord.length() > 1 ? 2 : (nextWord.length() > 0 ? 1 : 0);
                                    newPhrase += nextWord.substring(0, greaterThanOne) + "\u200B" + nextWord.substring(greaterThanOne, nextWord.length());

                                    // Keep looping through the words until we've reached the end
                                    while (nextWord.charAt(nextWord.length() - 1) != '.') {
                                        List<String> wordSelection = markovChain.get(nextWord);
                                        nextWord = null;

                                        for (int i = 1; i < arg.length; i++) {
                                            if (startWords.contains(arg[i])) {
                                                matches = false;
                                                nextWord = startWords.get(startWords.indexOf(arg[i]));
                                            }
                                        }
                                        if (nextWord == null) {
                                            int wordSelectionLen = wordSelection.size();
                                            nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
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
                        sendMessage(event, "\u0002\u0002" + newPhrase, false);
                        LOGGER.debug(newPhrase.replace('\u200B', '▮'));
                    } catch (IllegalArgumentException e) {
                        sendError(event, new Exception("No words have been added to the database, Try saying something!"));
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// !8ball - ALL HAIL THE MAGIC 8-BALL
            else if (commandChecker(event, arg, "8Ball")) {
                if (checkPerm(event.getUser(), 0)) {
                    int choice = randInt(1, 20);
                    String response = "";

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
                    sendMessage(event, response, true);
                    addCooldown(event.getUser());
                }
            }

// !setMessage - Sets different message formats
            else if (commandChecker(event, arg, "setMessage")) {
                if (checkPerm(event.getUser(), 9001)) {
                    switch (getArg(arg, 1).toLowerCase()) {
                        case "normal":
                            messageMode = MessageModes.normal;
                            sendMessage(event, "Message mode set back to normal", true);
                            break;
                        case "reverse":
                            messageMode = MessageModes.reversed;
                            sendMessage(event, "Message is now reversed", true);
                            break;
                        case "wordreverse":
                            messageMode = MessageModes.wordReversed;
                            sendMessage(event, "Message words reversed", true);
                            break;
                        case "scramble":
                            messageMode = MessageModes.scrambled;
                            sendMessage(event, "Messages are scrambled", true);
                            break;
                        case "wordscramble":
                            messageMode = MessageModes.wordScrambled;
                            sendMessage(event, "Message words are scrambled", true);
                            break;
                        default:
                            sendMessage(event, "Not a message mode", true);
                    }
                } else {
                    permErrorchn(event);
                }
            }


// !CheckLink - checks links, duh
            else if (commandChecker(event, arg, "CheckLink")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        Document doc = Jsoup.connect(getArg(arg, 1)).userAgent("FozruciX").timeout(5000).get();
                        sendMessage(event, "Title: " + doc.title(), false);
                        addCooldown(event.getUser());
                    } catch (UnsupportedMimeTypeException e) {
                        sendMessage(event, "type: " + e.getMimeType(), true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !comeback - gets one of the permission error statements
            else if (commandChecker(event, arg, "comeback")) {
                if (checkPerm(event.getUser(), 0)) {
                    permErrorchn(event);
                    addCooldown(event.getUser());
                }
            }


// !Time - Tell the time
            else if (commandChecker(event, arg, "time")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        String time = new Date().toString();
                        sendMessage(event, " The time is now " + time, true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// !perms - edit privileged users
            else if (commandChecker(event, arg, "perms")) {
                if (checkPerm(event.getUser(), Integer.MAX_VALUE)) {
                    if (getArg(arg, 1).equalsIgnoreCase("set")) {
                        try {
                            if (authedUser.contains(getArg(arg, 2))) {
                                try {
                                    authedUserLevel.set(authedUser.indexOf(getArg(arg, 2)), Integer.parseInt(getArg(arg, 3)));
                                } catch (Exception e) {
                                    sendError(event, e);
                                }
                                sendMessage(event, "Set " + getArg(arg, 2) + " To level " + getArg(arg, 3), true);
                            } else {
                                try {
                                    authedUser.add(getArg(arg, 2));
                                    authedUserLevel.add(Integer.parseInt(getArg(arg, 3)));
                                } catch (Exception e) {
                                    sendError(event, e);
                                }
                                sendMessage(event, "Added " + getArg(arg, 2) + " To authed users with level " + getArg(arg, 3), true);
                            }
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("del")) {
                        try {
                            int index = authedUser.indexOf(getArg(arg, 2));
                            authedUserLevel.remove(index);
                            authedUser.remove(index);
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                        sendMessage(event, "Removed " + getArg(arg, 2) + " from the authed user list", true);
                    } else if (getArg(arg, 1).equalsIgnoreCase("clear")) {
                        authedUser.clear();
                        authedUserLevel.clear();
                        sendMessage(event, "Permission list cleared", true);
                    } else if (getArg(arg, 1).equalsIgnoreCase("List")) {
                        sendMessage(event, authedUser.toString(), true);
                    } else {
                        int place = -1;
                        try {
                            for (int i = 0; authedUser.size() >= i; i++) {
                                if (authedUser.get(i).equalsIgnoreCase(getArg(arg, 1))) {
                                    place = i;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            sendMessage(event, "That user wasn't found in the list of authed users", false);
                        }
                        if (place == -1) {
                            sendMessage(event, "That user wasn't found in the list of authed users", false);
                        } else {
                            sendMessage(event, "User " + authedUser.get(place) + " Has permission level " + authedUserLevel.get(place), false);
                        }

                    }

                } else {
                    permErrorchn(event);
                }
            }

// !CalcA - Calculates with Wolfram Alpha
            else if (commandChecker(event, arg, "CalcA")) {
                if (checkPerm(event.getUser(), 9001)) {
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
                        sendError(event, e);
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
                        LOGGER.debug("Query URL:" + engine.toURL(query));

                        // This sends the URL to the Wolfram|Alpha server, gets the XML result
                        // and parses it into an object hierarchy held by the WAQueryResult object.
                        WAQueryResult queryResult = engine.performQuery(query);

                        if (queryResult.isError()) {
                            LOGGER.error("Query error");
                            LOGGER.error("  error code: " + queryResult.getErrorCode());
                            LOGGER.error("  error message: " + queryResult.getErrorMessage());
                        } else if (!queryResult.isSuccess()) {
                            LOGGER.warn("Query was not understood; no results available.");
                        } else {
                            // Got a result.
                            LOGGER.debug("Successful query. Pods follow:\n");
                            for (WAPod pod : queryResult.getPods()) {
                                if (!pod.isError()) {
                                    LOGGER.debug(pod.getTitle());
                                    LOGGER.debug("------------");
                                    for (WASubpod subPod : pod.getSubpods()) {
                                        for (Object element : subPod.getContents()) {
                                            if (element instanceof WAPlainText) {
                                                LOGGER.debug(((WAPlainText) element).getText());
                                                LOGGER.debug("");
                                            }
                                        }
                                    }
                                    LOGGER.debug("");
                                }
                            }
                            // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                            // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
                        }
                    } catch (WAException e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// !CalcJ - calculate a expression
            else if (commandChecker(event, arg, "CalcJ")) {
                if (checkPerm(event.getUser(), 0)) {
                    String[] args = formatStringArgs(arg);
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("CalcJ")
                            .description("Calculates an expression");
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-v", "--Val").type(Double.class).setDefault(-1.0)
                            .help("Sets what the variable starts at");
                    parser.addArgument("-c", "--char").type(String.class).setDefault("x")
                            .help("Sets what character the variable is");
                    parser.addArgument("-s", "--step").type(Double.class).setDefault(1.0)
                            .help("Sets How much to increase x at");
                    parser.addArgument("-a", "--amount").type(Byte.class).setDefault(3)
                            .help("Sets How many times to increase");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        LOGGER.debug(ns.toString());
                        if (containsAny(message, "-v", "-c", "-s", "-a", "--Val", "--char", "--step", "--amount")) {
                            double x = ns.getDouble("Val");
                            double step = ns.getDouble("step");
                            byte calcAmount = ns.getByte("amount");
                            if (calcAmount > 5 && !checkPerm(event.getUser(), 8)) {
                                calcAmount = 5;
                            }
                            int count = 0;
                            LinkedList<BigDecimal> eval = new LinkedList<>();
                            while (count <= calcAmount) {
                                VARIABLE_SET.set(ns.get("char"), x);
                                //noinspection SuspiciousToArrayCall
                                eval.add(EVALUATOR.evaluate(argJoiner(ns.getList("expression").toArray(new String[]{}), 0).toLowerCase(), VARIABLE_SET));
                                x += step;
                                count++;
                            }
                            sendMessage(event, eval.toString().replace("[", "").replace("]", "").replace(", ", " | "), true);
                        } else {
                            //noinspection SuspiciousToArrayCall
                            String[] expression = ns.getList("expression").toArray(new String[]{});
                            BigDecimal eval = EVALUATOR.evaluate(argJoiner(expression, 0, 0));
                            sendMessage(event, eval.toEngineeringString(), true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// !pix - shows everyone the real face
            else if (commandChecker(event, arg, "pix")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1) != null) {
                        if (checkPerm(event.getUser(), Integer.MAX_VALUE)) {
                            char toggle = getArg(arg, 1).toLowerCase().charAt(0);
                            if (toggle == '0' || toggle == 'n') {
                                updateAvatar = false;
                                sendMessage(event, "Avatar updates are now off", true);
                            } else if (toggle == '1' || toggle == 'y') {
                                updateAvatar = true;
                                sendMessage(event, "Avatar updates are now on", true);
                            }
                        }
                        if (getArg(arg, 1).equalsIgnoreCase("upload")) {
                            sendFile(event, DiscordAdapter.avatarFile, "[Me]");
                        }
                    } else {
                        sendMessage(event, avatar + " [Me]", true);
                    }
                    addCooldown(event.getUser());
                }
            }

// !Git - gets the link to source code
            else if (commandChecker(event, arg, "Git")) {
                sendMessage(event, "Link to source code: https://github.com/lilggamegenuis/FozruciX", true);
            }

// !Bugs - gets the link to issues
            else if (commandChecker(event, arg, "Bugs")) {
                sendMessage(event, "Link to issue tracker: https://github.com/lilggamegenuis/FozruciX/issues", true);
            }

// !vgm - links to my New mix tapes :V
            else if (commandChecker(event, arg, "vgm")) {
                sendMessage(event, "Link to My smps music: https://drive.google.com/open?id=0B3aju_x5_V--ZjAyLWZEUnV1aHc", true);
            }

// !GC - Runs the garbage collector
            else if (commandChecker(event, arg, "GC")) {
                int num = gc();
                if (num == 1) {
                    sendMessage(event, "Took out the trash", true);
                } else {
                    sendMessage(event, "Took out " + num + " Trash bags", true);
                }
            }

// !JS - evaluates a expression in JavaScript
            else if (commandChecker(event, arg, "JS")) {
                if (checkPerm(event.getUser(), 0)) {
                    String[] args = formatStringArgs(splitMessage(message, 0, false));
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("JS")
                            .description("Calculates an expression");
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-b", "--base", "-r", "--radix").type(Integer.class).setDefault(10)
                            .help("Sets what radix to output to. Only applies if output is numeric");
                    parser.addArgument("-k", "--kill").type(Boolean.class).action(Arguments.storeTrue()).setDefault(false)
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        LOGGER.debug(ns.toString());
                        if (ns.getBoolean("kill")) {
                            //noinspection deprecation
                            js.stop();
                            js = null;
                            sendMessage(event, "JavaScript Thread killed", false);
                        } else {
                            if (getArg(arg, 1) != null) {
                                Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
                                    if (ex instanceof ThreadDeath) {
                                        lastJsChannel.send().message(event.getUser(), "JavaScript thread killed by " + lastJsUser.getNick() + " in " + event.getChannel().getName());
                                    } else {
                                        sendError(event, ex);
                                    }
                                };
                                if (js == null) {
                                    //noinspection SuspiciousToArrayCall
                                    js = new JavaScript(event, argJoiner(ns.getList("expression").toArray(new String[]{}), 0, 0), ns.getInt("base"));
                                    js.setUncaughtExceptionHandler(exceptionHandler);
                                    js.start();
                                } else {
                                    //noinspection SuspiciousToArrayCall
                                    js.runNewJavaScript(event, argJoiner(ns.getList("expression").toArray(new String[]{}), 0, 0), ns.getInt("base"));
                                }
                                LOGGER.debug(ns.getString("expression"));
                                lastJsUser = event.getUserHostmask();
                                if (channel != null) {
                                    lastJsChannel = event.getChannel();
                                }
                            } else {
                                sendMessage(event, "Requires more arguments", true);
                            }
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// !py - Evaluates python code
            else if (commandChecker(event, arg, "py")) {
                if (checkPerm(event.getUser(), 0)) {
                    String[] args = formatStringArgs(splitMessage(message, 0, false));
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("py")
                            .description("Calculates an expression");
                    parser.addArgument("expression").nargs("*")
                            .help("The expression to evaluate");
                    parser.addArgument("-b", "--base", "-r", "--radix").type(Integer.class).setDefault(10)
                            .help("Sets what radix to output to. Only applies if output is numeric");
                    parser.addArgument("-k", "--kill").type(Boolean.class).action(Arguments.storeTrue()).setDefault(false)
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);
                        LOGGER.debug(ns.toString());
                        if (ns.getBoolean("kill")) {
                            //noinspection deprecation
                            py.stop();
                            py = null;
                            sendMessage(event, "Python Thread killed", false);
                        } else {
                            if (getArg(arg, 1) != null) {
                                Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> {
                                    if (ex instanceof ThreadDeath) {
                                        lastJsChannel.send().message(event.getUser(), "Python thread killed by " + lastJsUser.getNick() + " in " + event.getChannel().getName());
                                    } else {
                                        sendError(event, ex);
                                    }
                                };
                                if (py == null) {
                                    //noinspection SuspiciousToArrayCall
                                    py = new Python();
                                    py.setUncaughtExceptionHandler(exceptionHandler);
                                    py.start();
                                }
                                //noinspection SuspiciousToArrayCall
                                py.runNewPython(event, argJoiner(ns.getList("expression").toArray(new String[]{}), 0, 0), ns.getInt("base"));

                                LOGGER.debug(ns.getString("expression"));
                                lastJsUser = event.getUserHostmask();
                                if (channel != null) {
                                    lastJsChannel = event.getChannel();
                                }
                            } else {
                                sendMessage(event, "Requires more arguments", true);
                            }
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

// if someone tells the bot to "Go to hell" do this
            else if (message.contains(bot.getNick()) && message.toLowerCase().contains("go to hell")) {
                if (checkPerm(event.getUser(), 0) && !checkPerm(event.getUser(), 9001)) {
                    sendMessage(event, "I Can't go to hell, i'm all out of vacation days", false);
                }
            }

// !count - counts amount of something
            else if (commandChecker(event, arg, "count")) {
                if (checkPerm(event.getUser(), 1)) {
                    if (getArg(arg, 1) != null && getArg(arg, 1).equalsIgnoreCase("setup")) {
                        counter = getArg(arg, 2);
                        if (getArg(arg, 3) != null) {
                            counterCount = Integer.parseInt(getArg(arg, 3));
                        }
                    }
                    if (commandChecker(event, arg, "count")) {
                        counterCount++;
                        sendMessage(event, "Number of times that " + counter + " is: " + counterCount, false);
                    }
                    addCooldown(event.getUser());
                } else {
                    permErrorchn(event);
                }
            }

// !StringToBytes - convert a String into a Byte array
            else if (commandChecker(event, arg, "StringToBytes")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        sendMessage(event, getBytes(argJoiner(arg, 1)), true);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        sendMessage(event, "Not enough args. Must provide a string", true);
                    }
                    addCooldown(event.getUser());
                }
            }

// !LookUpWord - Looks up a word in the Wiktionary
            else if (commandChecker(event, arg, "LookupWord")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        String lookedUpWord = "Null";
                        LOGGER.debug("Looking up word");
                        // Connect to the Wiktionary database.
                        LOGGER.debug("Opening DICTIONARY");
                        IWiktionaryEdition wkt = JWKTL.openEdition(WIKTIONARY_DIRECTORY);
                        LOGGER.debug("Getting page for word");
                        IWiktionaryPage page = wkt.getPageForWord(getArg(arg, 1));
                        if (page != null) {
                            LOGGER.debug("Getting entry");
                            IWiktionaryEntry entry;
                            if (getArg(arg, 2) != null && isNumeric(getArg(arg, 2))) {
                                entry = page.getEntry(Integer.parseInt(getArg(arg, 2)));
                            } else {
                                entry = page.getEntry(0);
                            }
                            LOGGER.debug("getting sense");
                            IWiktionarySense sense = entry.getSense(1);
                            LOGGER.debug("getting Plain text");
                            if (getArg(arg, 2) != null) {
                                int subCommandNum = 2;
                                if (isNumeric(getArg(arg, 2))) {
                                    subCommandNum++;
                                }
                                if (arg.length > subCommandNum + arrayOffset && arg[subCommandNum - 1].equalsIgnoreCase("Example")) {
                                    if (sense.getExamples().size() > 0) {
                                        lookedUpWord = sense.getExamples().get(0).getPlainText();
                                    } else {
                                        sendMessage(event, "No examples found", true);
                                    }
                                } else {
                                    lookedUpWord = sense.getGloss().getPlainText();
                                }
                            } else {
                                lookedUpWord = sense.getGloss().getPlainText();
                            }
                            LOGGER.debug("Sending message");
                            if (!lookedUpWord.isEmpty()) {
                                sendMessage(event, lookedUpWord, true);
                            } else {
                                sendMessage(event, "Empty response from Database", true);
                            }
                        } else {
                            sendMessage(event, "That page couldn't be found.", true);
                        }

                        // Close the database connection.
                        wkt.close();
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                }
            }

//!lookup - Looks up something in Wikipedia
            else if (commandChecker(event, arg, "Lookup")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1) != null) {
                        sendMessage(event, "You forgot a param ya dingus", true);
                    } else try {
                        String[] listOfTitleStrings = {argJoiner(arg, 1)};
                        info.bliki.api.User user = new info.bliki.api.User("", "", "http://en.wikipedia.org/w/api.php");
                        user.login();
                        List<Page> pages = user.queryContent(listOfTitleStrings);
                        boolean found = false;
                        while (pages.size() > 0) {
                            Page page = pages.get(0);
                            if (page.toString().contains("#REDIRECT")) {
                                LOGGER.debug("Found redirect");
                                String link = page.toString();
                                link = link.substring(link.indexOf("[[") + 2, link.indexOf("]]"));
                                LOGGER.debug("Going to " + link);
                                pages = user.queryContent(new String[]{link});
                                continue;
                            }
                            found = true;
                            WikiModel wikiModel = new WikiModel("${image}", "${title}");
                            String plainStr = page.toString();
                            LinkedList<String> related = null;
                            if (plainStr.contains(" may refer to:")) {
                                LOGGER.debug("Found disambiguation page");
                                LinkedList<String> strings = new LinkedList<>(Arrays.asList(plainStr.split("[\n]")));
                                related = new LinkedList<>();
                                boolean category = true;
                                for (int i = 0; strings.size() > i; i++) {
                                    LOGGER.trace(strings.get(i));
                                    if (wildCardMatch(strings.get(i), "==*==")) {
                                        category = false;
                                    } else if (!category) {
                                        if (!strings.get(i).isEmpty()) {
                                            related.add(strings.get(i));
                                        } else if (strings.get(i + 1).isEmpty()) {
                                            category = true;
                                        }
                                    }
                                }
                            }
                            if (related != null) {
                                plainStr = page.getTitle() + " may refer to: ";
                                for (int i = 0; i < related.size() && i < 5; i++) {
                                    plainStr += related.get(i).replace("* ", "").replace("*", "") + "; ";
                                }
                                int lastIndex = plainStr.lastIndexOf(",");
                                if (lastIndex != -1)
                                    plainStr = plainStr.substring(0, lastIndex);
                            } else {
                                plainStr = page.getCurrentContent();

                            }
                            plainStr = wikiModel.render(new PlainTextConverter(), plainStr);
                            if (related == null) {
                                LOGGER.debug(plainStr);
                                int charIndex = StringUtils.ordinalIndexOf(plainStr, ".", 2);
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
                                    sendMessage(event, "That page couldn't be found.", true);
                                } else {
                                    sendMessage(event, plainStr + ".", true);
                                }
                            }
                            break;
                        }
                        if (!found) {
                            sendMessage(event, "That page couldn't be found.", true);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        sendError(event, new Exception("Error getting data, please try again"));
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    addCooldown(event.getUser());
                } else {
                    permErrorchn(event);
                }
            }

// !chat - chat's with a internet conversation bot
            else if (commandChecker(event, arg, "chat")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1).equalsIgnoreCase("clever")) {
                        if (!BOOLS.get(CLEVER_BOT_INT)) {
                            try {
                                chatterBotSession = BOT_FACTORY.create(ChatterBotType.CLEVERBOT).createSession();
                                BOOLS.set(CLEVER_BOT_INT);
                                //noinspection ConstantConditions
                                event.getUser().send().notice("CleverBot started");
                            } catch (Exception e) {
                                sendMessage(event, "Error: Could not create clever bot session. Error was: " + e, true);
                            }
                        }
                        try {
                            sendMessage(event, " " + botTalk("clever", argJoiner(arg, 2)), true);
                        } catch (Exception e) {
                            sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("pandora")) {
                        if (!BOOLS.get(PANDORA_BOT_INT)) {
                            try {
                                pandoraBotSession = BOT_FACTORY.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477").createSession();
                                BOOLS.set(PANDORA_BOT_INT);
                                //noinspection ConstantConditions
                                event.getUser().send().notice("PandoraBot started");
                            } catch (Exception e) {
                                sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                            }
                        }
                        try {
                            sendMessage(event, " " + botTalk("pandora", argJoiner(arg, 2)), true);
                        } catch (Exception e) {
                            sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                        }
                    } else if (getArg(arg, 1).equalsIgnoreCase("jabber")) {
                        if (!BOOLS.get(JABBER_BOT_INT)) {
                            try {
                                jabberBotSession = BOT_FACTORY.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477").createSession();
                                BOOLS.set(JABBER_BOT_INT);
                                //noinspection ConstantConditions
                                event.getUser().send().notice("PandoraBot started");
                            } catch (Exception e) {
                                sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                            }
                        }
                        try {
                            sendMessage(event, " " + botTalk("clever", argJoiner(arg, 1)), true);
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                    addCooldown(event.getUser());
                }
            }

// !temp - Converts a unit of temperature to another
            else if (commandChecker(event, arg, "temp")) {
                if (checkPerm(event.getUser(), 0)) {
                    int temp = Integer.parseInt(getArg(arg, 3));
                    double ans = 0;
                    String unit = "err";
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
                        sendMessage(event, "Incorrect arguments.", true);
                    } else {
                        sendMessage(event, " " + ans + unit, true);
                    }
                    addCooldown(event.getUser());
                }
            }


// !BlockConv - Converts blocks to bytes
            else if (commandChecker(event, arg, "BlockConv")) {
                if (checkPerm(event.getUser(), 0)) {
                    int data = Integer.parseInt(getArg(arg, 3));
                    double ans = 0;
                    String unit = "err";
                    boolean notify = true;
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
                        sendMessage(event, "Incorrect arguments.", true);
                    } else {
                        sendMessage(event, " " + ans + unit, true);
                        if (notify)
                            sendMessage(event, "NOTICE: this command currently doesn't work like it should. The only conversion that works is blocks to kb and kb to blocks", true);
                    }
                    addCooldown(event.getUser());
                }
            }


// !FC - Friend code database
            else if (commandChecker(event, arg, "FC")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        if (getArg(arg, 2) != null) {
                            if (getArg(arg, 1).equalsIgnoreCase("set")) {
                                if (FCList.containsKey(event.getUser().getNick().toLowerCase())) {
                                    String fc = getArg(arg, 2).replaceAll("[^\\d]", "");
                                    if (fc.length() == 12) {
                                        FCList.put(event.getUser().getNick().toLowerCase(), fc);
                                        sendMessage(event, "FC Edited", true);
                                    } else {
                                        sendMessage(event, "Incorrect FC", true);
                                    }
                                } else {
                                    String fc = getArg(arg, 2).replaceAll("[^\\d]", "");
                                    if (fc.length() == 12) {
                                        FCList.put(event.getUser().getNick().toLowerCase(), getArg(arg, 2).replaceAll("[^\\d]", ""));
                                        sendMessage(event, "Added " + event.getUser().getNick() + "'s FC to the DB as " + getArg(arg, 2).replaceAll("[^\\d]", ""), true);
                                    } else {
                                        sendMessage(event, "Incorrect FC", true);
                                    }
                                }
                            }
                        } else {
                            if (getArg(arg, 1).equalsIgnoreCase("list")) {
                                sendMessage(event, FCList.keySet().toString(), true);

                            } else if (getArg(arg, 1).equalsIgnoreCase("del")) {
                                if (FCList.containsKey(event.getUser().getNick().toLowerCase())) {
                                    FCList.remove(event.getUser().getNick().toLowerCase());
                                    sendMessage(event, "Friend code removed", true);
                                } else {
                                    sendMessage(event, "You haven't entered your Friend code yet", true);
                                }

                            } else if (FCList.containsKey(getArg(arg, 1).toLowerCase())) {
                                String fc = FCList.get(getArg(arg, 1).toLowerCase());
                                String fcParts[] = new String[3];
                                fcParts[0] = fc.substring(0, 4);
                                fcParts[1] = fc.substring(4, 8);
                                fcParts[2] = fc.substring(8);
                                fc = fcParts[0] + "-" + fcParts[1] + "-" + fcParts[2];
                                sendMessage(event, getArg(arg, 1) + ": " + fc, false);
                            } else {
                                sendMessage(event, "That user hasn't entered their FC yet", false);
                            }
                        }
                        addCooldown(event.getUser());
                    } catch (NullPointerException e) {
                        FCList = new TreeMap<>();
                        sendMessage(event, "Try the command again", true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !sql - execute sql statements
            else if (commandChecker(event, arg, "sql")) {
                if (checkPerm(event.getUser(), 9001)) {
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:mysql://Lil-G-s_PC:3306/world?user=Lil-G&password=" + CryptoUtil.decrypt(FozConfig.PASSWORD));

                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(argJoiner(splitMessage(message, 0, false), 1));

                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        LinkedList<String> results = new LinkedList<>();
                        boolean getColumns = true;
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
                            results.add(cols.toString());
                            getColumns = false;
                        }
                        LOGGER.debug(results + " " + results.size());
                        if (results.size() < 4) {
                            for (String result : results) {
                                sendMessage(event, result, true);
                            }
                        } else {
                            sendPage(event, arg, results);
                        }
                        conn.close();
                    } catch (SQLException ex) {
                        // handle any errors
                        String exceptMsg = ex.getMessage();
                        sendMessage(event, Colors.RED + "SQLException: " + exceptMsg.substring(0, exceptMsg.indexOf(": ") + 2) + "<SUPER SECRET PASSWORD>" + exceptMsg.substring(exceptMsg.indexOf(")"), exceptMsg.length()) + " SQLState: " + ex.getSQLState() + " VendorError: " + ex.getErrorCode(), false);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !memes - Got all dem memes
            else if (commandChecker(event, arg, "memes")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1) != null) {
                        try {
                            if (getArg(arg, 1).equalsIgnoreCase("set")) {
                                if (memes.containsKey(getArg(arg, 2).toLowerCase().replace("\u0001", ""))) {
                                    Meme meme = memes.get(getArg(arg, 2).toLowerCase());
                                    if (checkPerm(event.getUser(), 9001) || meme.getCreator().equalsIgnoreCase(event.getUser().getNick())) {
                                        if (getArg(arg, 3) == null) {
                                            memes.remove(getArg(arg, 2).toLowerCase().replace("\u0001", ""));
                                            sendMessage(event, "Meme " + getArg(arg, 2) + " Deleted!", true);
                                        } else {
                                            meme.setMeme(argJoiner(arg, 3));
                                            memes.put(getArg(arg, 2).toLowerCase().replace("\u0001", ""), meme);
                                            sendMessage(event, "Meme " + getArg(arg, 2) + " Edited!", true);
                                        }
                                    } else {
                                        sendMessage(event, "Sorry, Only the creator of the meme can edit it", true);
                                    }
                                } else {
                                    memes.put(getArg(arg, 2).toLowerCase().replace("\u0001", ""), new Meme(event.getUser().getNick(), argJoiner(arg, 3).replace("\u0001", "")));
                                    sendMessage(event, "Meme " + getArg(arg, 2) + " Created as " + argJoiner(arg, 3), true);
                                }
                            } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                                sendMessage(event, memes.toString().replace("\u0001", ""), true);
                            } else {
                                if (memes.containsKey(getArg(arg, 1).toLowerCase())) {
                                    sendMessage(event, getArg(arg, 1).replace("\u0001", "") + ": " + memes.get(getArg(arg, 1).toLowerCase()).getMeme().replace("\u0001", ""), false);
                                } else {
                                    sendMessage(event, "That Meme doesn't exist!", true);
                                }
                            }
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                        addCooldown(event.getUser());
                    }
                } else {
                    sendMessage(event, "Missing arguments", true);
                }
            }

// !q - adds to q
            else if (commandChecker(event, arg, "q")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        if (getArg(arg, 1).equalsIgnoreCase("del") || getArg(arg, 1).equalsIgnoreCase("pop") || getArg(arg, 1).equalsIgnoreCase("rem")) {
                            if (qList.remove(event.getUser().getNick())) {
                                sendMessage(event, "Removed from Q", true);
                            } else {
                                sendMessage(event, "You weren't in Q!", true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("add") || getArg(arg, 1).equalsIgnoreCase("push")) {
                            if (qList.add(event.getUser().getNick())) {
                                sendMessage(event, "Added to Q", true);
                            } else {
                                sendMessage(event, "You are already in Q!", true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                            sendMessage(event, "Current people in Q: " + qList.toString().replace("[", "").replace("]", ""), true);
                        } else if (getArg(arg, 1).equalsIgnoreCase("clear")) {
                            qList.clear();
                            qTimer = new StopWatch();
                            sendMessage(event, "Q has been qilled", true);
                        } else if (getArg(arg, 1).equalsIgnoreCase("start")) {
                            qTimer = new StopWatch();
                            int time = 10;
                            if (getArg(arg, 2) != null) {
                                time = Integer.parseInt(getArg(arg, 2));
                                time = time > 60 ? 30 : time;
                            }
                            sendMessage(event, "READY?!?!1/1 " + (time != 10 ? "starting in " + time + " sec!!!! " : "") + qList.toString().replace("[", "").replace("]", ""), false);
                            pause(time);
                            sendMessage(event, "3", false);
                            pause(1);
                            sendMessage(event, "2", false);
                            pause(1);
                            sendMessage(event, "1", false);
                            pause(1);
                            sendMessage(event, "GO! " + qList.toString().replace("[", "").replace("]", ""), false);
                            qTimer = new StopWatch();
                            qTimer.start();
                        } else if (getArg(arg, 1).equalsIgnoreCase("time")) {
                            sendMessage(event, "Current time: " + qTimer.toString(), true);
                        }
                        addCooldown(event.getUser());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !NoteJ - Leaves notes
            else if (commandChecker(event, arg, "NoteJ")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        if (getArg(arg, 1).equalsIgnoreCase("del")) {
                            int i = 0;
                            int index = -1;
                            boolean found = false;
                            while (i < noteList.size() && !found) {
                                if (noteList.get(i).getId().toString().equals(getArg(arg, 2))) {
                                    found = true;
                                    index = i;
                                } else {
                                    i++;
                                }
                            }
                            if (found) {
                                if (event.getUser().getNick().equalsIgnoreCase(noteList.get(index).getSender()) || checkPerm(event.getUser(), 9001)) {
                                    noteList.remove(index);
                                    sendMessage(event, "Note " + getArg(arg, 2) + " Deleted", true);
                                } else {
                                    sendMessage(event, "Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it", true);
                                }
                            } else {
                                sendMessage(event, "That ID wasn't found.", true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("list")) {
                            int i = 0;
                            LinkedList<String> found = new LinkedList<>();
                            LinkedList<String> foundUUID = new LinkedList<>();
                            while (noteList.size() > i) {
                                if (noteList.get(i).getSender().equalsIgnoreCase(event.getUser().getNick())) {
                                    found.add(noteList.get(i).getMessageForList());
                                    foundUUID.add(noteList.get(i).getUUIDForList());
                                }
                                i++;
                            }
                            sendMessage(event, found.toString(), true);
                            event.getUser().send().notice(foundUUID.toString());
                        } else {
                            Note note = new Note(event.getUser().getNick(), getArg(arg, 1), argJoiner(arg, 2), channel);
                            noteList.add(note);
                            sendMessage(event, "Left note \"" + argJoiner(arg, 2) + "\" for \"" + getArg(arg, 1) + "\".", false);
                            event.getUser().send().notice("ID is \"" + noteList.get(noteList.indexOf(note)).getId().toString() + "\"");
                        }
                        saveData();
                        addCooldown(event.getUser());
                    } catch (StringIndexOutOfBoundsException e) {
                        sendMessage(event, Colors.RED + "You need more parameters ya dingus", true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !Hello - Standard "Hello world" command
            else if (commandChecker(event, arg, "hello")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, "Hello World!", true);
                    addCooldown(event.getUser());
                }
            }

// !Bot - Explains that "yes this is a bot"
            else if (commandChecker(event, arg, "bot")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, "Yes, this is " + currentNick + "'s bot.", true);
                    addCooldown(event.getUser());
                }
            }

// !getName - gets the name of the bot
            else if (commandChecker(event, arg, "getName")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, bot.getUserBot().getRealName(), true);
                    addCooldown(event.getUser());
                }
            }

// !version - gets the version of the bot
            else if (commandChecker(event, arg, "version")) {
                if (checkPerm(event.getUser(), 0)) {
                    String version = "PircBotX: " + PircBotX.VERSION + ". BotVersion: " + VERSION + ". Java version: " + System.getProperty("java.version");
                    sendMessage(event, "Version: " + version, true);
                    addCooldown(event.getUser());
                }
            }

// !login - attempts to login to NickServ
            else if (commandChecker(event, arg, "login")) {
                if (checkPerm(event.getUser(), 0)) {
                    bot.sendIRC().mode(bot.getNick(), "+B");
                    bot.sendIRC().identify(CryptoUtil.decrypt(FozConfig.PASSWORD));
                    bot.sendRaw().rawLineNow("cs op #Lil-G|bot " + bot.getNick());
                    bot.sendRaw().rawLineNow("cs op #Lil-G|bot Lil-G");
                    bot.sendRaw().rawLineNow("cs op #SSB " + bot.getNick());
                    bot.sendRaw().rawLineNow("cs op #SSB Lil-G");
                    bot.sendRaw().rawLineNow("ns recover FozruciX " + CryptoUtil.decrypt(FozConfig.PASSWORD));
                    addCooldown(event.getUser());
                }
            }

// !getLogin - gets the login of the bot
            else if (commandChecker(event, arg, "getLogin")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, bot.getUserBot().getLogin(), true);
                    addCooldown(event.getUser());
                }
            }

// !getID - gets the ID of the user
            else if (commandChecker(event, arg, "getID")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, "You are :" + event.getUser().getUserId(), true);
                    addCooldown(event.getUser());
                }
            }

// !RandomNum - Gives the user a random Number
            else if (commandChecker(event, arg, "RandomNum")) {
                if (checkPerm(event.getUser(), 0)) {
                    long num1, num2;
                    if (getArg(arg, 1).contains("0x")) {
                        String num = getArg(arg, 1).substring(2);
                        num1 = Long.parseLong(num, 16);
                    } else {
                        num1 = Long.parseLong(getArg(arg, 1), 10);
                    }
                    if (getArg(arg, 2).contains("0x")) {
                        String num = getArg(arg, 2).substring(2);
                        num2 = Long.parseLong(num, 16);
                    } else {
                        num2 = Long.parseLong(getArg(arg, 2), 10);
                    }
                    sendMessage(event, " " + randInt((int) num1, (int) num2), true);
                }
            }

// !getState - Displays what version the bot is on
            else if (commandChecker(event, arg, "getState")) {
                if (checkPerm(event.getUser(), 0)) {
                    sendMessage(event, "State is: " + bot.getState(), true);
                    addCooldown(event.getUser());
                }
            }

// !prefix - Changes the command prefix when it isn't the standard "!"
            else if (arg[0].equalsIgnoreCase("!prefix") && !prefix.equals("!")) {
                if (checkPerm(event.getUser(), 9001)) {
                    prefix = argJoiner(arg, 1);
                    sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
                } else {
                    permError(event.getUser());
                }
            }

// !prefix - Changes the command prefix
            else if (commandChecker(event, arg, "prefix")) {
                if (checkPerm(event.getUser(), 9001)) {
                    prefix = getArg(arg, 1);
                    if (prefix.length() > 1 && !prefix.endsWith(".")) {
                        arrayOffset = 1;
                    } else {
                        arrayOffset = 0;
                    }
                    sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
                } else {
                    permError(event.getUser());
                }
            }

// !highlightAll - Highlights everyone
            else if (commandChecker(event, arg, "highlightAll")) {
                if (checkPerm(event.getUser(), 6)) {
                    sendMessage(event, argJoiner(arg, 1), false);
                } else {
                    permErrorchn(event);
                }
            }

// !upload - Uploads a file to discord
            else if (commandChecker(event, arg, "upload")) {
                if (checkPerm(event.getUser(), 9001)) {
                    if (getArg(arg, 2) != null) {
                        sendFile(event, new File(getArg(arg, 1)), argJoiner(arg, 2));
                    } else if (getArg(arg, 1) != null) {
                        sendFile(event, new File(getArg(arg, 1)));
                    } else {
                        sendMessage(event, "Fail", true);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !SayThis - Tells the bot to say something
            else if (commandChecker(event, arg, "SayThis")) {
                if (checkPerm(event.getUser(), 5)) {
                    sendMessage(event, argJoiner(arg, 1), false);
                } else {
                    permErrorchn(event);
                }
            }

// !makeDebug - reCreates the debug Window
            else if (commandChecker(event, arg, "makeDebug")) {
                if (checkPerm(event.getUser(), 9001)) {
                    makeDebug();
                } else {
                    permErrorchn(event);
                }
            }

// !makeDiscord - reCreates the discord connection
            else if (commandChecker(event, arg, "makeDiscord")) {
                if (checkPerm(event.getUser(), 9001)) {
                    try {
                        makeDiscord();
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !LoopSay - Tells the bot to say something and loop it
            else if (commandChecker(event, arg, "LoopSay")) {
                if (checkPerm(event.getUser(), 9001)) {
                    int i = Integer.parseInt(getArg(arg, 1));
                    int loopCount = 0;
                    try {
                        while (i > loopCount) {
                            sendMessage(event, argJoiner(arg, 2), false);
                            loopCount++;
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }

                } else {
                    permErrorchn(event);
                }
            }

// !ToSciNo - converts a number to scientific notation
            else if (commandChecker(event, arg, "ToSciNo")) {
                if (checkPerm(event.getUser(), 0)) {
                    NumberFormat formatter = new DecimalFormat("0.######E0");

                    long num = Long.parseLong(getArg(arg, 1));
                    try {
                        sendMessage(event, formatter.format(num), true);
                    } catch (Exception e) {
                        sendError(event, e);
                        //log(e.toString());
                    }
                    addCooldown(event.getUser());
                }
            }

// !DND - Dungeons and dragons. RNG the Game
            else if (commandChecker(event, arg, "DND")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (commandChecker(event, arg, "DND join")) {
                        sendMessage(event, "Syntax: " + prefix + "DND join <Character name> <Race (can be anything right now)> <Class> {<Familiar name> <Familiar Species>}", true);
                    } else {
                        if (getArg(arg, 1).equalsIgnoreCase("join")) {
                            if (DNDJoined.contains(event.getUser().getNick())) {
                                sendMessage(event, "You are already in the list!", true);
                            } else {
                                if (getArg(arg, 6) != null) {
                                    if (DNDPlayer.ifClassExists(getArg(arg, 4))) {
                                        if (DNDPlayer.ifSpeciesExists(getArg(arg, 6))) {
                                            DNDList.add(new DNDPlayer(getArg(arg, 2),
                                                    getArg(arg, 3),
                                                    getArg(arg, 4),
                                                    event.getUser().getNick(),
                                                    getArg(arg, 5),
                                                    getArg(arg, 6)));
                                            DNDJoined.add(event.getUser().getNick());
                                            sendMessage(event, "Added \"" + getArg(arg, 2) + "\" the " + getArg(arg, 4) + " with " + getArg(arg, 5) + " The " + getArg(arg, 6) + " to the game", true);

                                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                                debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                                debug.setFamiliar(DNDList.get(DNDJoined.indexOf(currentNick)).getFamiliar().getName());
                                            }
                                        } else {
                                            sendMessage(event, "Class doesn't exist", true);
                                        }
                                    } else {
                                        sendMessage(event, "Class doesn't exist", true);
                                    }
                                } else if (getArg(arg, 5) != null) {
                                    if (DNDPlayer.ifClassExists(getArg(arg, 4))) {
                                        DNDList.add(new DNDPlayer(getArg(arg, 2),
                                                getArg(arg, 3),
                                                getArg(arg, 4),
                                                event.getUser().getNick()));
                                        DNDJoined.add(event.getUser().getNick());
                                        sendMessage(event, "Added \"" + getArg(arg, 2) + "\" the " + getArg(arg, 4) + " to the game", true);
                                        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                            debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                        }
                                    } else {
                                        sendMessage(event, "That class doesn't exist!", true);
                                    }
                                } else {
                                    sendMessage(event, "Syntax: " + prefix + "DND join <Character name> <Race (can be anything right now)> <Class> {<Familiar name> <Familiar Species>}", true);
                                }
                            }
                        }
                    }
                    if (getArg(arg, 1).equalsIgnoreCase("info")) {
                        try {
                            int index = DNDJoined.indexOf(event.getUser().getNick());
                            if (index > -1) {
                                setDebugInfo(event);
                                sendMessage(event, DNDList.get(index).toString(), true);
                            } else {
                                sendMessage(event, "You have to join first!", true);
                            }
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                    if (getArg(arg, 1).equalsIgnoreCase("List")) {
                        try {
                            sendMessage(event, DNDList.toString(), true);
                        } catch (Exception e) {
                            sendError(event, e);
                        }

                        setDebugInfo(event);
                    }
                    if (getArg(arg, 1).equalsIgnoreCase("ListClass")) {
                        sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDClasses.values()), true);
                    }
                    if (getArg(arg, 1).equalsIgnoreCase("ListSpecies")) {
                        sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDFamiliars.values()), true);
                    }
                    //noinspection StatementWithEmptyBody
                    if (getArg(arg, 1).equalsIgnoreCase("DM") && checkPerm(event.getUser(), 5)) {
                        DNDDungeonMaster = argJoiner(arg, 2);
                    }
                    if (getArg(arg, 1).equalsIgnoreCase("Test")) { //testing COMMANDS.
                        //checkPerm(event.getUser())
                        try {
                            int index = DNDJoined.indexOf(event.getUser().getNick());
                            if (getArg(arg, 2).equalsIgnoreCase("addItem")) {
                                DNDList.get(index).addInventory(getArg(arg, 3));
                                sendMessage(event, "Added " + getArg(arg, 3) + " to your inventory", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("getItems")) {
                                sendMessage(event, DNDList.get(index).getInventory(), true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("delItem")) {
                                DNDList.get(index).removeFromInventory(getArg(arg, 3));
                                sendMessage(event, "removed " + getArg(arg, 3) + " to your inventory", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("addXP")) {
                                DNDList.get(index).addXP(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Added " + getArg(arg, 3) + " to your XP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("addHP")) {
                                DNDList.get(index).addHP(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Added " + getArg(arg, 3) + " to your HP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("subHP")) {
                                DNDList.get(index).hit(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Subbed " + getArg(arg, 3) + " from your HP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("addXPFam")) {
                                DNDList.get(index).getFamiliar().addXP(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Added " + getArg(arg, 3) + " to your familiar's XP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("addHPFam")) {
                                DNDList.get(index).getFamiliar().addHP(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Added " + getArg(arg, 3) + " to your familiar's HP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("subHPFam")) {
                                DNDList.get(index).getFamiliar().hit(Integer.parseInt(getArg(arg, 3)));
                                sendMessage(event, "Subbed " + getArg(arg, 3) + " from your familiar's HP", true);
                            }
                            if (getArg(arg, 2).equalsIgnoreCase("getFamiliar")) {
                                sendMessage(event, DNDList.get(index).getFamiliar().toString(), true);
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("clearList")) {
                                if (checkPerm(event.getUser(), 9001)) {
                                    DNDJoined.clear();
                                    DNDList.clear();
                                    sendMessage(event, "DND Player lists cleared", false);
                                }
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("DelChar")) {
                                if (checkPerm(event.getUser(), 9001)) {
                                    if (DNDJoined.contains(getArg(arg, 3))) {
                                        DNDJoined.remove(index);
                                    }
                                }
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("setPos")) {
                                DNDDungeon.setLocation(Integer.parseInt(getArg(arg, 3)), Integer.parseInt(getArg(arg, 4)));
                                sendMessage(event, "Pos is now: " + DNDDungeon.toString(), true);

                            }

                            if (getArg(arg, 2).equalsIgnoreCase("getPos")) {
                                Point temp = DNDDungeon.getLocation();
                                sendMessage(event, "Current location: (" + temp.x + "," + temp.y + ")", true);
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("movePos")) {
                                DNDDungeon.move(Integer.parseInt(getArg(arg, 3)), Integer.parseInt(getArg(arg, 4)));
                                Point temp = DNDDungeon.getLocation();
                                sendMessage(event, "New location: (" + temp.x + "," + temp.y + ")", true);
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("getSurroundings")) {
                                int[] tiles = DNDDungeon.getSurrounding();
                                sendMessage(event, " | " + tiles[7] + " | " + tiles[0] + " | " + tiles[1] + " | ", true);
                                sendMessage(event, " | " + tiles[6] + " | " + tiles[8] + " | " + tiles[2] + " | ", true);
                                sendMessage(event, " | " + tiles[5] + " | " + tiles[4] + " | " + tiles[3] + " | ", true);
                            }

                            int frameWidth = 300;
                            int frameHeight = 300;
                            if (getArg(arg, 2).equalsIgnoreCase("genDungeon")) {
                                if (getArg(arg, 3) != null) {
                                    DNDDungeon = new Dungeon(Integer.parseInt(getArg(arg, 3)));
                                } else {
                                    DNDDungeon = new Dungeon();
                                }
                                sendMessage(event, "Generated new dungeon", true);
                                frame.dispose();
                                frame = new JFrame();
                                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                                frame.setAlwaysOnTop(true);
                                frame.setSize(frameWidth, frameHeight);
                                frame.setVisible(true);
                                frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
                                frame.paintAll(frame.getGraphics());
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("draw")) {
                                frame.dispose();
                                frame = new JFrame();
                                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                                frame.setAlwaysOnTop(true);
                                frame.setSize(frameWidth, frameHeight);
                                frame.setVisible(true);
                                frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
                                frame.paintAll(frame.getGraphics());
                            }

                            if (getArg(arg, 2).equalsIgnoreCase("upload")) {
                                if (event instanceof DiscordMessageEvent) {
                                    BufferedImage bi = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                    Graphics2D ig2 = bi.createGraphics();
                                    new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()).paint(ig2);
                                    File output = new File("./output_image.png");
                                    if (ImageIO.write(bi, "PNG", output)) {
                                        ((DiscordMessageEvent) event).getDiscordEvent().getTextChannel().sendFile(output, null);
                                    } else {
                                        sendMessage(event, "Looks like something broke...", true);
                                    }
                                } else {
                                    sendMessage(event, "Sorry, this command only works on discord", true);
                                }

                            }
                        } catch (NullPointerException e) {
                            sendMessage(event, "You have to join first! (Null pointer)", true);
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                    setDebugInfo(event);
                    addCooldown(event.getUser());
                }
            }

// !S1TCG - Create Title card info
            else if (commandChecker(event, arg, "S1TCG")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (getArg(arg, 1) != null) {
                        try {
                            List<String> title = S1TCG.process(formatStringArgs(arg));
                            if (title.size() < 7) {
                                for (String aTitle : title) {
                                    sendNotice(event, event.getUser().getNick(), aTitle);
                                }
                            } else {
                                sendPage(event, arg, new LinkedList<>(title));
                            }
                            addCooldown(event.getUser());
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    } else {
                        sendNotice(event, event.getUser().getNick(), "Incorrect arguments");
                        getHelp(event, "s1tcg");
                    }
                }
            }

// !acc/68kcyc/asmcyclecounter - counts asm cycles
            else if (commandChecker(event, arg, "acc") || commandChecker(event, arg, "68kcyc") || commandChecker(event, arg, "asmcyclecounter")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        Process process = new ProcessBuilder("asmcyclecount\\asmCycleCount.exe", "t", "t", "\t" + argJoiner(arg, 1).replace("||", "\r\n\t").replace("//", "\r\n\t")).start();
                        //noinspection StatementWithEmptyBody
                        process.waitFor();
                        Scanner s = new Scanner(process.getInputStream());
                        LinkedList<String> output = new LinkedList<>();
                        while (s.hasNext()) {
                            output.add(s.nextLine());
                        }
                        output = new LinkedList<>(output.subList(0, output.size() / 2));
                        LOGGER.debug(output.toString());
                        if (output.size() > 3) {
                            sendPage(event, arg, output);
                        } else {
                            for (String anOutput : output) {
                                sendMessage(event, anOutput.replace('\t', ' ').replace(";", "  ;"), true);
                            }
                        }
                        addCooldown(event.getUser());
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !disasm - disassembles machine code for the specified CPU
            else if (commandChecker(event, arg, "disasm")) {
                if (checkPerm(event.getUser(), 0)) {
                    String byteStr = argJoiner(arg, 2).replace(" ", "");
                    byte[] bytes = DatatypeConverter.parseHexBinary(byteStr);
                    try (FileOutputStream fos = new FileOutputStream("Data\\temp.68k")) {
                        BufferedWriter delFile = new BufferedWriter(new FileWriter("Data\\temp.asm", false));
                        delFile.close();
                        fos.write(bytes);
                        String processor = getArg(arg, 1);
                        if (processor.equalsIgnoreCase("M68K")) {
                            processor = "68000";
                        } else if (processor.equalsIgnoreCase("x86")) {
                            processor = "8086";
                        }
                        Process process = new ProcessBuilder("C:\\Program Files (x86)\\IDA 6.8\\idaq", "-B", "-p" + processor, "data\\temp.68k").start();
                        //noinspection StatementWithEmptyBody
                        process.waitFor();
                        BufferedReader disasm = new BufferedReader(new FileReader("Data\\temp.asm"));
                        String disasmTemp;
                        boolean fileIsEmpty = true;
                        LinkedList<String> messagesToSend = new LinkedList<>();
                        while ((disasmTemp = disasm.readLine()) != null) {
                            if (!disasmTemp.startsWith(";") &&          // Check for comments
                                    !disasmTemp.startsWith(" #") &&
                                    !disasmTemp.startsWith("#") &&
                                    !disasmTemp.toLowerCase().contains("end") &&     //Check for various metadata instructions
                                    !disasmTemp.toLowerCase().contains("seg000") &&
                                    !disasmTemp.contains(".text") &&
                                    !disasmTemp.contains(".set") &&
                                    !disasmTemp.contains(".model") &&
                                    !disasmTemp.contains(".8086") &&
                                    !disasmTemp.contains("segment") &&
                                    !disasmTemp.contains(".686p") &&
                                    !disasmTemp.contains(".mmx") &&
                                    !disasmTemp.contains("assume") &&
                                    !disasmTemp.contains(".section") &&
                                    !disasmTemp.isEmpty()) {
                                messagesToSend.add(disasmTemp.replace("\t", " "));
                                if (fileIsEmpty) {
                                    fileIsEmpty = false;
                                }
                            }
                        }
                        disasm.close();
                        if (fileIsEmpty) {
                            sendMessage(event, "Processor is either not supported or some other error has occurred: Empty File", true);
                        } else {
                            if (messagesToSend.size() > 3) {
                                sendPage(event, arg, messagesToSend);
                            } else {
                                for (String aMessagesToSend : messagesToSend) {
                                    sendMessage(event, aMessagesToSend, true);
                                }
                            }
                        }
                        addCooldown(event.getUser());
                    } catch (IllegalArgumentException e) {
                        sendMessage(event, "Arguments have to be a Hexadecimal number: " + e.getCause(), true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !Trans - Translate from 1 language to another
            else if (commandChecker(event, arg, "trans")) {
                if (checkPerm(event.getUser(), 0)) {
                    String text;
                    LOGGER.debug("Setting key");
                    YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                    String[] args = formatStringArgs(arg);
                    ArgumentParser parser = ArgumentParsers.newArgumentParser("Trans")
                            .description("Translates from one language to another");
                    parser.addArgument("text").nargs("*")
                            .help("Text to translate");
                    parser.addArgument("-t", "--to").type(String.class).setDefault("English")
                            .help("Sets language to translate to");
                    parser.addArgument("-f", "--from").type(String.class).setDefault("detect")
                            .help("Sets language to translate from");
                    parser.addArgument("-d", "--detect").type(Boolean.class).action(Arguments.storeTrue()).setDefault(false)
                            .help("Kill the thread");
                    Namespace ns;
                    try {
                        ns = parser.parseArgs(args);

                        LOGGER.debug(ns.toString());
                        if (ns.getBoolean("detect")) {
                            sendMessage(event, fullNameToString(Detect.execute(ns.getString("text"))), true);
                        } else {
                            //noinspection SuspiciousToArrayCall
                            String textToTrans = argJoiner(ns.getList("text").toArray(new String[]{}), 0);
                            Language to = Language.valueOf(ns.getString("to").toUpperCase());
                            Language from;
                            if (ns.getString("from").equals("detect")) {
                                from = Detect.execute(textToTrans);
                            } else {
                                from = Language.valueOf(ns.getString("from").toUpperCase());
                            }
                            text = Translate.execute(textToTrans, from, to);
                            LOGGER.debug("Translating: " + text);
                            sendMessage(event, text, true);
                        }
                        addCooldown(event.getUser());
                    } catch (IllegalArgumentException e) {
                        sendError(event, new Exception("That Language doesn't exist!"));
                    } catch (ArgumentParserException e) {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            PrintWriter ps = new PrintWriter(new PrintStream(baos, true, "utf-8"));
                            parser.printHelp(ps);
                            String content = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
                            LOGGER.debug(content);
                            String[] lines = splitMessage(content, 0, false);
                            sendMessage(event, Arrays.toString(lines), true);
                        } catch (Exception ex) {
                            sendError(event, ex);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !BadTrans - Translate from english to.... english... badly
            else if (commandChecker(event, arg, "BadTrans")) {
                if (checkPerm(event.getUser(), 0)) {
                    String text;
                    LOGGER.debug("Setting key");
                    YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                    try {
                        if (getArg(arg, 1) != null) {
                            text = argJoiner(arg, 1);
                            System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.ENGLISH, Language.JAPANESE);
                            System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.JAPANESE, Language.VIETNAMESE);
                            System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.VIETNAMESE, Language.CHINESE);
                            System.out.print("Translating: " + text + " - ");
                            text = Translate.execute(text, Language.CHINESE, Language.ENGLISH);
                            LOGGER.debug("Translating: " + text);
                            sendMessage(event, text, true);
                        } else {
                            sendMessage(event, ">_>", true);
                        }
                        addCooldown(event.getUser());
                    } catch (IllegalArgumentException e) {
                        sendError(event, new Exception("That class doesn't exist!"));
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !DebugVar - changes a variable to the value
            else if (commandChecker(event, arg, "DebugVar")) {
                if (checkPerm(event.getUser(), 9001)) {
                    switch (getArg(arg, 1).toLowerCase()) { //Make sure strings are lowercase
                        case "i":
                            int i = Integer.parseInt(getArg(arg, 2));
                            sendMessage(event, "DEBUG: Var \"i\" is now \"" + i + "\"", true);
                            break;
                        case "jokenum":
                            jokeCommandDebugVar = Integer.parseInt(getArg(arg, 2));
                            sendMessage(event, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"", true);
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !cmd - Tells the bot to run a OS command
            else if (commandChecker(event, arg, "cmd")) {
                if (checkPerm(event.getUser(), 9001)) {
                    try {
                        if (getArg(arg, 1).equalsIgnoreCase("stop")) {
                            sendMessage(event, "Stopping", true);
                            singleCMD.interrupt();
                        } else {
                            try {
                                singleCMD = new CMD(event, arg);
                                singleCMD.start();
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else {
                    permError(event.getUser());
                }
            }

// > - runs COMMANDS without closing at the end
            else if (arg[0].startsWith(consolePrefix)) {
                if (checkPerm(event.getUser(), 9001)) {
                    if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "start")) {
                        terminal.interrupt();
                        terminal = new CommandLine(event, trimFrontOfArray(arg, 1));
                        terminal.start();
                        sendMessage(event, "Command line started", false);
                    } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "close")) {
                        terminal.doCommand(event, "exit");
                    } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "stop")) {
                        terminal.interrupt();
                    } else if (arg[0].substring(consolePrefix.length()).equalsIgnoreCase(consolePrefix + "prefix")) {
                        consolePrefix = arg[1];
                        sendMessage(event, "Console Prefix is now " + consolePrefix, true);
                    } else {
                        String command = message.substring(1);
                        terminal.doCommand(event, command);
                        LOGGER.debug("Running " + command);
                    }
                }
            } else if (arg[0].equalsIgnoreCase("\\\\prefix")) {
                if (checkPerm(event.getUser(), 9001)) {
                    consolePrefix = arg[1];
                    sendMessage(event, "Console Prefix is now " + consolePrefix, true);
                }
            }

// !SayRaw - Tells the bot to send a raw line
            else if (commandChecker(event, arg, "SayRaw")) {
                if (checkPerm(event.getUser(), 9001)) {
                    bot.sendRaw().rawLineNow(argJoiner(arg, 1));
                } else {
                    permErrorchn(event);
                }
            }

// !SayNotice - Tells the bot to send a notice
            else if (commandChecker(event, arg, "SayNotice")) {
                if (checkPerm(event.getUser(), 6)) {
                    bot.sendIRC().notice(getArg(arg, 1), argJoiner(arg, 2));
                } else {
                    permErrorchn(event);
                }
            }

// !SayCTCPCommand - Tells the bot to send a CTCP Command
            else if (commandChecker(event, arg, "SayCTCPCommand")) {
                if (checkPerm(event.getUser(), 9001)) {
                    bot.sendIRC().ctcpCommand(getArg(arg, 1), argJoiner(arg, 2));
                } else {
                    permErrorchn(event);
                }
            }

//// !SayMethod - Tells the bot to run a method
//		if (arguments[0].equalsIgnoreCase(prefix + "sayMethod")){
//			if(checkPerm(event.getUser())){
//				sendRawLineViaQueue(arguments[1]);
//			}
//			else {
//				permErrorchn(event, "can use this command");
//			}
//		}

// !leave - Tells the bot to leave the current channel
            else if (commandChecker(event, arg, "leave")) {
                if (checkPerm(event.getUser(), 5)) {
                    if (!commandChecker(event, arg, "leave")) {
                        event.getChannel().send().part(argJoiner(arg, 2));
                    } else {
                        event.getChannel().send().part("Ugh... Why do i always get the freaks...");
                    }
                } else if (network != Network.twitch) {
                    permErrorchn(event);
                }
            }

// !ReVoice - gives everyone voice if they didn't get it
            else if (commandChecker(event, arg, "ReVoice")) {
                for (User user1 : event.getChannel().getUsers()) {
                    user1.send().mode("+v");
                }
            }

// !kill - Tells the bot to disconnect from server and exit
            else if (commandChecker(event, arg, "kill")) {
                if (checkPerm(event.getUser(), 9001)) {
                    //noinspection ConstantConditions
                    saveData();
                    event.getUser().send().notice("Disconnecting from server and exiting");
                    try {
                        Thread exit = new Thread(() -> {
                            if (getArg(arg, 1) != null) {
                                manager.stop(argJoiner(arg, 1));
                            } else {
                                manager.stop("I'm only a year old and have already wasted my entire life.");
                            }
                            try {
                                pause(1);
                                System.exit(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, "Exit-thread");
                        exit.start();
                        //noinspection StatementWithEmptyBody
                        pause(5);
                    } catch (Exception e) {
                    }
                    System.exit(0);
                } else {
                    permErrorchn(event);
                }
            }

// !quitServ - Tells the bot to disconnect from server
            else if (commandChecker(event, arg, "quitServ")) {
                if (checkPerm(event.getUser(), 9001)) {
                    //noinspection ConstantConditions
                    saveData();
                    event.getUser().send().notice("Disconnecting from server");
                    if (getArg(arg, 1) != null) {
                        bot.sendIRC().quitServer(argJoiner(arg, 1));
                    } else {
                        bot.sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                    }
                    bot.stopBotReconnect();
                } else {
                    permErrorchn(event);
                }
            }


// !respawn - Tells the bot to restart and reconnect
            else if (commandChecker(event, arg, "respawn")) {
                if (checkPerm(event.getUser(), 5)) {
                    saveData();
                    bot.sendIRC().quitServer("Died! Respawning in about 5 seconds");
                } else {
                    permErrorchn(event);
                }
            }

// !recycle - Tells the bot to part and rejoin the channel
            else if (commandChecker(event, arg, "recycle")) {
                if (checkPerm(event.getUser(), 2)) {
                    saveData();
                    event.getChannel().send().cycle();
                    addCooldown(event.getUser());
                } else {
                    permErrorchn(event);
                }
            }

// !getUserLevels - gets the user levels of the user
            else if (commandChecker(event, arg, "getUserLevels")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (channel == null) {
                        return;
                    }
                    try {
                        List<UserLevel> userLevels = Lists.newArrayList(event.getUser().getUserLevels(event.getChannel()).iterator());
                        sendMessage(event, userLevels.toString(), true);
                        addCooldown(event.getUser());
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !getCpu - Gets info about CPU
            else if (commandChecker(event, arg, "getCpu")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        String processorTime = getWMIValue("Select PercentProcessorTime from Win32_PerfFormattedData_PerfOS_Processor ", "Name");
                        sendMessage(event, "Processor time: " + processorTime, true);
                        addCooldown(event.getUser());
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !getBat - Gets info about battery
            else if (commandChecker(event, arg, "getBat")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        String statuses[] = {"discharging",
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
                        int batteryStatus = Integer.parseInt(getWMIValue("Select BatteryStatus from Win32_Battery", "BatteryStatus"));
                        String batteryPercentRemaining = getWMIValue("Select EstimatedChargeRemaining from Win32_Battery", "EstimatedChargeRemaining");
                        sendMessage(event, "Remaining battery: " + batteryPercentRemaining + "% Battery status: " + statuses[batteryStatus], true);
                        addCooldown(event.getUser());
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !getMem - Gets various info about memory
            else if (commandChecker(event, arg, "getMem")) {
                if (checkPerm(event.getUser(), 0)) {
                    Runtime runtime = Runtime.getRuntime();
                    String send = "Current memory usage: " + formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + formatFileSize(runtime.totalMemory()) + ". Total memory that can be used: " + formatFileSize(runtime.maxMemory()) + ".  Active Threads: " + Thread.activeCount() + "/" + ManagementFactory.getThreadMXBean().getThreadCount() + ".  Available Processors: " + runtime.availableProcessors();
                    sendMessage(event, send, false);
                    addCooldown(event.getUser());
                }
            }

// !getDiscordStatus - does what it says
            else if (commandChecker(event, arg, "getDiscordStatus")) {
                if (checkPerm(event.getUser(), 0)) {
                    try {
                        sendMessage(event, DiscordAdapter.getJda().getStatus().toString(), false);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            }

// !jniTest - Test method with JNI
            else if (commandChecker(event, arg, "jniTest")) {
                if (checkPerm(event.getUser(), 0)) {
                    LOGGER.trace("Starting test");
                    jniTest();
                    addCooldown(event.getUser());
                }
            }

// !ChangeNick - Changes the nick of the bot
            else if (commandChecker(event, arg, "changeNick")) {
                if (checkPerm(event.getUser(), 9001)) {
                    if (event instanceof DiscordMessageEvent) {
                        DiscordAdapter.getJda().getAccountManager().setNickname(((DiscordMessageEvent) event).getDiscordEvent().getGuild(), getArg(arg, 1));
                    } else {
                        bot.sendIRC().changeNick(getArg(arg, 1));
                        debug.setNick(getArg(arg, 1));
                    }
                } else {
                    permErrorchn(event);
                }
            }

// !SayAction - Makes the bot do a action
            else if (commandChecker(event, arg, "SayAction")) {
                if (checkPerm(event.getUser(), 9001)) {
                    event.getChannel().send().action(argJoiner(arg, 1));
                } else {
                    permErrorchn(event);
                }
            }

// !jToggle - toggle joke COMMANDS
            else if (commandChecker(event, arg, "jToggle")) {
                if (getArg(arg, 1).equalsIgnoreCase("toggle")) {
                    if (checkPerm(event.getUser(), 2)) {
                        BOOLS.flip(JOKE_COMMANDS);
                        if (BOOLS.get(JOKE_COMMANDS)) {
                            sendMessage(event, "Joke COMMANDS are now enabled", true);
                        } else {
                            sendMessage(event, "Joke COMMANDS are now disabled", true);
                        }
                    } else {
                        permErrorchn(event);
                    }
                } else {
                    if (checkPerm(event.getUser(), 0)) {
                        if (BOOLS.get(JOKE_COMMANDS)) {
                            sendMessage(event, "Joke COMMANDS are currently enabled", true);
                        } else {
                            sendMessage(event, "Joke COMMANDS are currently disabled", true);
                        }
                    }
                }
            }

// !sudo/make me a sandwich - You should already know this joke
            else if (commandChecker(event, arg, "make me a sandwich")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        sendMessage(event, "No, make one yourself", false);
                        addCooldown(event.getUser());
                    } else {
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                    }
                }
            } else if (commandChecker(event, arg, "sudo make me a sandwich")) {
                if (checkPerm(event.getUser(), 9001)) {
                    sendMessage(event, "Ok", false);
                    addCooldown(event.getUser());
                } else {
                    sendMessage(event, "This command requires root permissions", true);
                }
            }

// !Splatoon - Joke command - ask the splatoon question
            else if (commandChecker(event, arg, "Splatoon")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        sendMessage(event, " YOU'RE A KID YOU'RE A SQUID", true);
                        addCooldown(event.getUser());
                    } else {
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                    }
                }
            }

// !attempt - Joke command - NOT ATTEMPTED
            else if (commandChecker(event, arg, "attempt")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        sendMessage(event, " NOT ATTEMPTED", true);
                        addCooldown(event.getUser());
                    } else {
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                    }
                }
            }

//// !stfu - Joke command - say "no u"
//		if (commandChecker(event, arg, "stfu")){
//			if(BOOLS[JOKE_COMMANDS] || checkPerm(event.getUser()))
//				sendMessage(event, sender + ": " + prefix + "no u");
//		}

// !EatABowlOfDicks - Joke command - joke help command
            else if (commandChecker(event, arg, "EatABowlOfDicks")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        sendMessage(event, "no u", true);
                        addCooldown(event.getUser());
                    }
                }
            }

// !eat a bowl of dicks - Joke command - joke help command
            else if (commandChecker(event, arg, "eat")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        if ((getArg(arg, 0) + getArg(arg, 1) + getArg(arg, 2) + getArg(arg, 3)).equalsIgnoreCase("EatABowlOfDicks")) {
                            sendMessage(event, "no u", true);
                            addCooldown(event.getUser());
                        }
                    }
                }
            }

// !my  - Joke command - This was requested by Greeny in #origami64. ask him about it
            else if (commandChecker(event, arg, "my")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        if (getArg(arg, 1).equalsIgnoreCase("DickSize")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "8" + StringUtils.leftPad("D", size, "=") + " - " + size, true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("vaginaDepth")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, Color.RED + "Error: IntegerOutOfBoundsException: Less than Integer.MIN_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "|" + StringUtils.leftPad("{0}", size, "=") + " -  -" + size, true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("BallCount")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, StringUtils.leftPad("", size, "8") + " - " + size, true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("xdLength")) {
                            int size = randInt(0, jokeCommandDebugVar);
                            sendMessage(event, "X" + StringUtils.leftPad("", size, "D") + " - " + size, true);
                        } else if (getArg(arg, 1).equalsIgnoreCase("ass")) {
                            sendMessage(event, "No.", true);
                        } else if (getArg(arg, 1).equalsIgnoreCase("iq")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                sendMessage(event, "Your IQ is: " + randInt(0, jokeCommandDebugVar * 10), true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("powerLevel")) {
                            if (checkPerm(event.getUser(), 9001)) {
                                sendMessage(event, "Its OVER 9000!!!!", true);
                            } else {
                                sendMessage(event, "The scouter says their power level is... " + randInt(-1, 9000) + "!", true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("bullshit")) {
                            String[] listOfBullshit = {"Not bullshit", "A little bullshit", "Almost not bullshit", "Normal bullshit", "Put on your boots", "High bullshit", "Get the shovel", "Total bullshit", "Get a vest", "Holy shit", "Super bullshit", "Fucking bullshit", "GET IN THE LIFE BOATS"};
                            if (getArg(arg, 2) != null) {
                                sendMessage(event, "Bullshit reading: " + listOfBullshit[hash(argJoiner(arg, 2), listOfBullshit.length)], true);
                            } else {
                                sendMessage(event, "Bullshit reading: " + listOfBullshit[randInt(0, listOfBullshit.length - 1)], true);
                            }
                        } else if (getArg(arg, 1).equalsIgnoreCase("help")) {
                            sendMessage(event, "Current things you can measure are: DickSize, VaginaDepth, BallCount, xdLength, ass (>_>), iq, powerLevel, bullShit", true);

                        }
                        addCooldown(event.getUser());
                    }
                }
            }


// !potato - Joke command - say "i am potato" in Japanese
            else if (commandChecker(event, arg, "potato")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        byte[] bytes = "わたしわポタトデス".getBytes(Charset.forName("UTF-8"));
                        String v = new String(bytes, Charset.forName("UTF-8"));
                        sendMessage(event, v, true);
                    } else
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                }
            }

// !WhatIs? - Joke command -
            else if (commandChecker(event, arg, "WhatIs?")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        int num = randInt(0, DICTIONARY.length - 1);
                        String comeback = String.format(DICTIONARY[num], argJoiner(arg, 1));
                        sendMessage(event, comeback, true);
                        addCooldown(event.getUser());
                    } else
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                }
            }

// !rip - Joke command - never forgetti the spaghetti
            else if (commandChecker(event, arg, "rip")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        if (getArg(arg, 1).equalsIgnoreCase(currentNick)) {
                            sendMessage(event, currentNick + " Will live forever!", false);
                        } else if (getArg(arg, 1).equalsIgnoreCase(bot.getNick())) {
                            sendMessage(event, ">_>", false);
                        } else {
                            sendMessage(event, "Rest in spaghetti, never forgetti. May the pasta be with " + argJoiner(arg, 1), false);
                        }
                    } else
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);
                }
            }

// !GayDar - Joke command - picks random user
            else if (commandChecker(event, arg, "GayDar")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (channel == null) {
                        sendMessage(event, "Its you", true);
                        return;
                    }
                    if (BOOLS.get(JOKE_COMMANDS) || checkPerm(event.getUser(), 1)) {
                        Iterator<User> user = event.getChannel().getUsers().iterator();
                        LinkedList<String> userList = new LinkedList<>();
                        while (user.hasNext()) {
                            userList.add(user.next().getNick());
                        }
                        int num = randInt(0, userList.size());
                        boolean notMe = true;
                        while (notMe) {
                            if (userList.get(num).equalsIgnoreCase(currentNick)) {
                                notMe = false;
                                num = randInt(0, userList.size());
                            } else {
                                notMe = false;
                            }
                        }
                        try {
                            sendMessage(event, "It's " + userList.get(num).toUpperCase() + "!", false);
                            addCooldown(event.getUser());
                        } catch (Exception e) {
                            sendMessage(event, "Error: " + e, false);
                        }
                    } else
                        sendMessage(event, " Sorry, Joke COMMANDS are disabled", true);

                    LOGGER.debug(event.getMessage());
                }
            }
        }

// s/*/*[/g] - sed
        if (arg[0].toLowerCase().startsWith("s/")) {
            if (checkPerm(event.getUser(), 0)) {
                if (channel == null) {
                    sendMessage(event, "This is for channels", true);
                    return;
                }
                HashMap<String, ArrayList<String>> map = allowedCommands.get(getSeverName(event, true));
                ArrayList commands;
                if (map != null) {
                    commands = map.get(channel);
                } else {
                    commands = null;
                }
                if (commands != null && commands.contains("sed")) {
                    sendNotice(event, event.getUser().getNick(), "Sorry, you can't use that command here");
                } else {
                    String[] msg = message.split("/");
                    if (msg.length > 2) {
                        if (!msg[1].isEmpty() || !msg[1].equals(".")) {
                            String find = msg[1];
                            String replace = msg[2];
                            boolean replaceAll = msg.length > 3 && msg[3].toLowerCase().startsWith("g");
                            for (int i = lastEvents.size() - 1; i >= 0; i--) {
                                MessageEvent last = lastEvents.get(i);
                                if (last.equals(event) || wildCardMatch(last.getMessage(), "s/*/*")) continue;
                                if (last.getChannel().equals(event.getChannel())) {
                                    String lastMessage = last.getMessage();
                                    if (message.contains(find)) {
                                        if (replaceAll) {
                                            lastMessage = lastMessage.replace(find, replace);
                                        } else {
                                            lastMessage = lastMessage.replaceFirst(find, replace);
                                        }
                                        sendMessage(event, "What " + last.getUser().getNick() + " meant to say was: " + lastMessage, false, false);
                                        addCooldown(event.getUser(), 15);
                                        return;
                                    }
                                }
                            }
                        } else {
                            sendMessage(event, "Sorry, we ain't having none of that spam shit", true);
                        }
                    }
                }
            }
        }
    }

    private String doChatFunctions(String message) {
        if (!wildCardMatch(message, "[$*(*)]")) {
            return message;
        }
        String[] chatFunctions = splitMessage(message, 0, false);
        StringBuilder returnStr = new StringBuilder();
        for (String possibleFunction : chatFunctions) {
            if (wildCardMatch(possibleFunction, "[$char(*)]")) {
                String sub = possibleFunction.substring(possibleFunction.indexOf('(') + 1, possibleFunction.indexOf(')'));
                int charVal = Integer.parseInt(sub);
                char character = (char) charVal;
                possibleFunction = character + "";
            }
            returnStr.append(possibleFunction).append(" ");
        }
        return returnStr.substring(0, returnStr.length() - 1);
    }

    @Override
    public synchronized void onPart(PartEvent part) {
        log(part);
    }

    @Override
    public synchronized void onPrivateMessage(@NotNull PrivateMessageEvent PM) {
        String[] arg = splitMessage(PM.getMessage());

// !rps - Rock! Paper! ehh you know the rest
        if (commandChecker(PM, arg, "rps")) {
            //noinspection ConstantConditions
            String nick = PM.getUser().getNick();
            if (checkPerm(PM.getUser(), 9001)) {
                boolean found = false;
                boolean isFirstPlayer = true;
                RPSGame game = null;
                int i = 0;
                for (; i > RPS_GAMES.size(); i++) {
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
                    RPS_GAMES.add(new RPSGame(PM.getUser().getNick(), getArg(arg, 1)));
                    PM.getUser().send().notice("Created a game with " + getArg(arg, 1));
                } else {
                    PM.getUser().send().notice("You aren't in a game!");
                }
            }
        }
// !rejoin - Rejoins all channels
        else if (PM.getMessage().equalsIgnoreCase(prefix + "rejoin")) {
            ImmutableList<String> autoChannels = bot.getConfiguration().getAutoJoinChannels().keySet().asList();
            for (int i = 0; i < autoChannels.size(); i++) {
                bot.send().joinChannel(autoChannels.get(i));
            }
        }

// !login - Sets the authed named to the new name ...if the password is right
        else if (commandChecker(PM, arg, "login")) {
            if (CryptoUtil.encrypt(getArg(arg, 1)).equals(FozConfig.PASSWORD)) {
                currentNick = PM.getUser().getNick();
                currentUsername = PM.getUser().getLogin();
                currentHost = PM.getUser().getHostname();
                PM.getUser().send().message("Welcome back Lil-G");
            } else
                PM.getUser().send().message("password is incorrect.");
        }
        //Only allow me (Lil-G) to use PM COMMANDS except for the login command
        else if (checkPerm(PM.getUser(), 6)) {


// !SendTo - Tells the bot to say something on a channel
            if (commandChecker(PM, arg, "SendTo")) {
                bot.sendIRC().message(getArg(arg, 1), getScramble(argJoiner(arg, 2)));
            }

// !sendAction - Tells the bot to make a action on a channel
            else if (commandChecker(PM, arg, "sendAction")) {
                bot.sendIRC().action(getArg(arg, 1), getScramble(argJoiner(arg, 2)));
            }
// !sendRaw - Tells the bot to say a raw line
            else if (commandChecker(PM, arg, "sendRaw") && checkPerm(PM.getUser(), 9001)) {
                bot.sendRaw().rawLineNow(argJoiner(arg, 1));
            }

// !part - leaves a channel
            else if (commandChecker(PM, arg, "part")) {
                if (getArg(arg, 2) != null) {
                    bot.sendRaw().rawLineNow("part " + getArg(arg, 1) + " :" + argJoiner(arg, 2));
                } else {
                    bot.sendRaw().rawLineNow("part " + getArg(arg, 1));
                }
                PM.getUser().send().notice("Successfully Disconnected to " + getArg(arg, 1));
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
                PM.getUser().send().notice("Successfully connected to " + getArg(arg, 1));
            }

// !QuitServ - Tells the bot to disconnect from server
            else if (commandChecker(PM, arg, "QuitServ") && checkPerm(PM.getUser(), Integer.MAX_VALUE)) {
                PM.getUser().send().notice("Disconnecting from server");
                if (getArg(arg, 1) != null) {
                    bot.sendIRC().quitServer(argJoiner(arg, 1));
                } else {
                    bot.sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                }
                try {
                    pause(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.exit(0);
            }
        } else if (PM.getMessage().startsWith(prefix) || PM.getMessage().startsWith(consolePrefix) || PM.getMessage().startsWith(PM.getBot().getNick())) {
            doCommand(new MessageEvent(bot, null, PM.getUser().getNick(), PM.getUserHostmask(), PM.getUser(), PM.getMessage(), null));
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
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        if (!PM.getMessage().contains(CryptoUtil.decrypt(FozConfig.PASSWORD))) {
            log(PM);
        }
    }

    public void onNotice(@NotNull NoticeEvent event) {
        String message = event.getMessage();
        //noinspection ConstantConditions
        if (event.getUser() == null) {
            return;
        }
        if (!event.getUser().getNick().equalsIgnoreCase("NickServ") || !event.getUser().getNick().equalsIgnoreCase("irc.badnik.net")) {
            //noinspection StatementWithEmptyBody
            if (message.contains("*** Found your hostname") ||
                    message.contains("Password accepted - you are now recognized.") ||
                    message.contains("This nickname is registered and protected.  If it is your") ||
                    message.contains("*** You are connected using SSL cipher") ||
                    message.contains("please choose a different nick.") ||
                    message.contains("nick, type /msg NickServ IDENTIFY password.  Otherwise,")) {

            } else if (message.contains("\u0001AVATAR")) {
                event.getUser().send().notice("\u0001AVATAR " + avatar + "\u0001");
            } else {
                bot.sendIRC().notice(currentNick, "Got notice from " + event.getUser().getNick() + ". Notice was : " + event.getMessage());
            }
        }
        checkNote(event, event.getUser().getNick(), null);
        if (bot.isConnected()) {
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
        debug.updateBot(bot);
        log(event);
    }

    public void onAction(@NotNull ActionEvent action) {
        //noinspection ConstantConditions
        onMessage(new MessageEvent(bot, action.getChannel(), action.getChannelSource(), action.getUserHostmask(), action.getUser(), action.getAction(), null), false);
        log(action);
    }

    public void onJoin(@NotNull JoinEvent join) {
        @SuppressWarnings("ConstantConditions") String hostmask = join.getUser().getHostmask();
        LOGGER.debug("User Joined: " + (hostmask == null ? join.getUser().getNick() : hostmask));
        if (checkOP(join.getChannel())) {
            //noinspection ConstantConditions
            if (checkPerm(join.getUser(), 0)) {
                join.getChannel().send().voice(join.getUserHostmask());
            }
        }
        //noinspection ConstantConditions
        log(join);
        checkNote(join, join.getUser().getNick(), join.getChannel().getName());
        if (debug != null) {
            debug.updateBot(bot);
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
    }

    public synchronized void onNickChange(@NotNull NickChangeEvent nick) {
        if (nick.getNewNick().equalsIgnoreCase(currentNick)) {
            currentNick = null;
            currentUsername = null;
            currentHost = null;
            LOGGER.debug("resetting Authed nick");
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }

        if (nick.getOldNick().equalsIgnoreCase(currentNick)) {
            currentNick = nick.getNewNick();
            //noinspection ConstantConditions
            currentUsername = nick.getUser().getLogin();
            currentHost = nick.getUser().getHostname();
            LOGGER.debug("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostname());
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
        checkNote(nick, nick.getNewNick(), null);
        debug.updateBot(bot);
        log(nick);
    }

    public synchronized void onNickAlreadyInUse(@NotNull NickAlreadyInUseEvent nick) {

        BOOLS.set(NICK_IN_USE);
        nick.respond(nick.getUsedNick() + 1);
    }

    public synchronized void onQuit(@NotNull QuitEvent quit) {
        if (quit.getReason().contains("RECOVER") || quit.getReason().contains("GHOST") || quit.getReason().contains("REGAIN")) { //Recover event
            BOOLS.set(NICK_IN_USE);
        }
        log(quit);
    }

    public synchronized void onKick(@NotNull KickEvent kick) {
        //noinspection ConstantConditions
        if (kick.getRecipient().getNick().equalsIgnoreCase(bot.getNick())) {
            try {
                pause(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bot.send().joinChannel(kick.getChannel().getName());
        }
        log(kick);
    }

    public synchronized void onUnknown(@NotNull UnknownEvent event) {
        String line = event.getLine();
        if (line.contains("\u0001AVATAR\u0001")) {
            //noinspection ConstantConditions
            line = line.substring(line.indexOf(":") + 1, line.indexOf("!"));
            bot.send().notice(line, "\u0001AVATAR " + avatar + "\u0001");
        }
        LOGGER.debug("Received unknown: " + event.getLine());
        if (debug != null) {
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
    }

    /**
     * Checks if the user attempting to use the command is allowed
     *
     * @param user User trying to use command
     * @return Boolean true if allowed, false if not
     */
    private boolean checkPerm(@NotNull User user, int requiredUserLevel) {
        if (user.getNick().equalsIgnoreCase(currentNick) && user.getLogin().equalsIgnoreCase(currentUsername) && user.getHostname().equalsIgnoreCase(currentHost)) {
            return true;
        } else if (authedUser.contains(user.getNick())) {
            int index = authedUser.indexOf(user.getNick() + "!" + user.getLogin() + "@" + user.getHostname());
            if (index > -1) {
                if (authedUserLevel.get(index) >= requiredUserLevel) {
                    return true;
                }
            }
        } else {
            int index = authedUser.size() - 1;
            while (index > -1) {
                String ident = authedUser.get(index);
                if (matchHostMask(user.getHostmask(), ident)) {
                    return authedUserLevel.get(index) >= requiredUserLevel;
                }
                index--;
            }
            ArrayList<UserLevel> levels = Lists.newArrayList(user.getUserLevels(lastEvents.get().getChannel()).iterator());
            if (requiredUserLevel <= getUserLevel(levels)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void loadData() {
        SaveDataStore save = FozConfig.loadData(GSON);
        loadData(save);
    }

    private synchronized void loadData(SaveDataStore save) {
        loadData(save, false);
    }

    private synchronized void loadData(boolean writeOnce) {
        loadData(FozConfig.loadData(GSON), writeOnce);
    }

    private synchronized void loadData(SaveDataStore save, boolean writeOnce) {
        if (writeOnce && noteList == null)
            noteList = save.getNoteList();

        if (writeOnce && authedUser == null)
            authedUser = save.getAuthedUser();

        if (writeOnce && authedUserLevel == null)
            authedUserLevel = save.getAuthedUserLevel();

        if (writeOnce && avatar == null)
            avatar = save.getAvatarLink();

        if (writeOnce && memes == null)
            memes = save.getMemes();

        if (writeOnce && FCList == null)
            FCList = save.getFCList();

        if (writeOnce && DNDJoined == null)
            DNDJoined = save.getDNDJoined();

        if (writeOnce && DNDList == null)
            DNDList = save.getDNDList();

        if (writeOnce && markovChain == null)
            markovChain = save.getMarkovChain();

        if (writeOnce && allowedCommands == null)
            allowedCommands = save.getAllowedCommands();
    }

    private synchronized void saveData() {
        /*if (!BOOLS[DATA_LOADED]) {
            LOGGER.debug("Data save canceled because data hasn't been loaded yet");
            return;
        }*/
        try {
            SaveDataStore save = new SaveDataStore(authedUser, authedUserLevel, DNDJoined, DNDList, noteList, avatar, memes, FCList, markovChain, allowedCommands);
            FozConfig.saveData(save, GSON);
        } catch (ConcurrentModificationException e) {
            LOGGER.debug("Data not saved. " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkNote(@NotNull Event event, @NotNull String user, @Nullable String channel) {
        System.out.print("Debug: Starting checkNote -> ");
        try {
            for (int i = 0; i < noteList.size(); i++) {
                System.out.print("Checking if " + noteList.get(i).getReceiver() + " matches " + user + " -> ");
                if (wildCardMatch(user.toLowerCase(), noteList.get(i).getReceiver().toLowerCase())) {
                    System.out.print("Found match! -> ");
                    if (channel != null) {
                        try {
                            sendMessage((MessageEvent) event, user + ": " + noteList.get(i).displayMessage(), true);
                        } catch (ClassCastException e) {
                            //noinspection ConstantConditions
                            lastEvents.add(new MessageEvent(bot, ((JoinEvent) event).getChannel(), channel, ((JoinEvent) event).getUserHostmask(), ((JoinEvent) event).getUser(), null, null));
                            //noinspection ConstantConditions
                            sendMessage(((JoinEvent) event).getUser().getNick(), user + ": " + noteList.get(i).displayMessage());
                        }
                    } else {
                        sendNotice(event, user, noteList.get(i).displayMessage());
                    }
                    noteList.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            if (event instanceof JoinEvent) {
                e.printStackTrace();
            } else {
                sendError(lastEvents.get(), e);
            }
        }
        System.out.println("| Ending checkNote");
    }

    private synchronized void setDebugInfo(@NotNull MessageEvent event) {
        int index = DNDJoined.indexOf(currentNick);

        //noinspection ConstantConditions
        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
            debug.setPlayerName(DNDList.get(index).getPlayerName());
            debug.setPlayerHP(DNDList.get(index).getHPAmounts());
            debug.setPlayerXP(DNDList.get(index).getXPAmounts());

            debug.setFamiliar(DNDList.get(index).getFamiliar().getName());
            debug.setFamiliarHP(DNDList.get(index).getFamiliar().getHPAmounts());
            debug.setFamiliarXP(DNDList.get(index).getFamiliar().getXPAmounts());
        }


        debug.updateBot(bot);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public MessageEvent getLastEvent() {
        return lastEvents.get();
    }

    private boolean commandChecker(GenericMessageEvent event, String[] args, String command) {
        return commandChecker(event, args, command, true);
    }

    private boolean commandChecker(GenericMessageEvent event, String[] args, String command, boolean printMsg) {
        try {
            String chanName;
            if (event instanceof PrivateMessageEvent) {
                chanName = "PM";
            } else {
                chanName = ((MessageEvent) event).getChannel().getName();
            }
            boolean isCommand = false;
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
                HashMap<String, ArrayList<String>> temp = allowedCommands.get(getSeverName((Event) event, true));
                ArrayList commands;
                if (temp != null) {
                    commands = temp.get(chanName);
                } else {
                    commands = null;
                }
                if (commands != null && commands.contains(command.toLowerCase())) {
                    if (printMsg) {
                        sendNotice(event, event.getUser().getNick(), "Sorry, you can't use that command here");
                    }
                } else {
                    if (event instanceof DiscordMessageEvent) /*then*/
                        ((DiscordMessageEvent) event).getDiscordEvent().getTextChannel().sendTyping();
                    LOGGER.trace("Found command: " + command);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getHelp(GenericMessageEvent event, String command) {
        switch (command.toLowerCase()) {
            case "commands":
                sendNotice(event, event.getUser().getNick(), "Really? ಠ_ಠ");
                break;
            case "helpme":
                sendNotice(event, event.getUser().getNick(), "Changed to COMMANDS (Except you already know that since you just used it...)");
                break;
            case "time":
                sendNotice(event, event.getUser().getNick(), "Displays info from the Date class");
                break;
            case "hello":
                sendNotice(event, event.getUser().getNick(), "Just your average \"hello world!\" program");
                break;
            case "randomnum":
                sendNotice(event, event.getUser().getNick(), "Creates a random number between the 2 integers");
                sendNotice(event, event.getUser().getNick(), "Usage: first number sets the minimum number, second sets the maximum");
                break;
            case "version":
                sendNotice(event, event.getUser().getNick(), "Displays the version of the bot");
                break;
            case "stringtobytes":
                sendNotice(event, event.getUser().getNick(), "Converts a String into a Byte array");
                break;
            case "temp":
                sendNotice(event, event.getUser().getNick(), "Converts a temperature unit to another unit.");
                sendNotice(event, event.getUser().getNick(), "Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
                break;
            case "chat":
                sendNotice(event, event.getUser().getNick(), "This command functions like ELIZA. Talk to it and it talks back.");
                sendNotice(event, event.getUser().getNick(), "Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky. Second parameter is the Message to send. Could also be the special param \"\\setup\" to actually start the bot.");
                break;
            case "calcJ":
                sendNotice(event, event.getUser().getNick(), "This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
                sendNotice(event, event.getUser().getNick(), "Usage 1: The simple way is to type out the expression without any VARIABLE_SET. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
                break;
            case "calcjs":
                sendNotice(event, event.getUser().getNick(), "Renamed to just \"JS\"");
            case "js":
                sendNotice(event, event.getUser().getNick(), "This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
                sendNotice(event, event.getUser().getNick(), "Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
                break;
            case "notej":
                sendNotice(event, event.getUser().getNick(), "Allows the user to leave notes");
                sendNotice(event, event.getUser().getNick(), "SubCommand add <Nick to leave note to> <message>: adds a note. SubCommand del <Given ID>: Deletes a set note Usage: . SubCommand list: Lists notes you've left");
                break;
            case "memes":
                sendNotice(event, event.getUser().getNick(), "Meme database. To get a meme you simply have to do \"Memes <meme name>\"");
                sendNotice(event, event.getUser().getNick(), "SubCommand set <Meme Name> <The Meme>: Sets up a meme. Note, When Setting a meme that already exists, you have to be the creator to edit it.  SubCommand list: Lists all the memes in the database");
                break;
            case "disasm":
                sendNotice(event, event.getUser().getNick(), "Disassembles bytes from different CPUs");
                sendNotice(event, event.getUser().getNick(), "Usage: 1st param is the CPU to read from. 2nd param is the bytes to assemble. You can use M68k as a shorthand instead of typing 68000. List of available CPUs https://www.hex-rays.com/products/ida/support/idadoc/618.shtml");
                break;
            case "attempt":
                sendNotice(event, event.getUser().getNick(), "Its a inside-joke with my friends in school. If i'm not away, ask me and i'll tell you about it.");
                break;
            case "reverselist":
                sendNotice(event, event.getUser().getNick(), "Reverses a list, pretty self explanatory");
                break;
            case "getdate":
                sendNotice(event, event.getUser().getNick(), "Gets the date");
                break;
            case "markov":
                sendNotice(event, event.getUser().getNick(), "Creates a markov chain from everything seen in chat");
                break;
            case "8ball":
                sendNotice(event, event.getUser().getNick(), "Rolls the magic 8Ball");
                break;
            case "checklink":
                sendNotice(event, event.getUser().getNick(), "Checks links, what else");
                break;
            case "calca":
                sendNotice(event, event.getUser().getNick(), "Currently broken: Calculates math using Wolfram Alpha");
                break;
            case "solvefor":
                sendNotice(event, event.getUser().getNick(), "Currently broken: Solves for a equation");
                break;
            case "count":
                sendNotice(event, event.getUser().getNick(), "");
                break;
            case "Lookupword":
                sendNotice(event, event.getUser().getNick(), "Looks up a word in the DICTIONARY");
                break;
            case "lookup":
                sendNotice(event, event.getUser().getNick(), "Looks up a word in the wikipedia");
                break;
            case "blockconv":
                sendNotice(event, event.getUser().getNick(), "Converts blocks to a actually known format");
                break;
            case "fc":
                sendNotice(event, event.getUser().getNick(), "Stores FC codes in a database so you can retrieve it");
                break;
            case "q":
                sendNotice(event, event.getUser().getNick(), "Allows from syncing up something such as sync watching a show");
                break;
            case "toscino":
                sendNotice(event, event.getUser().getNick(), "Converts a number to Scientific notation");
                break;
            case "dnd":
                sendNotice(event, event.getUser().getNick(), "");
                break;
            case "acc":
            case "68kcyc":
            case "asmcyclecounter":
                sendNotice(event, event.getUser().getNick(), "Counts cycles for 68k asm instructions. Use || to separate lines");
                break;
            case "trans":
                sendNotice(event, event.getUser().getNick(), "Translates between languages. -t is to, -f is from, and -d is detect");
                break;
            case "badtrans":
                sendNotice(event, event.getUser().getNick(), "Translates between languages... badly...");
                break;
            case "s1tcg":
                sendNotice(event, event.getUser().getNick(), "Generates Title card information for Sonic 1. Use \"-x xpos\" to specify X position, \"-y ypos\" to specify Y position, in hexadecimal. Use \"-l label\" to specify a label.");
                sendNotice(event, event.getUser().getNick(), "Example: !s1tcg -x F8 -y F8 -l GreenHillTitle RED HILL");
                break;
            default:
                sendNotice(event, event.getUser().getNick(), "That either isn't a command, or " + currentNick + " hasn't add that to the help yet.");
        }
    }

    public synchronized void setAvatar(String avatar) {
        FozruciX.avatar = avatar;
        if (!(network == Network.discord || network == Network.twitch) && currentNick != null && updateAvatar) {
            sendNotice(currentNick, "\u0001AVATAR " + avatar + "\u0001");
        }
    }

    private synchronized void addWords(@NotNull String phrase) {
        if (phrase.contains(prefix) ||
                phrase.contains(consolePrefix)) {
            return;
        }
        // put each word into an array
        String[] words = phrase.split(" ");

        // Loop through each word, check if it's already added
        // if its added, then get the suffix ArrayList and add the word
        // if it hasn't been added then add the word to the list
        // if its the first or last word then select the _start / _end key

        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) {
                break;
            }
            // Add the start and end words to their own
            if (i == 0) {
                LinkedList<String> startWords = markovChain.get("_start");
                startWords.add(words[i]);

                LinkedList<String> suffix = markovChain.get(words[i]);
                if (suffix == null) {
                    suffix = new LinkedList<>();
                    if (words.length == 1) {
                        return;
                    } else {
                        suffix.add(words[i + 1]);
                    }
                    markovChain.put(words[i], suffix);
                }

            } else if (i == words.length - 1) {
                LinkedList<String> endWords = markovChain.get("_end");
                endWords.add(words[i]);

            } else {
                LinkedList<String> suffix = markovChain.get(words[i]);
                if (suffix == null) {
                    suffix = new LinkedList<>();
                    if (words.length == 1) {
                        return;
                    } else {
                        suffix.add(words[i + 1]);
                    }
                    markovChain.put(words[i], suffix);
                } else {
                    if (words.length == 1) {
                        return;
                    } else {
                        suffix.add(words[i + 1]);
                    }
                    markovChain.put(words[i], suffix);
                }
            }
        }
    }

    public enum Network {
        normal, twitch, discord
    }

    private enum EventType {
        message(" <%s> "), privMessage(" <%s> "), action(" *%s "), notice(" *%s* "), join(" %s "), part(" %s "), quit(" %s ");
        private String val;

        EventType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    private enum MessageModes {
        normal, reversed, wordReversed, scrambled, wordScrambled
    }

    private class Python extends Thread {
        private MessageEvent event;
        private String eval;
        private int radix = 10;
        private ScriptEngine engine;

        public Python() {
            engine = new ScriptEngineManager().getEngineByName("python");
        }

        @Override
        public void run() {
            try {
                Object temp = engine.eval(eval);
                if (temp != null) {
                    String eval = temp.toString();
                    if (isNumeric(eval)) {
                        if (radix == 10) {
                            sendMessage(event, eval, true);
                            LOGGER.debug("Outputting as decimal");
                        } else {
                            String basePrefix = "";
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
                            eval = Long.toString(Long.parseLong(eval), radix).toUpperCase();
                            if (Math.abs(eval.length()) % 2 == 1) {
                                eval = "0" + eval;

                            }
                            sendMessage(event, basePrefix + eval, true);
                            LOGGER.debug("Outputting as base " + radix);
                        }
                    } else if (eval.length() < 470) {
                        sendMessage(event, eval, true);
                    } else {
                        sendPage(event, new String[]{"!PY", this.eval, "" + radix}, new LinkedList<>(Collections.singletonList(eval)));
                    }
                }
            } catch (Exception e) {
                sendError(event, e);
            }
        }

        void runNewPython(MessageEvent event, String code, int base) {
            this.event = event;
            eval = code;
            radix = base;
            run();
        }

    }

    private class JavaScript extends Thread {
        private final String[] unsafeAttributes = {
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
        private final String[] unsafeClasses = {
                "java.lang.reflect",
                "java.lang.invoke",
        };
        @NotNull
        private String factorialFunct = "function fact(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} " +
                "function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} " +
                "function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;} " +
                "function solvefor(expr, solve){var eq = algebra.parse(expr); var ans = eq.solveFor(solve); return solve + \" = \" + ans.toString(); }  var life = 42; " +
                "function roughSizeOf(e){for(var f=[],o=[e],t=0;o.length;){var n=o.pop();if(\"boolean\"==typeof n)t+=4;else if(\"string\"==typeof n)t+=2*n.length;else if(\"number\"==typeof n)t+=8;else if(\"object\"==typeof n&&-1===f.indexOf(n)){f.push(n);for(var r in n)o.push(n[r])}}return t}" +
                "fish = 4; eight = 6; triangle = 14; leet = 1337;";
        private MessageEvent event;
        private String arg;
        private int radix = 10;
        private ScriptEngine botOPEngine;
        private ScriptEngine normalUserEngine;

        JavaScript(@NotNull MessageEvent event, String arg, int radix) {
            setName("JavaScript Thread");
            this.event = event;
            this.arg = arg;
            this.radix = radix;
            normalUserEngine = new NashornScriptEngineFactory().getScriptEngine(new JSClassFilter());
            try {
                normalUserEngine.eval(factorialFunct);
                normalUserEngine.eval(new InputStreamReader(new FileInputStream("algebra.min.js")));
                ScriptContext context = normalUserEngine.getContext();
                int globalScope = context.getScopes().get(0);
                for (String unsafeAttribute : unsafeAttributes) {
                    context.removeAttribute(unsafeAttribute, globalScope);
                }
                botOPEngine = new NashornScriptEngineFactory().getScriptEngine();
            } catch (Exception e) {
                sendError(event, e);
            }
        }

        void runNewJavaScript(MessageEvent event, String arg, int radix) {
            this.event = event;
            this.arg = arg;
            this.radix = radix;
            run();
        }

        @Override
        public void run() {
            try {
                ScriptEngine engine;
                if (checkPerm(event.getUser(), 9001)) {
                    engine = botOPEngine;
                } else {
                    engine = normalUserEngine;
                }

                Object temp = engine.eval(arg);
                if (temp != null) {
                    String eval = temp.toString();
                    if (isNumeric(eval)) {
                        if (radix == 10) {
                            sendMessage(event, eval, true);
                            LOGGER.debug("Outputting as decimal");
                        } else {
                            String basePrefix = "";
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
                            eval = Long.toString(Long.parseLong(eval), radix).toUpperCase();
                            if (Math.abs(eval.length()) % 2 == 1) {
                                eval = "0" + eval;

                            }
                            sendMessage(event, basePrefix + eval, true);
                            LOGGER.debug("Outputting as base " + radix);
                        }
                    } else if (eval.length() < 470) {
                        sendMessage(event, eval, true);
                    } else {
                        sendPage(event, new String[]{"!JS", arg, "" + radix}, new LinkedList<>(Collections.singletonList(eval)));
                    }
                }
            } catch (Exception e) {
                sendError(event, e);
            }
        }

        private class JSClassFilter implements ClassFilter {
            @Override
            public boolean exposeToScripts(@NotNull String requestedClass) {
                for (String unsafeClass : unsafeClasses) {
                    if (requestedClass.equals(unsafeClass)) return false;
                }
                return true;
            }
        }
    }
}