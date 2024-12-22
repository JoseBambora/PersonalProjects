package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jdaextension.interfaces.UserCommandInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;

public class UserCommand extends Command<UserCommand> {
    private UserCommandInterface controller;
    public UserCommand(String name) {
        super(name);
        controller = null;
    }
    protected void setController(UserCommandInterface controller) {
        this.controller = controller;
    }

    protected CommandData build() {
        return Commands.user(name).setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected Response executeCommand(CommandInteraction event) {
        ResponseCommand responseSlashCommand = new ResponseCommand(event,isSendThinking(), isEphemeral());
        controller.onCall((UserContextInteractionEvent) event,responseSlashCommand);
        return responseSlashCommand;
    }
}
