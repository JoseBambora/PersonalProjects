package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.generic.UserContextEvent;
import org.jdaextension.responses.ResponseCommand;

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
    protected ResponseCommand executeCommand(CommandInteraction event) {
        ResponseCommand responseSlashCommand = new ResponseCommand(event, "usercontext", isSendThinking(), isEphemeral());
        controller.onCall((UserContextInteractionEvent) event, responseSlashCommand);
        return responseSlashCommand;
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }
}
