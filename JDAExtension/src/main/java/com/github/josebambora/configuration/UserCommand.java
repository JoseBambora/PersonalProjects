package com.github.josebambora.configuration;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import com.github.josebambora.generic.GenericEvents;
import com.github.josebambora.generic.UserContextEvent;
import com.github.josebambora.responses.ResponseCommand;

public class UserCommand extends Command<UserCommand> {
    private final UserContextEvent controller;

    protected UserCommand(UserContextEvent controller) {
        super();
        this.controller = controller;
    }

    protected CommandData build() {
        return Commands.user(name).setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected void executeCommand(CommandInteraction event) {
        ResponseCommand responseSlashCommand = new ResponseCommand(event, "usercontext", isSendThinking(), isEphemeral());
        controller.onCall((UserContextInteractionEvent) event, responseSlashCommand);
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }
}
