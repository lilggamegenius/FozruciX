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
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
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

@SuppressWarnings ("unused")
enum MessageModes {
    normal, reversed, wordReversed, scrambled, wordScrambled
}

class MyBotX extends ListenerAdapter{
	private final static File WIKTIONARY_DIRECTORY = new File("Data\\Wiktionary");
	private final int BLOCKS = 128;
	private final int BLOCKSMB = 8 * BLOCKS;
	private final int BLOCKSGB = 8192 * BLOCKS;
	private final String consolePrefix = "\\";
	//boolean spinStarted = false;
	private final String[] dictionary = {"i don't know what \"%s\" is, do i look like a dictionary?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck.", ">_>"};
	private final String[] commands = {"HelpMe", " Time", " calcj", " randomNum", " StringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " ChngCMD", " SayThis", " ToSciNo", " Trans", " DebugVar", " RunCmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", "NoteJ (Mostly fixed)", " jtoggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
	private final String PASSWORD = setPassword();
	private final ChatterBotFactory factory = new ChatterBotFactory();
	private final String VERSION = "PircBotX: 2.1-20151112.042241-148. BotVersion: 2.0";
	private final ExtendedDoubleEvaluator calc = new ExtendedDoubleEvaluator();
	private final StaticVariableSet<Double> variables = new StaticVariableSet<>();
	private final String DNDDungeonMaster = "Null";
	private final boolean drawDungeon = false;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final int frameWidth = 300;
	private final int frameHeight = 300;
	private String prefix = "!";
	private boolean jokeCommands = true;
	private boolean chngCMDRan = false;
	private String currentNick = "Lil-G";
	private String currentUsername = "GameGenuis";
	private String currentHost = "friendly.local.noob";
	private int i = 0;
	private ChatterBot cleverBot;
	private ChatterBotSession cleverBotsession;
	private boolean cleverBotInt;
	private ChatterBot pandoraBot;
	private ChatterBotSession pandoraBotsession;
	private boolean pandoraBotInt;
	private MessageEvent lastEvent;
	private runCMD singleCMD = null;
	private List<Note> noteList = new ArrayList<>();
	private List<String> authedUser = new ArrayList<>();
	private List<Integer> authedUserLevel = new ArrayList<>();
	private List<String> DNDJoined = new ArrayList<>();
	private List<DNDPlayer> DNDList = new ArrayList<>();
	private Dungeon DNDDungeon = new Dungeon();
	private DebugWindow debug;
	private int jokeCommandDebugVar = 30;
	private CommandLine terminal;
	private boolean nickInUse = false;
	private String counter = "";
	private int countercount = 0;
	private JFrame frame = new JFrame();
	private MessageModes messageMode = MessageModes.normal;

	@SuppressWarnings ("unused")
	public MyBotX(){
	}

	@SuppressWarnings ("unused")
	public MyBotX(boolean Twitch){
		if (Twitch) {
            currentNick = "lilggamegenuis";
            currentUsername = currentNick;
            currentHost = currentUsername + ".tmi.twitch.tv";
        }
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

	private static String getBytes(String bytes){
		byte[] Bytes = bytes.getBytes();
        return Arrays.toString(Bytes);
    }

	@SuppressWarnings ("unused")
	public static String formatFileSize(long size){
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

	private static boolean isNumeric(String str){
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    private static int gc(){
        int timesRan = 0;
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
	    //noinspection UnusedAssignment
	    obj = null;
	    while (ref.get() != null){
		    System.gc();
            timesRan++;
        }
        return timesRan;
    }

    public void onConnect(ConnectEvent event) throws Exception {

        event.getBot().sendIRC().mode(event.getBot().getNick(), "+B");

	    if (event.getBot().getServerHostname().equalsIgnoreCase("irc.twitch.tv")){
		    currentNick = "lilggamegenuis";
            currentUsername = currentNick;
            currentHost = currentUsername + ".tmi.twitch.tv";
        }

	    System.out.println("Creating Debug window");
	    SwingUtilities.invokeLater(new Runnable(){
		    public void run(){
			    debug = new DebugWindow(event.getBot());
		    }
	    });
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

	    if (drawDungeon){
		    SwingUtilities.invokeLater(new Runnable(){
			    public void run(){
				    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				    frame.setSize(frameWidth, frameHeight);
				    frame.setVisible(true);
				    frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
				    frame.paintAll(frame.getGraphics());
			    }
		    });
	    }
    }

	private void sendMessage(MessageEvent event, String msgToSend, boolean addNick){
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
	        for(char msgChar : msgChars){
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

        if (addNick) {
            event.respond(msgToSend);
        } else {
            event.getChannel().send().message(msgToSend);
        }
    }

	@SuppressWarnings ("SameParameterValue")
	private void sendMessage(Event event, String msgToSend, boolean addNick){
		sendMessage((MessageEvent) event, msgToSend, addNick);
    }

	@SuppressWarnings ("StatementWithEmptyBody")
	@Override
	public void onMessage(MessageEvent event) {
        lastEvent = event;
        String[] arg = splitMessage(event.getMessage());
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);

		//noinspection ConstantConditions
		checkIfUserHasANote(event, event.getUser().getNick(), true);

// !helpMe
        if (arg[0].equalsIgnoreCase(prefix + "helpme")) {
            if (checkPerm(event.getUser(), 0)) {

                if (event.getMessage().equalsIgnoreCase(prefix + "helpme")) {
                    sendMessage(event, "List of commands so far. for more info on these commands do " + prefix + "helpme. commands with \"Joke: \" are joke commands that can be disabled", true);
                    sendMessage(event, Arrays.asList(commands).toString(), true);
                } else if (arg[1].equalsIgnoreCase("Helpme")) {
                    sendMessage(event, "Really? ಠ_ಠ", true);
                } else if (arg[1].equalsIgnoreCase("time")) {
                    sendMessage(event, "Displays info from the Date class", true);
                } else if (arg[1].equalsIgnoreCase("Hello")) {
                    sendMessage(event, "Just your average \"hello world!\" program", true);
                } else if (arg[1].equalsIgnoreCase("Attempt")) {
                    sendMessage(event, "Its a inside-joke to my friends in school. If i'm not away, ask me and i'll tell you about it.", true);
                } else if (arg[1].equalsIgnoreCase("RandomNum")) {
                    sendMessage(event, "Creates a random number between the 2 integers", true);
                    sendMessage(event, "Usage: first number sets the minimum number, second sets the maximum", true);
                } else if (arg[1].equalsIgnoreCase("version")) {
                    sendMessage(event, "Displays the version of the bot", true);
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    sendMessage(event, "Converts a String into a Byte array", true);
                } else if (arg[1].equalsIgnoreCase("temp")) {
                    sendMessage(event, "Converts a temperature unit to another unit.", true);
                    sendMessage(event, "Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.", true);
                } else if (arg[1].equalsIgnoreCase("chat")) {
                    sendMessage(event, "This command functions like ELIZA. Talk to it and it talks back.", true);
                    sendMessage(event, "Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky (JabberWacky not yet implemented). Second parameter is the PM.getMessage() to send. Could also be the special param \"\\setup\" to actually start the bot.", true);
                } else if (arg[1].equalsIgnoreCase("calcj")) {
                    sendMessage(event, "This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"", true);
                    sendMessage(event, "Usage 1: The simple way is to type out the expression without any variables. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.", true);
                } else if (arg[1].equalsIgnoreCase("CalcJS")) {
                    sendMessage(event, "This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.", true);
                    sendMessage(event, "Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)", true);
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    sendMessage(event, "Converts a String into a Byte array", true);
                } else if (arg[1].equalsIgnoreCase("NoteJ")) {
                    sendMessage(event, "Allows the user to leave notes", true);
                    sendMessage(event, "Subcommand add: adds a note. Usage: add <Nick to leave note to> <message>. Subcommand del: Deletes a set note Usage: del <Given ID>. Subcommand list: Lists notes you've left", true);
                } else {
                    sendMessage(event, "That either isn't a command, or " + currentNick + " hasn't add that to the help yet.", true);
                }
            }

        }

// !Connect - joins a channel
		if (arg[0].equalsIgnoreCase(prefix + "Connect")){
			if (checkPerm(event.getUser(), 5)){
				event.getBot().send().joinChannel(arg[1]);
			}
		}

// !8ball - ALL HAIL THE MAGIC 8-BALL
        if (arg[0].equalsIgnoreCase(prefix + "8Ball")) {
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
        if (arg[0].equalsIgnoreCase(prefix + "setMessage")) {
            if (checkPerm(event.getUser(), 5)) {
                switch (arg[1].toLowerCase()) {
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
            }
        }

// !getChannelName - Gets channel name, for debuging
        if (event.getMessage().equalsIgnoreCase(prefix + "GetChannelName")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, event.getChannel().getName(), true);
            }
        }

// !Time - Tell the time
        if (event.getMessage().equalsIgnoreCase(prefix + "time")) {
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
                            sendMessage(event, "Set " + arg[2] + " To level " + arg[3], true);
                        } else {
                            try {
                                authedUser.add(arg[2]);
                                authedUserLevel.add(Integer.parseInt(arg[3]));
                            } catch (Exception e) {
                                sendError(event, e);
                            }
                            sendMessage(event, "Added " + arg[2] + " To authed users with level " + arg[3], true);
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
                    sendMessage(event, "Removed " + arg[2] + " from the authed user list", true);
                } else if (arg[1].equalsIgnoreCase("clear")) {
                    authedUser.clear();
                    authedUserLevel.clear();
                    sendMessage(event, "Permission list cleared", true);
                } else if (arg[1].equalsIgnoreCase("List")) {
                    sendMessage(event, authedUser.toString(), true);
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
                        sendMessage(event, eval.toString(), true);
                    } else {
                        double eval = calc.evaluate(arg[1]);
                        sendMessage(event, "" + eval, true);
                    }
                } catch (Exception e) {
                    sendMessage(event, "Error: " + e, false);
                }
            }
        }

// !Git - gets the link to source code
        if (arg[0].equalsIgnoreCase(prefix + "Git")) {
            sendMessage(event, "Link to source code: https://github.com/lilggamegenuis/FozruciX", true);
        }

// !GC - Runs the garbage collector
        if (arg[0].equalsIgnoreCase(prefix + "GC")) {
            int num = gc();
            if (num == 1) {
                sendMessage(event, "Took out the trash", true);
            } else {
                sendMessage(event, "Took out " + num + " Trash bags", true);
            }
        }

// !CalcJS - evaluates a expression in JavaScript
        if (arg[0].equalsIgnoreCase(prefix + "calcJS")) {
            if (checkPerm(event.getUser(), 0)) {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                String factorialFunct =
                        "function factorial(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;}  var life = 42; ";
                String eval;
                try {
                    if (arg[1].contains(";")) {
                        if (!checkPerm(event.getUser(), 2)) {
                            sendMessage(event, "Sorry, only Privileged users can use ;", true);
                        } else {
                            eval = engine.eval(factorialFunct + arg[1]) + "";
                            if (isNumeric(eval)) {
                                if (arg.length < 3) {
                                    sendMessage(event, eval, true);
                                    System.out.println("Outputting as decimal");
                                } else {
                                    eval = Long.toString(Long.parseLong(eval), Integer.parseInt(arg[2]));
                                    sendMessage(event, eval, true);
                                    System.out.println("Outputting as base " + arg[2]);
                                }
                            } else {
                                sendMessage(event, eval, true);
                            }
                        }
                    } else {
                        eval = engine.eval(factorialFunct + arg[1]) + "";
                        if (isNumeric(eval)) {
                            if (arg.length < 3) {
                                sendMessage(event, eval, true);
                                System.out.println("Outputting as decimal");
                            } else {
                                eval = Long.toString(Long.parseLong(eval), Integer.parseInt(arg[2]));
                                sendMessage(event, eval, true);
                                System.out.println("Outputting as base " + arg[2]);
                            }
                        } else {
                            sendMessage(event, eval, true);
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
                sendMessage(event, "I'm a bot", true);
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
                sendMessage(event, "Number of times that " + counter + " is: " + countercount, false);
            }
        }

// !StringToBytes - convert a String into a Byte array
        if (arg[0].equalsIgnoreCase(prefix + "StringToBytes")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    sendMessage(event, getBytes(arg[1]), true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage(event, "Not enough args. Must provide a string", true);
                }
            }
        }

// !LookUpWord - Looks up a word in the Wiktionary
        if (arg[0].equalsIgnoreCase(prefix + "LookupWord")) {
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
                        IWiktionaryEntry entry;
                        if (arg.length > 2 && isNumeric(arg[2])) {
                            entry = page.getEntry(Integer.parseInt(arg[2]));
                        } else {
                            entry = page.getEntry(0);
                        }
                        System.out.println("getting sense");
                        IWiktionarySense sense = entry.getSense(1);
                        System.out.println("getting Plain text");
                        if (arg.length > 2) {
                            int subCommandNum = 2;
                            if (isNumeric(arg[2])) {
                                subCommandNum++;
                            }
                            if (arg.length > subCommandNum && arg[subCommandNum - 1].equalsIgnoreCase("Example")) {
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
        if (arg[0].equalsIgnoreCase(prefix + "Lookup")) {
            DatabaseConfiguration dbConfig = new DatabaseConfiguration("localhost:63342/FozruciX/Data/Wiktionary", "Wikipedia", "lilggamegenuis", setPassword(), WikiConstants.Language.english);

            Wikipedia wiki = null;
            try {
                wiki = new Wikipedia(dbConfig);
            } catch (WikiInitializationException e1) {
                sendMessage(event, "Could not initialize Wikipedia.", true);
            }

            // Get the page with title "Hello world".
            String title = arg[1];
            try {
	            //noinspection ConstantConditions,ConstantConditions
	            Page page = wiki.getPage(title);
	            sendMessage(event, page.getText(), true);
            } catch (WikiApiException e) {
                sendMessage(event, "Page " + title + " does not exist", true);
            } catch (Exception e) {
                sendError(event, e);
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
                            sendMessage(event, "Error: Could not create clever bot session. Error was: " + e, true);
                        }
                    } else {
                        if (cleverBotInt) {
                            try {
                                sendMessage(event, " " + botTalk("clever", arg[2]), true);
                            } catch (Exception e) {
                                sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                            }
                        } else {
                            sendMessage(event, " You have to start CleverBot before you can talk to it. star it with \\setup", true);
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
                            sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                        }
                    } else {
                        if (pandoraBotInt) {
                            try {
                                sendMessage(event, " " + botTalk("pandora", arg[2]), true);
                            } catch (Exception e) {
                                sendMessage(event, "Error: Problem with bot. Error was: " + e, true);
                            }
                        } else {
                            sendMessage(event, " You have to start PandoraBot before you can talk to it. start it with \\setup", true);
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
                    sendMessage(event, "Incorrect arguments.", true);
                } else {
                    sendMessage(event, " " + ans + unit, true);
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
                    sendMessage(event, "Incorrect arguments.", true);
                } else {
                    sendMessage(event, " " + ans + unit, true);
                    if (notify)
                        sendMessage(event, "NOTICE: this command currently doesn't work like it should. The only conversion that works is blocks to kb and kb to blocks", true);
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
                                sendMessage(event, "Note " + arg[2] + " Deleted", true);
                            } else {
                                sendMessage(event, "Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it", true);
                            }
                        } else {
                            sendMessage(event, "That ID wasn't found.", true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }

                if (arg[1].equalsIgnoreCase("add")) {
                    try {
                        Note note = new Note(event.getUser().getNick(), arg[2], arg[3], event.getChannel().getName());
                        noteList.add(note);
                        sendMessage(event, "Left note \"" + arg[3] + "\" for \"" + arg[2] + "\".", false);
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
                    sendMessage(event, found.toString(), true);
                    event.getUser().send().notice(foundUUID.toString());
                }
                saveData(event);
            }
        }

// !Hello - Standard "Hello world" command
        if (event.getMessage().equalsIgnoreCase(prefix + "hello")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "Hello World!", true);
            }
        }

// !Bot - Explains that "yes this is a bot"
        if (event.getMessage().equalsIgnoreCase(prefix + "bot")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "Yes, this is " + currentNick + "'s bot.", true);
            }
        }

// !getname - gets the name of the bot
        if (event.getMessage().equalsIgnoreCase(prefix + "getname")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, event.getBot().getUserBot().getRealName(), true);
            }
        }

// !version - gets the version of the bot
        if (event.getMessage().equalsIgnoreCase(prefix + "version")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "Version: " + VERSION, true);
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
                sendMessage(event, event.getBot().getUserBot().getLogin(), true);
            }
        }

// !getID - gets the ID of the user
        if (arg[0].equalsIgnoreCase(prefix + "getID")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "You are :" + event.getUser().getUserId(), true);
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

                sendMessage(event, " " + randInt((int) num1, (int) num2), true);
            }
        }

// !getState - Displays what version the bot is on
        if (event.getMessage().equalsIgnoreCase(prefix + "getState")) {
            if (checkPerm(event.getUser(), 0)) {
                sendMessage(event, "State is: " + event.getBot().getState(), true);
            }
        }

// !ChngCMD - Changes the command prefix when it isn't the standard "!"
        if (arg[0].equalsIgnoreCase("!chngcmd") && !prefix.equals("!")) {
            if (checkPerm(event.getUser(), 5)) {
                prefix = arg[1];
                sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
                chngCMDRan = true;
            } else {
                permError(event.getUser(), "can change the command character");
            }
        }

// !ChngCMD - Changes the command prefix
        if (arg[0].equalsIgnoreCase(prefix + "chngcmd") && !chngCMDRan) {
            if (checkPerm(event.getUser(), 5)) {
                prefix = arg[1];
                sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
            } else {
                permError(event.getUser(), "can change the command character");
            }
        }
        chngCMDRan = false;

// !Saythis - Tells the bot to say someting
        if (arg[0].equalsIgnoreCase(prefix + "saythis")) {
            if (checkPerm(event.getUser(), 4)) {
                sendMessage(event, arg[1], false);
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
                        sendMessage(event, arg[2], false);
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
                    sendMessage(event, formatter.format(num), true);
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
                    sendMessage(event, "Syntax: " + prefix + "DND join <Character name> <Race (can be anything right now)> <Class> {<Familiar name> <Familiar Species>}", true);
                } else {
                    if (arg[1].equalsIgnoreCase("join")) {
                        if (DNDJoined.contains(event.getUser().getNick())) {
                            sendMessage(event, "You are already in the list!", true);
                        } else {
                            if (arg.length == 5) {
                                if (DNDPlayer.ifClassExists(arg[3])) {
                                    DNDList.add(new DNDPlayer(arg[2], arg[3], arg[4], event.getUser().getNick()));
                                    DNDJoined.add(event.getUser().getNick());
                                    sendMessage(event, "Added \"" + arg[2] + "\" the " + arg[4] + " to the game", true);
                                    if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                        debug.setPlayerName(DNDList.get(DNDJoined.indexOf(currentNick)).getPlayerName());
                                    }
                                } else {
                                    sendMessage(event, "That class doesn't exist!", true);
                                }
                            }
                            if (arg.length == 7) {
                                if (DNDPlayer.ifClassExists(arg[4])) {
                                    if (DNDPlayer.ifSpeciesExists(arg[6])) {
                                        DNDList.add(new DNDPlayer(arg[2], arg[3], arg[4], event.getUser().getNick(), arg[5], arg[6]));
                                        DNDJoined.add(event.getUser().getNick());
                                        sendMessage(event, "Added \"" + arg[2] + "\" the " + arg[4] + " with " + arg[5] + " The " + arg[6] + " to the game", true);

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
                            sendMessage(event, DNDList.get(index).toString(), true);
                        } else {
                            sendMessage(event, "You have to join first!", true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                }
                if (arg[1].equalsIgnoreCase("List")) {
                    try {
                        sendMessage(event, DNDList.toString(), true);
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
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDClasses.values()), true);
                }
                if (arg[1].equalsIgnoreCase("ListSpecies")) {
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDFamiliars.values()), true);
                }
	            //noinspection StatementWithEmptyBody
	            if (arg[1].equalsIgnoreCase("DM")){
		            //todo
	            }
                if (arg[1].equalsIgnoreCase("Test")) { //testing commands.
	                //checkPerm(event.getUser())
	                try{
		                int index = DNDJoined.indexOf(event.getUser().getNick());
		                if (arg[2].equalsIgnoreCase("addItem")){
			                DNDList.get(index).addInventory(arg[3]);
			                sendMessage(event, "Added " + arg[3] + " to your inventory", true);
		                }
		                if (arg[2].equalsIgnoreCase("getItems")){
			                sendMessage(event, DNDList.get(index).getInventory(), true);
		                }
		                if (arg[2].equalsIgnoreCase("delItem")){
			                DNDList.get(index).removeFromInventory(arg[3]);
			                sendMessage(event, "removed " + arg[3] + " to your inventory", true);
		                }
		                if (arg[2].equalsIgnoreCase("addXP")){
			                DNDList.get(index).addXP(Integer.parseInt(arg[3]));
			                sendMessage(event, "Added " + arg[3] + " to your XP", true);
		                }
		                if (arg[2].equalsIgnoreCase("addHP")){
			                DNDList.get(index).addHP(Integer.parseInt(arg[3]));
			                sendMessage(event, "Added " + arg[3] + " to your HP", true);
		                }
		                if (arg[2].equalsIgnoreCase("subHP")){
			                DNDList.get(index).hit(Integer.parseInt(arg[3]));
			                sendMessage(event, "Subbed " + arg[3] + " from your HP", true);
		                }
		                if (arg[2].equalsIgnoreCase("addXPFam")){
			                DNDList.get(index).getFamiliar().addXP(Integer.parseInt(arg[3]));
			                sendMessage(event, "Added " + arg[3] + " to your familiar's XP", true);
		                }
		                if (arg[2].equalsIgnoreCase("addHPFam")){
			                DNDList.get(index).getFamiliar().addHP(Integer.parseInt(arg[3]));
			                sendMessage(event, "Added " + arg[3] + " to your familiar's HP", true);
		                }
		                if (arg[2].equalsIgnoreCase("subHPFam")){
			                DNDList.get(index).getFamiliar().hit(Integer.parseInt(arg[3]));
			                sendMessage(event, "Subbed " + arg[3] + " from your familiar's HP", true);
		                }
		                if (arg[2].equalsIgnoreCase("getFamiliar")){
			                sendMessage(event, DNDList.get(index).getFamiliar().toString(), true);
		                }

		                if (arg[2].equalsIgnoreCase("clearList")){
			                if (checkPerm(event.getUser(), 3)){
				                DNDJoined.clear();
				                DNDList.clear();
				                sendMessage(event, "DND Player lists cleared", false);
			                }
		                }

		                if (arg[2].equalsIgnoreCase("DelChar")){
			                if (checkPerm(event.getUser(), 5)){
				                if (DNDJoined.contains(arg[3])){
					                DNDJoined.remove(index);
				                }
			                }
		                }

		                if (arg[2].equalsIgnoreCase("setPos")){
			                DNDDungeon.setLocation(Integer.parseInt(arg[3]), Integer.parseInt(arg[4]));
			                sendMessage(event, "Pos is now: " + DNDDungeon.toString(), true);

		                }

		                if (arg[2].equalsIgnoreCase("getPos")){
			                Point temp = DNDDungeon.getLocation();
			                sendMessage(event, "Current location: (" + temp.x + "," + temp.y + ")", true);
		                }

		                if (arg[2].equalsIgnoreCase("movePos")){
			                DNDDungeon.move(Integer.parseInt(arg[3]), Integer.parseInt(arg[4]));
			                Point temp = DNDDungeon.getLocation();
			                sendMessage(event, "New location: (" + temp.x + "," + temp.y + ")", true);
		                }

		                if (arg[2].equalsIgnoreCase("getSurroundings")){
			                int[] tiles = DNDDungeon.getSurrounding();
			                sendMessage(event, " | " + tiles[7] + " | " + tiles[0] + " | " + tiles[1] + " | ", true);
			                sendMessage(event, " | " + tiles[6] + " | " + tiles[8] + " | " + tiles[2] + " | ", true);
			                sendMessage(event, " | " + tiles[5] + " | " + tiles[4] + " | " + tiles[3] + " | ", true);
		                }

		                if (arg[2].equalsIgnoreCase("genDungeon")){
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

		                if (arg[2].equalsIgnoreCase("draw")){
			                frame.dispose();
			                frame = new JFrame();
			                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			                frame.setAlwaysOnTop(true);
			                frame.setSize(frameWidth, frameHeight);
			                frame.setVisible(true);
			                frame.getContentPane().add(new DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
			                frame.paintAll(frame.getGraphics());
		                }
	                } catch (NullPointerException e){
		                sendMessage(event, "You have to join first! (Null pointer)", true);
	                } catch (Exception e){
		                sendError(event, e);
	                }
                }
                setDebugInfo(event);
            }
        }

// !Trans - Translate from 1 language to another
        if (arg[0].equalsIgnoreCase(prefix + "trans")) {
            if (checkPerm(event.getUser(), 0)) {
	            String text;
	            System.out.println("Setting key");
	            YandexTranslatorAPI.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");
                try {
                    if (arg[1].equalsIgnoreCase("\\detect")) {
                        sendMessage(event, fullNameToString(Detect.execute(arg[2])), true);
                    } else {
                        System.out.println("Getting to lang");
                        Language to = Language.valueOf(arg[2].toUpperCase());
                        System.out.println("Getting from lang");
                        Language from = Language.valueOf(arg[1].toUpperCase());
                        System.out.println("Executing trans");
                        text = Translate.execute(arg[3], from, to);
                        System.out.println("Translating");
                        System.out.println(text);
                        sendMessage(event, text, true);
                    }
                } catch (IllegalArgumentException e) {
                    sendError(event, new Exception("That class doesn't exist!"));
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
                        sendMessage(event, "DEBUG: Var \"i\" is now \"" + i + "\"", true);
                        break;
                    case "jokenum":
                        jokeCommandDebugVar = Integer.parseInt(arg[2]);
                        sendMessage(event, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"", true);
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
                        sendMessage(event, "Stopping", true);
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

// !recycle - Tells the bot to part and rejoin the channel
        if (arg[0].equalsIgnoreCase(prefix + "recycle")) {
            if (checkPerm(event.getUser(), 3)) {
                saveData(event);
                event.getChannel().send().cycle();
            } else {
                permErrorchn(event, "can recycle the bot");
            }
        }
// !revoice - gives everyone voice if they didn't get it
        if (arg[0].equalsIgnoreCase(prefix + "revoice")) {
	        for(User user1 : event.getChannel().getUsers()){
		        event.getBot().sendRaw().rawLineNow("mode " + event.getChannel().getName() + " +v " + user1.getNick());
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
                        sendMessage(event, "Joke commands are currently enabled", true);
                    } else {
                        sendMessage(event, "Joke commands are currently disabled", true);
                    }
                }
            }
            if (arg[1].equalsIgnoreCase("toggle")) {
                if (checkPerm(event.getUser(), 2)) {
                    jokeCommands = !jokeCommands;
                    if (jokeCommands) {
                        sendMessage(event, "Joke commands are now enabled", true);
                    } else {
                        sendMessage(event, "Joke commands are now disabled", true);
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
                    sendMessage(event, " YOU'RE A KID YOU'RE A SQUID", true);
                else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !attempt - Joke command - NOT ATTEMPTED
        if (event.getMessage().equalsIgnoreCase(prefix + "attempt")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    sendMessage(event, " NOT ATTEMPTED", true);
                else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

//// !stfu - Joke command - say "no u"
//		if (event.getMessage().equalsIgnoreCase(prefix + "stfu")){
//			if(jokeCommands || checkPerm(event.getUser()))
//				sendMessage(event, sender + ": " + prefix + "no u");
//		}

// !eatabowlofdicks - Joke command - joke help command
        if (event.getMessage().equalsIgnoreCase(prefix + "eatabowlofdicks")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    sendMessage(event, "no u", true);
            }
        }

// !myDickSize - Joke command - This was requested by Greeny in #origami64. ask him about it
        if (event.getMessage().equalsIgnoreCase(prefix + "myDickSize")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    if (event.getChannel().getName().equalsIgnoreCase("#origami64") || event.getChannel().getName().equalsIgnoreCase("#Lil-G|bot") || event.getChannel().getName().equalsIgnoreCase("#SSB")) {
                        if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                            sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", false);
                        } else {
                            int size = randInt(0, jokeCommandDebugVar);
                            sendMessage(event, "8" + StringUtils.leftPad("D", size, "=") + " - " + size, false);
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
                    sendMessage(event, v, true);
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
            }
        }

// !whatis? - Joke command -
        if (arg[0].equalsIgnoreCase(prefix + "whatis?")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1)) {
                    int num = randInt(0, dictionary.length - 1);
                    String comeback = String.format(dictionary[num], arg[1]);
                    sendMessage(event, comeback, true);
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);
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
                        sendMessage(event, "It's " + userList.get(num).toUpperCase() + "!", false);
                    } catch (Exception e) {
                        sendMessage(event, "Error: " + e, false);
                    }
                } else
                    sendMessage(event, " Sorry, Joke commands are disabled", true);

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
	        //noinspection ConstantConditions
	        PM.getUser().send().notice("\u0001AVATAR http://puu.sh/kA75A.jpg 19117\u0001");
        }
        if (PM.getMessage().contains("\u0001A")) {
	        //noinspection ConstantConditions
	        PM.getUser().send().notice("\u0001AVATAR http://puu.sh/kA75A.jpg 19117\u0001");
        }

// !login - Sets the authed named to the new name ...if the password is right
        if (arg[0].equalsIgnoreCase(prefix + "login")) {
            if (arg[1].equals(PASSWORD)) {
                currentNick = PM.getUser().getNick();
                currentUsername = PM.getUser().getLogin();
                currentHost = PM.getUser().getHostname();
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
	    debug.updateBot(PM.getBot());
	    checkIfUserHasANote(PM, PM.getUser().getNick(), false);
	    debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onJoin(JoinEvent join) {
        System.out.println("User Joined");
        if (join.getChannel().getName().equalsIgnoreCase("#Lil-G|Bot") || join.getChannel().getName().equalsIgnoreCase("#SSB")) {
            join.getChannel().send().voice(join.getUserHostmask());
        }
	    //noinspection ConstantConditions
	    checkIfUserHasANote(join, join.getUser().getNick(), true);
	    debug.updateBot(join.getBot());
	    debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onNotice(NoticeEvent event) {
        String message = event.getMessage();
	    //noinspection ConstantConditions
	    if (!event.getUser().getNick().equalsIgnoreCase("NickServ") || !event.getUser().getNick().equalsIgnoreCase("irc.badnik.net")){
		    //noinspection StatementWithEmptyBody
		    if (message.contains("*** Found your hostname") ||
				    message.contains("Password accepted - you are now recognized.") ||
				    message.contains("This nickname is registered and protected.  If it is your") ||
                    message.contains("*** You are connected using SSL cipher") ||
                    message.contains("please choose a different nick.") ||
                    message.contains("nick, type /msg NickServ IDENTIFY password.  Otherwise,")) {

            } else if (message.contains("\u0001AVATAR")) {
                event.getUser().send().notice("\u0001AVATAR http://puu.sh/kA75A.jpg\u0001");
            } else {
                event.getBot().sendIRC().notice(currentNick, "Got notice from " + event.getUser().getNick() + ". Notice was : " + event.getMessage());
            }
        }
        checkIfUserHasANote(event, event.getUser().getNick(), false);
        if (event.getBot().isConnected()) {
	        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
	    debug.updateBot(event.getBot());
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
	        //noinspection ConstantConditions
	        currentUsername = nick.getUser().getLogin();
	        currentHost = nick.getUser().getHostname();
	        System.out.print("setting Authed nick as " + nick.getNewNick() + "!" + nick.getUser().getLogin() + "@" + nick.getUser().getHostname());
	        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        }
	    debug.updateBot(nick.getBot());
    }

    public void onNickAlreadyInUse(NickAlreadyInUseEvent nick) {
        nickInUse = true;
        nick.respond(nick.getUsedNick() + 1);
    }

    public void onUnknown(UnknownEvent event) {

        if (event.getLine().contains("AVATAR")) {
	        //noinspection ConstantConditions
	        event.getBot().sendRaw().rawLineNow("notice " + lastEvent.getUser().getNick() + " AVATAR http://puu.sh/kA75A.jpg");
        }
        System.out.println("Recieved unknown");
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    /**
     * tells the user they don't have permission to use the command
     *
     * @param user User trying to use command
     * @param e    String to send back
     */
    private void permError(User user, String e){
        user.send().notice("Sorry, only Authed users " + e);
    }

    /**
     * same as permError() except to be used in channels
     *
     * @param event Channel that the user used the command in
     * @param e     String to send back
     */
    private void permErrorchn(MessageEvent event, String e){
        sendMessage(event, "Sorry, only Authed users " + e, true);
    }

    /**
     * Checks if the user attempting to use the command is allowed
     *
     * @param user User trying to use command
     * @return Boolean true if allowed, false if not
     */
    private boolean checkPerm(User user, int userLevel){
        if (user.getNick().equalsIgnoreCase(currentNick) && user.getLogin().equalsIgnoreCase(currentUsername) && user.getHostname().equalsIgnoreCase(currentHost)) {
            return true;
        } else if (authedUser.contains(user.getNick())) {
	        int index = authedUser.indexOf(user.getNick() + "!" + user.getLogin() + "@" + user.getHostname());
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
	            String Hostname = ident.substring(ident.indexOf("@") + 1);
	            if (nick.equalsIgnoreCase(user.getNick()) || nick.equalsIgnoreCase("*")) {
                    if (userName.equalsIgnoreCase(user.getLogin()) || userName.equalsIgnoreCase("*")) {
	                    if (Hostname.equalsIgnoreCase(user.getHostname()) || Hostname.equalsIgnoreCase("*")){
		                    return authedUserLevel.get(index) >= userLevel;
                        }
                    }
                }
                index--;
            }
            if (userLevel < -1) {
                return true;
            }
        }

        return false;
    }

	private String[] splitMessage(String stringToSplit){
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


	private void sendError(MessageEvent event, Exception e){
		sendMessage(event, Colors.RED + "Error: " + e.getCause() + ". From " + e, false);
    }

	private String botTalk(String bot, String message) throws Exception{
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

	private void saveData(MessageEvent event){
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

	private void checkIfUserHasANote(Event event, String user, boolean inChannel){
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
                        sendMessage(event, message, true);
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

	private void setDebugInfo(MessageEvent event){
		int index = DNDJoined.indexOf(currentNick);

	    //noinspection ConstantConditions
	    if (event.getUser().getNick().equalsIgnoreCase(currentNick)){
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

	private String fullNameToString(Language language){
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

	private String setPassword(){
		File file = new File("pass.bin");
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