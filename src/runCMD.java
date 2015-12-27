import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by ggonz on 10/16/2015.
 */
class runCMD extends Thread{
    private final MessageEvent event;
    private final String[] arg;
    private String console = "cmd.exe";

    public runCMD(MessageEvent event, String[] arg) {
        this.event = event;
        this.arg = arg;
    }

    @Override
    public void run() {
        boolean term;
        if (arg[1].equalsIgnoreCase("cmd") || arg[1].equalsIgnoreCase("command"))
            term = false;
        else if (arg[1].equalsIgnoreCase("term") || arg[1].equalsIgnoreCase("terminal"))
            term = true;
        else {
            console = arg[1];
            term = false;
        }
        Process p;
        ProcessBuilder builder = new ProcessBuilder(console);
        try {
            p = builder.start();
        } catch (Exception e) {

            p = null;
        }
        //get stdin of shell
        BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

        //single execution
        if (term) try{
            String termStr = "bash -c ";
            p_stdin.write(termStr + arg[2]);
        } catch (Exception e){
            e.printStackTrace();
        }
        else{
            try {
                p_stdin.write(arg[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
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
            if (!output.contains("C:\\Users\\ggonz\\workspace\\pircbotx-master")
                    && !output.equals("Microsoft Windows [Version 10.0.10240]")
                    && !output.equals("(c) 2015 Microsoft Corporation. All rights reserved.")
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