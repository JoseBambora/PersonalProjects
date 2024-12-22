package org.jdaextension.configuration;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jdaextension.configuration.option.Option;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseSlashCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashCommand extends ButtonBehaviour<SlashCommand> {
    private final String name;
    private final String description;
    private SlashCommandInterface controller;
    private final Map<String, Option> options;
    private boolean sendThinking;
    private boolean ephemeral;
    private final List<Permission> permissions;

    public SlashCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.options = new HashMap<>();
        this.controller = null;
        this.sendThinking = false;
        this.ephemeral = false;
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

    public SlashCommand addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public SlashCommand setEphemeral() {
        ephemeral = true;
        return this;
    }

    protected Response execute(SlashCommandInteractionEvent event) {
        if(permissions.isEmpty() || (event.getMember() != null && event.getMember().hasPermission(permissions))) {
            if (sendThinking)
                event.deferReply().setEphemeral(ephemeral).queue();
            Map<String, Object> variables = new HashMap<>();
            for (Map.Entry<String, Option> optionEntry : options.entrySet())
                variables.put(optionEntry.getKey(), optionEntry.getValue().parser(event));
            ResponseSlashCommand responseSlashCommand = new ResponseSlashCommand(event,sendThinking, ephemeral);
            controller.onCall(event, variables,responseSlashCommand);
            return responseSlashCommand;
        }
        else {
            return new ResponseSlashCommand(event,sendThinking, ephemeral)
                    .setTemplate("403")
                    .setVariable("message","You do not have access to this command");
        }
    }

    protected CommandData build() {
        SlashCommandData scd =  Commands.slash(this.name,this.description)
                .addOptions(options.values()
                        .stream()
                        .sorted((o1, _) -> !o1.isRequired() ? 1 : -1 )
                        .map(Option::buildOption).toList());
        return permissions.isEmpty() ? scd : scd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    public String getName() {
        return name;
    }

    public boolean isSendThinking() {
        return sendThinking;
    }
    public boolean isEphemeral() {
        return ephemeral;
    }
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        options.get(event.getFocusedOption().getName()).onAutoComplete(event);
    }
}
