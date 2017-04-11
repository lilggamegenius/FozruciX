package com.LilG.CMD

import org.pircbotx.hooks.types.GenericMessageEvent
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

/**
 * Created by ggonz on 10/16/2015.
 * Single use Command prompt - closes after a single command
 */
class CMD(private val event: GenericMessageEvent, private val arg: Array<String>) : Thread() {

    override fun run() {
        val console: String
        if (arg[1].equals("cmd", ignoreCase = true) || arg[1].equals("command", ignoreCase = true)) {
            console = "cmd.exe"
        } else if (arg[1].equals("term", ignoreCase = true) || arg[1].equals("terminal", ignoreCase = true)) {
            console = "bash.exe"
        } else if (arg[1].equals("ps", ignoreCase = true) || arg[1].equals("powershell", ignoreCase = true)) {
            console = "powershell.exe"
        } else {
            console = arg[1]
        }
        val builder = ProcessBuilder(console)
        val p: Process
        try {
            p = builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        try {
            val p_stdin = BufferedWriter(OutputStreamWriter(p.outputStream))
            //single execution
            p_stdin.write(arg[2])
            p_stdin.newLine()
            p_stdin.flush()
            // finally close the shell by execution exit command
            p_stdin.write("exit")
            p_stdin.newLine()
            p_stdin.flush()
        } catch (e: Exception) {
            e.printStackTrace()

        }

        // write stdout of shell (=output of all commands)
        val s = Scanner(p.inputStream)
        var output: String
        while (s.hasNextLine() && !Thread.interrupted()) {
            output = s.nextLine()
            println(output)
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
                event.respond(output)
            }
        }
        s.close()
    }
}