package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jdaextension.interfaces.MessageCommandInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;

public class MessageCommand extends Command<MessageCommand> {
    private final MessageCommandInterface controller;

    protected MessageCommand(MessageCommandInterface controller) {
        super();
        this.controller = controller;
    }

    protected CommandData build() {
        return Commands.message(name).setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected Response executeCommand(CommandInteraction event) {
        ResponseCommand responseSlashCommand = new ResponseCommand(event, isSendThinking(), isEphemeral());
        controller.onCall((MessageContextInteractionEvent) event, responseSlashCommand);
        return responseSlashCommand;
    }
}
