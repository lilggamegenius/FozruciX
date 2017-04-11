package com.LilG

import net.dv8tion.jda.core.audio.AudioSendHandler

/**
 * Created by lil-g on 12/23/16.
 */
class MultiAudioPlayer : AudioSendHandler {

    override fun canProvide(): Boolean {
        return true
    }

    override fun provide20MsAudio(): ByteArray {
        return ByteArray(0)
    }
}
