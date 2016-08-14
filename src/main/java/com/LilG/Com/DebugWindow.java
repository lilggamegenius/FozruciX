package com.LilG.Com;

import com.google.common.collect.ImmutableSortedSet;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ConnectEvent;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
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
    @Nullable
    private PircBotX bot;
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
    private final Timer timer = new Timer(1000, (ActionListener) e -> memoryUsageTF.setText("Current memory usage: " + formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + formatFileSize(runtime.totalMemory()) + ". Total memory that can be used: " + formatFileSize(runtime.maxMemory()) + ".  Active Threads: " + Thread.activeCount() + "/" + ManagementFactory.getThreadMXBean().getThreadCount() + ".  Available Processors: " + runtime.availableProcessors()));

    DebugWindow(@NotNull ConnectEvent event) {
        this.bot = event.getBot();
        JLabel currentNickL, lastMessageL, currDML, myPlayerNameL, myPlayerHPL, myPlayerXPL, myFamiliarL, myFamiliarHPL, myFamiliarXPL, memoryUsageL;
        String network = bot.getServerInfo().getNetwork();
        String nick = bot.getNick();
        if (network == null) {
            if (event instanceof DiscordConnectEvent) {
                network = "Discord";
                nick = ((DiscordConnectEvent) event).getReadyEvent().getJDA().getSelfInfo().getUsername();
            } else {
                network = bot.getServerHostname();
                network = network.substring(network.indexOf(".") + 1, network.lastIndexOf("."));
            }
        }
        setTitle(nick + " @ " + network);
        channels = getChannels(event);


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
        setVisible(true);

        selectedChannel = (String) comboBox.getSelectedItem();

        timer.start();
    }

    @Nullable
    public ConnectEvent getConnectEvent() {
        return connectEvent;
    }

    private String[] getChannels(ConnectEvent event) {
        if (event instanceof DiscordConnectEvent) {
            jda = ((DiscordConnectEvent) event).getReadyEvent().getJDA();
        }
        return getChannels();
    }

    private String[] getChannels() {
        ArrayList<String> channelList = new ArrayList<>();

        if (jda != null) {
            java.util.List<Guild> guildList = jda.getGuilds();
            for (Guild guild : guildList) {
                java.util.List<TextChannel> channels = guild.getTextChannels();
                channelList.addAll(channels.stream().map(channel -> guild.getName() + ": #" + channel.getName()).collect(Collectors.toList()));
            }
        } else {
            ImmutableSortedSet<Channel> channel = bot.getUserBot().getChannels();
            channelList.addAll(channel.stream().map(Channel::getName).collect(Collectors.toList()));
        }
        return channelList.toArray(new String[channelList.size()]);
    }

    private void sendMessage() {
        selectedChannel = (String) comboBox.getSelectedItem();
        bot.send().message(selectedChannel, FozruciX.getScramble(messageTF.getText()));
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
