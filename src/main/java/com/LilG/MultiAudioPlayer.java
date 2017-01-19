package com.LilG;

import net.dv8tion.jda.core.audio.AudioSendHandler;

/**
 * Created by lil-g on 12/23/16.
 */
public class MultiAudioPlayer implements AudioSendHandler {

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public byte[] provide20MsAudio() {
        return new byte[0];
    }
}
