package org.jdaextension.configuration;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jdaextension.configuration.option.Option;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;

import java.util.*;

public class SlashCommand extends Command<SlashCommand> {
    private final String description;
    private SlashCommandInterface controller;
    private final Map<String, Option<?>> options;

    public SlashCommand(String name, String description) {
        super(name);
        this.description = description;
        this.options = new HashMap<>();
        this.controller = null;
    }

    protected void setController(SlashCommandInterface controller) {
        this.controller = controller;
    }

    public SlashCommand addOption(Option<?> option) {
        this.options.put(option.getName(),option);
        return this;
    }

    public SlashCommand addOptions(Option<?> ... options) {
        return addOptions(Arrays.asList(options));
    }

    public SlashCommand addOptions(Collection<Option<?>> options) {
        options.forEach(o -> this.options.put(o.getName(),o));
        return this;
    }

    protected CommandData build() {
        SlashCommandData scd =  Commands.slash(this.name,this.description)
                .addOptions(options.values()
                        .stream()
                        .sorted()
                        .map(Option::buildOption).toList());
        return permissions.isEmpty() ? scd : scd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected Response executeCommand(CommandInteraction event) {
        Map<String, Object> variables = new HashMap<>();
        for (Map.Entry<String, Option<?>> optionEntry : options.entrySet())
            variables.put(optionEntry.getKey(), optionEntry.getValue().parser((SlashCommandInteractionEvent) event));
        ResponseCommand responseSlashCommand = new ResponseCommand(event,isSendThinking(), isEphemeral());
        controller.onCall((SlashCommandInteractionEvent) event, variables,responseSlashCommand);
        return responseSlashCommand;
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        options.get(event.getFocusedOption().getName()).onAutoComplete(event);
    }
}
