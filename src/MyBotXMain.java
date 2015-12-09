import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by ggonz on 10/12/2015.
 */
public class MyBotXMain {
    public static void main(String[] args) throws Exception {
        //Configure what we want our bot to do
        boolean debug = false;
        String nick = "FozruciX";
        String login = "SmugLeaf";
        String realName = "Lil-Gs Bot";
        String badnickNET = "irc.badnik.net";
        String twitch = "irc.twitch.tv";

        Configuration.Builder normal = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setNickservPassword(setPassword(true))
                .setName(nick) //Set the nick of the bot.
                .setLogin(login)
                .setRealName(realName)
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
                .addAutoJoinChannel("#pokemon")
                .addAutoJoinChannel("#retro")
                .addAutoJoinChannel("#retrotech")
                .addAutoJoinChannel("#SSB")
                .addAutoJoinChannel("#origami64")
                .addListener(new MyBotX()); //Add our listener that will be called on Events


        Configuration.Builder debugConfig = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setNickservPassword(setPassword(true))
                .setName(nick) //Set the nick of the bot.
                .setLogin(login)
                .setRealName(realName)
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
                .addAutoJoinChannel("#SSB")
                .addAutoJoinChannel("#origami64")
                .addListener(new MyBotX()); //Add our listener that will be called on Events

        Configuration.Builder twitchNormal = new Configuration.Builder()
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
                .addListener(new MyBotX(true)); //Add our listener that will be called on Events


        Configuration.Builder twitchDebug = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setName(nick.toLowerCase()) //Set the nick of the bot.
                .setLogin(nick.toLowerCase())
                .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
                .addListener(new MyBotX(true)); //Add our listener that will be called on Events

        //Create our bot with the configuration
        MultiBotManager manager = new MultiBotManager();
        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnickNET, 6697));
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, setPassword(false)));
        } else {
            manager.addBot(normal.buildForServer(badnickNET, 6697));
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, setPassword(false)));
        }
        //Connect to the server
        manager.start();


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
}
