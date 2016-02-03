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
import com.google.common.collect.ImmutableMap;
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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import sun.misc.Unsafe;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyBotX extends ListenerAdapter {
    private final static File WIKTIONARY_DIRECTORY = new File("Data\\Wiktionary");
    //boolean spinStarted = false;
    private final String[] dictionary = {"i don't know what \"%s\" is, do i look like a dictionary?", "Go look it up yourself.", "Why not use your computer and look \"%s\" up.", "Google it.", "Nope.", "Get someone else to do it.", "Why not get that " + Colors.RED + "Other bot" + Colors.NORMAL + " to do it?", "There appears to be a error between your " + Colors.BOLD + "seat" + Colors.NORMAL + " and the " + Colors.BOLD + "Keyboard" + Colors.NORMAL + " >_>", "Uh oh, there appears to be a User error.", "error: Fuck count too low, Cannot give Fuck.", ">_>"};
    private final String[] listOfNoes = {" It’s not a priority for me at this time.", "I’d rather stick needles in my eyes.", "My schedule is up in the air right now. SEE IT WAFTING GENTLY DOWN THE CORRIDOR.", "I don’t love it, which means I’m not the right person for it.", "I would prefer another option.", "I would be the absolute worst person to execute, are you on crack?!", "Life is too short TO DO THINGS YOU don’t LOVE.", "I no longer do things that make me want to kill myself", "You should do this yourself, you would be awesome sauce.", "I would love to say yes to everything, but that would be stupid", "Fuck no.", "Some things have come up that need my attention.", "There is a person who totally kicks ass at this. I AM NOT THAT PERSON.", "Shoot me now...", "It would cause the slow withering death of my soul.", "I’d rather remove my own gallbladder with an oyster fork.", "I'd love to but I did my own thing and now I've got to undo it."};
    private final String[] commands = {"commands", " Time", " calcj", " randomNum", " StringToBytes", " Chat", " Temp", " BlockConv", " Hello", " Bot", " GetName", " recycle", " Login", " GetLogin", " GetID", " GetSate", " ChngCMD", " SayThis", " ToSciNo", " Trans", " DebugVar", " RunCmd", " SayRaw", " SayCTCPCommnad", " Leave", " Respawn", " Kill", " ChangeNick", " SayAction", " NoteJ", "Memes", " jtoggle", " Joke: Splatoon", "Joke: Attempt", " Joke: potato", " Joke: whatIs?", "Joke: getFinger", " Joke: GayDar"};
    private final String PASSWORD = setPassword(true);
    private final ChatterBotFactory factory = new ChatterBotFactory();
    private final ExtendedDoubleEvaluator calc = new ExtendedDoubleEvaluator();
    private final StaticVariableSet<Double> variables = new StaticVariableSet<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String prefix = "!";
    private boolean jokeCommands = true;
    private boolean chngCMDRan = false;
    private String currentNick = "Lil-G";
    private String currentUsername = "GameGenuis";
    private String currentHost = "friendly.local.noob";
    private ChatterBotSession cleverBotsession;
    private boolean cleverBotInt;
    private ChatterBotSession pandoraBotsession;
    private boolean pandoraBotInt;
    private ChatterBotSession jabberBotsession;
    private boolean jabberBotInt;
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
    private List<RPSGame> rpsGames = new ArrayList<>();
    private String avatar = "http://puu.sh/mhwsr.gif";
    private boolean color = true;
    private HashMap<String, Meme> memes = new HashMap<>();

    @SuppressWarnings("unused")
    public MyBotX() {
    }

    @SuppressWarnings("unused")
    public MyBotX(boolean Twitch) {
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
        SwingUtilities.invokeLater(() -> debug = new DebugWindow(event.getBot()));
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

        if (nickInUse || event.getBot().getUserBot().getNick().equalsIgnoreCase("FozruciX1")) {
            bot.sendRaw().rawLineNow("ns recover " + event.getBot().getConfiguration().getName() + " " + PASSWORD);
        }

        String network = event.getBot().getServerInfo().getNetwork();
        if (network == null) {
            network = event.getBot().getServerHostname();
            network = network.substring(network.indexOf(".") + 1, network.lastIndexOf("."));
        }

        BufferedReader br = new BufferedReader(new FileReader("Data/" + network + "-Data.json"));
        SaveDataStore save = gson.fromJson(br, SaveDataStore.class);
        noteList = save.getNoteList();
        authedUser = save.getAuthedUser();
        authedUserLevel = save.getAuthedUserLevel();
        DNDJoined = save.getDNDJoined();
        DNDList = save.getDNDList();
        if (save.getAvatarLink() != null) {
            avatar = save.getAvatarLink();
        }
        memes = save.getMemes();


        /*boolean drawDungeon = false;
        if (drawDungeon) {
            SwingUtilities.invokeLater(() -> {
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setSize(frameWidth, frameHeight);
                frame.setVisible(true);
                frame.getContentPane().add(new com.LilG.Com.DrawWindow(DNDDungeon.getMap(), DNDDungeon.getMap_size(), DNDDungeon.getLocation()));
                frame.paintAll(frame.getGraphics());
            });
        }*/
    }

    private String getScramble(String msgToSend) {
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

// !formatting - toggles color (Mostly in the errors)
        if (arg[0].equalsIgnoreCase(prefix + "formatting")) {
            if (checkPerm(event.getUser(), 5)) {
                color = !color;
                if (color) {
                    sendMessage(event, "Color formatting is now On", true);
                } else {
                    sendMessage(event, "Color formatting is now Off", true);
                }
            }
        }


// !commands
        if (arg[0].equalsIgnoreCase(prefix + "commands")) {
            if (checkPerm(event.getUser(), 0)) {
                if (event.getMessage().equalsIgnoreCase(prefix + "commands")) {
                    sendNotice(event.getUser().getNick(), "List of commands so far. for more info on these commands do " + prefix + "commands. commands with \"Joke: \" are joke commands that can be disabled");
                    sendNotice(event.getUser().getNick(), Arrays.asList(commands).toString());
                } else if (arg[1].equalsIgnoreCase("commands")) {
                    sendNotice(event.getUser().getNick(), "Really? ಠ_ಠ");
                } else if (arg[1].equalsIgnoreCase("helpMe")) {
                    sendNotice(event.getUser().getNick(), "Changed to commands (");
                } else if (arg[1].equalsIgnoreCase("time")) {
                    sendNotice(event.getUser().getNick(), "Displays info from the Date class");
                } else if (arg[1].equalsIgnoreCase("Hello")) {
                    sendNotice(event.getUser().getNick(), "Just your average \"hello world!\" program");
                } else if (arg[1].equalsIgnoreCase("Attempt")) {
                    sendNotice(event.getUser().getNick(), "Its a inside-joke to my friends in school. If i'm not away, ask me and i'll tell you about it.");
                } else if (arg[1].equalsIgnoreCase("RandomNum")) {
                    sendNotice(event.getUser().getNick(), "Creates a random number between the 2 integers");
                    sendNotice(event.getUser().getNick(), "Usage: first number sets the minimum number, second sets the maximum");
                } else if (arg[1].equalsIgnoreCase("version")) {
                    sendNotice(event.getUser().getNick(), "Displays the version of the bot");
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    sendNotice(event.getUser().getNick(), "Converts a String into a Byte array");
                } else if (arg[1].equalsIgnoreCase("temp")) {
                    sendNotice(event.getUser().getNick(), "Converts a temperature unit to another unit.");
                    sendNotice(event.getUser().getNick(), "Usage: First parameter is the unit its in. Second parameter is the unit to convert to. Third parameter is the number to convert to.");
                } else if (arg[1].equalsIgnoreCase("chat")) {
                    sendNotice(event.getUser().getNick(), "This command functions like ELIZA. Talk to it and it talks back.");
                    sendNotice(event.getUser().getNick(), "Usage: First parameter defines what service to use. it supports CleverBot, PandoraBot, and JabberWacky. Second parameter is the Message to send. Could also be the special param \"\\setup\" to actually start the bot.");
                } else if (arg[1].equalsIgnoreCase("calcj")) {
                    sendNotice(event.getUser().getNick(), "This command takes a expression and evaluates it. There are 2 different functions. Currently the only variable is \"x\"");
                    sendNotice(event.getUser().getNick(), "Usage 1: The simple way is to type out the expression without any variables. Usage 2: 1st param is what to start x at. 2nd is what to increment x by. 3rd is amount of times to increment x. last is the expression.");
                } else if (arg[1].equalsIgnoreCase("CalcJS")) {
                    sendNotice(event.getUser().getNick(), "Renamed to just \"JS\"");
                } else if (arg[1].equalsIgnoreCase("JS")) {
                    sendNotice(event.getUser().getNick(), "This command takes a expression and evaluates it using JavaScript's eval() function. that means that it can also run native JS Code as well.");
                    sendNotice(event.getUser().getNick(), "Usage: simply enter a expression and it will evaluate it. if it contains spaces, enclose in quotes. After the expression you may also specify which radix to output to (default is 10)");
                } else if (arg[1].equalsIgnoreCase("StringToBytes")) {
                    sendNotice(event.getUser().getNick(), "Converts a String into a Byte array");
                } else if (arg[1].equalsIgnoreCase("NoteJ")) {
                    sendNotice(event.getUser().getNick(), "Allows the user to leave notes");
                    sendNotice(event.getUser().getNick(), "SubCommand add <Nick to leave note to> <message>: adds a note. Subcommand del <Given ID>: Deletes a set note Usage: . Subcommand list: Lists notes you've left");
                } else if (arg[1].equalsIgnoreCase("Memes")) {
                    sendNotice(event.getUser().getNick(), "Meme database. To get a meme you simply have to do \"Memes <meme name>\"");
                    sendNotice(event.getUser().getNick(), "Subcommand set <Meme Name> <The Meme>: Sets up a meme. Note, When Seting a meme that already exists, you have to be the creator to edit it.  Subcommand list: Lists all the memes in the database");
                } else {
                    sendNotice(event.getUser().getNick(), "That either isn't a command, or " + currentNick + " hasn't add that to the help yet.");
                }
            }

        }

// !serverHostName - Gets the Server Host Name
        if (arg[0].equalsIgnoreCase(prefix + "serverHostName")) {
            if (checkPerm(event.getUser(), 5)) {
                sendMessage(event, event.getBot().getServerHostname(), false);
            }
        }

// !Connect - joins a channel
        if (arg[0].equalsIgnoreCase(prefix + "Connect")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().send().joinChannel(arg[1]);
            }
        }

// !QuitServ - Tells the bot to disconnect from server
        if (arg[0].equalsIgnoreCase(prefix + "quitserv")) {
            //noinspection ConstantConditions
            event.getUser().send().notice("Disconnecting from server");
            if (arg.length > 1) {
                event.getBot().sendIRC().quitServer(arg[1]);
            } else {
                event.getBot().sendIRC().quitServer("I'm only a year old and have already wasted my entire life.");
            }
            try {
                wait(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

// !setAvatar - sets the avatar of the bot
        if (arg[0].equalsIgnoreCase(prefix + "setAvatar")) {
            if (checkPerm(event.getUser(), 5)) {
                avatar = arg[1];
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

// !rps - Rock! Paper! ehh you know the rest
        if (arg[0].equalsIgnoreCase(prefix + "rps")) {
            if (checkPerm(event.getUser(), 0)) {
                //todo
            }
        }

// !reverseList - Reverses a list
        if (arg[0].equalsIgnoreCase(prefix + "reverseList")) {
            if (checkPerm(event.getUser(), 0)) {
                String[] list = Arrays.copyOfRange(arg, 1, arg.length);
                String temp = "Uh oh, something broke";
                int i;
                for (i = list.length - 1; i > 0; i--) {
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
        if (arg[0].equalsIgnoreCase(prefix + "CheckLink")) {
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
        if (event.getMessage().equalsIgnoreCase(prefix + "testPermError")) {
            if (checkPerm(event.getUser(), 5)) {
                permErrorchn(event);
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
                } else {
                    int place = -1;
                    try {
                        for (int i = 0; authedUser.size() >= i; i++) {
                            if (authedUser.get(i).equalsIgnoreCase(arg[1])) {
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

// !vgm - links to my New mixtapes :V
        if (arg[0].equalsIgnoreCase(prefix + "vgm")) {
            sendMessage(event, "Link to My smps music: https://drive.google.com/open?id=0B3aju_x5_V--ZjAyLWZEUnV1aHc", true);
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

// !SolveFor - Solves for a equation
        if (arg[0].equalsIgnoreCase(prefix + "SolveFor")) {
            if (checkPerm(event.getUser(), 0)) {
                String expression = arg[1];
                String solveFor = arg[2];
                // Use DynJS runtime
                RuntimeFactory factory = RuntimeFactory.init(MyBotX.class.getClassLoader(), RuntimeFactory.RuntimeType.DYNJS);
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
        if (arg[0].equalsIgnoreCase(prefix + "JS")) {
            if (checkPerm(event.getUser(), 0)) {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                try {
                    String factorialFunct = "function factorial(num) {  if (num < 0) {    return -1;  } else if (num == 0) {    return 1;  }  var tmp = num;  while (num-- > 2) {    tmp *= num;  }  return tmp;} " +
                            "function getBit(num, bit) {  var result = (num >> bit) & 1; return result == 1} " +
                            "function offset(array, offsetNum){array = eval(\"\" + array + \"\");var size = array.length * offsetNum;var result = [];for(var i = 0; i < array.length; i++){result[i] = parseInt(array[i], 16) + size} return result;} " +
                            "function solvefor(expr, solve){var eq = algebra.parse(expr); var ans = eq.solveFor(solve); return solve + \" = \" + ans.toString(); }  var life = 42; ";
                    if (!checkPerm(event.getUser(), 5)) {
                        factorialFunct += "Packages = \"nah fam\"; JavaImporter = \"tbh smh fam\"; Java = \"tbh smh fam\"; java = \"nah fam\"; ";
                    }
                    engine.eval(factorialFunct);
                    String eval;
                    eval = engine.eval(arg[1]).toString();
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
                } catch (Exception e) {
                    sendError(event, e);
                }
            }/* else {
                sendMessage(event, "Sorry this command is broken right now, please come back later.", true);
            }*/
        }

// if someone tells the bot to "Go to hell" do this
        if (event.getMessage().contains(event.getBot().getNick()) && event.getMessage().toLowerCase().contains("Go to hell".toLowerCase())) {
            if (checkPerm(event.getUser(), 0) && !checkPerm(event.getUser(), 5)) {
                sendMessage(event, "I Can't go to hell, i'm all out of vacation days", false);
            }
        }

// !count - counts amount of something
        if (arg[0].equalsIgnoreCase(prefix + "count")) {
            if (checkPerm(event.getUser(), 1)) {
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
            if (checkPerm(event.getUser(), 5)) {
                String[] listOfTitleStrings = {arg[1]};
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
        if (arg[0].equalsIgnoreCase(prefix + "chat")) {
            if (checkPerm(event.getUser(), 0)) {
                if (arg[1].equalsIgnoreCase("clever")) {
                    if (arg[2].equalsIgnoreCase("\\setup")) {
                        try {
                            cleverBotsession = factory.create(ChatterBotType.CLEVERBOT).createSession();
                            cleverBotInt = true;
                            //noinspection ConstantConditions
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
                            ChatterBot pandoraBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                            pandoraBotsession = pandoraBot.createSession();
                            pandoraBotInt = true;
                            //noinspection ConstantConditions
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

                } else if (arg[1].equalsIgnoreCase("jabber")) {
                    if (arg[2].equalsIgnoreCase("\\setup")) {
                        try {
                            ChatterBot jabberBot = factory.create(ChatterBotType.JABBERWACKY, "b0dafd24ee35a477");
                            jabberBotsession = jabberBot.createSession();
                            jabberBotInt = true;
                            //noinspection ConstantConditions
                            event.getUser().send().notice("PandoraBot started");
                        } catch (Exception e) {
                            sendMessage(event, "Error: Could not create pandora bot session. Error was: " + e, true);
                        }
                    } else {
                        if (jabberBotInt) {
                            try {
                                sendMessage(event, " " + botTalk("pandora", arg[2]), true);
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
                int BLOCKS = 128;
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
                        int BLOCKSMB = 8 * BLOCKS;
                        ans = data / BLOCKSMB;
                        unit = "Blocks";
                    }
                } else if (arg[1].equalsIgnoreCase("gb")) {
                    if (arg[2].equalsIgnoreCase("blocks")) {
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

// !memes - Got all dem memes
        if (arg[0].equalsIgnoreCase(prefix + "memes")) {
            if (checkPerm(event.getUser(), 0)) {
                try {
                    if (arg[1].equalsIgnoreCase("set")) {
                        if (memes.containsKey(arg[2].toLowerCase())) {
                            Meme meme = memes.get(arg[2].toLowerCase());
                            if (checkPerm(event.getUser(), 5) || meme.getCreator().equalsIgnoreCase(event.getUser().getNick())) {
                                if (arg.length == 3) {
                                    memes.remove(arg[2].toLowerCase());
                                    sendMessage(event, "Meme " + arg[2] + " Deleted!", true);
                                } else {
                                    meme.setMeme(arg[3]);
                                    memes.put(arg[2].toLowerCase(), meme);
                                    sendMessage(event, "Meme " + arg[2] + " Edited!", true);
                                }
                            } else {
                                sendMessage(event, "Sorry, Only the creator of the meme can edit it", true);
                            }
                        } else {
                            memes.put(arg[2].toLowerCase(), new Meme(event.getUser().getNick(), arg[3]));
                            sendMessage(event, "Meme " + arg[2] + " Created as " + arg[3], true);
                        }
                    } else if (arg[1].equalsIgnoreCase("list")) {
                        sendMessage(event, memes.values().toString(), true);
                    } else {
                        if (memes.containsKey(arg[1].toLowerCase())) {
                            sendMessage(event, arg[1] + ": " + memes.get(arg[1].toLowerCase()).getMeme(), false);
                        } else {
                            sendMessage(event, "That Meme doesn't exist!", true);
                        }
                    }
                } catch (Exception e) {
                    sendError(event, e);
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
                            if (event.getUser().getNick().equalsIgnoreCase(noteList.get(index).getSender())) {
                                noteList.remove(index);
                                sendMessage(event, "com.LilG.Com.DataClasses.Note " + arg[2] + " Deleted", true);
                            } else {
                                sendMessage(event, "Nick didn't match nick that left note, as of right now there is no alias system so if you did leave this note; switch to the nick you used when you left it", true);
                            }
                        } else {
                            sendMessage(event, "That ID wasn't found.", true);
                        }
                    } catch (Exception e) {
                        sendError(event, e);
                    }
                } else if (arg[1].equalsIgnoreCase("list")) {
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
                } else {
                    try {
                        Note note = new Note(event.getUser().getNick(), arg[2], arg[3], event.getChannel().getName());
                        noteList.add(note);
                        sendMessage(event, "Left note \"" + arg[3] + "\" for \"" + arg[2] + "\".", false);
                        event.getUser().send().notice("ID is \"" + noteList.get(noteList.indexOf(note)).getId().toString() + "\"");
                    } catch (Exception e) {
                        sendError(event, e);
                    }
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
        if (event.getMessage().equalsIgnoreCase(prefix + "version") && !event.getChannel().getName().equalsIgnoreCase("#deltasmash")) {
            if (checkPerm(event.getUser(), 0)) {
                String VERSION = "PircBotX: " + PircBotX.VERSION + ". BotVersion: 2.1. Java version: " + System.getProperty("java.version");

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
                permError(event.getUser());
            }
        }

// !ChngCMD - Changes the command prefix
        if (arg[0].equalsIgnoreCase(prefix + "chngcmd") && !chngCMDRan) {
            if (checkPerm(event.getUser(), 5)) {
                prefix = arg[1];
                sendMessage(event, "Command variable is now \"" + prefix + "\"", true);
            } else {
                permError(event.getUser());
            }
        }
        chngCMDRan = false;

// !Saythis - Tells the bot to say someting
        if (arg[0].equalsIgnoreCase(prefix + "saythis")) {
            if (checkPerm(event.getUser(), 4)) {
                sendMessage(event, arg[1], false);
            } else {
                permErrorchn(event);
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
                permErrorchn(event);
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
                            setDebugInfo(event);
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

                    setDebugInfo(event);
                }
                if (arg[1].equalsIgnoreCase("ListClass")) {
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDClasses.values()), true);
                }
                if (arg[1].equalsIgnoreCase("ListSpecies")) {
                    sendMessage(event, "List of classes: " + Arrays.toString(DNDPlayer.DNDFamiliars.values()), true);
                }
                //noinspection StatementWithEmptyBody
                if (arg[1].equalsIgnoreCase("DM")) {
                    //todo
                }
                if (arg[1].equalsIgnoreCase("Test")) { //testing commands.
                    //checkPerm(event.getUser())
                    try {
                        int index = DNDJoined.indexOf(event.getUser().getNick());
                        if (arg[2].equalsIgnoreCase("addItem")) {
                            DNDList.get(index).addInventory(arg[3]);
                            sendMessage(event, "Added " + arg[3] + " to your inventory", true);
                        }
                        if (arg[2].equalsIgnoreCase("getItems")) {
                            sendMessage(event, DNDList.get(index).getInventory(), true);
                        }
                        if (arg[2].equalsIgnoreCase("delItem")) {
                            DNDList.get(index).removeFromInventory(arg[3]);
                            sendMessage(event, "removed " + arg[3] + " to your inventory", true);
                        }
                        if (arg[2].equalsIgnoreCase("addXP")) {
                            DNDList.get(index).addXP(Integer.parseInt(arg[3]));
                            sendMessage(event, "Added " + arg[3] + " to your XP", true);
                        }
                        if (arg[2].equalsIgnoreCase("addHP")) {
                            DNDList.get(index).addHP(Integer.parseInt(arg[3]));
                            sendMessage(event, "Added " + arg[3] + " to your HP", true);
                        }
                        if (arg[2].equalsIgnoreCase("subHP")) {
                            DNDList.get(index).hit(Integer.parseInt(arg[3]));
                            sendMessage(event, "Subbed " + arg[3] + " from your HP", true);
                        }
                        if (arg[2].equalsIgnoreCase("addXPFam")) {
                            DNDList.get(index).getFamiliar().addXP(Integer.parseInt(arg[3]));
                            sendMessage(event, "Added " + arg[3] + " to your familiar's XP", true);
                        }
                        if (arg[2].equalsIgnoreCase("addHPFam")) {
                            DNDList.get(index).getFamiliar().addHP(Integer.parseInt(arg[3]));
                            sendMessage(event, "Added " + arg[3] + " to your familiar's HP", true);
                        }
                        if (arg[2].equalsIgnoreCase("subHPFam")) {
                            DNDList.get(index).getFamiliar().hit(Integer.parseInt(arg[3]));
                            sendMessage(event, "Subbed " + arg[3] + " from your familiar's HP", true);
                        }
                        if (arg[2].equalsIgnoreCase("getFamiliar")) {
                            sendMessage(event, DNDList.get(index).getFamiliar().toString(), true);
                        }

                        if (arg[2].equalsIgnoreCase("clearList")) {
                            if (checkPerm(event.getUser(), 3)) {
                                DNDJoined.clear();
                                DNDList.clear();
                                sendMessage(event, "DND Player lists cleared", false);
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
                            sendMessage(event, "Pos is now: " + DNDDungeon.toString(), true);

                        }

                        if (arg[2].equalsIgnoreCase("getPos")) {
                            Point temp = DNDDungeon.getLocation();
                            sendMessage(event, "Current location: (" + temp.x + "," + temp.y + ")", true);
                        }

                        if (arg[2].equalsIgnoreCase("movePos")) {
                            DNDDungeon.move(Integer.parseInt(arg[3]), Integer.parseInt(arg[4]));
                            Point temp = DNDDungeon.getLocation();
                            sendMessage(event, "New location: (" + temp.x + "," + temp.y + ")", true);
                        }

                        if (arg[2].equalsIgnoreCase("getSurroundings")) {
                            int[] tiles = DNDDungeon.getSurrounding();
                            sendMessage(event, " | " + tiles[7] + " | " + tiles[0] + " | " + tiles[1] + " | ", true);
                            sendMessage(event, " | " + tiles[6] + " | " + tiles[8] + " | " + tiles[2] + " | ", true);
                            sendMessage(event, " | " + tiles[5] + " | " + tiles[4] + " | " + tiles[3] + " | ", true);
                        }

                        int frameWidth = 300;
                        int frameHeight = 300;
                        if (arg[2].equalsIgnoreCase("genDungeon")) {
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

                        if (arg[2].equalsIgnoreCase("draw")) {
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
                        if (arg.length == 3) {
                            Language to = Language.valueOf(arg[1].toUpperCase());
                            Language from = Detect.execute(arg[2]);
                            text = Translate.execute(arg[2], from, to);
                            System.out.print("Translating: " + text);
                            if (arg[2].contains(text)) {
                                sendMessage(event, "Yandex couldn't translate that.", true);
                            } else {
                                sendMessage(event, text, true);
                            }
                        } else if (arg.length == 4) {
                            Language to = Language.valueOf(arg[2].toUpperCase());
                            Language from = Language.valueOf(arg[1].toUpperCase());
                            text = Translate.execute(arg[3], from, to);
                            System.out.print("Translating: " + text);
                            if (arg[3].contains(text)) {
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

// !debugvar - changes a variable to the value
        if (arg[0].equalsIgnoreCase(prefix + "debugvar")) {
            if (checkPerm(event.getUser(), 5)) {
                switch (arg[1].toLowerCase()) { //Make sure strings are lowercsae
                    case "i":
                        int i = Integer.parseInt(arg[2]);
                        sendMessage(event, "DEBUG: Var \"i\" is now \"" + i + "\"", true);
                        break;
                    case "jokenum":
                        jokeCommandDebugVar = Integer.parseInt(arg[2]);
                        sendMessage(event, "DEBUG: Var \"jokeCommandDebugVar\" is now \"" + jokeCommandDebugVar + "\"", true);
                }
            } else {
                permErrorchn(event);
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
                permError(event.getUser());
            }
        }

// \ - runs commands without closing at the end
        String consolePrefix = "\\";
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
                permErrorchn(event);
            }
        }

// !SayNotice - Tells the bot to send a notice
        if (arg[0].equalsIgnoreCase(prefix + "SayNotice")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().notice(arg[1], arg[2]);
            } else {
                permErrorchn(event);
            }
        }

// !SayCTCPCommand - Tells the bot to send a CTCP Command
        if (arg[0].equalsIgnoreCase(prefix + "SayCTCPCommand")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().ctcpCommand(arg[2], arg[1]);
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
        if (arg[0].equalsIgnoreCase(prefix + "leave")) {
            if (checkPerm(event.getUser(), 5)) {
                if (arg[1].length() == 2) {
                    event.getChannel().send().part(arg[1]);
                } else {
                    event.getChannel().send().part();
                }
            } else if (!event.getBot().getServerHostname().equalsIgnoreCase("irc.twitch.tv")) {
                permErrorchn(event);
            }
        }

// !respawn - Tells the bot to restart and reconnect
        if (arg[0].equalsIgnoreCase(prefix + "respawn")) {
            if (checkPerm(event.getUser(), 3)) {
                saveData(event);
                event.getBot().sendIRC().quitServer("Died! Respawning in about 10 seconds");
                event.getBot().stopBotReconnect();
            } else {
                permErrorchn(event);
            }
        }

// !recycle - Tells the bot to part and rejoin the channel
        if (arg[0].equalsIgnoreCase(prefix + "recycle")) {
            if (checkPerm(event.getUser(), 3)) {
                saveData(event);
                event.getChannel().send().cycle();
            } else {
                permErrorchn(event);
            }
        }
// !revoice - gives everyone voice if they didn't get it
        if (arg[0].equalsIgnoreCase(prefix + "revoice")) {
            for (User user1 : event.getChannel().getUsers()) {
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
                permErrorchn(event);
            }
        }


// !changenick - Changes the nick of the bot
        if (arg[0].equalsIgnoreCase(prefix + "changeNick")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getBot().sendIRC().changeNick(arg[1]);
                debug.setNick(arg[1]);
            } else {
                permErrorchn(event);
            }
        }

// !SayAction - Makes the bot do a action
        if (arg[0].equalsIgnoreCase(prefix + "SayAction")) {
            if (checkPerm(event.getUser(), 5)) {
                event.getChannel().send().action(arg[1]);
            } else {
                permErrorchn(event);
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
                    permErrorchn(event);
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

// !my  - Joke command - This was requested by Greeny in #origami64. ask him about it
        if (arg[0].equalsIgnoreCase(prefix + "my")) {
            if (checkPerm(event.getUser(), 0)) {
                if (jokeCommands || checkPerm(event.getUser(), 1))
                    if (channel.equalsIgnoreCase("#origami64") || channel.equalsIgnoreCase("#sm64") || channel.equalsIgnoreCase("#Lil-G|bot") || channel.equalsIgnoreCase("#SSB") || channel.equalsIgnoreCase("#firemario_sucks ")) {

                        if (arg[1].equalsIgnoreCase("DickSize")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "8" + StringUtils.leftPad("D", size, "=") + " - " + size, true);
                            }
                        } else if (arg[1].equalsIgnoreCase("vaginadepth")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Less than Integer.MIN_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, "|" + StringUtils.leftPad("{0}", size, "=") + " -  -" + size, true);
                            }
                        } else if (arg[1].equalsIgnoreCase("BallCount")) {
                            if (event.getUser().getNick().equalsIgnoreCase(currentNick)) {
                                sendMessage(event, "Error: IntegerOutOfBoundsException: Greater than Integer.MAX_VALUE", true);
                            } else {
                                int size = randInt(0, jokeCommandDebugVar);
                                sendMessage(event, StringUtils.leftPad("", size, "8") + " - " + size, true);
                            }
                        } else if (arg[1].equalsIgnoreCase("xdlength")) {
                            int size = randInt(0, jokeCommandDebugVar);
                            sendMessage(event, "X" + StringUtils.leftPad("", size, "D") + " - " + size, true);
                        } else if (arg[1].equalsIgnoreCase("ass")) {
                            sendMessage(event, "No.", true);
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

                System.out.println(event.getMessage());
            }
        }

        saveData(lastEvent);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
        String DNDDungeonMaster = "Null";
        debug.setCurrDM(DNDDungeonMaster);
        debug.setMessage(event.getUser().getNick() + ": " + event.getMessage());
        //noinspection ConstantConditions
        checkIfUserHasANote(event, event.getUser().getNick(), event.getChannel().getName());
    }

    public void onPrivateMessage(PrivateMessageEvent PM) {

        String[] arg = splitMessage(PM.getMessage());

// !rps - Rock! Paper! ehh you know the rest
        if (arg[0].equalsIgnoreCase(prefix + "rps")) {
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
                    if (arg.length > 1) {
                        switch (arg[1]) {
                            case "r":
                                if (isFirstPlayer) {
                                    game.setP1Choice(1);
                                } else {
                                    game.setP2Choice(1);
                                }
                        }
                    }
                } else if (arg.length > 1 && !(arg[1].equalsIgnoreCase("r") || arg[1].equalsIgnoreCase("p") || arg[1].equalsIgnoreCase("s"))) {
                    rpsGames.add(new RPSGame(PM.getUser().getNick(), arg[1]));
                    PM.getUser().send().notice("Created a game with " + arg[1]);
                } else {
                    PM.getUser().send().notice("You aren't in a game!");
                }
            }
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
        else if (checkPerm(PM.getUser(), 5) &&
                !arg[0].equalsIgnoreCase(prefix + "login") &&
                !arg[0].equalsIgnoreCase(prefix + "rps") &&
                arg[0].contains(prefix)) {


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
            if (arg[0].equalsIgnoreCase(prefix + "quitserv")) {
                PM.getUser().send().notice("Disconnecting from server");
                if (arg.length > 1) {
                    PM.getBot().sendIRC().quitServer(arg[1]);
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
                ImmutableMap autoChannels = PM.getBot().getConfiguration().getAutoJoinChannels();
                for (int i = 0; i < autoChannels.size(); i++) {

                }
            }
        } else if (arg[0].startsWith(prefix)) {
            permError(PM.getUser());
            PM.getBot().sendIRC().notice(currentNick, "Attempted use of PM commands by " + PM.getUser().getNick() + ". The command used was \"" + PM.getMessage() + "\"");
        }
        debug.updateBot(PM.getBot());
        checkIfUserHasANote(PM, PM.getUser().getNick(), null);
        debug.setCurrentNick(currentNick + "!" + currentUsername + "@" + currentHost);
    }

    public void onJoin(JoinEvent join) {
        System.out.println("User Joined");
        User bot = join.getBot().getUserBot();
        if (checkOP(join.getChannel())) {
            if (checkPerm(join.getUser(), 0)) {
                join.getChannel().send().voice(join.getUserHostmask());
            }
        }
        //noinspection ConstantConditions
        checkIfUserHasANote(join, join.getUser().getNick(), join.getChannel().getName());
        debug.updateBot(join.getBot());
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
        checkIfUserHasANote(event, event.getUser().getNick(), null);
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
        nickInUse = true;
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
    private boolean checkPerm(User user, int userLevel) {
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
                        if (Hostname.equalsIgnoreCase(user.getHostname()) || Hostname.equalsIgnoreCase("*")) {
                            return authedUserLevel.get(index) >= userLevel;
                        }
                    }
                }
                index--;
            }
            if (userLevel <= 0) {
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
        if (color) {
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
            SaveDataStore save = new SaveDataStore(noteList, authedUser, authedUserLevel, DNDJoined, DNDList, avatar, memes);
            FileWriter writer = new FileWriter("Data/" + network + "-Data.json");
            writer.write(gson.toJson(save));
            writer.close();

            System.out.println("Data saved!");
        } catch (Exception e) {
            sendError(event, e);
        }
    }

    private void checkIfUserHasANote(Event event, String user, String channel) {
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
                    System.out.println("com.LilG.Com.DataClasses.Note Loop Start");
                    int index = indexList.size() - 1;
                    System.out.println("Index " + index);
                    String receiver = noteList.get(index).getReceiver();
                    System.out.println(receiver);
                    String message = noteList.get(index).displayMessage();
                    System.out.println(message);
                    if (channel != null) {
                        sendMessage(channel, message);
                    } else {
                        event.getBot().sendIRC().notice(receiver, message);
                    }
                    noteList.remove(index);
                    indexList.remove(index);
                    System.out.println(" com.LilG.Com.DataClasses.Note Loop End");

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
        for (; length < argToStartFrom; argToStartFrom++) {
            strToReturn += " " + args[argToStartFrom];
        }
        return strToReturn;
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