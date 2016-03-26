package com.LilG.Com; /**
 * Created by Lil-G on 10/11/2015.
 * Main bot class
 */

import com.LilG.Com.CMD.CommandLine;
import com.LilG.Com.CMD.runCMD;
import com.LilG.Com.DND.DNDPlayer;
import com.LilG.Com.DND.Dungeon;
import com.LilG.Com.DataClasses.Meme;
import com.LilG.Com.DataClasses.Note;
import com.LilG.Com.DataClasses.SaveDataStore;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rmtheis.yandtran.YandexTranslatorAPI;
import com.rmtheis.yandtran.detect.Detect;
import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;
import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import info.bliki.api.Page;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
import io.nodyn.NoOpExitHandler;
import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.*;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import sun.misc.Unsafe;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FozruciX extends ListenerAdapter {
    private final static File WIKTIONARY_DIRECTORY = new File("Data\\Wiktionary");
    private final int jokeCommands = 0;
    private final int prefixRan = 1;
    private final int cleverBotInt = 2;
    private final int pandoraBotInt = 3;
    private final int jabberBotInt = 4;
    private final int nickInUse = 5;
    private final int color = 6;
    private final int respondToPMs = 7;
    private final String[] dictionary = {"i don't know what \"%s\" is, do i look like a dictionary?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck.", ">_>"};
    private final String[] listOfNoes = {" It’s not a priority for me at this time.", "I’d rather stick needles in my eyes.", "My schedule is up in the air right now. SEE IT WAFTING GENTLY DOWN THE CORRIDOR.", "I don’t love it, which means I’m not the right person for it.", "I would prefer another option.", "I would be the absolute worst person to execute, are you on crack?!", "Life is too short TO DO THINGS YOU don’t LOVE.", "I no longer do things that make me want to kill myself", "You should do this yourself, you would be awesome sauce.", "I would love to say yes to everything, but that would be stupid", "Fuck no.", "Some things have come up that need my attention.", "There is a person who totally kicks ass at this. I AM NOT THAT PERSON.", "Shoot me now...", "It would cause the slow withering death of my soul.", "I’d rather remove my own gallbladder with an oyster fork.", "I'd love to but I did my own thing and now I've got to undo it."};
    private final String[] commands = {"commands", " Time", " calcj", " randomNum", " StringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " prefix", " SayThis", " ToSciNo", " Trans", " DebugVar", " RunCmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", " NoteJ", "Memes", " jtoggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
    private final String PASSWORD = setPassword(true);
    private final ChatterBotFactory factory = new ChatterBotFactory();
    private final ExtendedDoubleEvaluator calc = new ExtendedDoubleEvaluator();
    private final StaticVariableSet<Double> variables = new StaticVariableSet<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private volatile BitSet bools = new BitSet(8); // true, false, null, null, null, false, true, true, false
    private String prefix = "!";
    private String currentNick = "Lil-G";
    private String currentUsername = "GameGenuis";
    private String currentHost = "friendly.local.noob";
    private ChatterBotSession cleverBotsession;
    private ChatterBotSession pandoraBotsession;
    private ChatterBotSession jabberBotsession;
    private MessageEvent lastEvent;
    private runCMD singleCMD = null;
    private volatile AtomicReference<List<Note>> noteList;
    private List<String> authedUser = new ArrayList<>();
    private List<Integer> authedUserLevel = new ArrayList<>();
    private List<String> DNDJoined = new ArrayList<>();
    private List<DNDPlayer> DNDList = new ArrayList<>();
    private Dungeon DNDDungeon = new Dungeon();
    private DebugWindow debug;
    private int jokeCommandDebugVar = 30;
    private volatile CommandLine terminal;
    private String counter = "";
    private int countercount = 0;
    private JFrame frame = new JFrame();
    private MessageModes messageMode = MessageModes.normal;
    private List<RPSGame> rpsGames = new ArrayList<>();
    private volatile AtomicReference<String> avatar;
    private volatile AtomicReference<HashMap<String, Meme>> memes;
    private int arrayOffset = 0;
    private volatile Thread js;
    private volatile HashMap<UserHostmask, Integer> notifiedUserList = new HashMap<>();
    private volatile AtomicReference<HashMap<String, String>> FCList;
    private volatile AtomicReference<MultiBotManager> manager;
    @SuppressWarnings("unused")
    public FozruciX(MultiBotManager manager, List<Note> noteList, CommandLine terminal, String avatar, HashMap<String, Meme> memes, Thread js, HashMap<String, String> FCList) {
        // true, false, null, null, null, false, true, true
        bools.set(jokeCommands);
        bools.set(color);
        bools.set(respondToPMs);

        this.manager = new AtomicReference<>(manager);
        this.noteList = new AtomicReference<>(noteList);
        this.avatar = new AtomicReference<>(avatar);
        this.memes = new AtomicReference<>(memes);
        this.FCList = new AtomicReference<>(FCList);
    }


    @SuppressWarnings("unused")
    public FozruciX(boolean Twitch, MultiBotManager manager, List<Note> noteList, String avatar, HashMap<String, Meme> memes, HashMap<String, String> FCList) {
        if (Twitch) {
            currentNick = "lilggamegenuis";
            currentUsername = currentNick;
            currentHost = currentUsername + ".tmi.twitch.tv";
        }
        // true, false, null, null, null, false, true, true
        bools.set(jokeCommands);
        bools.set(color);
        bools.set(respondToPMs);

        this.manager = new AtomicReference<>(manager);
        this.noteList = new AtomicReference<>(noteList);
        this.avatar = new AtomicReference<>(avatar);
        this.memes = new AtomicReference<>(memes);
        this.FCList = new AtomicReference<>(FCList);
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

    private static String getBytes(String bytes) {
        byte[] Bytes = bytes.getBytes();
        return Arrays.toString(Bytes);
    }

    @SuppressWarnings("unused")
    public static String formatFileSize(long size) {
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
            hrSize = dec.format((double) size).concat(" Bytes");
        }

        return hrSize;
    }

    private static boolean isNumeric(String str) {
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

    public static Unsafe getUnsafe() throws SecurityException {
        return Unsafe.getUnsafe();
    }

    public static String setPassword(boolean password) {
        File file;
        if (password) {
            file = new File("pass.bin");
        } else {
            file = new File("twitch.bin");
        }
        FileInputStream fin = null;
        String ret = " ";
        try {
            // create FileInputStream object
            fin = new FileInputStream(file);

            byte fileContent[] = new byte[(int) file.length()];

            // Reads up to certain bytes of data from this input stream into an array of bytes.
            fin.read(fileContent);
            //create string from byte array
            ret = new String(fileContent);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
        return ret;
    }

    private void makeDebug(Event event) {
        System.out.println("Creating Debug window");
        debug = new DebugWindow(event.getBot());
        System.out.println("Debug window created");
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onDisconnect(DisconnectEvent DC) {
        debug.dispose();
    }

    public void onConnect(ConnectEvent event) throws Exception {
        PircBotX bot = event.getBot();
        bot.sendIRC().mode(event.getBot().getNick(), "+B");

        makeDebug(event);

        bot.sendRaw().rawLineNow("ns recover " + event.getBot().getConfiguration().getName() + " " + PASSWORD);

        loadData();




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

    private void loadData() {
        try {
            String network = lastEvent.getBot().getServerInfo().getNetwork();
            if (network == null) {
                network = lastEvent.getBot().getServerHostname();
                network = network.substring(network.indexOf(".") + 1, network.lastIndexOf("."));
            }

            BufferedReader br = new BufferedReader(new FileReader("Data/" + network + "-Data.json"));
            SaveDataStore save = gson.fromJson(br, SaveDataStore.class);
            BufferedReader uniBr = new BufferedReader(new FileReader("Data/Data.json"));
            SaveDataStore uniSave = gson.fromJson(uniBr, SaveDataStore.class);
            noteList.set(uniSave.getNoteList());
            authedUser = save.getAuthedUser();
            authedUserLevel = save.getAuthedUserLevel();
            DNDJoined = save.getDNDJoined();
            DNDList = save.getDNDList();

            String avatarTemp = uniSave.getAvatarLink();
            avatar.set((avatarTemp == null) ? avatarTemp : avatar.get());

            memes.set(uniSave.getMemes());
            FCList.set(uniSave.getFCList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectBot(Event event, String[] arg, boolean SSL) {
        PircBotX bot = event.getBot();
        Configuration.Builder normal;
        if (SSL) {
            normal = new Configuration.Builder()
                    .setEncoding(Charset.forName("UTF-8"))
                    .setAutoReconnect(true)
                    .setAutoReconnectAttempts(5)
                    .setNickservPassword(FozruciX.setPassword(true))
                    .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                    .setLogin(bot.getConfiguration().getLogin())
                    .setRealName(bot.getConfiguration().getRealName())
                    .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                    .addListener(new FozruciX(manager.get(), noteList.get(), terminal, avatar.get(), memes.get(), js, FCList.get()));
        } else {
            normal = new Configuration.Builder()
                    .setEncoding(Charset.forName("UTF-8"))
                    .setAutoReconnect(true)
                    .setAutoReconnectAttempts(5)
                    .setNickservPassword(FozruciX.setPassword(true))
                    .setName(bot.getConfiguration().getName()) //Set the nick of the bot.
                    .setLogin(bot.getConfiguration().getLogin())
                    .setRealName(bot.getConfiguration().getRealName())
                    .addListener(new FozruciX(manager.get(), noteList.get(), terminal, avatar.get(), memes.get(), js, FCList.get()));
        }

        if (arg.length == 2 + arrayOffset) {
            manager.get().addBot(normal.buildForServer(arg[1 + arrayOffset]));
            sendMessage((MessageEvent) event, "Connecting bot to " + arg[1 + arrayOffset], false);
        } else if (arg.length == 3 + arrayOffset) {
            manager.get().addBot(normal.buildForServer(arg[1 + arrayOffset], Integer.parseInt(arg[2 + arrayOffset])));
            sendMessage((MessageEvent) event, "Connecting bot to " + arg[1 + arrayOffset] + " To port " + arg[2 + arrayOffset], false);
        } else if (arg.length == 4 + arrayOffset) {
            manager.get().addBot(normal.buildForServer(arg[1 + arrayOffset], Integer.parseInt(arg[2 + arrayOffset]), arg[3 + arrayOffset]));
            sendMessage((MessageEvent) event, "Connecting bot to " + arg[1 + arrayOffset], false);
        }
    }

    private int getUserLevel(ArrayList<UserLevel> levels) {
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
                    if (levels.get(levels.size() - 2) != UserLevel.OP) {
                        break;
                    }
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

    public String getScramble(String msgToSend) {
        if (msgToSend.contains("\r") || msgToSend.contains("\n")) {
            msgToSend = msgToSend.replace("\r", "").replace("\n", "");
        }
        if (messageMode == MessageModes.reversed) {
            msgToSend = new StringBuilder(msgToSend).reverse().toString();
        } else if (messageMode == MessageModes.wordReversed) {
            List<String> message = new ArrayList<>(Arrays.asList(msgToSend.split("\\s+")));
            msgToSend = "";
            for (int i = message.size() - 1; i >= 0; i--) {
                msgToSend += message.get(i) + " ";
            }
        } else if (messageMode == MessageModes.scrambled) {
            char[] msgChars = msgToSend.toCharArray();
            ArrayList<Character> chars = new ArrayList<>();
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
            List<String> message = new ArrayList<>(Arrays.asList(msgToSend.split("\\s+")));
            msgToSend = "";
            while (message.size() != 0) {
                int num = randInt(0, message.size() - 1);
                msgToSend += message.get(num) + " ";
                message.remove(num);
            }
        }
        return msgToSend;
    }

    private void sendPage(MessageEvent event, String[] arg, ArrayList<String> messagesToSend) {
        try {
            UUID name = UUID.randomUUID();
            File f = new File("Data/site/temp/" + name + ".htm");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
            bw.write("<html>");
            bw.write("<head>");
            bw.write("<link href=\"CommandStyles.css\" rel=\"stylesheet\" type=\"text/css\">");
            bw.write("<title>" + event.getUser().getNick() + ": " + event.getBot().getNick() + "'s Command output</title>");
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

    private void sendMessage(MessageEvent event, String msgToSend, boolean addNick) {
        msgToSend = getScramble(msgToSend);

        if (addNick) {
            event.respond(msgToSend);
        } else {
            event.getChannel().send().message(msgToSend);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void sendNotice(String userToSendTo, String msgToSend) {
        msgToSend = getScramble(msgToSend);
        lastEvent.getBot().send().notice(userToSendTo, msgToSend);
    }

    @SuppressWarnings("SameParameterValue")
    private void sendMessage(String userToSendTo, String msgToSend) {
        msgToSend = getScramble(msgToSend);
        lastEvent.getBot().send().message(userToSendTo, msgToSend);
    }

    @SuppressWarnings({"StatementWithEmptyBody", "ConstantConditions"})
    @Override
    public void onMessage(MessageEvent event) {
        lastEvent = event;
        String[] arg = splitMessage(event.getMessage());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        //noinspection ConstantConditions
        checkUserNote(event, event.getUser().getNick(), event.getChannel().getName());

// !formatting - toggles color (Mostly in the errors)
        if (commandChecker(arg, "formatting")) {
            if (checkPerm(event.getUser(), 9001)) {
                bools.flip(color);
                if (bools.get(color)) {
                    sendMessage(event, "Color formatting is now On", true);
                } else {
                    sendMessage(event, "Color formatting is now Off", true);
                }
            }
        }

// !helpme - redirect to !commands
        if (commandChecker(arg, "helpme")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "This commands was changed to commands.", true);
            }
        }

// !commands
        if (commandChecker(arg, "commands")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg.length < 2 + arrayOffset) {
                    sendNotice(event.getUser().getNick(), "List of commands so far. for more info on these commands do " + prefix + "commands. commands with \"Joke: \" are joke commands that can be disabled");
                    sendNotice(event.getUser().getNick(), Arrays.asList(commands).toString());
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("commands")) {
                    sendNotice(event.getUser().getNick(), "Really? ಠ_ಠ");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("helpMe")) {
                    sendNotice(event.getUser().getNick(), "Changed to commands (Except you already know that since you just used it...)");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("time")) {
                    sendNotice(event.getUser().getNick(), "Displays info from the Date class");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("Hello")) {
                    sendNotice(event.getUser().getNick(), "Just your average \"hello world!\" program");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("Attempt")) {
                    sendNotice(event.getUser().getNick(), "Its a inside-joke to my friends in school. If i'm not away, ask me and i'll tell you about it.");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("RandomNum")) {
                    sendNotice(event.getUser().getNick(), "Creates a random number between the 2 integers");
                    sendNotice(event.getUser().getNick(), "Usage: first number sets the minimum number, second sets the maximum");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("version")) {
                    sendNotice(event.getUser().getNick(), "Displays the version of the bot");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("StringToBytes")) {
                    sendNotice(event.getUser().getNick(), "Converts a String into a Byte array");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("temp")) {
                    sendNotice(event.getUser().getNick(), "Converts a temperature unit to another unit.");
                    sendNotice(event.getUser().getNick(), "Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("chat")) {
                    sendNotice(event.getUser().getNick(), "This command functions like ELIZA. Talk to it and it talks back.");
                    sendNotice(event.getUser().getNick(), "Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky. Second parameter is the Message to send. Could also be the special param \"\\setup\" to actually start the bot.");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("calcj")) {
                    sendNotice(event.getUser().getNick(), "This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
                    sendNotice(event.getUser().getNick(), "Usage 1: The simple way is to type out the expression without any variables. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("CalcJS")) {
                    sendNotice(event.getUser().getNick(), "Renamed to just \"JS\"");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("JS")) {
                    sendNotice(event.getUser().getNick(), "This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
                    sendNotice(event.getUser().getNick(), "Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("StringToBytes")) {
                    sendNotice(event.getUser().getNick(), "Converts a String into a Byte array");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("NoteJ")) {
                    sendNotice(event.getUser().getNick(), "Allows the user to leave notes");
                    sendNotice(event.getUser().getNick(), "SubCommand add <Nick to leave note to> <message>: adds a note. Subcommand del <Given ID>: Deletes a set note Usage: . Subcommand list: Lists notes you've left");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("Memes")) {
                    sendNotice(event.getUser().getNick(), "Meme database. To get a meme you simply have to do \"Memes <meme name>\"");
                    sendNotice(event.getUser().getNick(), "Subcommand set <Meme Name> <The Meme>: Sets up a meme. Note, When Seting a meme that already exists, you have to be the creator to edit it.  Subcommand list: Lists all the memes in the database");
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("disasm")) {
                    sendNotice(event.getUser().getNick(), "Disassembles bytes from different CPUs");
                    sendNotice(event.getUser().getNick(), "Usage: 1st param is the CPU to read from. 2nd param is the bytes to assemble. You can use M68k as a shorthand instead of typing 68000. List of avalible CPUs https://www.hex-rays.com/products/ida/support/idadoc/618.shtml");
                } else {
                    sendNotice(event.getUser().getNick(), "That either isn't a command, or " + currentNick + " hasn't add that to the help yet.");
                }
            }

        }

// !getBotList - gets all bots
        if (commandChecker(arg, "getBotList")) {
            if (checkPerm(event.getUser(), 9001)) {
                sendMessage(event, manager.get().getBots().toString(), true);
            }
        }

// !addServer - adds a bot to a server
        if (commandChecker(arg, "addServer")) {
            if (checkPerm(event.getUser(), 9001)) {
                connectBot(event, arg, false);
            }
        }

// !addServerSSL - adds a bot to a server
        if (commandChecker(arg, "addServerSSL")) {
            if (checkPerm(event.getUser(), 9001)) {
                connectBot(event, arg, true);
            }
        }

// !serverHostName - Gets the Server Host Name
        if (commandChecker(arg, "serverHostName")) {
            if (checkPerm(event.getUser(), 9001)) {
                sendMessage(event, event.getBot().getServerHostname(), false);
            }
        }

// !clearLogin - Clears login info to test auth related thing
        if (commandChecker(arg, "clearLogin")) {
            if (checkPerm(event.getUser(), 9001)) {
                currentNick = "Null";
                currentUsername = "Null";
                currentHost = "Null";
                sendMessage(event, "Logged out", false);
            }
        }

// !respondToPMs - sets whether or not to respond to PMs
        if (commandChecker(arg, "respondToPMs")) {
            if (checkPerm(event.getUser(), 9001)) {
                bools.flip(respondToPMs);
                sendMessage(event, "Responding to PMs: " + bools.get(respondToPMs), false);
            }
        }

// !Connect - joins a channel
        if (commandChecker(arg, "Connect")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getBot().send().joinChannel(arg[1 + arrayOffset]);
            }
        }



// !setAvatar - sets the avatar of the bot
        if (commandChecker(arg, "setAvatar")) {
            if (checkPerm(event.getUser(), 9001)) {
                avatar.set(arg[1 + arrayOffset]);
                sendMessage(event, "Avatar set", false);
                List<User> users = new ArrayList<>();
                for (Channel channel : event.getBot().getUserBot().getChannels()) {
                    for (User curUser : channel.getUsers()) {
                        if (users.indexOf(curUser) == -1 && !curUser.getNick().equalsIgnoreCase(event.getBot().getNick())) {
                            users.add(curUser);
                        }
                    }
                }
                for (int i = 0; users.size() >= i; i++) {
                    if (users.get(i).getRealName().startsWith("\u0003")) {
                        event.getBot().send().notice(users.get(i).getNick(), "\u0001AVATAR " + avatar + "\u0001");
                    }
                }

            } else {
                permErrorchn(event);
            }
        }

// !loadData - force a reload of the save data
        if (commandChecker(arg, "loadData")) {
            if (checkPerm(event.getUser(), 2)) {
                loadData();
            }
        }

// !rps - Rock! Paper! ehh you know the rest
        if (commandChecker(arg, "rps")) {
            if (checkPerm(event.getUser(), 0)) {
                //todo
            }
        }

// !reverseList - Reverses a list
        if (commandChecker(arg, "reverseList")) {
            if (checkPerm(event.getUser(), 0)) {
                String[] list = Arrays.copyOfRange(arg, 1, arg.length);
                String temp = "Uh oh, something broke";
                int i;
                for (i = list.length - 1 + arrayOffset; i > 0; i--) {
                    temp = list[0];
                    for (int index = 1; i > index; index++) {
                        list[index - 1] = list[index];
                    }
                    list[i] = temp;
                }
                list[i] = temp;
                String str = new ArrayList<>(Arrays.asList(list)).toString();
                sendMessage(event, str, true);
            }
        }

// !8ball - ALL HAIL THE MAGIC 8-BALL
        if (commandChecker(arg, "8Ball")) {
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
            event.respond(response);
        }

// !setMessage - Sets different message formats
        if (commandChecker(arg, "setMessage")) {
            if (checkPerm(event.getUser(), 9001)) {
                switch (arg[1 + arrayOffset].toLowerCase()) {
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

// !getChannelName - Gets channel name, for debuging
        if (commandChecker(arg, "GetChannelName")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, event.getChannel().getName(), true);
            }
        }

// url checker - Checks if string contains a url and parses
        String regex = "/((([A-Za-z]{3,9}:(?://)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)((?:/[\\+~%/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.!/\\\\\\w]*))?)/";
        String channel = event.getChannel().getName();
        if (event.getMessage().matches(regex) && !channel.equalsIgnoreCase("#origami64") && !channel.equalsIgnoreCase("#retro")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(event.getMessage());
            if (matcher.find()) {
                try {
                    URI url = new URI(matcher.group(0));
                    sendMessage(event, "Title: " + url.getHost(), false);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !CheckLink - checks links, duh
        if (commandChecker(arg, "CheckLink")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    Matcher match = Pattern.compile("/((([A-Za-z]{3,9}:(?://)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)((?:/[\\+~%/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.!/\\\\\\w]*))?)/").matcher(event.getMessage());
                    if (match.find()) {
                        System.out.print("Matched");
                        for (int i = 0; i < match.groupCount(); i++) {
                            System.out.println("Found value: " + match.group(i));
                        }
                        Document doc = Jsoup.connect("http://example.com/").get();
                        sendMessage(event, "Title: " + doc.title(), false);
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !testPermError - gets one of the permission error statements
        if (commandChecker(arg, "testPermError")) {
            if (checkPerm(event.getUser(), 9001)) {
                permErrorchn(event);
            }
        }


// !Time - Tell the time
        if (commandChecker(arg, "time")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    String time = new Date().toString();
                    sendMessage(event, " The time is now " + time, true);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !perms - edit privileged users
        if (commandChecker(arg, "perms")) {
            if (checkPerm(event.getUser(), Integer.MAX_VALUE)) {
                if (arg[1 + arrayOffset].equalsIgnoreCase("set")) {
                    try {
                        if (authedUser.contains(arg[2 + arrayOffset])) {
                            try {
                                authedUserLevel.set(authedUser.indexOf(arg[2 + arrayOffset]), Integer.parseInt(arg[3 + arrayOffset]));
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                            sendMessage(event, "Set " + arg[2 + arrayOffset] + " To level " + arg[3 + arrayOffset], true);
                        } else {
                            try {
                                authedUser.add(arg[2 + arrayOffset]);
                                authedUserLevel.add(Integer.parseInt(arg[3 + arrayOffset]));
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                            sendMessage(event, "Added " + arg[2 + arrayOffset] + " To authed users with level " + arg[3 + arrayOffset], true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("del")) {
                    try {
                        int index = authedUser.indexOf(arg[2 + arrayOffset]);
                        authedUserLevel.remove(index);
                        authedUser.remove(index);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    sendMessage(event, "Removed " + arg[2 + arrayOffset] + " from the authed user list", true);
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("clear")) {
                    authedUser.clear();
                    authedUserLevel.clear();
                    sendMessage(event, "Permission list cleared", true);
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("List")) {
                    sendMessage(event, authedUser.toString(), true);
                } else {
                    int place = -1;
                    try {
                        for (int i = 0; authedUser.size() >= i; i++) {
                            if (authedUser.get(i).equalsIgnoreCase(arg[1 + arrayOffset])) {
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
            }
        }

// !calcj - calculate a expression
        if (commandChecker(arg, "calcJ")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    if (arg.length != 2 + arrayOffset) {
                        double x = Double.parseDouble(arg[1 + arrayOffset]);
                        double step = Double.parseDouble(arg[2 + arrayOffset]);
                        double calcAmount = Double.parseDouble(arg[3 + arrayOffset]) - 1;
                        if (calcAmount > 5) {
                            calcAmount = 5;
                        }
                        int count = 0;
                        ArrayList<Double> eval = new ArrayList<>(0);

                        while (count <= calcAmount) {
                            variables.set("x", x);
                            eval.add(calc.evaluate(argJoiner(arg, 4).toLowerCase(), variables));
                            x += step;
                            count++;
                        }
                        sendMessage(event, eval.toString(), true);
                    } else {
                        double eval = calc.evaluate(arg[1 + arrayOffset]);
                        sendMessage(event, "" + eval, true);
                    }
                } catch (Exception e) {
                    sendMessage(event, "Error: " + e, false);
                }
            }
        }

// !Git - gets the link to source code
        if (commandChecker(arg, "Git")) {
            sendMessage(event, "Link to source code: https://github.com/lilggamegenuis/FozruciX", true);
        }

// !vgm - links to my New mixtapes :V
        if (commandChecker(arg, "vgm")) {
            sendMessage(event, "Link to My smps music: https://drive.google.com/open?id=0B3aju_x5_V--ZjAyLWZEUnV1aHc", true);
        }

// !GC - Runs the garbage collector
        if (commandChecker(arg, "GC")) {
            int num = gc();
            if (num == 1) {
                sendMessage(event, "Took out the trash", true);
            } else {
                sendMessage(event, "Took out " + num + " Trash bags", true);
            }
        }

// !SolveFor - Solves for a equation
        if (commandChecker(arg, "SolveFor")) {
            if (checkPerm(event.getUser(), 0)) {
                String expression = arg[1 + arrayOffset];
                String solveFor = arg[2 + arrayOffset];
                // Use DynJS runtime
                RuntimeFactory factory = RuntimeFactory.init(FozruciX.class.getClassLoader(), RuntimeFactory.RuntimeType.DYNJS);
                // Set config to run main.js
                NodynConfig config = new NodynConfig(new String[]{"-e", "var algebra = require('algebra.js'); var exp = new algebra.parse(\"" + expression + "\"); var ans = eq.solveFor(\"" + solveFor + "\"); eq.toString()   " + solveFor + " = ans.toString()"});
                // Create a new Nodyn and run it
                Nodyn nodyn = factory.newRuntime(config);
                nodyn.setExitHandler(new NoOpExitHandler());
                try {
                    int exitValue = nodyn.run();
                    System.out.print(exitValue);
                    sendMessage(event, nodyn.toString(), true);
                } catch (Throwable t) {
                    //noinspection ConstantConditions
                    sendError(event, (Exception) t);
                }
            }
        }

// !JS - evaluates a expression in JavaScript
        if (commandChecker(arg, "JS")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg.length > 1) {
                    if (js != null) {
                        js.stop();
                    }
                    js = new Thread(() -> {
                        ScriptEngine engine;
                        try {
                            final String[] unsafeAttributes = {
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
                            final String[] unsafeClasses = {
                                    "java.lang.reflect",
                                    "java.lang.invoke",
                            };
                            String factorialFunct = "function factorial(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} " +
                                    "function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} " +
                                    "function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;} " +
                                    "function solvefor(expr, solve){var eq = algebra.parse(expr); var ans = eq.solveFor(solve); return solve + \" = \" + ans.toString(); }  var life = 42; ";
                            if (!checkPerm(event.getUser(), 9001)) {
                                engine = new NashornScriptEngineFactory().getScriptEngine(requestedClass -> {
                                    for (String unsafeClass : unsafeClasses) {
                                        if (requestedClass.equals(unsafeClass)) return false;
                                    }
                                    return true;
                                });
                                ScriptContext ctx = engine.getContext();
                                int globalScope = ctx.getScopes().get(0);
                                for (String unsafeAttribute : unsafeAttributes) {
                                    ctx.removeAttribute(unsafeAttribute, globalScope);
                                }
                            } else {
                                engine = new NashornScriptEngineFactory().getScriptEngine();
                            }
                            engine.eval(factorialFunct);
                            engine.eval(new InputStreamReader(new FileInputStream("algebra.min.js")));
                            String eval;
                            eval = engine.eval(arg[1 + arrayOffset]).toString(); //todo
                            if (isNumeric(eval)) {
                                if (arg.length < 3 + arrayOffset) {
                                    sendMessage(event, eval, true);
                                    System.out.println("Outputting as decimal");
                                } else {
                                    String basePrefix = "";
                                    switch (Integer.parseInt(arg[2 + arrayOffset])) {
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
                                    eval = Long.toString(Long.parseLong(eval), Integer.parseInt(arg[2 + arrayOffset])).toUpperCase();
                                    if (eval.length() % 2 == 1) {
                                        eval = "0" + eval;
                                    }
                                    sendMessage(event, basePrefix + eval, true);
                                    System.out.println("Outputting as base " + arg[2 + arrayOffset]);
                                }
                            } else {
                                sendMessage(event, eval, true);
                            }
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    });
                    js.start();
                } else {
                    sendMessage(event, "Requires more arguments", true);
                }
            }
        }

// if someone tells the bot to "Go to hell" do this
        if (event.getMessage().contains(event.getBot().getNick()) && event.getMessage().toLowerCase().contains("Go to hell".toLowerCase())) {
            if (checkPerm(event.getUser(), 0) && !checkPerm(event.getUser(), 9001)) {
                sendMessage(event, "I Can't go to hell, i'm all out of vacation days", false);
            }
        }

// !count - counts amount of something
        if (commandChecker(arg, "count")) {
            if (checkPerm(event.getUser(), 1)) {
                if (arg.length != 1 + arrayOffset && arg[1 + arrayOffset].equalsIgnoreCase("setup")) {
                    counter = arg[2 + arrayOffset];
                    if (arg.length == 4 + arrayOffset) {
                        countercount = Integer.parseInt(arg[3 + arrayOffset]);
                    }
                }
                if (commandChecker(arg, "count")) {
                    countercount++;
                    sendMessage(event, "Number of times that " + counter + " is: " + countercount, false);
                }
            }
        }

// !StringToBytes - convert a String into a Byte array
        if (commandChecker(arg, "StringToBytes")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    sendMessage(event, getBytes(argJoiner(arg, 1)), true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage(event, "Not enough args. Must provide a string", true);
                }
            }
        }

// !LookUpWord - Looks up a word in the Wiktionary
        if (commandChecker(arg, "LookupWord")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    String message = "Null";
                    System.out.println("Looking up word");
                    // Connect to the Wiktionary database.
                    System.out.println("Opening dictionary");
                    IWiktionaryEdition wkt = JWKTL.openEdition(WIKTIONARY_DIRECTORY);
                    System.out.println("Getting page for word");
                    IWiktionaryPage page = wkt.getPageForWord(arg[1 + arrayOffset]);
                    if (page != null) {
                        System.out.println("Getting entry");
                        IWiktionaryEntry entry;
                        if (arg.length > 2 + arrayOffset && isNumeric(arg[2 + arrayOffset])) {
                            entry = page.getEntry(Integer.parseInt(arg[2 + arrayOffset]));
                        } else {
                            entry = page.getEntry(0);
                        }
                        System.out.println("getting sense");
                        IWiktionarySense sense = entry.getSense(1);
                        System.out.println("getting Plain text");
                        if (arg.length > 2 + arrayOffset) {
                            int subCommandNum = 2;
                            if (isNumeric(arg[2 + arrayOffset])) {
                                subCommandNum++;
                            }
                            if (arg.length > subCommandNum + arrayOffset && arg[subCommandNum - 1].equalsIgnoreCase("Example")) {
                                if (sense.getExamples().size() > 0) {
                                    message = sense.getExamples().get(0).getPlainText();
                                } else {
                                    sendMessage(event, "No examples found", true);
                                }
                            } else {
                                message = sense.getGloss().getPlainText();
                            }
                        } else {
                            message = sense.getGloss().getPlainText();
                        }
                        System.out.println("Sending message");
                        if (!message.isEmpty()) {
                            sendMessage(event, message, true);
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
            }
        }

//lookup - Looks up something in Wikipedia
        if (commandChecker(arg, "Lookup")) {
            if (checkPerm(event.getUser(), 9001)) {
                String[] listOfTitleStrings = {argJoiner(arg, 1)};
                info.bliki.api.User user = new info.bliki.api.User("", "", "http://en.wikipedia.org/w/api.php");
                user.login();
                List<Page> listOfPages = user.queryContent(listOfTitleStrings);
                WikiModel wikiModel = new WikiModel("${image}", "${title}");
                System.out.println(listOfPages.get(0).toString());
                String plainStr = wikiModel.render(new PlainTextConverter(), listOfPages.get(0).getCurrentContent()).replace("\r", "").replace("\n", "");
                Pattern pattern = Pattern.compile("[^.]*");
                Matcher matcher = pattern.matcher(plainStr);
                if (matcher.find()) {
                    sendMessage(event, matcher.group(0).replaceAll("\\{\\{[^\\}]+\\}\\}", "") + ". ", true);
                }

            }
        }

// !chat - chat's with a internet conversation bot
        if (commandChecker(arg, "chat")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg[1 + arrayOffset].equalsIgnoreCase("clever")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("\\setup")) {
                        try {
                            cleverBotsession = factory.create(ChatterBotType.CLEVERBOT).createSession();
                            bools.set(cleverBotInt);
                            //noinspection ConstantConditions
                            event.getUser().send().notice("CleverBot started");
                        } catch (Exception e) {
                            sendMessage(event, "Error: Could not create clever bot session. Error was: " + e, true);
                        }
                    } else {
                        if (bools.get(cleverBotInt)) {
                            try {
                                sendMessage(event, " " + botTalk("clever", argJoiner(arg, 2)), true);
                            } catch (Exception e) {
                                sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                            }
                        } else {
                            sendMessage(event, " You have to start CleverBot before you can talk to it. star it with \\setup", true);
                        }
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("pandora")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("\\setup")) {
                        try {
                            ChatterBot pandoraBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                            pandoraBotsession = pandoraBot.createSession();
                            bools.set(pandoraBotInt);
                            //noinspection ConstantConditions
                            event.getUser().send().notice("PandoraBot started");
                        } catch (Exception e) {
                            sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                        }
                    } else {
                        if (bools.get(pandoraBotInt)) {
                            try {
                                sendMessage(event, " " + botTalk("pandora", argJoiner(arg, 2)), true);
                            } catch (Exception e) {
                                sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                            }
                        } else {
                            sendMessage(event, " You have to start PandoraBot before you can talk to it. start it with \\setup", true);
                        }
                    }

                } else if (arg[1 + arrayOffset].equalsIgnoreCase("jabber")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("\\setup")) {
                        try {
                            ChatterBot jabberBot = factory.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477");
                            jabberBotsession = jabberBot.createSession();
                            bools.set(jabberBotInt);
                            //noinspection ConstantConditions
                            event.getUser().send().notice("PandoraBot started");
                        } catch (Exception e) {
                            sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                        }
                    } else {
                        if (bools.get(jabberBotInt)) {
                            try {
                                sendMessage(event, " " + botTalk("pandora", argJoiner(arg, 2)), true);
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                        } else {
                            sendMessage(event, " You have to start PandoraBot before you can talk to it. start it with \\setup", true);
                        }
                    }


                }
            }
        }

// !temp - Converts a unit of temperature to another
        if (commandChecker(arg, "temp")) {
            if (checkPerm(event.getUser(), 0)) {
                int temp = Integer.parseInt(arg[3 + arrayOffset]);
                double ans = 0;
                String unit = "err";
                if (arg[1 + arrayOffset].equalsIgnoreCase("F")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("C")) {
                        ans = (temp - 32) * 5 / 9;
                        unit = "C";
                    } else if (arg[2 + arrayOffset].equalsIgnoreCase("K")) {
                        ans = (temp - 32) * 5 / 9 + 273.15;
                        unit = "K";
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("C")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("F")) {
                        ans = (temp * 9 / 5) + 32;
                        unit = "F";
                    } else if (arg[2 + arrayOffset].equalsIgnoreCase("K") && temp < 0) {
                        ans = temp + 273.15;
                        unit = "K";
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("K")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("F")) {
                        ans = (temp - 273.15) * 9 / 5 + 32;
                        unit = "F";
                    } else if (arg[2 + arrayOffset].equalsIgnoreCase("C")) {
                        ans = temp - 273.15;
                        unit = "C";
                    }
                }
                if (unit.equalsIgnoreCase("err")) {
                    sendMessage(event, "Incorrect arguments.", true);
                } else {
                    sendMessage(event, " " + ans + unit, true);
                }
            }
        }


// !blockconv - Converts blocks to bytes
        if (commandChecker(arg, "blockconv")) {
            if (checkPerm(event.getUser(), 0)) {
                int data = Integer.parseInt(arg[3 + arrayOffset]);
                double ans = 0;
                String unit = "err";
                boolean notify = true;
                int BLOCKS = 128;
                if (arg[1 + arrayOffset].equalsIgnoreCase("blocks")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("kb")) {
                        ans = BLOCKS * data;
                        unit = "KB";
                        notify = false;
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("kb")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("blocks")) {
                        ans = data / BLOCKS;
                        unit = "Blocks";
                        notify = false;
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("mb")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("blocks")) {
                        int BLOCKSMB = 8 * BLOCKS;
                        ans = data / BLOCKSMB;
                        unit = "Blocks";
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("gb")) {
                    if (arg[2 + arrayOffset].equalsIgnoreCase("blocks")) {
                        int BLOCKSGB = 8192 * BLOCKS;
                        ans = data / BLOCKSGB;
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
            }
        }

// !do nothing

// !FC - Friend code database
        if (commandChecker(arg, "FC")) {
            if (!event.getChannel().getName().equals("#deltasmash") && checkPerm(event.getUser(), 0)) {
                try {
                    if (arg.length > 2) {
                        if (arg[1].equalsIgnoreCase("set")) {
                            if (FCList.get().containsKey(event.getUser().getNick())) {
                                String fc = arg[2 + arrayOffset].replaceAll("[^\\d]", "");
                                if (fc.length() == 12) {
                                    FCList.get().put(event.getUser().getNick(), fc);
                                    sendMessage(event, "FC Edited", true);
                                } else {
                                    sendMessage(event, "Incorrect FC", true);
                                }
                            } else {
                                String fc = arg[2 + arrayOffset].replaceAll("[^\\d]", "");
                                if (fc.length() == 12) {
                                    FCList.get().put(event.getUser().getNick(), arg[2].replaceAll("[^\\d]", ""));
                                    sendMessage(event, "Added " + event.getUser().getNick() + "'s FC to the DB as " + arg[2 + arrayOffset].replaceAll("[^\\d]", ""), true);
                                } else {
                                    sendMessage(event, "Incorrect FC", true);
                                }
                            }
                        }
                    } else {
                        if (arg[1].equalsIgnoreCase("list")) {
                            sendMessage(event, FCList.get().keySet().toString(), true);

                        } else if (arg[1].equalsIgnoreCase("del")) {
                            if (FCList.get().containsKey(event.getUser().getNick())) {
                                FCList.get().remove(event.getUser().getNick());
                                sendMessage(event, "Friend code removed", true);
                            } else {
                                sendMessage(event, "You haven't entered your Friend code yet", true);
                            }

                        } else if (FCList.get().containsKey(arg[1])) {
                            String fc = FCList.get().get(arg[1 + arrayOffset]);
                            String fcParts[] = new String[3];
                            fcParts[0] = fc.substring(0, 4);
                            fcParts[1] = fc.substring(4, 8);
                            fcParts[2] = fc.substring(8);
                            fc = fcParts[0] + "-" + fcParts[1] + "-" + fcParts[2];
                            sendMessage(event, arg[1 + arrayOffset] + ": " + fc, false);
                        } else {
                            sendMessage(event, "That user hasn't entered their FC yet", false);
                        }
                    }
                } catch (NullPointerException e) {
                    FCList.set(new HashMap<>());
                    sendMessage(event, "Try the command again", true);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !memes - Got all dem memes
        if (commandChecker(arg, "memes")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg.length > 1 + arrayOffset) {
                    try {
                        if (arg[1 + arrayOffset].equalsIgnoreCase("set")) {
                            if (memes.get().containsKey(arg[2 + arrayOffset].toLowerCase())) {
                                Meme meme = memes.get().get(arg[2 + arrayOffset].toLowerCase());
                                if (checkPerm(event.getUser(), 9001) || meme.getCreator().equalsIgnoreCase(event.getUser().getNick())) {
                                    if (arg.length == 3 + arrayOffset) {
                                        memes.get().remove(arg[2 + arrayOffset].toLowerCase());
                                        sendMessage(event, "Meme " + arg[2 + arrayOffset] + " Deleted!", true);
                                    } else {
                                        meme.setMeme(argJoiner(arg, 3));
                                        memes.get().put(arg[2 + arrayOffset].toLowerCase(), meme);
                                        sendMessage(event, "Meme " + arg[2 + arrayOffset] + " Edited!", true);
                                    }
                                } else {
                                    sendMessage(event, "Sorry, Only the creator of the meme can edit it", true);
                                }
                            } else {
                                memes.get().put(arg[2 + arrayOffset].toLowerCase(), new Meme(event.getUser().getNick(), argJoiner(arg, 3)));
                                sendMessage(event, "Meme " + arg[2 + arrayOffset] + " Created as " + argJoiner(arg, 3), true);
                            }
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("list")) {
                            sendMessage(event, memes.get().values().toString(), true);
                        } else {
                            if (memes.get().containsKey(arg[1 + arrayOffset].toLowerCase())) {
                                sendMessage(event, arg[1 + arrayOffset] + ": " + memes.get().get(arg[1 + arrayOffset].toLowerCase()).getMeme(), false);
                            } else {
                                sendMessage(event, "That Meme doesn't exist!", true);
                            }
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
            } else {
                sendMessage(event, "Missing arguments", true);
            }
        }

// !notej - Leaves notes
        if (commandChecker(arg, "notej")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg[1 + arrayOffset].equalsIgnoreCase("del")) {
                    try {
                        int i = 0;
                        int index = -1;
                        boolean found = false;
                        while (i < noteList.get().size() && !found) {
                            if (noteList.get().get(i).getId().toString().equals(arg[2 + arrayOffset])) {
                                found = true;
                                index = i;
                            } else {
                                i++;
                            }
                        }
                        if (found) {
                            if (event.getUser().getNick().equalsIgnoreCase(noteList.get().get(index).getSender())) {
                                noteList.get().remove(index);
                                sendMessage(event, "Note " + arg[2 + arrayOffset] + " Deleted", true);
                            } else {
                                sendMessage(event, "Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it", true);
                            }
                        } else {
                            sendMessage(event, "That ID wasn't found.", true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else if (arg[1 + arrayOffset].equalsIgnoreCase("list")) {
                    int i = 0;
                    List<String> found = new ArrayList<>();
                    List<String> foundUUID = new ArrayList<>();
                    while (noteList.get().size() > i) {
                        if (noteList.get().get(i).getSender().equalsIgnoreCase(event.getUser().getNick())) {
                            found.add(noteList.get().get(i).getMessageForList());
                            foundUUID.add(noteList.get().get(i).getUUIDForList());
                        }
                        i++;
                    }
                    sendMessage(event, found.toString(), true);
                    event.getUser().send().notice(foundUUID.toString());
                } else {
                    try {
                        Note note = new Note(event.getUser().getNick(), arg[1 + arrayOffset], argJoiner(arg, 2), event.getChannel().getName());
                        noteList.get().add(note);
                        sendMessage(event, "Left note \"" + argJoiner(arg, 2) + "\" for \"" + arg[1 + arrayOffset] + "\".", false);
                        event.getUser().send().notice("ID is \"" + noteList.get().get(noteList.get().indexOf(note)).getId().toString() + "\"");
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
                saveData(event);
            }
        }

// !Hello - Standard "Hello world" command
        if (commandChecker(arg, "hello")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "Hello World!", true);
            }
        }

// !Bot - Explains that "yes this is a bot"
        if (commandChecker(arg, "bot")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "Yes, this is " + currentNick + "'s bot.", true);
            }
        }

// !getname - gets the name of the bot
        if (commandChecker(arg, "getname")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, event.getBot().getUserBot().getRealName(), true);
            }
        }

// !version - gets the version of the bot
        if (commandChecker(arg, "version") && !event.getChannel().getName().equalsIgnoreCase("#deltasmash")) {
            if (checkPerm(event.getUser(), 0)) {
                String VERSION = "PircBotX: " + PircBotX.VERSION + ". BotVersion: 2.1. Java version: " + System.getProperty("java.version");

                sendMessage(event, "Version: " + VERSION, true);
            }
        }

// !login - attempts to login to nickserv
        if (commandChecker(arg, "login")) {
            if (checkPerm(event.getUser(), 0)) {
                event.getBot().sendIRC().mode(event.getBot().getNick(), "+B");
                event.getBot().sendIRC().identify(PASSWORD);
                event.getBot().sendRaw().rawLineNow("cs op #Lil-G|bot " + event.getBot().getNick());
                event.getBot().sendRaw().rawLineNow("cs op #Lil-G|bot Lil-G");
                event.getBot().sendRaw().rawLineNow("cs op #SSB " + event.getBot().getNick());
                event.getBot().sendRaw().rawLineNow("cs op #SSB Lil-G");
                event.getBot().sendRaw().rawLineNow("ns recover FozruciX " + PASSWORD);
            }
        }

// !getLogin - gets the login of the bot
        if (commandChecker(arg, "getLogin")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, event.getBot().getUserBot().getLogin(), true);
            }
        }

// !getID - gets the ID of the user
        if (commandChecker(arg, "getID")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "You are :" + event.getUser().getUserId(), true);
            }
        }

// !RandomNum - Gives the user a random Number
        if (commandChecker(arg, "randomnum")) {
            if (checkPerm(event.getUser(), 0)) {
                long num1, num2;
                if (arg[1 + arrayOffset].contains("0x")) {
                    arg[1 + arrayOffset] = arg[1 + arrayOffset].substring(2);
                    num1 = Long.parseLong(arg[1 + arrayOffset], 16);
                } else {
                    num1 = Long.parseLong(arg[1 + arrayOffset], 9001);
                }
                if (arg[2 + arrayOffset].contains("0x")) {
                    arg[2 + arrayOffset] = arg[2 + arrayOffset].substring(2);
                    num2 = Long.parseLong(arg[2 + arrayOffset], 16);
                } else {
                    num2 = Long.parseLong(arg[2 + arrayOffset], 9001);
                }

                sendMessage(event, " " + randInt((int) num1, (int) num2), true);
            }
        }

// !getState - Displays what version the bot is on
        if (commandChecker(arg, "getState")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "State is: " + event.getBot().getState(), true);
            }
        }

// !prefix - Changes the command prefix when it isn't the standard "!"
        if (arg[0].equalsIgnoreCase("!prefix") && !prefix.equals("!")) {
            if (checkPerm(event.getUser(), 9001)) {
                prefix = arg[1];
                if (prefix.length() > 1 && !prefix.endsWith(".")) {
                    arrayOffset = 1;
                } else {
                    arrayOffset = 0;
                }
                sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
                bools.set(prefixRan);
            } else {
                permError(event.getUser());
            }
        }

// !prefix - Changes the command prefix
        if (commandChecker(arg, "prefix") && !bools.get(prefixRan)) {
            if (checkPerm(event.getUser(), 9001)) {
                prefix = arg[1 + arrayOffset];
                if (prefix.length() > 1 && !prefix.endsWith(".")) {
                    arrayOffset = 1;
                } else {
                    arrayOffset = 0;
                }
                sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
                bools.clear(prefixRan);
            } else {
                permError(event.getUser());
            }
        }
        bools.clear(prefixRan);

// !Saythis - Tells the bot to say someting
        if (commandChecker(arg, "saythis")) {
            if (checkPerm(event.getUser(), 6)) {
                sendMessage(event, argJoiner(arg, 1 + arrayOffset), false);
            } else {
                permErrorchn(event);
            }
        }

// !LoopSay - Tells the bot to say someting and loop it
        if (commandChecker(arg, "loopsay")) {
            if (checkPerm(event.getUser(), 9001)) {
                int i = Integer.parseInt(arg[1 + arrayOffset]);
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
        if (commandChecker(arg, "ToSciNo")) {
            if (checkPerm(event.getUser(), 0)) {
                NumberFormat formatter = new DecimalFormat("0.######E0");

                long num = Long.parseLong(arg[1 + arrayOffset]);
                try {
                    sendMessage(event, formatter.format(num), true);
                } catch (Exception e) {
                    sendError(event, e);
                    //log(e.toString());
                }
            }
        }

// !DND - Dungeons and dragons. RNG the Game
        if (commandChecker(arg, "DND")) {
            if (checkPerm(event.getUser(), 0)) {
                if (commandChecker(arg, "DND join")) {
                    sendMessage(event, "Syntax: " + prefix + "DND join <Character name> <Race (can be anything right now)> <Class> {<Familiar name> <Familiar Species>}", true);
                } else {
                    if (arg[1 + arrayOffset].equalsIgnoreCase("join")) {
                        if (DNDJoined.contains(event.getUser().getNick())) {
                            sendMessage(event, "You are already in the list!", true);
                        } else {
                            if (arg.length == 5 + arrayOffset) {
                                if (DNDPlayer.ifClassExists(arg[3 + arrayOffset])) {
                                    DNDList.add(new DNDPlayer(arg[2 + arrayOffset], arg[3 + arrayOffset], arg[4 + arrayOffset], event.getUser().getNick()));
                                    DNDJoined.add(event.getUser().getNick());
                                    sendMessage(event, "Added \"" + arg[2 + arrayOffset] + "\" the " + arg[4 + arrayOffset] + " to the game", true);
                                    if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                        debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                    }
                                } else {
                                    sendMessage(event, "That class doesn't exist!", true);
                                }
                            }
                            if (arg.length == 7 + arrayOffset) {
                                if (DNDPlayer.ifClassExists(arg[4 + arrayOffset])) {
                                    if (DNDPlayer.ifSpeciesExists(arg[6 + arrayOffset])) {
                                        DNDList.add(new DNDPlayer(arg[2 + arrayOffset], arg[3 + arrayOffset], arg[4 + arrayOffset], event.getUser().getNick(), arg[5 + arrayOffset], arg[6 + arrayOffset]));
                                        DNDJoined.add(event.getUser().getNick());
                                        sendMessage(event, "Added \"" + arg[2 + arrayOffset] + "\" the " + arg[4 + arrayOffset] + " with " + arg[5 + arrayOffset] + " The " + arg[6 + arrayOffset] + " to the game", true);

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
                            } else {
                                sendMessage(event, "Invalid number of arguments", true);
                            }
                        }
                    }
                }
                if (arg[1 + arrayOffset].equalsIgnoreCase("info")) {
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
                if (arg[1 + arrayOffset].equalsIgnoreCase("List")) {
                    try {
                        sendMessage(event, DNDList.toString(), true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }

                    setDebugInfo(event);
                }
                if (arg[1 + arrayOffset].equalsIgnoreCase("ListClass")) {
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDClasses.values()), true);
                }
                if (arg[1 + arrayOffset].equalsIgnoreCase("ListSpecies")) {
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDFamiliars.values()), true);
                }
                //noinspection StatementWithEmptyBody
                if (arg[1 + arrayOffset].equalsIgnoreCase("DM")) {
                    //todo
                }
                if (arg[1 + arrayOffset].equalsIgnoreCase("Test")) { //testing commands.
                    //checkPerm(event.getUser())
                    try {
                        int index = DNDJoined.indexOf(event.getUser().getNick());
                        if (arg[2 + arrayOffset].equalsIgnoreCase("addItem")) {
                            DNDList.get(index).addInventory(arg[3 + arrayOffset]);
                            sendMessage(event, "Added " + arg[3 + arrayOffset] + " to your inventory", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("getItems")) {
                            sendMessage(event, DNDList.get(index).getInventory(), true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("delItem")) {
                            DNDList.get(index).removeFromInventory(arg[3 + arrayOffset]);
                            sendMessage(event, "removed " + arg[3 + arrayOffset] + " to your inventory", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("addXP")) {
                            DNDList.get(index).addXP(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Added " + arg[3 + arrayOffset] + " to your XP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("addHP")) {
                            DNDList.get(index).addHP(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Added " + arg[3 + arrayOffset] + " to your HP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("subHP")) {
                            DNDList.get(index).hit(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Subbed " + arg[3 + arrayOffset] + " from your HP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("addXPFam")) {
                            DNDList.get(index).getFamiliar().addXP(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Added " + arg[3 + arrayOffset] + " to your familiar's XP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("addHPFam")) {
                            DNDList.get(index).getFamiliar().addHP(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Added " + arg[3 + arrayOffset] + " to your familiar's HP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("subHPFam")) {
                            DNDList.get(index).getFamiliar().hit(Integer.parseInt(arg[3 + arrayOffset]));
                            sendMessage(event, "Subbed " + arg[3 + arrayOffset] + " from your familiar's HP", true);
                        }
                        if (arg[2 + arrayOffset].equalsIgnoreCase("getFamiliar")) {
                            sendMessage(event, DNDList.get(index).getFamiliar().toString(), true);
                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("clearList")) {
                            if (checkPerm(event.getUser(), 9001)) {
                                DNDJoined.clear();
                                DNDList.clear();
                                sendMessage(event, "DND Player lists cleared", false);
                            }
                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("DelChar")) {
                            if (checkPerm(event.getUser(), 9001)) {
                                if (DNDJoined.contains(arg[3 + arrayOffset])) {
                                    DNDJoined.remove(index);
                                }
                            }
                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("setPos")) {
                            DNDDungeon.setLocation(Integer.parseInt(arg[3 + arrayOffset]), Integer.parseInt(arg[4 + arrayOffset]));
                            sendMessage(event, "Pos is now: " + DNDDungeon.toString(), true);

                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("getPos")) {
                            Point temp = DNDDungeon.getLocation();
                            sendMessage(event, "Current location: (" + temp.x + "," + temp.y + ")", true);
                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("movePos")) {
                            DNDDungeon.move(Integer.parseInt(arg[3 + arrayOffset]), Integer.parseInt(arg[4 + arrayOffset]));
                            Point temp = DNDDungeon.getLocation();
                            sendMessage(event, "New location: (" + temp.x + "," + temp.y + ")", true);
                        }

                        if (arg[2 + arrayOffset].equalsIgnoreCase("getSurroundings")) {
                            int[] tiles = DNDDungeon.getSurrounding();
                            sendMessage(event, " | " + tiles[7] + " | " + tiles[0] + " | " + tiles[1] + " | ", true);
                            sendMessage(event, " | " + tiles[6] + " | " + tiles[8] + " | " + tiles[2] + " | ", true);
                            sendMessage(event, " | " + tiles[5] + " | " + tiles[4] + " | " + tiles[3] + " | ", true);
                        }

                        int frameWidth = 300;
                        int frameHeight = 300;
                        if (arg[2 + arrayOffset].equalsIgnoreCase("genDungeon")) {
                            DNDDungeon = new Dungeon();
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

                        if (arg[2 + arrayOffset].equalsIgnoreCase("draw")) {
                            frame.dispose();
                            frame = new JFrame();
                            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            frame.setAlwaysOnTop(true);
                            frame.setSize(frameWidth, frameHeight);
                            frame.setVisible(true);
                            frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
                            frame.paintAll(frame.getGraphics());
                        }
                    } catch (NullPointerException e) {
                        sendMessage(event, "You have to join first! (Null pointer)", true);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
                setDebugInfo(event);
            }
        }

// !disasm - disassembles machine code for the specified CPU
        if (commandChecker(arg, "disasm")) {
            if (checkPerm(event.getUser(), 0)) {
                String byteStr = argJoiner(arg, 2).replace(" ", "");
                byte[] bytes = DatatypeConverter.parseHexBinary(byteStr);
                try (FileOutputStream fos = new FileOutputStream("Data\\temp.68k")) {
                    BufferedWriter delFile = new BufferedWriter(new FileWriter("Data\\temp.asm", false));
                    delFile.close();
                    fos.write(bytes);
                    String processor = arg[1 + arrayOffset];
                    if (processor.equalsIgnoreCase("M68K")) {
                        processor = "68000";
                    }
                    Process process = new ProcessBuilder("C:\\Program Files (x86)\\IDA 6.8\\idaq", "-B", "-p" + processor, "data\\temp.68k").start();
                    while (process.isAlive()) {
                    }
                    BufferedReader disasm = new BufferedReader(new FileReader("Data\\temp.asm"));
                    String disasmTemp;
                    int lines = 0;
                    boolean fileIsEmpty = true;
                    ArrayList<String> messagesToSend = new ArrayList<>();
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
                            lines++;
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
                } catch (IllegalArgumentException e) {
                    sendMessage(event, "Arguments have to be a Hexadecimal number: " + e.getCause(), true);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !Trans - Translate from 1 language to another
        if (commandChecker(arg, "trans")) {
            if (checkPerm(event.getUser(), 0)) {
                String text;
                System.out.println("Setting key");
                YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                try {
                    if (arg[1 + arrayOffset].equalsIgnoreCase("\\detect")) {
                        sendMessage(event, fullNameToString(Detect.execute(argJoiner(arg, 2))), true);
                    } else {
                        if (arg.length == 3 + arrayOffset) {
                            Language to = Language.valueOf(arg[1 + arrayOffset].toUpperCase());
                            Language from = Detect.execute(argJoiner(arg, 2));
                            text = Translate.execute(argJoiner(arg, 2), from, to);
                            System.out.print("Translating: " + text);
                            if (argJoiner(arg, 2).contains(text)) {
                                sendMessage(event, "Yandex couldn't translate that.", true);
                            } else {
                                sendMessage(event, text, true);
                            }
                        } else if (arg.length == 4 + arrayOffset) {
                            Language to = Language.valueOf(arg[2 + arrayOffset].toUpperCase());
                            Language from = Language.valueOf(arg[1 + arrayOffset].toUpperCase());
                            text = Translate.execute(argJoiner(arg, 3), from, to);
                            System.out.print("Translating: " + text);
                            if (argJoiner(arg, 3).contains(text)) {
                                sendMessage(event, "Yandex couldn't translate that.", true);
                            } else {
                                sendMessage(event, text, true);
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    sendError(event, new Exception("That class doesn't exist!"));
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !BadTrans - Translate from english to.... english... badly
        if (commandChecker(arg, "BadTrans")) {
            if (checkPerm(event.getUser(), 0)) {
                String text;
                System.out.println("Setting key");
                YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                try {
                    if (arg.length > 1 + arrayOffset) {
                        text = argJoiner(arg, 1);
                        System.out.print("Translating: " + text + " - ");
                        text = Translate.execute(text, Language.ENGLISH, Language.JAPANESE);
                        System.out.print("Translating: " + text + " - ");
                        text = Translate.execute(text, Language.JAPANESE, Language.VIETNAMESE);
                        System.out.print("Translating: " + text + " - ");
                        text = Translate.execute(text, Language.VIETNAMESE, Language.CHINESE);
                        System.out.print("Translating: " + text + " - ");
                        text = Translate.execute(text, Language.CHINESE, Language.ENGLISH);
                        System.out.println("Translating: " + text);
                        sendMessage(event, text, true);
                    } else {
                        sendMessage(event, ">_>", true);
                    }

                } catch (IllegalArgumentException e) {
                    sendError(event, new Exception("That class doesn't exist!"));
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !debugvar - changes a variable to the value
        if (commandChecker(arg, "debugvar")) {
            if (checkPerm(event.getUser(), 9001)) {
                switch (arg[1 + arrayOffset].toLowerCase()) { //Make sure strings are lowercsae
                    case "i":
                        int i = Integer.parseInt(arg[2 + arrayOffset]);
                        sendMessage(event, "DEBUG: Var \"i\" is now \"" + i + "\"", true);
                        break;
                    case "jokenum":
                        jokeCommandDebugVar = Integer.parseInt(arg[2 + arrayOffset]);
                        sendMessage(event, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"", true);
                }
            } else {
                permErrorchn(event);
            }
        }

// !runcmd - Tells the bot to run a OS command
        if (commandChecker(arg, "runcmd")) {
            if (checkPerm(event.getUser(), 9001)) {
                try {
                    if (!arg[1 + arrayOffset].equalsIgnoreCase("stop")) {
                        try {
                            singleCMD = new runCMD(event, arg);
                            singleCMD.start();
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                    if (arg[1 + arrayOffset].equalsIgnoreCase("stop")) {
                        sendMessage(event, "Stopping", true);
                        singleCMD.interrupt();
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            } else {
                permError(event.getUser());
            }
        }

// \ - runs commands without closing at the end
        String consolePrefix = "\\";
        if (arg[0].startsWith(consolePrefix)) {
            if (checkPerm(event.getUser(), 9001)) {
                if (arg[0].equalsIgnoreCase("\\start")) {
                    terminal = new CommandLine(event, arg);
                    terminal.start();
                    sendMessage(event, "Command line started", true);
                } else if (arg[0].substring(1).equalsIgnoreCase("\\close")) {
                    terminal.doCommand(event, "exit");
                } else if (arg[0].substring(1).equalsIgnoreCase("\\stop")) {
                    terminal.interrupt();
                } else {
                    terminal.doCommand(event, event.getMessage().substring(1));
                }
            }
        }

// !SayRaw - Tells the bot to send a raw line
        if (commandChecker(arg, "SayRaw")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getBot().sendRaw().rawLineNow(argJoiner(arg, 1));
            } else {
                permErrorchn(event);
            }
        }

// !SayNotice - Tells the bot to send a notice
        if (commandChecker(arg, "SayNotice")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getBot().sendIRC().notice(arg[1 + arrayOffset], argJoiner(arg, 2));
            } else {
                permErrorchn(event);
            }
        }

// !SayCTCPCommand - Tells the bot to send a CTCP Command
        if (commandChecker(arg, "SayCTCPCommand")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getBot().sendIRC().ctcpCommand(arg[1 + arrayOffset], argJoiner(arg, 2));
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
        if (commandChecker(arg, "leave")) {
            if (checkPerm(event.getUser(), 9001)) {
                if (!commandChecker(arg, "leave")) {
                    event.getChannel().send().part(argJoiner(arg, 2));
                } else {
                    event.getChannel().send().part();
                }
            } else if (!event.getBot().getServerHostname().equalsIgnoreCase("irc.twitch.tv")) {
                permErrorchn(event);
            }
        }

// !respawn - Tells the bot to restart and reconnect
        if (commandChecker(arg, "respawn")) {
            if (checkPerm(event.getUser(), 5)) {
                saveData(event);
                event.getBot().sendIRC().quitServer("Died! Respawning in about 5 seconds");
                event.getBot().stopBotReconnect();
            } else {
                permErrorchn(event);
            }
        }

// !recycle - Tells the bot to part and rejoin the channel
        if (commandChecker(arg, "recycle")) {
            if (checkPerm(event.getUser(), 2)) {
                saveData(event);
                event.getChannel().send().cycle();
            } else {
                permErrorchn(event);
            }
        }
// !revoice - gives everyone voice if they didn't get it
        if (commandChecker(arg, "revoice")) {
            for (User user1 : event.getChannel().getUsers()) {
                event.getBot().sendRaw().rawLineNow("mode " + event.getChannel().getName() + " +v " + user1.getNick());
            }
        }

// !kill - Tells the bot to disconnect from server and exit
        if (commandChecker(arg, "kill")) {
            if (checkPerm(event.getUser(), 9001)) {
                //noinspection ConstantConditions
                saveData(event);
                event.getUser().send().notice("Disconnecting from server");
                new Thread(() -> {
                    if (arg.length > 1 + arrayOffset) {
                        manager.get().stop(argJoiner(arg, 1));
                    } else {
                        manager.get().stop("I'm only a year old and have already wasted my entire life.");
                    }
                }).start();
                System.exit(0);
            } else {
                permErrorchn(event);
            }
        }

// !quitServ - Tells the bot to disconnect from server
        if (commandChecker(arg, "quitServ")) {
            if (checkPerm(event.getUser(), 9001)) {
                //noinspection ConstantConditions
                saveData(event);
                event.getUser().send().notice("Disconnecting from server");
                if (arg.length > 1 + arrayOffset) {
                    event.getBot().sendIRC().quitServer(argJoiner(arg, 1));
                } else {
                    event.getBot().sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                }

            } else {
                permErrorchn(event);
            }
        }

// !getUserLevels - gets the user levels of the user
        if (commandChecker(arg, "getUserLevels")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    List<UserLevel> userLevels = Lists.newArrayList(event.getUser().getUserLevels(event.getChannel()).iterator());
                    sendMessage(event, userLevels.toString(), true);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }


// !changenick - Changes the nick of the bot
        if (commandChecker(arg, "changeNick")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getBot().sendIRC().changeNick(arg[1 + arrayOffset]);
                debug.setNick(arg[1 + arrayOffset]);
            } else {
                permErrorchn(event);
            }
        }

// !SayAction - Makes the bot do a action
        if (commandChecker(arg, "SayAction")) {
            if (checkPerm(event.getUser(), 9001)) {
                event.getChannel().send().action(argJoiner(arg, 2));
            } else {
                permErrorchn(event);
            }
        }

// !jtoggle - toggle joke commands
        if (commandChecker(arg, "jtoggle")) {
            if (commandChecker(arg, "jtoggle")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (bools.get(jokeCommands)) {
                        sendMessage(event, "Joke commands are currently enabled", true);
                    } else {
                        sendMessage(event, "Joke commands are currently disabled", true);
                    }
                }
            }
            if (arg[1 + arrayOffset].equalsIgnoreCase("toggle")) {
                if (checkPerm(event.getUser(), 2)) {
                    bools.flip(jokeCommands);
                    if (bools.get(jokeCommands)) {
                        sendMessage(event, "Joke commands are now enabled", true);
                    } else {
                        sendMessage(event, "Joke commands are now disabled", true);
                    }
                } else {
                    permErrorchn(event);
                }
            }
        }

// !sudo/make me a sandwich - You should already know this joke
        if (commandChecker(arg, "make me a sandwich")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1)) {
                    sendMessage(event, "No, make one yourself", false);
                } else {
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
                }
            }
        }
        if (commandChecker(arg, "sudo make me a sandwich")) {
            if (checkPerm(event.getUser(), 9001)) {
                sendMessage(event, "Ok", false);
            } else {
                sendMessage(event, "This command requires root permissions", true);
            }
        }

// !Splatoon - Joke command - ask the splatoon question
        if (commandChecker(arg, "Splatoon")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1))
                    sendMessage(event, " YOU'RE A KID YOU'RE A SQUID", true);
                else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !attempt - Joke command - NOT ATTEMPTED
        if (commandChecker(arg, "attempt")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1))
                    sendMessage(event, " NOT ATTEMPTED", true);
                else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

//// !stfu - Joke command - say "no u"
//		if (commandChecker(arg, "stfu")){
//			if(bools.get(jokeCommands) || checkPerm(event.getUser()))
//				sendMessage(event, sender + ": " + prefix + "no u");
//		}

// !eatabowlofdicks - Joke command - joke help command
        if (commandChecker(arg, "eatabowlofdicks")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1))
                    sendMessage(event, "no u", true);
            }
        }

// !my  - Joke command - This was requested by Greeny in #origami64. ask him about it
        if (commandChecker(arg, "my")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1))
                    if (!channel.equalsIgnoreCase("#retro")) {

                        if (arg[1 + arrayOffset].equalsIgnoreCase("DickSize")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "8" + StringUtils.leftPad("D", size, "=") + " - " + size, true);
                            }
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("vaginadepth")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Less than Integer.MIN_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "|" + StringUtils.leftPad("{0}", size, "=") + " -  -" + size, true);
                            }
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("BallCount")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, StringUtils.leftPad("", size, "8") + " - " + size, true);
                            }
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("xdlength")) {
                            int size = randInt(0, jokeCommandDebugVar);
                            sendMessage(event, "X" + StringUtils.leftPad("", size, "D") + " - " + size, true);
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("ass")) {
                            sendMessage(event, "No.", true);
                        } else if (arg[1 + arrayOffset].equalsIgnoreCase("powerLevel")) {
                            if (checkPerm(event.getUser(), 9001)) {
                                sendMessage(event, "Its OVER 9000!!!!", true);
                            } else {
                                sendMessage(event, "The scouter says their power level is... " + randInt(-1, 9000) + "!", true);
                            }
                        }
                    }
            }
        }


// !potato - Joke command - say "i am potato" in Japanese
        if (commandChecker(arg, "potato")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1)) {
                    byte[] bytes = "わたしわポタトデス".getBytes(Charset.forName("UTF-8"));
                    String v = new String(bytes, Charset.forName("UTF-8"));
                    sendMessage(event, v, true);
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !whatis? - Joke command -
        if (commandChecker(arg, "whatis?")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1)) {
                    int num = randInt(0, dictionary.length - 1);
                    String comeback = String.format(dictionary[num], argJoiner(arg, 1));
                    sendMessage(event, comeback, true);
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !rip - Joke command - never forgetti the spaghetti
        if (commandChecker(arg, "rip")) {
            if (checkPerm(event.getUser(), 0)) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1)) {
                    if (arg[1 + arrayOffset].equalsIgnoreCase(currentNick)) {
                        sendMessage(event, currentNick + " Will live forever!", false);
                    } else if (arg[1 + arrayOffset].equalsIgnoreCase(event.getBot().getNick())) {
                        sendMessage(event, ">_>", false);
                    } else {
                        sendMessage(event, "Rest in spaghetti, never forgetti. May the pasta be with " + argJoiner(arg, 1), false);
                    }
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !GayDar - Joke command - picks random user
        if (commandChecker(arg, "GayDar")) {
            if (checkPerm(event.getUser(), 0) && !channel.equalsIgnoreCase("#retro")) {
                if (bools.get(jokeCommands) || checkPerm(event.getUser(), 1)) {
                    Iterator<User> user = event.getChannel().getUsers().iterator();
                    List<String> userList = new ArrayList<>();
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
                    } catch (Exception e) {
                        sendMessage(event, "Error: " + e, false);
                    }
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);

                System.out.println(event.getMessage());
            }
        }

        saveData(event);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        String DNDDungeonMaster = "Null";
        debug.setCurrDM(DNDDungeonMaster);
        debug.setMessage(event.getUser().getNick() + ": " + event.getMessage());
    }

    public void onPrivateMessage(PrivateMessageEvent PM) {

        String[] arg = splitMessage(PM.getMessage());

// !rps - Rock! Paper! ehh you know the rest
        if (commandChecker(arg, "rps")) {
            //noinspection ConstantConditions
            String nick = PM.getUser().getNick();
            if (checkPerm(PM.getUser(), 0)) {
                boolean found = false;
                boolean isFirstPlayer = true;
                RPSGame game = null;
                int i = 0;
                for (; i > rpsGames.size(); i++) {
                    if (rpsGames.get(i).isInGame(nick)) {
                        found = true;
                        game = rpsGames.get(i);
                        isFirstPlayer = game.isFirstPlayer(nick);
                        break;
                    }
                }
                if (found) {
                    if (arg.length > 1 + arrayOffset) {
                        switch (arg[1 + arrayOffset]) {
                            case "r":
                                if (isFirstPlayer) {
                                    game.setP1Choice(1);
                                } else {
                                    game.setP2Choice(1);
                                }
                        }
                    }
                } else if (arg.length > 1 + arrayOffset && !(arg[1 + arrayOffset].equalsIgnoreCase("r") || arg[1 + arrayOffset].equalsIgnoreCase("p") || arg[1 + arrayOffset].equalsIgnoreCase("s"))) {
                    rpsGames.add(new RPSGame(PM.getUser().getNick(), arg[1 + arrayOffset]));
                    PM.getUser().send().notice("Created a game with " + arg[1 + arrayOffset]);
                } else {
                    PM.getUser().send().notice("You aren't in a game!");
                }
            }
        }

// !login - Sets the authed named to the new name ...if the password is right
        if (commandChecker(arg, "login")) {
            if (arg[1 + arrayOffset].equals(PASSWORD)) {
                currentNick = PM.getUser().getNick();
                currentUsername = PM.getUser().getLogin();
                currentHost = PM.getUser().getHostname();
            } else
                PM.getUser().send().message("password is incorrect.");
        }
        //Only allow me (Lil-G) to use PM commands except for the login command
        else if (checkPerm(PM.getUser(), 6) &&
                !commandChecker(arg, "login") &&
                !commandChecker(arg, "rps") &&
                arg[0].contains(prefix)) {


// !SendTo - Tells the bot to say someting on a channel
            if (commandChecker(arg, "sendto")) {
                PM.getBot().sendIRC().message(arg[1 + arrayOffset], argJoiner(arg, 2));
            }

// !sendAction - Tells the bot to make a action on a channel
            if (commandChecker(arg, "sendAction")) {
                PM.getBot().sendIRC().action(arg[1 + arrayOffset], argJoiner(arg, 2));
            }
// !sendRaw - Tells the bot to say a raw line
            if (commandChecker(arg, "sendRaw")) {
                PM.getBot().sendRaw().rawLineNow(argJoiner(arg, 1));
            }

// !part - leaves a channel
            if (commandChecker(arg, "part")) {
                if (arg.length != 2 + arrayOffset) {
                    PM.getBot().sendRaw().rawLineNow("part " + arg[1 + arrayOffset] + " " + arg[2 + arrayOffset]);
                } else {
                    PM.getBot().sendRaw().rawLineNow("part " + arg[1 + arrayOffset]);
                }
                PM.getUser().send().notice("Successfully connected to " + arg[1 + arrayOffset]);
            }

// !changenick- Changes the nick of the bot
            if (commandChecker(arg, "changeNick") && checkPerm(PM.getUser(), 9001)) {
                PM.getBot().sendIRC().changeNick(argJoiner(arg, 1));
                debug.setNick(argJoiner(arg, 1));
            }


// !Connect - Tells the bot to connect to specified channel
            if (commandChecker(arg, "connect")) {
                if (arg.length != 2 + arrayOffset) {
                    PM.getBot().sendIRC().joinChannel(arg[1 + arrayOffset], arg[2 + arrayOffset]);
                } else {
                    PM.getBot().sendIRC().joinChannel(arg[1 + arrayOffset]);
                }
                PM.getUser().send().notice("Successfully connected to " + arg[1 + arrayOffset]);
            }

// !QuitServ - Tells the bot to disconnect from server
            if (commandChecker(arg, "quitserv") && checkPerm(PM.getUser(), Integer.MAX_VALUE)) {
                PM.getUser().send().notice("Disconnecting from server");
                if (arg.length > 1 + arrayOffset) {
                    PM.getBot().sendIRC().quitServer(argJoiner(arg, 1));
                } else {
                    PM.getBot().sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
                }
                try {
                    wait(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.exit(0);
            }

// !rejoin - Rejoins all channels
            if (PM.getMessage().equalsIgnoreCase(prefix + "rejoin")) {
                ImmutableList<String> autoChannels = PM.getBot().getConfiguration().getAutoJoinChannels().values().asList();
                for (int i = 0; i < autoChannels.size(); i++) {
                    PM.getBot().send().joinChannel(autoChannels.get(i));
                }
            }
        } else if (arg[0].startsWith(prefix) && bools.get(respondToPMs)) {
            if (notifiedUserList.containsKey(PM.getUserHostmask())) {
                if (notifiedUserList.get(PM.getUserHostmask()) < 3) {
                    permError(PM.getUser());
                    PM.getBot().sendIRC().notice(currentNick, "Attempted use of PM commands by " + PM.getUser().getNick() + ". The command used was \"" + PM.getMessage() + "\"");
                    notifiedUserList.put(PM.getUserHostmask(), notifiedUserList.get(PM.getUserHostmask()) + 1);
                }
            } else {
                notifiedUserList.put(PM.getUserHostmask(), 1);
                permError(PM.getUser());
                PM.getBot().sendIRC().notice(currentNick, "Attempted use of PM commands by " + PM.getUser().getNick() + ". The command used was \"" + PM.getMessage() + "\"");
            }
        }
        debug.updateBot(PM.getBot());
        checkUserNote(PM, PM.getUser().getNick(), null);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onNotice(NoticeEvent event) {
        String message = event.getMessage();
        //noinspection ConstantConditions
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
                event.getBot().sendIRC().notice(currentNick, "Got notice from " + event.getUser().getNick() + ". Notice was : " + event.getMessage());
            }
        }
        checkUserNote(event, event.getUser().getNick(), null);
        if (event.getBot().isConnected()) {
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
        debug.updateBot(event.getBot());
    }

    public void onJoin(JoinEvent join) {
        System.out.println("User Joined");
        if (checkOP(join.getChannel())) {
            if (checkPerm(join.getUser(), 0)) {
                join.getChannel().send().voice(join.getUserHostmask());
            }
        }
        //noinspection ConstantConditions
        checkUserNote(join, join.getUser().getNick(), join.getChannel().getName());
        debug.updateBot(join.getBot());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onNickChange(NickChangeEvent nick) {
        if (nick.getNewNick().equalsIgnoreCase(currentNick)) {
            currentNick = null;
            currentUsername = null;
            currentHost = null;
            System.out.println("resetting Authed nick");
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }

        if (nick.getOldNick().equalsIgnoreCase(currentNick)) {
            currentNick = nick.getNewNick();
            //noinspection ConstantConditions
            currentUsername = nick.getUser().getLogin();
            currentHost = nick.getUser().getHostname();
            System.out.println("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostname());
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
        debug.updateBot(nick.getBot());
    }

    public void onNickAlreadyInUse(NickAlreadyInUseEvent nick) {
        bools.set(nickInUse);
        nick.respond(nick.getUsedNick() + 1);
    }

    public void onKick(KickEvent kick) {
        //noinspection ConstantConditions
        if (kick.getRecipient().getNick().equalsIgnoreCase(kick.getBot().getNick())) {
            kick.getBot().send().joinChannel(kick.getChannel().getName());
        }
    }

    public void onUnknown(UnknownEvent event) {
        String line = event.getLine();
        if (line.contains("\u0001AVATAR\u0001")) {
            //noinspection ConstantConditions
            line = line.substring(line.indexOf(":") + 1, line.indexOf("!"));
            event.getBot().send().notice(line, "\u0001AVATAR " + avatar + "\u0001");
        }
        System.out.println("Recieved unknown: " + event.getLine());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public boolean checkOP(Channel chn) {
        User bot = chn.getBot().getUserBot();
        return chn.isHalfOp(bot) || chn.isOp(bot) || chn.isSuperOp(bot) || chn.isOwner(bot);
    }

    /**
     * tells the user they don't have permission to use the command
     *
     * @param user User trying to use command
     */
    private void permError(User user) {
        int num = randInt(0, listOfNoes.length - 1);
        String comeback = listOfNoes[num];
        user.send().notice(comeback);
    }

    /**
     * same as permError() except to be used in channels
     *
     * @param event Channel that the user used the command in
     */
    private void permErrorchn(MessageEvent event) {
        int num = randInt(0, listOfNoes.length - 1);
        String comeback = listOfNoes[num];
        sendMessage(event, comeback, true);
    }

    /**
     * Checks if the user attempting to use the command is allowed
     *
     * @param user User trying to use command
     * @return Boolean true if allowed, false if not
     */
    private boolean checkPerm(User user, int requiredUserLevel) {
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
                String nick = ident.substring(0, ident.indexOf("!"));
                String userName = ident.substring(ident.indexOf("!") + 1, ident.indexOf("@"));
                String Hostname = ident.substring(ident.indexOf("@") + 1);
                if (nick.equalsIgnoreCase(user.getNick()) || nick.equalsIgnoreCase("*")) {
                    if (userName.equalsIgnoreCase(user.getLogin()) || userName.equalsIgnoreCase("*")) {
                        if (Hostname.equalsIgnoreCase(user.getHostname()) || Hostname.equalsIgnoreCase("*")) {
                            return authedUserLevel.get(index) >= requiredUserLevel;
                        }
                        }
                    }
                index--;
                }
            ArrayList<UserLevel> levels = Lists.newArrayList(user.getUserLevels(lastEvent.getChannel()).iterator());
            if (requiredUserLevel <= getUserLevel(levels)) {
                return true;
            }
            }
        return false;
    }

    private String[] splitMessage(String stringToSplit) {
        return splitMessage(stringToSplit, 0);
    }

    private String[] splitMessage(String stringToSplit, int amountToSplit) {
        if (stringToSplit == null)
            return new String[0];

        List<String> list = new ArrayList<>();
        Matcher argSep = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(stringToSplit);
        while (argSep.find())
            list.add(argSep.group(1));

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
        return list.toArray(new String[list.size()]);
    }

    private void sendError(MessageEvent event, Exception e) {
        System.out.println("Error: " + e.getCause());
        String cause;
        if (bools.get(color)) {
            cause = Colors.RED + "Error: " + e.getCause();
        } else {
            cause = "Error: " + e.getCause();
        }
        String from = ". From " + e;
        if (cause.contains("jdk.nashorn.internal.runtime.ParserException") || from.contains("javax.script.ScriptException")) {
            if (cause.contains("Expected an operand but found eof")) {
                sendMessage(event, "Your expression has spaces so it needs to be enclosed in quotes", false);
            } else if (cause.contains("Expected an operand but found")) {
                sendMessage(event, "There was a syntax error", false);
            } else if (cause.contains("TypeError: Cannot read property")) {
                sendMessage(event, "There was a type error, Cannot read property", false);
            } else {
                if (cause.contains("\r") || cause.contains("\n")) {
                    sendMessage(event, cause.substring(0, cause.indexOf("\r")), false);
                } else {
                    sendMessage(event, cause, false);
                }
            }
        } else {
            sendMessage(event, cause + from, false);
        }
    }

    private String botTalk(String bot, String message) throws Exception {
        if (bot.equalsIgnoreCase("clever")) {
            return cleverBotsession.think(message);
        } else if (bot.equalsIgnoreCase("pandora")) {
            return pandoraBotsession.think(message);
        } else if (bot.equalsIgnoreCase("jabber") || bot.equalsIgnoreCase("jabberwacky")) {
            return jabberBotsession.think(message);
        } else {
            return "Error, not a valid bot";
        }
    }

    private void saveData(MessageEvent event) {
        try {
            String network = event.getBot().getServerInfo().getNetwork();
            if (network == null) {
                network = event.getBot().getServerHostname();
                network = network.substring(network.indexOf(".") + 1, network.lastIndexOf("."));
            }
            SaveDataStore save = new SaveDataStore(authedUser, authedUserLevel, DNDJoined, DNDList);
            SaveDataStore universalSave = new SaveDataStore(noteList.get(), avatar.get(), memes.get(), FCList.get());
            FileWriter writer = new FileWriter("Data/" + network + "-Data.json");
            FileWriter uniWriter = new FileWriter("Data/Data.json");
            writer.write(gson.toJson(save));
            writer.close();

            uniWriter.write(gson.toJson(universalSave));
            uniWriter.close();

            //System.out.println("Data saved!");
        } catch (Exception e) {
            sendError(event, e);
        }
    }

    private void checkUserNote(Event event, String user, String channel) {
        int i = 0;
        List<Integer> indexList = new ArrayList<>();
        while (i < noteList.get().size()) {
            if (noteList.get().get(i).getReceiver().equalsIgnoreCase(user)) {
                indexList.add(i);
            }
            i++;
        }
        try {
            if (i != -1) {
                while (!indexList.isEmpty()) {
                    System.out.println("Note Loop Start");
                    int index = indexList.size() - 1;
                    System.out.println("Index " + index);
                    String receiver = noteList.get().get(index).getReceiver();
                    System.out.println(receiver);
                    String message = noteList.get().get(index).displayMessage();
                    System.out.println(message);
                    if (channel != null) {
                        sendMessage(channel, message);
                    } else {
                        event.getBot().sendIRC().notice(receiver, message);
                    }
                    noteList.get().remove(index);
                    indexList.remove(index);
                    System.out.println(" Note Loop End");

                }
            }
        } catch (Exception e) {
            sendError(lastEvent, e);
        }

    }

    private void setDebugInfo(MessageEvent event) {
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


        debug.updateBot(event.getBot());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    private String argJoiner(String[] args, int argToStartFrom) throws ArrayIndexOutOfBoundsException {
        int length = args.length;
        String strToReturn = "";
        for (; length > argToStartFrom; argToStartFrom++) {
            strToReturn += args[argToStartFrom] + " ";
        }
        return strToReturn;
    }

    private boolean commandChecker(String[] args, String command) { //todo Make sure every command starts with this
        if (args[0].startsWith(prefix)) {
            if (prefix.length() > 1 && !prefix.endsWith(".")) {
                return args[1].equalsIgnoreCase(command);
            } else {
                return args[0].equalsIgnoreCase(prefix + command);
            }
        } else {
            //todo Make it check for bots nick
        }
        return false;
    }

    private String fullNameToString(Language language) {
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

    private enum MessageModes {
        normal, reversed, wordReversed, scrambled, wordScrambled
    }


}