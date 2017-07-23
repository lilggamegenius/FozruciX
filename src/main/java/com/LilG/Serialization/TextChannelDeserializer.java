package com.LilG.Serialization;

import com.LilG.FozConfig;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;

public class TextChannelDeserializer extends JsonDeserializer<TextChannel> {

	public TextChannelDeserializer() {
		super();
	}

	@Override
	public TextChannel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return FozConfig.jda.getTextChannelById(p.getValueAsString());
	}
}