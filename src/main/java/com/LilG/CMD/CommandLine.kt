package com.LilG.CMD

import ch.qos.logback.classic.Level
import com.LilG.FozruciX
import com.LilG.utils.LilGUtil
import com.jcraft.jsch.*
import com.sun.jna.Platform
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.types.GenericMessageEvent
import org.slf4j.LoggerFactory
import java.io.*
import javax.swing.JOptionPane

/**
 * Created by Lil-G on 11/13/2015.
 * this class is meant to interface with the systems command processor to allow the bot to run system commands
 * this version does not close after it has run allowing it to have the same environment without having to set it again
 */
class CommandLine : Thread {
    @Volatile private var event: GenericMessageEvent? = null
    @Volatile private var command: String? = null
    @Volatile private var newCommand = false
    private var p: Process? = null
    private var p_stdin: BufferedWriter? = null
    private var p_inputStream: BufferedReader? = null
    private var echoOff = false
    private var sshSession: Session? = null
    private var sshChannel: Channel? = null
    private var ssh = false
    private var waitNum: Byte = 0

    constructor(event: GenericMessageEvent, vararg commandLine: String) {
        LOGGER.level = Level.ALL
        this.name = "Commandline: " + commandLine[0]
        this.event = event
        val console = arrayOf(commandLine[0], "", "")
        if (commandLine[0].equals("cmd", ignoreCase = true) || commandLine[0].equals("command", ignoreCase = true)) {
            console[0] = "cmd.exe"
        } else if (commandLine[0].equals("term", ignoreCase = true) || commandLine[0].equals("terminal", ignoreCase = true) || commandLine[0].equals("bash", ignoreCase = true)) {
            console[0] = "bash"
        } else if (commandLine[0].equals("ps", ignoreCase = true) || commandLine[0].equals("powershell", ignoreCase = true)) {
            console[0] = "powershell.exe"
            console[1] = "-Command"
            console[2] = "-"
        } else if (commandLine[0].equals("ssh", ignoreCase = true)) {
            ssh = true
        }
        try {
            if (!ssh) {
                val builder = ProcessBuilder(*console)
                p = builder.start()
                builder.redirectErrorStream(true)
                p_stdin = BufferedWriter(OutputStreamWriter(p!!.outputStream))
                if (console[0] == "cmd.exe") {
                    p_stdin!!.write("@echo off\n")
                }
                p_inputStream = BufferedReader(InputStreamReader(p!!.inputStream))
                LilGUtil.pause(1)
                if (!p!!.isAlive) {
                    interrupt()
                    throw IllegalStateException("Process exited")
                }
            } else {
                var host = JOptionPane.showInputDialog("Enter username@hostname", "lil-g@ssh.lilggamegenius.ml")
                val user = host.substring(0, host.indexOf('@'))
                host = host.substring(host.indexOf('@') + 1)
                try {
                    val ssh = JSch()
                    ssh.setKnownHosts(if (Platform.isLinux()) "~/.ssh/known_hosts" else "C:/Users/ggonz/AppData/Local/lxss/home/lil-g/.ssh/known_hosts")
                    sshSession = ssh.getSession(user, host, 22)
                    val passwd = JOptionPane.showInputDialog("Enter password")
                    sshSession!!.setPassword(passwd)
                    val ui = object : MyUserInfo() {
                        override fun showMessage(message: String) {
                            JOptionPane.showMessageDialog(
                                    null, message)
                        }

                        override fun promptYesNo(message: String): Boolean {
                            val options = arrayOf<Any>("yes", "no")
                            val foo = JOptionPane.showOptionDialog(null,
                                    message,
                                    "Warning",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE, null, options, options[0])
                            return foo == 0
                        }

                        // If password is not given before the invocation of Session#connect(),
                        // implement also following methods,
                        //   * UserInfo#getPassword(),
                        //   * UserInfo#promptPassword(String message) and
                        //   * UIKeyboardInteractive#promptKeyboardInteractive()

                    }

                    sshSession!!.userInfo = ui
                    sshSession!!.connect()
                    sshChannel = sshSession!!.openChannel("shell")
                    p_stdin = BufferedWriter(OutputStreamWriter(sshChannel!!.outputStream))
                    sshChannel!!.connect()
                    p_inputStream = BufferedReader(InputStreamReader(sshChannel!!.inputStream))
                } catch (e: Exception) {
                    sendError(event, e)
                }

            }
            if (ssh || console[0] == "bash") {
                p_stdin!!.write("export PS1=''\n")
            }
        } catch (e: Exception) {
            sendError(event, e)
        }

    }


    constructor() {
        LOGGER.level = Level.ALL
        val platform = if (Platform.isLinux()) "bash" else "cmd.exe"
        this.name = "Commandline: " + platform
        val builder = ProcessBuilder(platform)
        try {
            p = builder.start()
            p_stdin = BufferedWriter(OutputStreamWriter(p!!.outputStream))
            p_stdin!!.write(if (Platform.isLinux()) "export PS1=''" else "@echo off\n")
            p_inputStream = BufferedReader(InputStreamReader(p!!.inputStream))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        newCommand = true
        start()
    }

    private fun sendError(event: GenericMessageEvent, e: Exception) {
        FozruciX.sendError(event as MessageEvent, e)
    }

    fun doCommand(event: GenericMessageEvent, command: String) {
        this.event = event
        this.command = command
        newCommand = true
    }

    override fun run() {
        exitLoop@ while (!Thread.interrupted()) { //--------------- Main Waiting Loop ----------------------
            if (newCommand) {
                try {
                    // write stdout of shell (=output of all commands)
                    if (echoOff) {
                        if (command == null) {
                            command = " "
                        }
                        p_stdin!!.write(command!!)
                        p_stdin!!.newLine()
                        p_stdin!!.flush()
                        try {
                            var output: String
                            var end = System.currentTimeMillis() + 5 * 1000
                            while (System.currentTimeMillis() < end) {
                                if (p_inputStream!!.ready()) {
                                    output = p_inputStream!!.readLine()
                                    LOGGER.debug(command + " -> ■" + output + "■ chars: " + output.length)
                                    if (!output.contains(">") &&
                                            !output.contains("Microsoft Windows") &&
                                            !output.contains("Microsoft Corporation. All rights reserved") &&
                                            !output.contains("Windows PowerShell") &&
                                            !output.contains("Copyright") &&
                                            !output.contains("export PS1=''") &&
                                            output != " " &&
                                            output != "\u0000" &&
                                            output != command &&
                                            !output.isEmpty()) {

                                        if (Thread.interrupted()) {
                                            break@exitLoop
                                        }
                                        event!!.respondWith(output.replace(command!!, ""))
                                        Thread.sleep(1000)
                                        if (Thread.interrupted()) {
                                            break@exitLoop
                                        }
                                    }
                                    end = System.currentTimeMillis() + 5 * 1000
                                }
                            }
                            LOGGER.info("Exiting output loop")
                            newCommand = false
                        } catch (e: NullPointerException) {
                            LOGGER.error("Error ", e)
                            interrupt()
                            break@exitLoop
                        } catch (e: IOException) {
                            LOGGER.error("Error ", e)
                            interrupt()
                            break@exitLoop
                        } catch (e: InterruptedException) {
                            interrupt()
                            break@exitLoop
                        } catch (e: Exception) {
                            sendError(event!!, e)
                            interrupt()
                        }

                    } else {
                        echoOff = true
                    }
                } catch (e: IOException) {
                    if (event != null) {
                        sendError(event!!, e)
                    }
                    interrupt()
                    break
                } catch (e: Exception) {
                    if (event != null) {
                        sendError(event!!, e)
                    }
                }

            } else {
                try {
                    LilGUtil.pause(2, false)
                    if (waitNum > 100) {
                        continue
                    }
                    waitNum++
                    var count = 0
                    while (p_inputStream!!.ready()) {

                        p_inputStream!!.read()
                        count++
                    }
                    if (count > 0) {
                        LOGGER.trace("Read $count bytes")
                    }
                } catch (e: NullPointerException) {
                    LOGGER.error("Error ", e)
                    interrupt()
                    break@exitLoop
                } catch (e: Exception) {
                    LOGGER.error("Error ", e)
                }

            }
        }
        try {
            p_stdin!!.write("exit")
            p_stdin!!.newLine()
            p_stdin!!.flush()
            p_stdin!!.close()
            if (!ssh) {
                p!!.inputStream.close()
                if (p!!.isAlive) {
                    (this as java.lang.Object).wait(5000)
                    p!!.destroy()
                }
            } else {
                ssh = false
                sshChannel!!.disconnect()
                sshSession!!.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        LOGGER.info("Exiting > thread")
    }

    abstract class MyUserInfo : UserInfo, UIKeyboardInteractive {
        override fun getPassword(): String? {
            return null
        }

        override fun promptYesNo(str: String): Boolean {
            return false
        }

        override fun getPassphrase(): String? {
            return null
        }

        override fun promptPassphrase(message: String): Boolean {
            return false
        }

        override fun promptPassword(message: String): Boolean {
            return false
        }

        override fun showMessage(message: String) {}

        override fun promptKeyboardInteractive(destination: String,
                                               name: String,
                                               instruction: String,
                                               prompt: Array<String>,
                                               echo: BooleanArray): Array<String>? {
            return null
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CommandLine::class.java) as ch.qos.logback.classic.Logger
    }
}
