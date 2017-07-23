package com.LilG.Serialization;

import com.LilG.FozConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;

public class TextChannelKeyDeserializer extends KeyDeserializer {

	public TextChannelKeyDeserializer() {
		super();
	}

	@Override
	public TextChannel deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		return FozConfig.jda.getTextChannelById(key);
	}
}