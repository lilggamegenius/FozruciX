import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;

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
        final String PASSWORD = setPassword();
        Configuration normal = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setNickservPassword(PASSWORD)
                .setName("FozruciX") //Set the nick of the bot.
                .setLogin("SmugLeaf")
                .setRealName("Lil-Gs Bot")
                .setServer("irc.Badnik.net", 6697) //Join the BadnikNET network
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
                .addAutoJoinChannel("#pokemon")
                .addAutoJoinChannel("#retro")
                .addAutoJoinChannel("#retrotech")
                .addAutoJoinChannel("#SSB")
                .addAutoJoinChannel("#origami64")
                .addListener(new MyBotX()) //Add our listener that will be called on Events
                .buildConfiguration();

        Configuration debugConfig = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setNickservPassword(PASSWORD)
                .setName("FozruciX") //Set the nick of the bot. CHANGE IN YOUR CODE
                .setLogin("SmugLeaf")
                .setRealName("Lil-Gs Bot")
                .setServer("irc.Badnik.net", 6697) //Join the BadnikNET network
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
                .addAutoJoinChannel("#SSB")
                .addAutoJoinChannel("#origami64")
                .addListener(new MyBotX()) //Add our listener that will be called on Events
                .buildConfiguration();

        //Create our bot with the configuration
        PircBotX bot;
        if (debug) {
            bot = new PircBotX(debugConfig);
        } else {
            bot = new PircBotX(normal);
        }
        //Connect to the server
        bot.startBot();
        bot.sendIRC().mode(bot.getNick(), "+B");


    }

    public static String setPassword() {
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
            System.out.println("File content: " + ret);
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
