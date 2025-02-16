package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.generic.MessageContextEvent;
import org.jdaextension.responses.ResponseCommand;

public class MessageCommand extends Command<MessageCommand> {
    private final MessageContextEvent controller;

    protected MessageCommand(MessageContextEvent controller) {
        super();
        this.controller = controller;
    }

    protected CommandData build() {
        return Commands.message(name).setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
    }

    @Override
    protected void executeCommand(CommandInteraction event) {
        ResponseCommand responseSlashCommand = new ResponseCommand(event, "messagecontext", isSendThinking(), isEphemeral());
        controller.onCall((MessageContextInteractionEvent) event, responseSlashCommand);
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }
}
