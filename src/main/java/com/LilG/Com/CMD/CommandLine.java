package com.LilG.Com.CMD;

import com.LilG.Com.FozruciX;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by Lil-G on 11/13/2015.
 * this class is meant to interface with the systems command processor to allow the bot to run system commands
 * this version does not close after it has run allowing it to have the same environment without having to set it again
 */
public class CommandLine extends Thread {
    private final static Logger LOGGER = Logger.getLogger(CommandLine.class);
    private volatile GenericMessageEvent event;
    private volatile String command;
    private boolean newCommand = false;
    private Process p;
    private BufferedWriter p_stdin;
    private boolean echoOff = false;

    public CommandLine(@NotNull GenericMessageEvent event, @NotNull String commandLine) {
        this.event = event;

        String console[] = new String[]{null, "", ""};
        if (commandLine.equalsIgnoreCase("cmd") || commandLine.equalsIgnoreCase("command")) {
            console[0] = "cmd.exe";
        } else if (commandLine.equalsIgnoreCase("term") || commandLine.equalsIgnoreCase("terminal") || commandLine.equalsIgnoreCase("bash")) {
            console[0] = "bash.exe";
        } else if (commandLine.equalsIgnoreCase("ps") || commandLine.equalsIgnoreCase("powershell")) {
            console[0] = "powershell.exe";
            console[1] = "-Command";
            console[2] = "-";
        } else {
            console[0] = commandLine;
        }
        ProcessBuilder builder = new ProcessBuilder(console);
        try {
            p = builder.start();
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            if (console[0].equals("cmd.exe")) {
                p_stdin.write("@echo off\n");
            }
        } catch (Exception e) {
            sendError(event, e);
        }
    }


    public CommandLine() {
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
                    if (command == null) {
                        command = " ";
                    }
                    p_stdin.write(command);
                    p_stdin.newLine();
                    p_stdin.flush();
                    p_stdin.newLine();
                    p_stdin.flush();

                    // write stdout of shell (=output of all commands)
                    if (echoOff) {
                        try {
                            Scanner s = new Scanner(p.getInputStream());
                            String output;
                            while (s.hasNext()) {
                                output = s.nextLine();
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
                                        continue exitLoop;
                                    }
                                    event.respondWith(output.replace(command, ""));
                                    sleep(1000);
                                    if (Thread.interrupted()) {
                                        continue exitLoop;
                                    }
                                }
                            }
                            LOGGER.info("Closing input scanner");
                            s.close();
                            newCommand = false;
                        } catch (InterruptedException e) {
                            interrupt();
                        } catch (Exception e) {
                            sendError(event, e);
                        }
                    } else {
                        echoOff = true;
                    }
                } catch (Exception e) {
                    sendError(event, e);
                }
            }
        }
        LOGGER.info("Exiting > thread");
    }
}
