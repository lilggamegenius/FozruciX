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
    String termStr = "cd C:\\cygwin64\\bin && bash -c ";
    String console = "cmd.exe";
    MessageEvent event;
    String[] arg;
    String command;
    boolean term;
    Process p;
    ProcessBuilder builder;
    BufferedWriter p_stdin;

    public CommandLine(MessageEvent event, String[] arg) {
        this.event = event;
        this.arg = arg;

        if (arg[1].equalsIgnoreCase("cmd") || arg[1].equalsIgnoreCase("command"))
            term = false;
        else if (arg[1].equalsIgnoreCase("term") || arg[1].equalsIgnoreCase("terminal"))
            term = true;
        else {
            console = arg[1];
            term = false;
        }
        builder = new ProcessBuilder(console);
        try {
            p = builder.start();
        } catch (Exception e) {
            p = null;
        }
        try {
            p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        } catch (NullPointerException e) {
            p_stdin = null;
        }

        if (term) {
            try {
                p_stdin.write(termStr);
            } catch (Exception e) {
                sendError(event, e);
            }
        }
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
                if (!output.matches("/.:\\.*>/")
                        && !output.equals("Microsoft Windows [Version 10.0.10240]")
                        && !output.equals("(c) 2015 Microsoft Corporation. All rights reserved.")
                        && !output.contains("Directory of")
                        && !output.contains("<DIR>.")
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

    public void sendError(MessageEvent event, Exception e) {
        event.getChannel().send().message("Error: " + e.getCause() + ". From " + e);
    }
}
