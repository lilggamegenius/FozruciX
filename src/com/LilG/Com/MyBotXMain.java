package com.LilG.Com;

import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;

import java.nio.charset.Charset;

/**
 * Created by ggonz on 10/12/2015.
 * The Main file with all of the configs
 */
class MyBotXMain{
    public static void main(String[] args) throws Exception {
        //Configure what we want our bot to do
        final boolean debug = false;
        String nick = "FozruciX";
        String login = "SmugLeaf";
        String realName = "\u00034\u000F* What can I do for you, little buddy?";
        String badnickNET = "irc.badnik.net";
        String twitch = "irc.twitch.tv";
        String caffie = "irc.caffie.net";
        int attempts = Integer.MAX_VALUE;

        Configuration.Builder normal = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(attempts)
                .setNickservPassword(MyBotX.setPassword(true))
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
                .setAutoReconnectAttempts(attempts)
                .setNickservPassword(MyBotX.setPassword(true))
                .setName(nick) //Set the nick of the bot.
                .setLogin(login)
                .setRealName(realName)
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#Lil-G|bot") //Join the official #Lil-G|Bot channel
                .addAutoJoinChannel("#SSB")
                .addAutoJoinChannel("#origami64")
                .addListener(new MyBotX()); //Add our listener that will be called on Events

        Configuration.Builder twitchNormal = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(attempts)
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
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(attempts)
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setName(nick.toLowerCase()) //Set the nick of the bot.
                .setLogin(nick.toLowerCase())
                .addAutoJoinChannel("#lilggamegenuis") //Join lilggamegenuis's twitch chat
                .addListener(new MyBotX(true)); //Add our listener that will be called on Events

        Configuration.Builder normalSmwc = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(attempts)
                .setNickservPassword(MyBotX.setPassword(true))
                .setName(nick) //Set the nick of the bot.
                .setLogin(login)
                .setRealName(realName)
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#sm64")
                .addAutoJoinChannel("#pmd")
                .addAutoJoinChannel("#botTest")
                .addAutoJoinChannel("#unix")
                .addAutoJoinChannel("#smashbros")
                .addListener(new MyBotX()); //Add our listener that will be called on Events


        Configuration.Builder debugConfigSmwc = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF-8"))
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(attempts)
                .setNickservPassword(MyBotX.setPassword(true))
                .setName(nick) //Set the nick of the bot.
                .setLogin(login)
                .setRealName(realName)
                .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates())
                .addAutoJoinChannel("#sm64")
                .addAutoJoinChannel("#pmd")
                .addAutoJoinChannel("#botTest")
                .addAutoJoinChannel("#unix")
                .addAutoJoinChannel("#smashbros")
                .addListener(new MyBotX()); //Add our listener that will be called on Events

        //Create our bot with the configuration
        MultiBotManager manager = new MultiBotManager();
        if (debug) {
            manager.addBot(debugConfig.buildForServer(badnickNET, 6697));
            manager.addBot(debugConfigSmwc.buildForServer(caffie, 6697));
            manager.addBot(twitchDebug.buildForServer(twitch, 6667, MyBotX.setPassword(false)));
        } else {
            manager.addBot(normal.buildForServer(badnickNET, 6697));
            manager.addBot(normalSmwc.buildForServer(caffie, 6697));
            manager.addBot(twitchNormal.buildForServer(twitch, 6667, MyBotX.setPassword(false)));
        }
        //Connect to the server
        manager.start();


    }


}
