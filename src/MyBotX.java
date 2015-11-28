/**
 * Created by Lil-G on 10/11/2015.
 * Main bot class
 */


import com.fathzer.soft.javaluator.StaticVariableSet;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
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
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericMessageEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyBotX extends ListenerAdapter {
    final static File WIKTIONARY_DIRECTORY = new File("Data\\Wiktionary");
    final int BLOCKS = 128;
    final int BLOCKSMB = 8 * BLOCKS;
    final int BLOCKSGB = 8192 * BLOCKS;
    String prefix = "!";
    String consolePrefix = "\\";
    boolean jokeCommands = true;
    boolean chngCMDRan = false;
    //boolean spinStarted = false;
    String[] dictionary = {"i don't know what \"%s\" is, do i look like a dictionary?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck."};
    String[] commands = {"HelpMe", " Time", " calcj", " StringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " Login", " GetLogin", " GetID", " GetSate", " ChngCMD", " SayThis", " ToSciNo", " Trans", " DebugVar", " RunCmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", "NoteJ (Mostly fixed)", " jtoggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
    String currentNick = "Lil-G";
    String currentUsername = "GameGenuis";
    String currentHost = "friendly.local.noob";
    String PASSWORD = setPassword();
    int i = 0;
    ChatterBotFactory factory = new ChatterBotFactory();
    ChatterBot cleverBot;
    ChatterBotSession cleverBotsession;
    boolean cleverBotInt;
    ChatterBot pandoraBot;
    ChatterBotSession pandoraBotsession;
    boolean pandoraBotInt;
    String VERSION = "PircBotX: 2.1-20151112.042241-148. BotVersion: 2.0";
    MessageEvent lastEvent;
    ExtendedDoubleEvaluator calc = new ExtendedDoubleEvaluator();
    StaticVariableSet<Double> variables = new StaticVariableSet<>();
    runCMD singleCMD = null;
    List<Note> noteList = new ArrayList<>();
    List<String> authedUser = new ArrayList<>();
    List<Integer> authedUserLevel = new ArrayList<>();
    List<String> DNDJoined = new ArrayList<>();
    List<DNDPlayer> DNDList = new ArrayList<>();
    String DNDDungeonMaster = "Null";
    Dungeon DNDDungeon = new Dungeon();
    DebugWindow debug;
    int jokeCommandDebugVar = 30;
    CommandLine terminal;
    boolean nickInUse = false;
    String counter = "";
    int countercount = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    public static String getBytes(String bytes) {
        byte[] Bytes = bytes.getBytes();
        return Arrays.toString(Bytes);
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
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
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    public static int gc() {
        int timesRan = 0;
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
            timesRan++;
        }
        return timesRan;
    }

    public void onConnect(ConnectEvent event) throws Exception {

        event.getBot().sendIRC().mode(event.getBot().getNick(), "+B");

        debug = new DebugWindow(event.getBot().getNick());
        System.out.println("Debug window created");
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);

        if (nickInUse) {
            event.getBot().sendRaw().rawLineNow("ns recover " + event.getBot().getNick() + " " + PASSWORD);
        }

        BufferedReader br = new BufferedReader(new FileReader("Data/Data.json"));
        SaveDataStore save = gson.fromJson(br, SaveDataStore.class);
        noteList = save.getNoteList();
        authedUser = save.getAuthedUser();
        authedUserLevel = save.getAuthedUserLevel();
        DNDJoined = save.getDNDJoined();
        DNDList = save.getDNDList();
    }

    @Override
    public void onMessage(MessageEvent event) {
        lastEvent = event;
        String[] arg = splitMessage(event.getMessage());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);

        checkIfUserHasANote(event, event.getUser().getNick(), true);

// !helpMe
        if (arg[0].equalsIgnoreCase(prefix + "helpme")) {
            if (checkPerm(event.getUser(), 0)) {

                if (event.getMessage().equalsIgnoreCase(prefix + "helpme")) {
                    event.respond("List of commands so far. for more info on these commands do " + prefix + "helpme. commands with \"Joke: \" are joke commands that can be disabled");
                    ArrayList<String> cmdhelp = new ArrayList<>(Arrays.asList(commands));
                    event.respond(cmdhelp.toString());
                } else if (arg[1].equalsIgnoreCase("Helpme")) {
                    event.respond("Really? ಠ_ಠ");
                } else if (arg[1].equalsIgnoreCase("time")) {
                    event.respond("Displays info from the Date class");
                } else if (arg[1].equalsIgnoreCase("Hello")) {
                    event.respond("Just your average \"hello world!\" program");
                } else if (arg[1].equalsIgnoreCase("Attempt")) {
                    event.respond("Its a inside-joke to my friends in school. If i'm not away, ask me and i'll tell you about it.");
                } else if (arg[1].equalsIgnoreCase("RandomNum")) {
                    event.respond("Creates a random number between the 2 integers");
                    event.respond("Usage: first number sets the minimum number, second sets the maximum");
                } else if (arg[1].equalsIgnoreCase("version")) {
                    event.respond("Displays the version of the bot");
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    event.respond("Converts a String into a Byte array");
                } else if (arg[1].equalsIgnoreCase("temp")) {
                    event.respond("Converts a temperature unit to another unit.");
                    event.respond("Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
                } else if (arg[1].equalsIgnoreCase("chat")) {
                    event.respond("This command functions like ELIZA. Talk to it and it talks back.");
                    event.respond("Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky (JabberWacky not yet implemented). Second parameter is the PM.getMessage() to send. Could also be the special param \"\\setup\" to actually start the bot.");
                } else if (arg[1].equalsIgnoreCase("calcj")) {
                    event.respond("This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
                    event.respond("Usage 1: The simple way is to type out the expression without any variables. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
                } else if (arg[1].equalsIgnoreCase("CalcJS")) {
                    event.respond("This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
                    event.respond("Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    event.respond("Converts a String into a Byte array");
                } else if (arg[1].equalsIgnoreCase("NoteJ")) {
                    event.respond("Allows the user to leave notes");
                    event.respond("Subcommand add: adds a note. Usage: add <Nick to leave note to> <message>. Subcommand del: Deletes a set note Usage: del <Given ID>. Subcommand list: Lists notes you've left");
                } else {
                    event.respond("That either isn't a command, or " + currentNick + " hasn't add that to the help yet.");
                }
            }

        }

// !getChannelName - Gets channel name, for debuging
        if (event.getMessage().equalsIgnoreCase(prefix + "GetChannelName")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond(event.getChannel().getName());
            }
        }

// !Time - Tell the time
        if (event.getMessage().equalsIgnoreCase(prefix + "time")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    String time = new Date().toString();
                    event.respond(" The time is now " + time);
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !perms - edit privileged users
        if (arg[0].equalsIgnoreCase(prefix + "perms")) {
            if (checkPerm(event.getUser(), 5)) {
                if (arg[1].equalsIgnoreCase("set")) {
                    try {
                        if (authedUser.contains(arg[2])) {
                            try {
                                authedUserLevel.set(authedUser.indexOf(arg[2]), Integer.parseInt(arg[3]));
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                            event.respond("Set " + arg[2] + " To level " + arg[3]);
                        } else {
                            try {
                                authedUser.add(arg[2]);
                                authedUserLevel.add(Integer.parseInt(arg[3]));
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                            event.respond("Added " + arg[2] + " To authed users with level " + arg[3]);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else if (arg[1].equalsIgnoreCase("del")) {
                    try {
                        int index = authedUser.indexOf(arg[2]);
                        authedUserLevel.remove(index);
                        authedUser.remove(index);
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                    event.respond("Removed " + arg[2] + " from the authed user list");
                } else if (arg[1].equalsIgnoreCase("clear")) {
                    authedUser.clear();
                    authedUserLevel.clear();
                    event.respond("Permission list cleared");
                } else if (arg[1].equalsIgnoreCase("List")) {
                    event.respond(authedUser.toString());
                }
            }
        }

// !calcj - calculate a expression
        if (arg[0].equalsIgnoreCase(prefix + "calcJ")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    if (arg.length != 2) {
                        double x = Double.parseDouble(arg[1]);
                        double step = Double.parseDouble(arg[2]);
                        double calcAmount = Double.parseDouble(arg[3]) - 1;
                        int count = 0;
                        ArrayList<Double> eval = new ArrayList<>(0);

                        while (count <= calcAmount) {
                            variables.set("x", x);
                            eval.add(calc.evaluate(arg[4].toLowerCase(), variables));
                            x += step;
                            count++;
                        }
                        event.respond(eval.toString());
                    } else {
                        double eval = calc.evaluate(arg[1]);
                        event.respond("" + eval);
                    }
                } catch (Exception e) {
                    event.getChannel().send().message("Error: " + e);
                }
            }
        }

// !GC - Runs the garbage collector
        if (arg[0].equalsIgnoreCase(prefix + "GC")) {
            int num = gc();
            if (num == 1) {
                event.respond("It took " + num + " Time to run the Garbage collector");
            } else {
                event.respond("It took " + num + " Times to run the Garbage collector");
            }
        }

// !CalcJS - evaluates a expression in JavaScript
        if (arg[0].equalsIgnoreCase(prefix + "calcJS")) {
            if (checkPerm(event.getUser(), 0)) {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                String factorialFunct = "function factorial(num) { if (num < 0) { return -1; } else if (num == 0) { return 1; } var tmp = num; while (num-- > 2) { tmp *= num; } return tmp; } var life = 42; ";
                String eval;
                try {
                    if (arg[1].contains(";")) {
                        if (!checkPerm(event.getUser(), 2)) {
                            event.respond("Sorry, only Privileged users can use ;");
                        } else {
                            eval = engine.eval(factorialFunct + arg[1]) + "";
                            if (isNumeric(eval)) {
                                if (arg.length < 3) {
                                    event.respond(eval);
                                    System.out.println("Outputting as decimal");
                                } else {
                                    eval = Long.toString(Long.parseLong(eval), Integer.parseInt(arg[2]));
                                    event.respond(eval);
                                    System.out.println("Outputting as base " + arg[2]);
                                }
                            } else {
                                event.respond(eval);
                            }
                        }
                    } else {
                        eval = engine.eval(factorialFunct + arg[1]) + "";
                        if (isNumeric(eval)) {
                            if (arg.length < 3) {
                                event.respond(eval);
                                System.out.println("Outputting as decimal");
                            } else {
                                eval = Long.toString(Long.parseLong(eval), Integer.parseInt(arg[2]));
                                event.respond(eval);
                                System.out.println("Outputting as base " + arg[2]);
                            }
                        } else {
                            event.respond(eval);
                        }
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// if someone says hi, tell them its a bot
        if ((event.getMessage().contains("hi") || event.getMessage().contains("hey") || event.getMessage().contains("hello")) && event.getMessage().contains(event.getBot().getNick())) {
            if (checkPerm(event.getUser(), 0) && !checkPerm(event.getUser(), 5)) {
                event.respond("I'm a bot");
            }
        }

// !count - counts amount of something
        if (arg[0].equalsIgnoreCase(prefix + "count")) {
            if (arg.length != 1 && arg[1].equalsIgnoreCase("setup")) {
                counter = arg[2];
                if (arg.length == 4) {
                    countercount = Integer.parseInt(arg[3]);
                }
            }
            if (event.getMessage().equalsIgnoreCase(prefix + "count")) {
                countercount++;
                event.getChannel().send().message("Number of times that " + counter + " is: " + countercount);
            }
        }

// !StringToBytes - convert a String into a Byte array
        if (arg[0].equalsIgnoreCase(prefix + "StringToBytes")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    event.respond(getBytes(arg[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    event.respond("Not enough args. Must provide a string");
                }
            }
        }

// !LookUp - Looks up a word in the Wiktionary
        if (arg[0].equalsIgnoreCase(prefix + "Lookup")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    String message = "Null";
                    System.out.println("Looking up word");
                    // Connect to the Wiktionary database.
                    System.out.println("Opening dictionary");
                    IWiktionaryEdition wkt = JWKTL.openEdition(WIKTIONARY_DIRECTORY);
                    System.out.println("Getting page for word");
                    IWiktionaryPage page = wkt.getPageForWord(arg[1]);
                    if (page != null) {
                        System.out.println("Getting entry");
                        IWiktionaryEntry entry = page.getEntry(0);
                        System.out.println("getting sense");
                        IWiktionarySense sense = entry.getSense(1);
                        System.out.println("getting Plain text");
                        if (arg.length > 2) {
                            if (arg[2].equalsIgnoreCase("Example")) {
                                if (sense.getExamples().size() > 0) {
                                    message = sense.getExamples().get(0).getPlainText();
                                } else {
                                    event.respond("No examples found");
                                }
                            }
                        } else {
                            message = sense.getGloss().getPlainText();
                        }
                        System.out.println("Sending message");
                        event.respond(message);
                    } else {
                        event.respond("That page couldn't be found.");
                    }

                    // Close the database connection.
                    wkt.close();
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !chat - chat's with a internet conversation bot
        if (arg[0].equalsIgnoreCase(prefix + "chat")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg[1].equalsIgnoreCase("clever")) {
                    if (arg[2].equalsIgnoreCase("\\setup")) {
                        try {
                            cleverBot = factory.create(ChatterBotType.CLEVERBOT);
                            cleverBotsession = cleverBot.createSession();
                            cleverBotInt = true;
                            event.getUser().send().notice("CleverBot started");
                        } catch (Exception e) {
                            event.respond("Error: Could not create clever bot session. Error was: " + e);
                        }
                    } else {
                        if (cleverBotInt) {
                            try {
                                event.respond(" " + botTalk("clever", arg[2]));
                            } catch (Exception e) {
                                event.respond("Error: Problem with bot. Error was: " + e);
                            }
                        } else {
                            event.respond(" You have to start CleverBot before you can talk to it. star it with \\setup");
                        }
                    }

                } else if (arg[1].equalsIgnoreCase("pandora")) {
                    if (arg[2].equalsIgnoreCase("\\setup")) {
                        try {
                            pandoraBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                            pandoraBotsession = pandoraBot.createSession();
                            pandoraBotInt = true;
                            event.getUser().send().notice("PandoraBot started");
                        } catch (Exception e) {
                            event.respond("Error: Could not create pandora bot session. Error was: " + e);
                        }
                    } else {
                        if (pandoraBotInt) {
                            try {
                                event.respond(" " + botTalk("pandora", arg[2]));
                            } catch (Exception e) {
                                event.respond("Error: Problem with bot. Error was: " + e);
                            }
                        } else {
                            event.respond(" You have to start PandoraBot before you can talk to it. start it with \\setup");
                        }
                    }


                }
            }
        }

// !temp - Converts a unit of temperature to another
        if (arg[0].equalsIgnoreCase(prefix + "temp")) {
            if (checkPerm(event.getUser(), 0)) {
                int temp = Integer.parseInt(arg[3]);
                double ans = 0;
                String unit = "err";
                if (arg[1].equalsIgnoreCase("F")) {
                    if (arg[2].equalsIgnoreCase("C")) {
                        ans = (temp - 32) * 5 / 9;
                        unit = "C";
                    } else if (arg[2].equalsIgnoreCase("K")) {
                        ans = (temp - 32) * 5 / 9 + 273.15;
                        unit = "K";
                    }
                } else if (arg[1].equalsIgnoreCase("C")) {
                    if (arg[2].equalsIgnoreCase("F")) {
                        ans = (temp * 9 / 5) + 32;
                        unit = "F";
                    } else if (arg[2].equalsIgnoreCase("K") && temp < 0) {
                        ans = temp + 273.15;
                        unit = "K";
                    }
                } else if (arg[1].equalsIgnoreCase("K")) {
                    if (arg[2].equalsIgnoreCase("F")) {
                        ans = (temp - 273.15) * 9 / 5 + 32;
                        unit = "F";
                    } else if (arg[2].equalsIgnoreCase("C")) {
                        ans = temp - 273.15;
                        unit = "C";
                    }
                }
                if (unit.equalsIgnoreCase("err")) {
                    event.respond("Incorrect arguments.");
                } else {
                    event.respond(" " + ans + unit);
                }
            }
        }


// !blockconv - Converts blocks to bytes
        if (arg[0].equalsIgnoreCase(prefix + "blockconv")) {
            if (checkPerm(event.getUser(), 0)) {
                int data = Integer.parseInt(arg[3]);
                double ans = 0;
                String unit = "err";
                boolean notify = true;
                if (arg[1].equalsIgnoreCase("blocks")) {
                    if (arg[2].equalsIgnoreCase("kb")) {
                        ans = BLOCKS * data;
                        unit = "KB";
                        notify = false;
                    }
                } else if (arg[1].equalsIgnoreCase("kb")) {
                    if (arg[2].equalsIgnoreCase("blocks")) {
                        ans = data / BLOCKS;
                        unit = "Blocks";
                        notify = false;
                    }
                } else if (arg[1].equalsIgnoreCase("mb")) {
                    if (arg[2].equalsIgnoreCase("blocks")) {
                        ans = data / BLOCKSMB;
                        unit = "Blocks";
                    }
                } else if (arg[1].equalsIgnoreCase("gb")) {
                    if (arg[2].equalsIgnoreCase("blocks")) {
                        ans = data / BLOCKSGB;
                        unit = "Blocks";
                    }
                }
                if (unit.equals("err")) {
                    event.respond("Incorrect arguments.");
                } else {
                    event.respond(" " + ans + unit);
                    if (notify)
                        event.respond("NOTICE: this command currently doesn't work like it should. The only conversion that works is blocks to kb and kb to blocks");
                }
            }
        }

// !notej - Leaves notes
        if (arg[0].equalsIgnoreCase(prefix + "notej")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg[1].equalsIgnoreCase("del")) {
                    try {
                        int i = 0;
                        int index = -1;
                        boolean found = false;
                        while (i < noteList.size() && !found) {
                            if (noteList.get(i).getId().toString().equals(arg[2])) {
                                found = true;
                                index = i;
                            } else {
                                i++;
                            }
                        }
                        if (found) {
                            if (event.getUser().getNick().equalsIgnoreCase(noteList.get(index).sender)) {
                                noteList.remove(index);
                                event.respond("Note " + arg[2] + " Deleted");
                            } else {
                                event.respond("Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it");
                            }
                        } else {
                            event.respond("That ID wasn't found.");
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }

                if (arg[1].equalsIgnoreCase("add")) {
                    try {
                        Note note = new Note(event.getUser().getNick(), arg[2], arg[3], event.getChannel().getName());
                        noteList.add(note);
                        event.getChannel().send().message("Left note \"" + arg[3] + "\" for \"" + arg[2] + "\".");
                        event.getUser().send().notice("ID is \"" + noteList.get(noteList.indexOf(note)).getId().toString() + "\"");
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }

                if (arg[1].equalsIgnoreCase("list")) {
                    int i = 0;
                    List<String> found = new ArrayList<>();
                    List<String> foundUUID = new ArrayList<>();
                    while (noteList.size() > i) {
                        if (noteList.get(i).getSender().equalsIgnoreCase(event.getUser().getNick())) {
                            found.add(noteList.get(i).getMessageForList());
                            foundUUID.add(noteList.get(i).getUUIDForList());
                        }
                        i++;
                    }
                    event.respond(found.toString());
                    event.getUser().send().notice(foundUUID.toString());
                }
                saveData(event);
            }
        }

// !Hello - Standard "Hello world" command
        if (event.getMessage().equalsIgnoreCase(prefix + "hello")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond("Hello World!");
            }
        }

// !Bot - Explains that "yes this is a bot"
        if (event.getMessage().equalsIgnoreCase(prefix + "bot")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond("Yes, this is " + currentNick + "'s bot.");
            }
        }

// !getname - gets the name of the bot
        if (event.getMessage().equalsIgnoreCase(prefix + "getname")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond(event.getBot().getUserBot().getRealName());
            }
        }

// !version - gets the version of the bot
        if (event.getMessage().equalsIgnoreCase(prefix + "version")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond("Version: " + VERSION);
            }
        }

// !login - attempts to login to nickserv
        if (event.getMessage().equalsIgnoreCase(prefix + "login")) {
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
        if (event.getMessage().equalsIgnoreCase(prefix + "getLogin")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond(event.getBot().getUserBot().getLogin());
            }
        }

// !getID - gets the ID of the user
        if (arg[0].equalsIgnoreCase(prefix + "getID")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond("You are :" + event.getUser().getUserId());
            }
        }

// !RandomNum - Gives the user a random Number
        if (arg[0].equalsIgnoreCase(prefix + "randomnum")) {
            if (checkPerm(event.getUser(), 0)) {
                long num1, num2;
                if (arg[1].contains("0x")) {
                    arg[1] = arg[1].substring(2);
                    num1 = Long.parseLong(arg[1], 16);
                } else {
                    num1 = Long.parseLong(arg[1], 10);
                }
                if (arg[2].contains("0x")) {
                    arg[2] = arg[2].substring(2);
                    num2 = Long.parseLong(arg[2], 16);
                } else {
                    num2 = Long.parseLong(arg[2], 10);
                }

                event.respond(" " + randInt((int) num1, (int) num2));
            }
        }

// !getState - Displays what version the bot is on
        if (event.getMessage().equalsIgnoreCase(prefix + "getState")) {
            if (checkPerm(event.getUser(), 0)) {
                event.respond("State is: " + event.getBot().getState());
            }
        }

// !ChngCMD - Changes the command prefix when it isn't the standard "!"
        if (arg[0].equalsIgnoreCase("!chngcmd") && !prefix.equals("!")) {
            if (checkPerm(event.getUser(), 5)) {
                prefix = arg[1];
                event.respond("Command variable is now \"" + prefix + "\"");
                chngCMDRan = true;
            } else {
                permError(event.getUser(), "can change the command character");
            }
        }

// !ChngCMD - Changes the command prefix
        if (arg[0].equalsIgnoreCase(prefix + "chngcmd") && !chngCMDRan) {
            if (checkPerm(event.getUser(), 5)) {
                prefix = arg[1];
                event.respond("Command variable is now \"" + prefix + "\"");
            } else {
                permError(event.getUser(), "can change the command character");
            }
        }
        chngCMDRan = false;

// !Saythis - Tells the bot to say someting
        if (arg[0].equalsIgnoreCase(prefix + "saythis")) {
            if (checkPerm(event.getUser(), 4)) {
                event.respond(arg[1]);
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !LoopSay - Tells the bot to say someting and loop it
        if (arg[0].equalsIgnoreCase(prefix + "loopsay")) {
            if (checkPerm(event.getUser(), 5)) {
                int i = Integer.parseInt(arg[1]);
                int loopCount = 0;
                try {
                    while (i > loopCount) {
                        event.getChannel().send().message(arg[2]);
                        loopCount++;
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !ToSciNo - converts a number to scientific notation
        if (arg[0].equalsIgnoreCase(prefix + "ToSciNo")) {
            if (checkPerm(event.getUser(), 0)) {
                NumberFormat formatter = new DecimalFormat("0.######E0");

                long num = Long.parseLong(arg[1]);
                try {
                    event.respond(formatter.format(num));
                } catch (Exception e) {
                    sendError(event, e);
                    //log(e.toString());
                }
            }
        }

// !DND - Dungeons and dragons. RNG the Game
        if (arg[0].equalsIgnoreCase(prefix + "DND")) {
            if (checkPerm(event.getUser(), 0)) {
                if (event.getMessage().equalsIgnoreCase(prefix + "DND join")) {
                    event.respond("Syntax: " + prefix + "DND join <Character name> <Race (can be anything right now)> <Class> {<Familiar name> <Familiar Species>}");
                } else {
                    if (arg[1].equalsIgnoreCase("join")) {
                        if (DNDJoined.contains(event.getUser().getNick())) {
                            event.respond("You are already in the list!");
                        } else {
                            if (arg.length == 5) {
                                if (DNDPlayer.ifClassExists(arg[3])) {
                                    DNDList.add(new DNDPlayer(arg[2], arg[3], arg[4], event.getUser().getNick()));
                                    DNDJoined.add(event.getUser().getNick());
                                    event.respond("Added \"" + arg[2] + "\" the " + arg[4] + " to the game");
                                    if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                        debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                    }
                                } else {
                                    event.respond("That class doesn't exist!");
                                }
                            }
                            if (arg.length == 7) {
                                if (DNDPlayer.ifClassExists(arg[4])) {
                                    if (DNDPlayer.ifSpeciesExists(arg[6])) {
                                        DNDList.add(new DNDPlayer(arg[2], arg[3], arg[4], event.getUser().getNick(), arg[5], arg[6]));
                                        DNDJoined.add(event.getUser().getNick());
                                        event.respond("Added \"" + arg[2] + "\" the " + arg[4] + " with " + arg[5] + " The " + arg[6] + " to the game");

                                        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                            debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                            debug.setFamiliar(DNDList.get(DNDJoined.indexOf(currentNick)).getFamiliar().getName());
                                        }
                                    } else {
                                        event.respond("Class doesn't exist");
                                    }
                                } else {
                                    event.respond("Class doesn't exist");
                                }
                            } else {
                                event.respond("Invalid number of arguments");
                            }
                        }
                    }
                }
                if (arg[1].equalsIgnoreCase("info")) {
                    try {
                        int index = DNDJoined.indexOf(event.getUser().getNick());
                        if (index > -1) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                debug.setPlayerName(DNDList.get(index).getPlayerName());
                                debug.setPlayerHP(DNDList.get(index).getHPAmounts());
                                debug.setPlayerXP(DNDList.get(index).getXPAmounts());

                                debug.setFamiliar(DNDList.get(index).getFamiliar().getName());
                                debug.setFamiliarHP(DNDList.get(index).getFamiliar().getHPAmounts());
                                debug.setFamiliarXP(DNDList.get(index).getFamiliar().getXPAmounts());
                            }
                            event.respond(DNDList.get(index).toString());
                        } else {
                            event.respond("You have to join first!");
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
                if (arg[1].equalsIgnoreCase("List")) {
                    try {
                        event.respond(DNDList.toString());
                    } catch (Exception e) {
                        sendError(event, e);
                    }

                    int index = DNDJoined.indexOf(event.getUser().getNick());

                    if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                        debug.setPlayerName(DNDList.get(index).getPlayerName());
                        debug.setPlayerHP(DNDList.get(index).getHPAmounts());
                        debug.setPlayerXP(DNDList.get(index).getXPAmounts());

                        debug.setFamiliar(DNDList.get(index).getFamiliar().getName());
                        debug.setFamiliarHP(DNDList.get(index).getFamiliar().getHPAmounts());
                        debug.setFamiliarXP(DNDList.get(index).getFamiliar().getXPAmounts());
                    }
                }
                if (arg[1].equalsIgnoreCase("ListClass")) {
                    event.respond("List of classes: " + Arrays.toString(DNDPlayer.DNDClasses.values()));
                }
                if (arg[1].equalsIgnoreCase("ListSpecies")) {
                    event.respond("List of classes: " + Arrays.toString(DNDPlayer.DNDFamiliars.values()));
                }
                if (arg[1].equalsIgnoreCase("DM")) {

                }
                if (arg[1].equalsIgnoreCase("Test")) { //testing commands.
                    if (true) { //checkPerm(event.getUser())
                        try {
                            int index = DNDJoined.indexOf(event.getUser().getNick());
                            if (arg[2].equalsIgnoreCase("addItem")) {
                                DNDList.get(index).addInventory(arg[3]);
                                event.respond("Added " + arg[3] + " to your inventory");
                            }
                            if (arg[2].equalsIgnoreCase("getItems")) {
                                event.respond(DNDList.get(index).getInventory());
                            }
                            if (arg[2].equalsIgnoreCase("delItem")) {
                                DNDList.get(index).removeFromInventory(arg[3]);
                                event.respond("removed " + arg[3] + " to your inventory");
                            }
                            if (arg[2].equalsIgnoreCase("addXP")) {
                                DNDList.get(index).addXP(Integer.parseInt(arg[3]));
                                event.respond("Added " + arg[3] + " to your XP");
                            }
                            if (arg[2].equalsIgnoreCase("addHP")) {
                                DNDList.get(index).addHP(Integer.parseInt(arg[3]));
                                event.respond("Added " + arg[3] + " to your HP");
                            }
                            if (arg[2].equalsIgnoreCase("subHP")) {
                                DNDList.get(index).hit(Integer.parseInt(arg[3]));
                                event.respond("Subbed " + arg[3] + " from your HP");
                            }
                            if (arg[2].equalsIgnoreCase("addXPFam")) {
                                DNDList.get(index).getFamiliar().addXP(Integer.parseInt(arg[3]));
                                event.respond("Added " + arg[3] + " to your familiar's XP");
                            }
                            if (arg[2].equalsIgnoreCase("addHPFam")) {
                                DNDList.get(index).getFamiliar().addHP(Integer.parseInt(arg[3]));
                                event.respond("Added " + arg[3] + " to your familiar's HP");
                            }
                            if (arg[2].equalsIgnoreCase("subHPFam")) {
                                DNDList.get(index).getFamiliar().hit(Integer.parseInt(arg[3]));
                                event.respond("Subbed " + arg[3] + " from your familiar's HP");
                            }
                            if (arg[2].equalsIgnoreCase("getFamiliar")) {
                                event.respond(DNDList.get(index).getFamiliar().toString());
                            }

                            if (arg[2].equalsIgnoreCase("clearList")) {
                                if (checkPerm(event.getUser(), 3)) {
                                    DNDJoined.clear();
                                    DNDList.clear();
                                    event.getChannel().send().message("DND Player lists cleared");
                                }
                            }

                            if (arg[2].equalsIgnoreCase("DelChar")) {
                                if (checkPerm(event.getUser(), 5)) {
                                    if (DNDJoined.contains(arg[3])) {
                                        DNDJoined.remove(index);
                                    }
                                }
                            }

                            if (arg[2].equalsIgnoreCase("setPos")) {
                                DNDDungeon.setLocation(Integer.parseInt(arg[3]), Integer.parseInt(arg[4]));
                                event.respond("Pos is now: " + DNDDungeon.toString());
                            }

                            if (arg[2].equalsIgnoreCase("getPos")) {
                                event.respond(DNDDungeon.toString());
                            }

                            if (arg[2].equalsIgnoreCase("movePos")) {
                                DNDDungeon.move(new Point(Integer.parseInt(arg[3]), Integer.parseInt(arg[4])));
                                event.respond(DNDDungeon.toString());
                            }

                            if (arg[2].equalsIgnoreCase("getSurroundings")) {
                                Tile[] tiles = DNDDungeon.getSurroundingTiles();
                                event.respond(" | " + tiles[7] + " | " + tiles[0] + " | " + tiles[1] + " | ");
                                event.respond(" | " + tiles[6] + " | " + tiles[8] + " | " + tiles[2] + " | ");
                                event.respond(" | " + tiles[5] + " | " + tiles[4] + " | " + tiles[3] + " | ");
                            }

                            if (arg[2].equalsIgnoreCase("genDungeon")) {
                                DNDDungeon = new Dungeon();
                                event.respond("Generated new dungeon");
                            }
                        } catch (NullPointerException e) {
                            event.respond("You have to join first! (Null pointer)");
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                }
                setDebugInfo(event);
            }
        }

// !Trans - Translate from 1 language to another
        if (arg[0].equalsIgnoreCase(prefix + "trans")) {
            if (checkPerm(event.getUser(), 0)) {
                String text = "I think there was a error";
                System.out.println("Setting key");
                YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                try {
                    if (arg[1].equalsIgnoreCase("\\detect")) {
                        event.respond(fullNameToString(Detect.execute(arg[2])));
                    } else {
                        System.out.println("Getting to lang");
                        Language to = Language.valueOf(arg[2].toUpperCase());
                        System.out.println("Getting from lang");
                        Language from = Language.valueOf(arg[1].toUpperCase());
                        System.out.println("Executing trans");
                        text = Translate.execute(arg[3], from, to);
                        System.out.println("Translating");
                        System.out.println(text);
                        event.respond(text);
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }

// !debugvar - changes a variable to the value
        if (arg[0].equalsIgnoreCase(prefix + "debugvar")) {
            if (checkPerm(event.getUser(), 5)) {
                switch (arg[1].toLowerCase()) { //Make sure strings are lowercsae
                    case "i":
                        i = Integer.parseInt(arg[2]);
                        event.respond("DEBUG: Var \"i\" is now \"" + i + "\"");
                        break;
                    case "jokenum":
                        jokeCommandDebugVar = Integer.parseInt(arg[2]);
                        event.respond("DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"");
                }
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !runcmd - Tells the bot to run a OS command
        if (arg[0].equalsIgnoreCase(prefix + "runcmd")) {
            if (checkPerm(event.getUser(), 5)) {
                try {
                    if (!arg[1].equalsIgnoreCase("stop")) {
                        try {
                            singleCMD = new runCMD(event, arg);
                            singleCMD.start();
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    }
                    if (arg[1].equalsIgnoreCase("stop")) {
                        event.respond("Stopping");
                        singleCMD.interrupt();
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            } else {
                permError(event.getUser(), "can use this command");
            }
        }

// \ - runs commands without closing at the end
        if (arg[0].startsWith(consolePrefix)) {
            if (checkPerm(event.getUser(), 5)) {
                if (arg[0].substring(1).equalsIgnoreCase("\\start")) {
                    terminal = new CommandLine(event, arg);
                    terminal.start();
                    event.respond("Command line started");
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
        if (arg[0].equalsIgnoreCase(prefix + "SayRaw")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendRaw().rawLineNow(arg[1]);
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !SayNotice - Tells the bot to send a notice
        if (arg[0].equalsIgnoreCase(prefix + "SayNotice")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().notice(arg[1], arg[2]);
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !SayCTCPCommand - Tells the bot to send a CTCP Command
        if (arg[0].equalsIgnoreCase(prefix + "SayCTCPCommand")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().ctcpCommand(arg[2], arg[1]);
            } else {
                permErrorchn(event, "can use this command");
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
        if (arg[0].equalsIgnoreCase(prefix + "leave")) {
            if (checkPerm(event.getUser(), 5)) {
                if (arg[1].length() == 2) {
                    event.getChannel().send().part(arg[1]);
                } else {
                    event.getChannel().send().part();
                }
            } else {
                permErrorchn(event, "can use this command");
            }
        }

// !respawn - Tells the bot to restart and reconnect
        if (arg[0].equalsIgnoreCase(prefix + "respawn")) {
            if (checkPerm(event.getUser(), 3)) {
                saveData(event);
                event.getBot().sendIRC().quitServer("Died! Respawning in about 10 seconds");

            } else {
                permErrorchn(event, "can restart the bot");
            }
        }

// !kill - Tells the bot to disconnect from server
        if (event.getMessage().contentEquals(prefix + "kill")) {
            if (checkPerm(event.getUser(), 5)) {
                saveData(event);
                event.getBot().sendIRC().quitServer("Died! Out of lives. Game over.");
                System.exit(0);
            } else {
                permErrorchn(event, "can kill the bot");
            }
        }


// !changenick - Changes the nick of the bot
        if (arg[0].equalsIgnoreCase(prefix + "changeNick")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().changeNick(arg[1]);
                debug.setNick(arg[1]);
            } else {
                permErrorchn(event, "can change the Nick of the bot");
            }
        }

// !SayAction - Makes the bot do a action
        if (arg[0].equalsIgnoreCase(prefix + "SayAction")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getChannel().send().action(arg[1]);
            } else {
                permErrorchn(event, "can make the bot do actions");
            }
        }

// !jtoggle - toggle joke commands
        if (arg[0].equalsIgnoreCase(prefix + "jtoggle")) {
            if (event.getMessage().equalsIgnoreCase(prefix + "jtoggle")) {
                if (checkPerm(event.getUser(), 0)) {
                    if (jokeCommands) {
                        event.respond("Joke commands are currently enabled");
                    } else {
                        event.respond("Joke commands are currently disabled");
                    }
                }
            }
            if (arg[1].equalsIgnoreCase("toggle")) {
                if (checkPerm(event.getUser(), 2)) {
                    jokeCommands = !jokeCommands;
                    if (jokeCommands) {
                        event.respond("Joke commands are now enabled");
                    } else {
                        event.respond("Joke commands are now disabled");
                    }
                } else {
                    permErrorchn(event, "can enable or disable the use of joke commands");
                }
            }
        }


// !Splatoon - Joke command - ask the splatoon question
        if (event.getMessage().equalsIgnoreCase(prefix + "Splatoon")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    event.respond(" YOU'RE A KID YOU'RE A SQUID");
                else
                    event.respond(" Sorry, Joke commands are disabled");
            }
        }

// !attempt - Joke command - NOT ATTEMPTED
        if (event.getMessage().equalsIgnoreCase(prefix + "attempt")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    event.respond(" NOT ATTEMPTED");
                else
                    event.respond(" Sorry, Joke commands are disabled");
            }
        }

//// !stfu - Joke command - say "no u"
//		if (event.getMessage().equalsIgnoreCase(prefix + "stfu")){
//			if(jokeCommands || checkPerm(event.getUser()))
//				event.respond( sender + ": " + prefix + "no u");
//		}

// !eatabowlofdicks - Joke command - joke help command
        if (event.getMessage().equalsIgnoreCase(prefix + "eatabowlofdicks")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    event.respond("no u");
            }
        }

// !myDickSize - Joke command - This was requested by Greeny in #origami64. ask him about it
        if (event.getMessage().equalsIgnoreCase(prefix + "myDickSize")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    if (event.getChannel().getName().equalsIgnoreCase("#origami64") || event.getChannel().getName().equalsIgnoreCase("#Lil-G|bot") || event.getChannel().getName().equalsIgnoreCase("#SSB")) {
                        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                            event.getChannel().send().message("Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE");
                        } else {
                            int size = randInt(0, jokeCommandDebugVar);
                            event.getChannel().send().message("8" + StringUtils.leftPad("D", size, "=") + " - " + size);
                        }
                    }
            }
        }

// !potato - Joke command - say "i am potato" in Japanese
        if (event.getMessage().equalsIgnoreCase(prefix + "potato")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1)) {
                    byte[] bytes = "わたしわポタトデス".getBytes(Charset.forName("UTF-8"));
                    String v = new String(bytes, Charset.forName("UTF-8"));
                    event.respond(v);
                } else
                    event.respond(" Sorry, Joke commands are disabled");
            }
        }

// !whatis? - Joke command -
        if (arg[0].equalsIgnoreCase(prefix + "whatis?")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1)) {
                    int num = randInt(0, dictionary.length - 1);
                    String comeback = String.format(dictionary[num], arg[1]);
                    event.respond(comeback);
                } else
                    event.respond(" Sorry, Joke commands are disabled");
            }
        }


// !GayDar - Joke command - picks random user
        if (arg[0].equalsIgnoreCase(prefix + "GayDar")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1)) {
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
                        event.getChannel().send().message("It's " + userList.get(num).toUpperCase() + "!");
                    } catch (Exception e) {
                        event.getChannel().send().message("Error: " + e);
                    }
                } else
                    event.respond(" Sorry, Joke commands are disabled");

                System.out.print(event.getMessage());
            }
        }

        saveData(lastEvent);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        debug.setCurrDM(DNDDungeonMaster);
        debug.setMessage(event.getUser().getNick() + ": " + event.getMessage());
    }

    public void onPrivateMessage(PrivateMessageEvent PM) {

        String[] arg = splitMessage(PM.getMessage());

        if (PM.getMessage().equals("\u0001AVATAR\u0001")) {
            PM.getUser().send().notice("\u0001AVATAR http://puu.sh/kA75A.jpg 19117\u0001");
        }
        if (PM.getMessage().contains("\u0001A")) {
            PM.getUser().send().notice("\u0001AVATAR http://puu.sh/kA75A.jpg 19117\u0001");
        }

// !login - Sets the authed named to the new name ...if the password is right
        if (arg[0].equalsIgnoreCase(prefix + "login")) {
            if (arg[1].equals(PASSWORD)) {
                currentNick = PM.getUser().getNick();
                currentUsername = PM.getUser().getLogin();
            } else
                PM.getUser().send().message("password is incorrect.");
        }
        //Only allow me (Lil-G) to use PM commands except for the login command
        else if (checkPerm(PM.getUser(), 5) && !arg[0].equalsIgnoreCase(prefix + "login") && arg[0].contains(prefix)) {


// !SendTo - Tells the bot to say someting on a channel
            if (arg[0].equalsIgnoreCase(prefix + "sendto")) {
                PM.getBot().sendIRC().message(arg[1], arg[2]);
            }

// !sendAction - Tells the bot to make a action on a channel
            if (arg[0].equalsIgnoreCase(prefix + "sendAction")) {
                PM.getBot().sendIRC().action(arg[1], arg[2]);
            }
// !sendRaw - Tells the bot to say a raw line
            if (arg[0].equalsIgnoreCase(prefix + "sendRaw")) {
                PM.getBot().sendRaw().rawLineNow(arg[1]);
            }

// !changenick- Changes the nick of the bot
            if (arg[0].equalsIgnoreCase(prefix + "changeNick")) {
                PM.getBot().sendIRC().changeNick(arg[1]);
                debug.setNick(arg[1]);
            }


// !Connect - Tells the bot to connect to specified channel
            if (arg[0].equalsIgnoreCase(prefix + "connect")) {
                if (arg.length != 2) {
                    PM.getBot().sendIRC().joinChannel(arg[1], arg[2]);
                } else {
                    PM.getBot().sendIRC().joinChannel(arg[1]);
                }
                PM.getUser().send().notice("Successfully connected to " + arg[1]);
            }

// !QuitServ - Tells the bot to disconnect from server
            if (PM.getMessage().equalsIgnoreCase(prefix + "quitserv")) {
                PM.getUser().send().notice("Disconnecting from server");
                PM.getBot().sendIRC().quitServer();
            }
        } else if (arg[0].startsWith(prefix)) {
            permError(PM.getUser(), "can use PM commands");
            PM.getBot().sendIRC().notice(currentNick, "Attempted use of PM commands by " + PM.getUser().getNick() + ". The command used was \"" + PM.getMessage() + "\"");
        }
        checkIfUserHasANote(PM, PM.getUser().getNick(), false);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onJoin(JoinEvent join) {
        System.out.println("User Joined");
        if (join.getChannel().toString().equalsIgnoreCase("#Lil-G|Bot") || join.getChannel().toString().equalsIgnoreCase("#SSB")) {
            join.getChannel().send().voice(join.getUser());
        }
        checkIfUserHasANote(join, join.getUser().getNick(), true);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onNotice(NoticeEvent event) {
        String message = event.getMessage();
        if (!event.getUser().getNick().equalsIgnoreCase("NickServ") || !event.getUser().getNick().equalsIgnoreCase("irc.badnik.net")) {
            if (message.contains("*** Found your hostname") ||
                    message.contains("Password accepted - you are now recognized.") ||
                    message.contains("This nickname is registered and protected.  If it is your") ||
                    message.contains("*** You are connected using SSL cipher") ||
                    message.contains("please choose a different nick.") ||
                    message.contains("nick, type /msg NickServ IDENTIFY password.  Otherwise,")) {

            } else {
                event.getBot().sendIRC().notice(currentNick, "Got notice from " + event.getUser().getNick() + ". Notice was : " + event.getMessage());
            }
        }
        checkIfUserHasANote(event, event.getUser().getNick(), false);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onNickChange(NickChangeEvent nick) {
        if (nick.getNewNick().equalsIgnoreCase(currentNick)) {
            currentNick = null;
            currentUsername = null;
            currentHost = null;
            System.out.print("resetting Authed nick");
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }

        if (nick.getOldNick().equalsIgnoreCase(currentNick)) {
            currentNick = nick.getNewNick();
            currentUsername = nick.getUser().getLogin();
            currentHost = nick.getUser().getHostmask();
            System.out.print("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostmask());
            debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
    }

    public void onNickAlreadyInUse(NickAlreadyInUseEvent nick) {
        nickInUse = true;
        nick.respond(nick.getUsedNick() + 1);
    }

    public void onUnknown(UnknownEvent event) {
        if (event.getLine().equals("AVATAR")) {
            event.getBot().sendRaw().rawLineNow("notice " + lastEvent.getUser().getNick() + " AVATAR http://puu.sh/kA75A.jpg");
        }
        System.out.print("Recieved unknown");
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    /**
     * tells the user they don't have permission to use the command
     *
     * @param user User trying to use command
     * @param e    String to send back
     */
    public void permError(User user, String e) {
        user.send().notice("Sorry, only Authed users " + e);
    }

    /**
     * same as permError() except to be used in channels
     *
     * @param event Channel that the user used the command in
     * @param e     String to send back
     */
    public void permErrorchn(GenericMessageEvent event, String e) {
        event.respond("Sorry, only Authed users " + e);
    }

    /**
     * Checks if the user attempting to use the command is allowed
     *
     * @param user User trying to use command
     * @return Boolean true if allowed, false if not
     */
    public boolean checkPerm(User user, int userLevel) {
        if (user.getNick().equalsIgnoreCase(currentNick) && user.getLogin().equalsIgnoreCase(currentUsername) && user.getHostmask().equalsIgnoreCase(currentHost)) {
            return true;
        } else if (authedUser.contains(user.getNick())) {
            int index = authedUser.indexOf(user.getNick() + "!" + user.getLogin() + "@" + user.getHostmask());
            if (index > -1) {
                if (authedUserLevel.get(index) >= userLevel) {
                    return true;
                }
            }
        } else {
            int index = authedUser.size() - 1;
            while (index > -1) {
                String ident = authedUser.get(index);
                String nick = ident.substring(0, ident.indexOf("!"));
                String userName = ident.substring(ident.indexOf("!") + 1, ident.indexOf("@"));
                String hostMask = ident.substring(ident.indexOf("@") + 1);
                if (nick.equalsIgnoreCase(user.getNick()) || nick.equalsIgnoreCase("*")) {
                    if (userName.equalsIgnoreCase(user.getLogin()) || userName.equalsIgnoreCase("*")) {
                        if (hostMask.equalsIgnoreCase(user.getHostmask()) || hostMask.equalsIgnoreCase("*")) {
                            return authedUserLevel.get(index) >= userLevel;
                        }
                    }
                }
                index--;
            }
            if (userLevel > -1) {
                return true;
            }
        }

        return false;
    }

    public String[] splitMessage(String stringToSplit) {
        if (stringToSplit == null)
            return new String[0];

        List<String> list = new ArrayList<>();
        Matcher argSep = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(stringToSplit);
        while (argSep.find())
            list.add(argSep.group(1));

        for (int i = 0; list.size() > i; i++) { // go through all of the
            list.set(i, list.get(i).replaceAll("\"", "")); // remove quotes left in the string
            list.set(i, list.get(i).replaceAll("''", "\"")); // replace double ' to quotes
            // go to next string
        }
        return list.toArray(new String[list.size()]);
    }

    public void sendError(MessageEvent event, Exception e) {
        event.getChannel().send().message(Colors.RED + "Error: " + e.getCause() + ". From " + e);
    }

    public String botTalk(String bot, String message) throws Exception {
        if (bot.equalsIgnoreCase("clever")) {
            return cleverBotsession.think(message);
        } else if (bot.equalsIgnoreCase("pandora")) {
            return pandoraBotsession.think(message);
        } else if (bot.equalsIgnoreCase("")) {
            return "Error, not a valid bot";
        } else {
            return "Error, not a valid bot";
        }
    }

    public void saveData(MessageEvent event) {
        try {
            SaveDataStore save = new SaveDataStore(noteList, authedUser, authedUserLevel, DNDJoined, DNDList);
            FileWriter writer = new FileWriter("Data/Data.json");
            writer.write(gson.toJson(save));
            writer.close();

            System.out.println("Data saved!");
        } catch (Exception e) {
            sendError(event, e);
        }
    }

    public void checkIfUserHasANote(Event event, String user, boolean inChannel) {
        int i = 0;
        List<Integer> indexList = new ArrayList<>();
        while (i < noteList.size()) {
            if (noteList.get(i).getReceiver().equalsIgnoreCase(user)) {
                indexList.add(i);
            }
            i++;
        }
        try {
            if (i != -1) {
                while (!indexList.isEmpty()) {
                    System.out.println("Note Loop Start");
                    int index = indexList.size() - 1;
                    System.out.print("Index " + index + " ");
                    String receiver = noteList.get(index).getReceiver();
                    System.out.print(receiver + " ");
                    String message = noteList.get(index).displayMessage();
                    System.out.print(message);
                    if (inChannel) {
                        event.respond(message);
                    } else {
                        event.getBot().sendIRC().notice(receiver, message);
                    }
                    noteList.remove(index);
                    indexList.remove(index);
                    System.out.println(" Note Loop End");

                }
            }
        } catch (Exception e) {
            sendError(lastEvent, e);
        }

    }

    public void setDebugInfo(MessageEvent event) {
        int index = DNDJoined.indexOf(currentNick);

        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
            debug.setPlayerName(DNDList.get(index).getPlayerName());
            debug.setPlayerHP(DNDList.get(index).getHPAmounts());
            debug.setPlayerXP(DNDList.get(index).getXPAmounts());

            debug.setFamiliar(DNDList.get(index).getFamiliar().getName());
            debug.setFamiliarHP(DNDList.get(index).getFamiliar().getHPAmounts());
            debug.setFamiliarXP(DNDList.get(index).getFamiliar().getXPAmounts());
        }

        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public String fullNameToString(Language language) {
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

    public String setPassword() {
        File file = new File("pass.bin");
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


}