package com.LilG.Audio;

import ch.qos.logback.classic.Logger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lil-g on 12/23/16.
 */
public class MultiAudioPlayer implements AudioSendHandler {
	final static Logger LOGGER = (Logger) LoggerFactory.getLogger(MultiAudioPlayer.class);
	List<AudioPlayer> audioPlayers;

	@Override
	public boolean canProvide() {
		for (AudioPlayer input : audioPlayers) {
			if (input.getPlayingTrack() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] provide20MsAudio() {
		return new byte[0];
	}

	public boolean open(File file) throws IOException, UnsupportedAudioFileException {
		AudioFormat format = AudioSystem.getAudioFileFormat(file).getFormat();

		return false;
	}

	public boolean open(AudioInputStream stream) {

		return false;
	}

	public boolean open(InputStream stream) {

		return false;
	}
}
