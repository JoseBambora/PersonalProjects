package org.botgverreiro.bot.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * This is a basic class that gets the input arguments from a slash command.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Inputs {
    public static String getString(SlashCommandInteractionEvent event, String name) {
        OptionMapping optionMapping = event.getOption(name);
        return optionMapping != null ? optionMapping.getAsString() : null;
    }

    public static int getInteger(SlashCommandInteractionEvent event, String name) {
        OptionMapping optionMapping = event.getOption(name);
        return optionMapping != null ? optionMapping.getAsInt() : 0;
    }
}
