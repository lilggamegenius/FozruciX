package com.LilG.Serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class MixInModule extends SimpleModule {
	public MixInModule() {
		super("ModuleName", new Version(0, 0, 1, null));
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(Guild.class, GuildMixIn.class);
		context.setMixInAnnotations(TextChannel.class, TextChannelMixIn.class);
	}
}