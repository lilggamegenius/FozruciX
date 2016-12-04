package com.LilG.CMD;

import ch.qos.logback.classic.Level;
import com.LilG.FozruciX;
import com.LilG.utils.LilGUtil;
import com.jcraft.jsch.*;
import com.sun.jna.Platform;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;

/**
 * Created by Lil-G on 11/13/2015.
 * this class is meant to interface with the systems command processor to allow the bot to run system commands
 * this version does not close after it has run allowing it to have the same environment without having to set it again
 */
public class CommandLine extends Thread {
    private static final ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CommandLine.class);
    private volatile GenericMessageEvent event;
    private volatile String command;
    private volatile boolean newCommand = false;
    private Process p;
    private BufferedWriter p_stdin;
    private BufferedReader p_inputStream;
    private boolean echoOff = false;
    private Session sshSession = null;
    private Channel sshChannel = null;
    private boolean ssh = false;
    private byte waitNum = 0;

    public CommandLine(@NotNull GenericMessageEvent event, @NotNull String... commandLine) {
        LOGGER.setLevel(Level.ALL);
        this.setName("Commandline: " + commandLine[0]);
        this.event = event;
        String console[] = new String[]{commandLine[0], "", ""};
        if (commandLine[0].equalsIgnoreCase("cmd") || commandLine[0].equalsIgnoreCase("command")) {
            console[0] = "cmd.exe";
        } else if (commandLine[0].equalsIgnoreCase("term") || commandLine[0].equalsIgnoreCase("terminal") || commandLine[0].equalsIgnoreCase("bash")) {
            console[0] = "bash";
        } else if (commandLine[0].equalsIgnoreCase("ps") || commandLine[0].equalsIgnoreCase("powershell")) {
            console[0] = "powershell.exe";
            console[1] = "-Command";
            console[2] = "-";
        } else if (commandLine[0].equalsIgnoreCase("ssh")) {
            ssh = true;
        }
        try {
            if (!ssh) {
                ProcessBuilder builder = new ProcessBuilder(console);
                p = builder.start();
                builder.redirectErrorStream(true);
                p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                if (console[0].equals("cmd.exe")) {
                    p_stdin.write("@echo off\n");
                }
                p_inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
                LilGUtil.pause(1);
                if(!p.isAlive()){
                    interrupt();
                    throw new IllegalStateException("Process exited");
                }
            } else {
                String host = JOptionPane.showInputDialog("Enter username@hostname", "lil-g@ssh.lilggamegenuis.tk");
                String user = host.substring(0, host.indexOf('@'));
                host = host.substring(host.indexOf('@') + 1);
                try {
                    JSch ssh = new JSch();
                    ssh.setKnownHosts(Platform.isLinux() ? "~/.ssh/known_hosts" : "C:/Users/ggonz/AppData/Local/lxss/home/lil-g/.ssh/known_hosts");
                    sshSession = ssh.getSession(user, host, 22);
                    String passwd = JOptionPane.showInputDialog("Enter password");
                    sshSession.setPassword(passwd);
                    UserInfo ui = new MyUserInfo() {
                        public void showMessage(String message) {
                            JOptionPane.showMessageDialog(null, message);
                        }

                        public boolean promptYesNo(String message) {
                            Object[] options = {"yes", "no"};
                            int foo = JOptionPane.showOptionDialog(null,
                                    message,
                                    "Warning",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null, options, options[0]);
                            return foo == 0;
                        }

                        // If password is not given before the invocation of Session#connect(),
                        // implement also following methods,
                        //   * UserInfo#getPassword(),
                        //   * UserInfo#promptPassword(String message) and
                        //   * UIKeyboardInteractive#promptKeyboardInteractive()

                    };

                    sshSession.setUserInfo(ui);
                    sshSession.connect();
                    sshChannel = sshSession.openChannel("shell");
                    p_stdin = new BufferedWriter(new OutputStreamWriter(sshChannel.getOutputStream()));
                    sshChannel.connect();
                    p_inputStream = new BufferedReader(new InputStreamReader(sshChannel.getInputStream()));
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
            if (ssh || console[0].equals("bash")) {
                p_stdin.write("export PS1=''\n");
            }
        } catch (Exception e) {
            sendError(event, e);
        }
    }


    public CommandLine() {
        LOGGER.setLevel(Level.ALL);
        String platform = Platform.isLinux() ? "bash" : "cmd.exe";
        this.setName("Commandline: " + platform);
        ProcessBuilder builder = new ProcessBuilder(platform);
        try {
            p = builder.start();
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            p_stdin.write(Platform.isLinux() ? "export PS1=''" : "@echo off\n");
            p_inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        newCommand = true;
        start();
    }

    private void sendError(@NotNull GenericMessageEvent event, @NotNull Exception e) {
        FozruciX.sendError((MessageEvent) event, e);
    }

    public void doCommand(GenericMessageEvent event, String command) {
        this.event = event;
        this.command = command;
        newCommand = true;
    }

    @Override
    public void run() {
        exitLoop:
        while (!Thread.interrupted()) { //--------------- Main Waiting Loop ----------------------
            if (newCommand) {
                try {
                    // write stdout of shell (=output of all commands)
                    if (echoOff) {
                        if (command == null) {
                            command = " ";
                        }
                        p_stdin.write(command);
                        p_stdin.newLine();
                        p_stdin.flush();
                        try {
                            String output;
                            long end = System.currentTimeMillis() + (5 * 1000);
                            while (System.currentTimeMillis() < end) {
                                if (p_inputStream.ready()) {
                                    output = p_inputStream.readLine();
                                    LOGGER.debug(command + " -> ■" + output + "■ chars: " + output.length());
                                    if (!output.contains(">") &&
                                            !output.contains("Microsoft Windows") &&
                                            !output.contains("Microsoft Corporation. All rights reserved") &&
                                            !output.contains("Windows PowerShell") &&
                                            !output.contains("Copyright") &&
                                            !output.contains("export PS1=''") &&
                                            !output.equals(" ") &&
                                            !output.equals("\0") &&
                                            !output.equals(command) &&
                                            !output.isEmpty()) {

                                        if (Thread.interrupted()) {
                                            break exitLoop;
                                        }
                                        event.respondWith(output.replace(command, ""));
                                        sleep(1000);
                                        if (Thread.interrupted()) {
                                            break exitLoop;
                                        }
                                    }
                                    end = System.currentTimeMillis() + (5 * 1000);
                                }
                            }
                            LOGGER.info("Exiting output loop");
                            newCommand = false;
                        } catch (NullPointerException|IOException e) {
                            LOGGER.error("Error ", e);
                            interrupt();
                            break exitLoop;
                        } catch (InterruptedException e) {
                            interrupt();
                            break exitLoop;
                        } catch (Exception e) {
                            sendError(event, e);
                            interrupt();
                        }
                    } else {
                        echoOff = true;
                    }
                } catch (IOException e) {
                    if (event != null) {
                        sendError(event, e);
                    }
                    interrupt();
                    break;
                } catch (Exception e) {
                    if (event != null) {
                        sendError(event, e);
                    }
                }
            } else {
                try {
                    LilGUtil.pause(2, false);
                    if (waitNum > 100) {
                        continue;
                    }
                    waitNum++;
                    int count = 0;
                    while (p_inputStream.ready()) {
                        //noinspection ResultOfMethodCallIgnored
                        p_inputStream.read();
                        count++;
                    }
                    if (count > 0) {
                        LOGGER.trace("Read " + count + " bytes");
                    }
                } catch (NullPointerException e) {
                    LOGGER.error("Error ", e);
                    interrupt();
                    break exitLoop;
                } catch (Exception e) {
                    LOGGER.error("Error ", e);
                }
            }
        }
        try {
            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.close();
            if (!ssh) {
                p.getInputStream().close();
                if (p.isAlive()) {
                    wait(5000);
                    p.destroy();
                }
            } else {
                ssh = false;
                sshChannel.disconnect();
                sshSession.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Exiting > thread");
    }

    public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive {
        public String getPassword() {
            return null;
        }

        public boolean promptYesNo(String str) {
            return false;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return false;
        }

        public boolean promptPassword(String message) {
            return false;
        }

        public void showMessage(String message) {
        }

        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo) {
            return null;
        }
    }
}
