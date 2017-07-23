package com.LilG.Serialization;

import com.LilG.FozConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;

public class GuildKeyDeserializer extends KeyDeserializer {

	public GuildKeyDeserializer() {
		super();
	}

	@Override
	public Guild deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		//Use the string key here to return a real map key object
		return FozConfig.jda.getGuildById(key);
	}
}