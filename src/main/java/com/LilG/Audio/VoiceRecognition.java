package com.LilG.Audio;

import ch.qos.logback.classic.Logger;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static com.LilG.FozConfig.configuration;

/**
 * Created by Gabe on 7/11/2017.
 */
public class VoiceRecognition extends Thread {
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VoiceRecognition.class);
	private final static AudioFormat recognizerFormat = new AudioFormat(16000, 16, 1, false, false);
	AudioManager audioManager;
	VoiceRecognitionRecieveHandler recieveHandler;

	public VoiceRecognition(VoiceChannel channel) {
		this(channel.getGuild().getAudioManager());
	}

	public VoiceRecognition(AudioManager manager) {
		audioManager = manager;
		audioManager.setReceivingHandler((recieveHandler = new VoiceRecognitionRecieveHandler()));
		start();
	}

	@Override
	public void run() {
		StreamSpeechRecognizer recognizer;
		try {
			recognizer = new StreamSpeechRecognizer(configuration);
			recognizer.startRecognition(recieveHandler.getAudioInputStream());

			SpeechResult result;
			while ((result = recognizer.getResult()) != null) {
				String s = result.getResult().getBestResultNoFiller();
				// System.out.println("recognized: " + s);
				if (s.contains("Computer")) {
					LOGGER.info("Detected wake up word!");
				}
				LOGGER.info("Voice Recognition result: %s", result.getHypothesis());
			}
			recognizer.stopRecognition();
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	class VoiceRecognitionRecieveHandler implements AudioReceiveHandler {
		PipedOutputStream outputStream = new PipedOutputStream();

		public AudioInputStream getAudioInputStream() throws IOException, UnsupportedAudioFileException {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new PipedInputStream(outputStream));
			return AudioSystem.getAudioInputStream(VoiceRecognition.recognizerFormat, inputStream);
		}

		@Override
		public boolean canReceiveCombined() {
			return true;
		}

		@Override
		public boolean canReceiveUser() {
			return false;
		}

		@Override
		public void handleCombinedAudio(CombinedAudio combinedAudio) {
			try {
				outputStream.write(combinedAudio.getAudioData(1.0));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void handleUserAudio(UserAudio userAudio) {

		}
	}
}
