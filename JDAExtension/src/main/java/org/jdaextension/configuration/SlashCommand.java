package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.reponses.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

public class SlashCommand {

    private final String name;
    private final String description;
    private SlashCommandInterface controller;
    private final Map<String,Option> options;

    public SlashCommand(String name, String description) {
        this.name = name;
        this.description = description;
        options = new HashMap<>();
        this.controller = null;
    }

    protected void setController(SlashCommandInterface controller) {
        this.controller = controller;
    }

    public void addOption(Option option) {
        options.put(option.getName(),option);
    }

    protected void execute(SlashCommandInteractionEvent event) {
        Map<String, Object> variables = new HashMap<>();
        for(Map.Entry<String,Option> optionEntry : options.entrySet())
            variables.put(optionEntry.getKey(),optionEntry.getValue().parser(event));
        ResponseMessage responseMessage = controller.execute(event,variables);
        responseMessage.send(event);
    }

    protected CommandData build() {
        return Commands.slash(this.name,this.description).addOptions(options.values().stream().sorted((o1, _) -> !o1.isRequired() ? 1 : -1 ).map(Option::generateOptionData).toList());
    }

    public String getName() {
        return name;
    }
}
