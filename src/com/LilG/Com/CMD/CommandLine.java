package com.LilG.Com.CMD;

import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by Lil-G on 11/13/2015.
 * this class is meant to interface with the systems command processor to allow the bot to run system commands
 * this version does not close after it has run allowing it to have the same environment without having to set it again
 */
public class CommandLine extends Thread {
    private MessageEvent event;
    private String command;
    private Process p;
    private BufferedWriter p_stdin;

    public CommandLine(MessageEvent event, String[] arg) {
        this.event = event;

        boolean term;
        String console = "cmd.exe";
        if (arg[1].equalsIgnoreCase("cmd") || arg[1].equalsIgnoreCase("command"))
            term = false;
        else if (arg[1].equalsIgnoreCase("term") || arg[1].equalsIgnoreCase("terminal"))
            term = true;
        else {
            console = arg[1];
            term = false;
        }
        ProcessBuilder builder = new ProcessBuilder(console);
        try {
            p = builder.start();
        } catch (Exception e) {
            p = null;
        }
        try {
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        } catch (Exception e) {
            p_stdin = null;
        }

        if (term) {
            try {
                String termStr = "cd C:\\cygwin64\\bin && bash -c ";
                p_stdin.write(termStr);
            } catch (Exception e) {
                sendError(event, e);
            }
        }
    }

    private void sendError(MessageEvent event, Exception e){
        event.getChannel().send().message("Error: " + e.getCause() + ". From " + e);
    }

    public void doCommand(MessageEvent event, String command) {
        this.event = event;
        this.command = command;
        System.out.print("Running.");
        run();
    }

    @Override
    public void run() {
        try {
            p_stdin.write(command);
        } catch (Exception e) {
            sendError(event, e);
        }
        try {
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.newLine();
            p_stdin.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write stdout of shell (=output of all commands)
        try {
            Scanner s = new Scanner(p.getInputStream());
            String output;
            while (s.hasNextLine() && !Thread.interrupted()) {
                output = s.nextLine();
                System.out.println(output);
                if (!output.contains("C:\\Users\\FozruciX\\Workspace\\FozruciX>")
                        && !output.contains("Microsoft Windows [Version")
                        && !output.contains("Microsoft Corporation. All rights reserved.")
                        && !output.contains("Directory of")
                        && !output.contains("<DIR>          .")
                        && !output.contains("bytes free")
                        && !output.contains("Volume in drive C is OS")
                        && !output.contains("Volume Serial Number is")
                        && !output.contains(">exit")
                        && !output.isEmpty()) {
                    event.respond(output);
                }
            }
            s.close();
        } catch (Exception e) {
            sendError(event, e);
        }
    }
}
