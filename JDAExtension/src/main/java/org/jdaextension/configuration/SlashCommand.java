package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jdaextension.configuration.option.Option;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseCommand;

import java.util.*;

public class SlashCommand extends Command<SlashCommand> {
    private final Map<String, Option<?>> options;
    private final SlashEvent controller;
    private String description;

    protected SlashCommand(SlashEvent controller) {
        super();
        this.description = "";
        this.options = new HashMap<>();
        this.controller = controller;
    }

    public SlashCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public SlashCommand addOption(Option<?> option) {
        this.options.put(option.getName(), option);
        return this;
    }

    public SlashCommand addOptions(Option<?>... options) {
        return addOptions(Arrays.asList(options));
    }

    public SlashCommand addOptions(Collection<Option<?>> options) {
        options.forEach(o -> this.options.put(o.getName(), o));
        return this;
    }

    protected CommandData build() {
        SlashCommandData scd = Commands.slash(this.name, this.description)
                .addOptions(options.values()
                        .stream()
                        .sorted()
                        .map(Option::buildOption).toList());
        return permissions.isEmpty() ? scd : scd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected ResponseCommand executeCommand(CommandInteraction event) {
        Map<String, Object> variables = new HashMap<>();
        List<String> errorArgs = new ArrayList<>();
        for (Map.Entry<String, Option<?>> optionEntry : options.entrySet()) {
            Object parsed = optionEntry.getValue().parser((SlashCommandInteractionEvent) event);
            variables.put(optionEntry.getKey(), parsed);
            if (optionEntry.getValue().isRequired() && parsed == null)
                errorArgs.add(optionEntry.getKey());
        }
        ResponseCommand responseSlashCommand = new ResponseCommand(event, "command", isSendThinking(), isEphemeral());
        if (errorArgs.isEmpty())
            controller.onCall((SlashCommandInteractionEvent) event, variables, responseSlashCommand);
        else
            responseSlashCommand.setTemplate("400").setVariable("errors", errorArgs.stream().sorted().map(s -> "Argument `" + s + "` is missing").toList());
        return responseSlashCommand;
    }

    protected void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        options.get(event.getFocusedOption().getName()).onAutoComplete(event);
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }
}
