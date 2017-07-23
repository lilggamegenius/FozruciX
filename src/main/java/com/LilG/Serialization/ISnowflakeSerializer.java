package com.LilG.Serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.dv8tion.jda.core.entities.ISnowflake;

import java.io.IOException;

public class ISnowflakeSerializer extends StdSerializer<ISnowflake> {

	protected ISnowflakeSerializer(Class<ISnowflake> t) {
		super(t);
	}

	public ISnowflakeSerializer() {
		this(null);
	}

	@Override
	public void serialize(ISnowflake value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.getId());
	}
}

