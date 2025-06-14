package com.github.josebambora.configuration;

import com.github.josebambora.generic.SlashEventPageable;
import com.github.josebambora.responses.ResponseButton;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import com.github.josebambora.configuration.option.Option;
import com.github.josebambora.generic.GenericEvents;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;

import java.util.*;

public class SlashCommand extends Command<SlashCommand> {
    private final Map<String, Option<?>> options;
    private final SlashEvent controller;
    private final SlashEventPageable controllerPageable;
    private final Map<String, Pageable> pageableMap;
    private String description;

    protected SlashCommand(SlashEvent controller) {
        super();
        this.description = "";
        this.options = new HashMap<>();
        this.controller = controller;
        this.pageableMap = null;
        this.controllerPageable = null;
    }

    protected SlashCommand(SlashEventPageable controller) {
        super();
        this.description = "";
        this.options = new HashMap<>();
        this.controllerPageable = controller;
        this.controller = null;
        this.pageableMap = new HashMap<>();
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
    protected void executeCommand(CommandInteraction event) {
        Map<String, Object> variables = new HashMap<>();
        List<String> errorArgs = new ArrayList<>();
        for (Map.Entry<String, Option<?>> optionEntry : options.entrySet()) {
            Object parsed = optionEntry.getValue().parser((SlashCommandInteractionEvent) event);
            variables.put(optionEntry.getKey(), parsed);
            if (optionEntry.getValue().isRequired() && parsed == null)
                errorArgs.add(optionEntry.getKey());
        }
        ResponseCommand responseSlashCommand = new ResponseCommand(event, "command", isSendThinking(), isEphemeral());
        if (errorArgs.isEmpty()) {
            if(this.controller != null)
                controller.onCall((SlashCommandInteractionEvent) event, variables, responseSlashCommand);
            else if (pageableMap != null && controllerPageable != null) {
                this.pageableMap.entrySet()
                        .stream()
                        .filter(e -> !e.getValue().isOver())
                        .map(Map.Entry::getKey)
                        .forEach(this.pageableMap::remove);
                pageableMap.put(event.getUser().getId(),new Pageable());
                controllerPageable.onCall((SlashCommandInteractionEvent) event, variables, responseSlashCommand);
            }
        }
        else
            responseSlashCommand.setTemplate("400").setVariable("message", errorArgs.stream().sorted().map(s -> "Argument `" + s + "` is missing").toList()).send();
    }

    protected void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        options.get(event.getFocusedOption().getName()).onAutoComplete(event);
    }

    @Override
    protected GenericEvents getController() {
        return controller != null ? controller : controllerPageable;
    }

    @Override
    public void onButtonClicked(ButtonInteractionEvent event, String id) {
        if((id.equals("next-page") || id.equals("previous-page")) && pageableMap != null && controllerPageable != null) {
            ResponseButton responseButton = new ResponseButton(event);
            if(this.pageableMap.containsKey(event.getUser().getId())) {
                if (id.equals("next-page"))
                    this.pageableMap.get(event.getUser().getId()).next();
                else
                    this.pageableMap.get(event.getUser().getId()).previous();
                controllerPageable.onCall(event, id, pageableMap.get(event.getUser().getId()), responseButton);
            }
            else {
                responseButton.setTemplate("429").setVariable("message","This interaction has expired").send();
            }
        }
        else
            super.onButtonClicked(event, id);
    }
}
