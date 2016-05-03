package com.LilG.Com.CMD;

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
    private GenericMessageEvent event;
    private String command;
    private Process p;
    private BufferedWriter p_stdin;
    private boolean echoOff = false;

    public CommandLine(GenericMessageEvent event, String commandLine) {
        this.event = event;

        String console;
        if (commandLine.equalsIgnoreCase("cmd") || commandLine.equalsIgnoreCase("command")) {
            console = "cmd.exe";
        } else if (commandLine.equalsIgnoreCase("term") || commandLine.equalsIgnoreCase("terminal")) {
            console = "bash.exe";
        } else if (commandLine.equalsIgnoreCase("ps") || commandLine.equalsIgnoreCase("powershell")) {
            console = "powershell.exe";
        } else {
            console = commandLine;
        }
        ProcessBuilder builder = new ProcessBuilder(console);
        try {
            p = builder.start();
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            p_stdin.write("echo off\n");
        } catch (Exception e) {
            sendError(event, e);
        }
        run();
    }

    public CommandLine() {
    } //Dummy constructor, only to "initialize" a value to be overwritten later

    private void sendError(GenericMessageEvent event, Exception e) {
        ((MessageEvent) event).getChannel().send().message("Error: " + e.getCause() + ". From " + e);
        e.printStackTrace(System.err);
    }

    public void doCommand(GenericMessageEvent event, String command) {
        this.event = event;
        this.command = command;
        System.out.println("Running.");
        run();
    }

    @Override
    public void run() {
        try {
            if (command == null) {
                command = "";
            }
            p_stdin.write(command);
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.newLine();
            p_stdin.flush();

            // write stdout of shell (=output of all commands)
            if (echoOff) {
                Scanner s = new Scanner(p.getInputStream());
                String output;
                while (s.hasNext() && !Thread.interrupted()) {
                    output = s.nextLine();
                    System.out.println(command + " -> " + output);
                    if (!output.contains(">") &&
                            !output.contains("Microsoft Windows") &&
                            !output.contains("Microsoft Corporation. All rights reserved") &&
                            !output.contains("Windows PowerShell") &&
                            !output.contains("Copyright") &&
                            !output.equals(" ") &&
                            !output.equals(command) &&
                            !output.isEmpty()) {

                        event.respondWith(output.replace(command, ""));
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            sendError(event, e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                s.close();
            } else {
                echoOff = true;
            }

        } catch (Exception e) {
            sendError(event, e);
        }
    }
}
