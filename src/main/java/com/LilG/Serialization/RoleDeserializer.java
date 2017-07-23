package com.LilG.Serialization;

import com.LilG.FozConfig;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.dv8tion.jda.core.entities.Role;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {

	public RoleDeserializer() {
		super();
	}

	@Override
	public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return FozConfig.jda.getRoleById(p.getValueAsString());
	}
}