package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.interfaces.SlashCommandInterface;

import java.util.HashMap;
import java.util.Map;

public class Configuration extends ListenerAdapter {
    private final Map<String,SlashCommand> commands;

    public Configuration() {
        commands = new HashMap<>();
    }
    public void addCommand(SlashCommandInterface slashCommandClass) {
        SlashCommand slashCommand = slashCommandClass.configure();
        slashCommand.setController(slashCommandClass);
        commands.put(slashCommand.getName(),slashCommand);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commands.values().stream().map(SlashCommand::build).toList())
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.get(event.getName()).execute(event);
    }
}
