package com.LilG.Misc;

import ch.qos.logback.classic.Logger;
import com.LilG.DiscordAdapter;
import com.LilG.FozruciX;
import com.LilG.utils.LilGUtil;
import com.google.common.collect.ImmutableSortedSet;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.OutputEvent;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by ggonz on 11/4/2015.
 * A debug window to monitor certain variables
 */
public class DebugWindow extends JFrame {
    // Define constants, variables, and labels
    private static final int WIDTH = 800;
    private static final int HEIGHT = 220;
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DebugWindow.class);
    private JTextField currentNick;
    private JTextField lastMessage;
    private JTextField memoryUsage;
    private JTextField message;
    private JComboBox channelList;
    @NotNull
    private String[] channels = {"#null"};
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
    private String selectedChannel = "#null";
    private DefaultComboBoxModel<String> comboBox;
    private GridLayout gridLayout = new GridLayout(4, 2);
    private Runtime runtime = Runtime.getRuntime();
    //private FilePlayer player = new FilePlayer();
    private MaryInterface marytts;

    public DebugWindow(@NotNull ConnectEvent event, @NotNull FozruciX.Network network, @NotNull FozruciX fozruciX) {
        this.fozruciX = fozruciX;
        this.bot = event.getBot();
        this.network = network;
        JLabel currentNickL, lastMessageL, memoryUsageL;
        String networkName = bot.getServerInfo().getNetwork();
        String nick = bot.getNick();
        if (network == FozruciX.Network.discord) {
            jda = DiscordAdapter.getJda();
            networkName = "Discord";
            nick = jda.getSelfUser().getName();
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
        memoryUsageL = new JLabel("Memory Usage", SwingConstants.LEFT);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
                for (int i = 0; i > channels.length; i++) {
                    comboBox.addElement(channels[i]);
                }
            }
        };
        message.addActionListener(action);

        //Create Grid layout for window
        Container pane = getContentPane();
        pane.setLayout(gridLayout);


        super.setAlwaysOnTop(true);

        pane.add(currentNickL);
        pane.add(currentNick);

        pane.add(lastMessageL);
        pane.add(lastMessage);

        pane.add(memoryUsageL);
        pane.add(memoryUsage);

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
        pane.add(message);

        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setState(Frame.ICONIFIED);
        setVisible(true);

        selectedChannel = (String) comboBox.getSelectedItem();

        Timer timer = new Timer(2000, e -> memoryUsage.setText("Current memory usage: " + LilGUtil.formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + LilGUtil.formatFileSize(runtime.totalMemory()) + ". Total memory that can be used: " + LilGUtil.formatFileSize(runtime.maxMemory()) + ".  Active Threads: " + Thread.activeCount() + "/" + ManagementFactory.getThreadMXBean().getThreadCount() + ".  Available Processors: " + runtime.availableProcessors()));
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
            for (Guild guild : jda.getGuildsByName(guildName, false)) {
                if (guild.getName().equalsIgnoreCase(guildName)) {
                    for (TextChannel textChannel : guild.getTextChannels()) {
                        if (textChannel.getName().equalsIgnoreCase(channel) && !selectedChannel.contains(": v#")) {
                            String messageToSend = FozruciX.getScramble(message.getText());
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
                    /*for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                        if (voiceChannel.getName().equalsIgnoreCase(channel) && selectedChannel.contains(": v#")) {
                            AudioManager audioManager = voiceChannel;
                            File outputFile = new File("Data/messageSent.wav");
                            LOGGER.info("I currently have " + marytts.getAvailableVoices() + " voices in "
                                    + marytts.getAvailableLocales() + " languages available.");
                            LOGGER.info("Out of these, " + marytts.getAvailableVoices(Locale.US) + " are for US English.");
                            try {
                                if (!voiceChannel.equals(voiceChannel)) {
                                    audioManager.closeAudioConnection();
                                    audioManager.openAudioConnection(voiceChannel);
                                    audioManager.setSendingHandler(player);

                                }
                                AudioInputStream audio = marytts.generateAudio(FozruciX.getScramble(message.getText()));
                                AudioSystem.write(AudioSystem.getAudioInputStream(AudioSendHandler.INPUT_FORMAT, audio), AudioFileFormat.Type.WAVE, outputFile);
                                player.setAudioFile(outputFile);
                                player.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break exitLoop;
                        }
                    }*/
                }
            }
        } else {
            bot.send().message(selectedChannel, FozruciX.getScramble(message.getText()));
        }
        message.setText("");
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
        currentNick.setText(nick);
    }

    public void setMessage(String message) {
        lastMessage.setText(message);
    }

    public void setNick(String botNick) {
        setTitle(botNick);
    }

}