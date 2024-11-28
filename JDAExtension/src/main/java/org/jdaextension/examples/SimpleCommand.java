package org.jdaextension.examples;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.reponses.ResponseMessage;

import java.util.Map;

public class SimpleCommand implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        return new SlashCommand("test","test");
    }

    @Override
    public ResponseMessage onCall(SlashCommandInteractionEvent event, Map<String, Object> variables) {
        return new ResponseMessage("Template");
    }
}
