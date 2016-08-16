package com.LilG.Com;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

import javax.security.auth.login.LoginException;

public class Tester2 {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = new JDABuilder()
                .setAudioEnabled(false)
                .setBotToken("")
                .buildBlocking();
        System.out.println("Finished Logging in!");
    }
}