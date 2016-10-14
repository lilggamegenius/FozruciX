package com.LilG.Com;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableSortedSet;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.audio.AudioSendHandler;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.OutputEvent;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.LilG.Com.utils.LilGUtil.formatFileSize;

/**
 * Created by ggonz on 11/4/2015.
 * A debug window to monitor certain variables
 */
class DebugWindow extends JFrame {
    // Define constants, variables, and labels
    private static final int WIDTH = 800;
    private static final int HEIGHT = 220;
    @NotNull
    private final JTextField currentNickTF;
    @NotNull
    private final JTextField lastMessageTF;
    @NotNull
    private final JTextField currDMTF;
    @NotNull
    private final JTextField myPlayerNameTF;
    @NotNull
    private final JTextField myPlayerHPTF;
    @NotNull
    private final JTextField myPlayerXPTF;
    @NotNull
    private final JTextField myFamiliarTF;
    @NotNull
    private final JTextField myFamiliarHPTF;
    @NotNull
    private final JTextField myFamiliarXPTF;
    private final JTextField memoryUsageTF = new JTextField(10);
    @NotNull
    private final JTextField messageTF;
    @NotNull
    private PircBotX bot;
    @NotNull
    private FozruciX fozruciX;
    @NotNull
    private FozruciX.Network network;
    @Nullable
    private JDA jda;
    @Nullable
    private ConnectEvent connectEvent;
    @NotNull
    private String[] channels = {"#null"};
    @NotNull
    private String selectedChannel = "#null";
    private DefaultComboBoxModel<String> comboBox;
    private Runtime runtime = Runtime.getRuntime();

    private MaryInterface marytts;
    private FilePlayer player = new FilePlayer();

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DebugWindow.class);

    DebugWindow(@NotNull ConnectEvent event, @NotNull FozruciX.Network network, @NotNull FozruciX fozruciX) {
        this.fozruciX = fozruciX;
        this.bot = event.getBot();
        this.network = network;
        JLabel currentNickL, lastMessageL, currDML, myPlayerNameL, myPlayerHPL, myPlayerXPL, myFamiliarL, myFamiliarHPL, myFamiliarXPL, memoryUsageL;
        String networkName = bot.getServerInfo().getNetwork();
        String nick = bot.getNick();
        if (network == FozruciX.Network.discord) {
            jda = DiscordAdapter.getJda();
            networkName = "Discord";
            nick = jda.getSelfInfo().getUsername();
            try {
                marytts = new LocalMaryInterface();
                marytts.setVoice("cmu-bdl-hsmm");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (networkName == null) {
            networkName = bot.getServerHostname();
            networkName = networkName.substring(networkName.indexOf(".") + 1, networkName.lastIndexOf("."));
        }
        setTitle(nick + " @ " + networkName);
        channels = getChannels();


        currentNickL = new JLabel("Currently Registered User", SwingConstants.LEFT);
        lastMessageL = new JLabel("Last message", SwingConstants.LEFT);
        currDML = new JLabel("Current Dungeon master", SwingConstants.LEFT);
        myPlayerNameL = new JLabel("My username", SwingConstants.LEFT);
        myPlayerHPL = new JLabel("HP", SwingConstants.LEFT);
        myPlayerXPL = new JLabel("XP", SwingConstants.LEFT);
        myFamiliarL = new JLabel("Familiar", SwingConstants.LEFT);
        myFamiliarHPL = new JLabel("Familiar HP", SwingConstants.LEFT);
        myFamiliarXPL = new JLabel("Familiar XP", SwingConstants.LEFT);
        memoryUsageL = new JLabel("Memory Usage", SwingConstants.LEFT);

        currentNickTF = new JTextField(10);
        lastMessageTF = new JTextField(10);
        currDMTF = new JTextField(10);
        myPlayerNameTF = new JTextField(10);
        myPlayerHPTF = new JTextField(10);
        myPlayerXPTF = new JTextField(10);
        myFamiliarTF = new JTextField(10);
        myFamiliarHPTF = new JTextField(10);
        myFamiliarXPTF = new JTextField(10);
        messageTF = new JTextField(512);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
                for (int i = 0; i > channels.length; i++) {
                    comboBox.addElement(channels[i]);
                }
            }
        };
        messageTF.addActionListener(action);

        //Create Grid layout for window
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(11, 2));


        super.setAlwaysOnTop(true);

        pane.add(currentNickL);
        pane.add(currentNickTF);

        pane.add(lastMessageL);
        pane.add(lastMessageTF);

        pane.add(currDML);
        pane.add(currDMTF);

        pane.add(myPlayerNameL);
        pane.add(myPlayerNameTF);

        pane.add(myPlayerHPL);
        pane.add(myPlayerHPTF);

        pane.add(myPlayerXPL);
        pane.add(myPlayerXPTF);

        pane.add(myFamiliarL);
        pane.add(myFamiliarTF);

        pane.add(myFamiliarHPL);
        pane.add(myFamiliarHPTF);

        pane.add(myFamiliarXPL);
        pane.add(myFamiliarXPTF);

        pane.add(memoryUsageL);
        pane.add(memoryUsageTF);

        comboBox = new DefaultComboBoxModel<>(channels);
        comboBox.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                selectedChannel = (String) comboBox.getSelectedItem();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                selectedChannel = (String) comboBox.getSelectedItem();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                selectedChannel = (String) comboBox.getSelectedItem();
            }
        });
        pane.add(new JComboBox<>(comboBox));
        pane.add(messageTF);

        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setState(Frame.ICONIFIED);
        setVisible(true);

        selectedChannel = (String) comboBox.getSelectedItem();

        Timer timer = new Timer(1000, e -> memoryUsageTF.setText("Current memory usage: " + formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + formatFileSize(runtime.totalMemory()) + ". Total memory that can be used: " + formatFileSize(runtime.maxMemory()) + ".  Active Threads: " + Thread.activeCount() + "/" + ManagementFactory.getThreadMXBean().getThreadCount() + ".  Available Processors: " + runtime.availableProcessors()));
        timer.start();
    }

    @Nullable
    public ConnectEvent getConnectEvent() {
        return connectEvent;
    }

    private String[] getChannels() {
        ArrayList<String> channelList = new ArrayList<>();

        if (network == FozruciX.Network.discord) {
            java.util.List<Guild> guildList = jda.getGuilds();
            for (Guild guild : guildList) {
                java.util.List<TextChannel> channels = guild.getTextChannels();
                channelList.addAll(channels.stream().map(channel -> guild.getName() + ": #" + channel.getName()).collect(Collectors.toList()));
                java.util.List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
                channelList.addAll(voiceChannels.stream().map(channel -> guild.getName() + ": v#" + channel.getName()).collect(Collectors.toList()));
            }
        } else {
            ImmutableSortedSet<Channel> channel = bot.getUserBot().getChannels();
            channelList.addAll(channel.stream().map(Channel::getName).collect(Collectors.toList()));
        }
        return channelList.toArray(new String[channelList.size()]);
    }

    private void sendMessage() {
        selectedChannel = (String) comboBox.getSelectedItem();
        if (network == FozruciX.Network.discord) {
            String guildName = selectedChannel.substring(0, selectedChannel.indexOf(':'));
            String channel = selectedChannel.substring(selectedChannel.indexOf('#') + 1);
            exitLoop:
            for (Guild guild : jda.getGuildsByName(guildName)) {
                if (guild.getName().equalsIgnoreCase(guildName)) {
                    for (TextChannel textChannel : guild.getTextChannels()) {
                        if (textChannel.getName().equalsIgnoreCase(channel) && !selectedChannel.contains(": v#")) {
                            String messageToSend = FozruciX.getScramble(messageTF.getText());
                            textChannel.sendMessage(messageToSend);
                            try {
                                messageToSend = "PRIVMSG #" + textChannel.getName() + " :" + messageToSend;
                                fozruciX.onOutput(new OutputEvent(bot, messageToSend, Utils.tokenizeLine(messageToSend)));
                            }catch(Exception e) {
                                LOGGER.error("Error sending output event", e);
                            }
                            break exitLoop;
                        }
                    }
                    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                        if (voiceChannel.getName().equalsIgnoreCase(channel) && selectedChannel.contains(": v#")) {
                            AudioManager audioManager = jda.getAudioManager(guild);
                            VoiceChannel currentVoiceChannel = audioManager.getConnectedChannel();
                            File outputFile = new File("Data/messageSent.wav");
                            LOGGER.info("I currently have " + marytts.getAvailableVoices() + " voices in "
                                    + marytts.getAvailableLocales() + " languages available.");
                            LOGGER.info("Out of these, " + marytts.getAvailableVoices(Locale.US) + " are for US English.");
                            try {
                                if (!voiceChannel.equals(currentVoiceChannel)) {
                                    audioManager.closeAudioConnection();
                                    audioManager.openAudioConnection(voiceChannel);
                                    audioManager.setSendingHandler(player);

                                }
                                AudioInputStream audio = marytts.generateAudio(FozruciX.getScramble(messageTF.getText()));
                                AudioSystem.write(AudioSystem.getAudioInputStream(AudioSendHandler.INPUT_FORMAT, audio), AudioFileFormat.Type.WAVE, outputFile);
                                player.setAudioFile(outputFile);
                                player.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break exitLoop;
                        }
                    }
                }
            }
        } else {
            bot.send().message(selectedChannel, FozruciX.getScramble(messageTF.getText()));
        }
        messageTF.setText("");
    }

    public void updateBot(@NotNull PircBotX bot) {
        this.bot = bot;
        channels = getChannels();
        selectedChannel = (String) comboBox.getSelectedItem();
        updateChannels();
        comboBox.setSelectedItem(selectedChannel);
    }

    private void updateChannels() {
        comboBox.removeAllElements();
        for (String channel : channels) {
            comboBox.addElement(channel);
        }
        comboBox.setSelectedItem(selectedChannel);
    }

    public void setCurrentNick(String nick) {
        currentNickTF.setText(nick);
    }

    public void setMessage(String message) {
        lastMessageTF.setText(message);
    }

    public void setCurrDM(String DM) {
        currDMTF.setText(DM);
    }

    public void setPlayerName(String DNDName) {
        myPlayerNameTF.setText(DNDName);
    }

    public void setPlayerHP(String HP) {
        myPlayerHPTF.setText(HP);
    }

    public void setPlayerXP(String XP) {
        myPlayerXPTF.setText(XP);
    }

    public void setFamiliar(String Familiar) {
        myFamiliarTF.setText(Familiar);
    }

    public void setFamiliarHP(String HP) {
        myFamiliarHPTF.setText(HP);
    }

    public void setFamiliarXP(String XP) {
        myFamiliarXPTF.setText(XP);
    }

    public void setNick(String botNick) {
        setTitle(botNick);
    }

}

class DrawWindow extends Component {
    private final int map_size;
    private final int[][] map;
    private final Point currentPoint;

    public DrawWindow(int[][] map, int map_size, Point currentPoint) {
        this.map_size = map_size;
        this.map = map;
        this.currentPoint = currentPoint;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int scale = 5;
        g2d.scale(scale, scale);
        try {
            for (int dy = 0; dy < map_size; dy++) {
                for (int dx = 0; dx < map_size; dx++) {
                    if (dx == currentPoint.x && dy == currentPoint.y) {
                        g2d.setColor(Color.RED);
                    } else if (map[dx][dy] == 0) {
                        g2d.setColor(Color.CYAN);
                    } else if (map[dx][dy] == 1) {
                        g2d.setColor(Color.BLUE);
                    } else {
                        g2d.setColor(Color.MAGENTA);
                    }
                    g2d.drawLine(dx, dy, dx, dy);
                    //Thread.sleep(5);                 //1000 milliseconds is one second.
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
