package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Configuration extends ListenerAdapter {
    private final Map<String,SlashCommand> commands;
    private final String channelIdSlashCommands;

    public Configuration() {
        commands = new HashMap<>();
        channelIdSlashCommands = System.getenv("CHANNEL_SLASH_COMMANDS");
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
        if(event.getChannel().getId().equals(channelIdSlashCommands))
            commands.get(event.getName()).execute(event);
        else
            event.reply("Wrong Channel").queue();
    }


    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String command = event.getButton().getId().split("_")[0];
        commands.get(command).onButtonClick(event);
    }
}
