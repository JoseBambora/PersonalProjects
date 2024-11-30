package org.jdaextension.configuration;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.ResponseButtonClick;
import org.jdaextension.responses.ResponseMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SlashCommand {
    private final String name;
    private final String description;
    private SlashCommandInterface controller;
    private final Map<String,Option> options;
    private final Map<String, Function<ButtonInteractionEvent, ResponseButtonClick>> buttonsInteractions;
    private boolean sendThinking;
    private final List<Permission> permissions;

    public SlashCommand(String name, String description) {
        this.name = name;
        this.description = description;
        options = new HashMap<>();
        buttonsInteractions = new HashMap<>();
        this.controller = null;
        this.sendThinking = false;
        this.permissions = new ArrayList<>();
    }

    public SlashCommand setSendThinking() {
        this.sendThinking =  true;
        return this;
    }
    protected void setController(SlashCommandInterface controller) {
        this.controller = controller;
    }

    public SlashCommand addOption(Option option) {
        options.put(option.getName(),option);
        return this;
    }

    public SlashCommand addButtonClick(String buttonID, Function<ButtonInteractionEvent, ResponseButtonClick> buttonClickFunction) {
        buttonsInteractions.put(buttonID,buttonClickFunction);
        return this;
    }

    public SlashCommand addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    protected void execute(SlashCommandInteractionEvent event) {
        if(event.getMember().hasPermission(permissions)) {
            if (sendThinking)
                event.deferReply().queue();
            Map<String, Object> variables = new HashMap<>();
            for (Map.Entry<String, Option> optionEntry : options.entrySet())
                variables.put(optionEntry.getKey(), optionEntry.getValue().parser(event));
            ResponseMessage responseMessage = controller.onCall(event, variables);
            responseMessage.send(event, sendThinking);
        }
        else
            event.reply("You do not have access to this command").queue();
    }

    protected CommandData build() {
        SlashCommandData scd =  Commands.slash(this.name,this.description)
                .addOptions(options.values()
                        .stream()
                        .sorted((o1, _) -> !o1.isRequired() ? 1 : -1 )
                        .map(Option::generateOptionData).toList());
        return permissions.isEmpty() ? scd : scd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    public String getName() {
        return name;
    }

    protected void onButtonClick(ButtonInteractionEvent event) {
        String[] split = event.getButton().getId().split("_");
        this.buttonsInteractions.get(split[split.length-1]).apply(event).send(event);
    }
}
