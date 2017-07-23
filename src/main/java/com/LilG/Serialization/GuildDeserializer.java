package com.LilG.Serialization;

import com.LilG.FozConfig;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;

public class GuildDeserializer extends JsonDeserializer<Guild> {

	public GuildDeserializer() {
		super();
	}

	@Override
	public Guild deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return FozConfig.jda.getGuildById(p.getValueAsString());
	}
}