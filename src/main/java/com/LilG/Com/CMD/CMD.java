package com.LilG.Com.CMD;

import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by ggonz on 10/16/2015.
 * Single use Command prompt - closes after a single command
 */
public class CMD extends Thread {
    private final GenericMessageEvent event;
    private final String[] arg;

    public CMD(GenericMessageEvent event, String[] arg) {
        this.event = event;
        this.arg = arg;
    }

    @Override
    public void run() {
        String console;
        if (arg[1].equalsIgnoreCase("cmd") || arg[1].equalsIgnoreCase("command")) {
            console = "cmd.exe";
        } else if (arg[1].equalsIgnoreCase("term") || arg[1].equalsIgnoreCase("terminal")) {
            console = "bash.exe";
        } else if (arg[1].equalsIgnoreCase("ps") || arg[1].equalsIgnoreCase("powershell")) {
            console = "powershell.exe";
        } else {
            console = arg[1];
        }
        ProcessBuilder builder = new ProcessBuilder(console);
        Process p;
        try {
            p = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            //single execution
            p_stdin.write(arg[2]);
            p_stdin.newLine();
            p_stdin.flush();
            // finally close the shell by execution exit command
            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();
        } catch (Exception e) {
            e.printStackTrace();

        }

        // write stdout of shell (=output of all commands)
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
    }
}