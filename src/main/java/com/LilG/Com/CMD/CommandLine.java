package com.LilG.Com.CMD;

import com.LilG.Com.FozruciX;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by Lil-G on 11/13/2015.
 * this class is meant to interface with the systems command processor to allow the bot to run system commands
 * this version does not close after it has run allowing it to have the same environment without having to set it again
 */
public class CommandLine extends Thread {
    private final static Logger LOGGER = Logger.getLogger(CommandLine.class);
    Session sshSession;
    private volatile GenericMessageEvent event;
    private volatile String command;
    private boolean newCommand = false;
    private Process p;
    private BufferedWriter p_stdin;
    private boolean echoOff = false;
    private boolean ssh = false;

    public CommandLine(@NotNull GenericMessageEvent event, @NotNull String... commandLine) {
        LOGGER.setLevel(Level.ALL);
        this.event = event;

        String console[] = new String[]{commandLine[0], "", ""};
        if (commandLine[0].equalsIgnoreCase("cmd") || commandLine[0].equalsIgnoreCase("command")) {
            console[0] = "cmd.exe";
        } else if (commandLine[0].equalsIgnoreCase("term") || commandLine[0].equalsIgnoreCase("terminal") || commandLine[0].equalsIgnoreCase("bash")) {
            console[0] = "bash.exe";
        } else if (commandLine[0].equalsIgnoreCase("ps") || commandLine[0].equalsIgnoreCase("powershell")) {
            console[0] = "powershell.exe";
            console[1] = "-Command";
            console[2] = "-";
        } else if (commandLine[0].equalsIgnoreCase("ssh")) {
            ssh = true;
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Address");
            String addr = scanner.nextLine();
            System.out.print("Enter Username");
            String username = scanner.nextLine();
            System.out.print("Enter port");
            int port = scanner.nextInt();
            try {
                sshSession = (new JSch()).getSession(username, addr, port);
                Properties config = new Properties();
                config.put("userauth", "keyboard-interactive");
                sshSession.setConfig(config);
                sshSession.connect();
            } catch (Exception e) {
                sendError(event, e);
            }
        }
        try {
            if (!ssh) {
                p = new ProcessBuilder(console).start();
                p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                if (console[0].equals("cmd.exe")) {
                    p_stdin.write("@echo off\n");
                }
            } else {

            }
        } catch (Exception e) {
            sendError(event, e);
        }
    }


    public CommandLine() {
        LOGGER.setLevel(Level.ALL);
        ProcessBuilder builder = new ProcessBuilder("cmd.exe");
        try {
            p = builder.start();
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            p_stdin.write("@echo off\n");
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
        while (!Thread.interrupted()) {
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
                        InputStream p_inputStream = p.getInputStream();
                        try {
                            BufferedReader s = new BufferedReader(new InputStreamReader(p_inputStream));
                            String output;
                            long end = System.currentTimeMillis() + (5 * 1000);
                            while (System.currentTimeMillis() < end) {
                                if (s.ready()) {
                                    output = s.readLine();
                                    LOGGER.debug(command + " -> " + output);
                                    if (!output.contains(">") &&
                                            !output.contains("Microsoft Windows") &&
                                            !output.contains("Microsoft Corporation. All rights reserved") &&
                                            !output.contains("Windows PowerShell") &&
                                            !output.contains("Copyright") &&
                                            !output.equals(" ") &&
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
                        } catch (InterruptedException e) {
                            interrupt();
                        } catch (Exception e) {
                            sendError(event, e);
                            interrupt();
                        }
                    } else {
                        echoOff = true;
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }
        try {
            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.close();
            p.getInputStream().close();
            if (p.isAlive()) {
                wait(5000);
                p.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Exiting > thread");
    }
}
