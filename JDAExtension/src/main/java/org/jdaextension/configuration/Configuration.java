package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.generic.MessageContextEvent;
import org.jdaextension.generic.MessageReceiverEvent;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.generic.UserContextEvent;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseCommand;
import org.jdaextension.responses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Configuration extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private final Map<String, SlashCommand> slashCommands;
    private final Map<String, UserCommand> userCommands;
    private final Map<String, MessageCommand> messageCommands;
    private final Map<Integer, MessageReceiver> messageReceivers;

    public Configuration() {
        slashCommands = new HashMap<>();
        userCommands = new HashMap<>();
        messageCommands = new HashMap<>();
        messageReceivers = new HashMap<>();
    }

    public void addCommand(SlashEvent slashCommandClass) {
        SlashCommand slashCommand = new SlashCommand(slashCommandClass);
        slashCommandClass.configure(slashCommand);
        slashCommands.put(slashCommand.getName(), slashCommand);
    }

    public void addCommand(UserContextEvent userCommandClass) {
        UserCommand userCommand = new UserCommand(userCommandClass);
        userCommandClass.configure(userCommand);
        userCommands.put(userCommand.getName(), userCommand);
    }

    public void addCommand(MessageContextEvent messageCommandClass) {
        MessageCommand messageCommand = new MessageCommand(messageCommandClass);
        messageCommandClass.configure(messageCommand);
        messageCommands.put(messageCommand.getName(), messageCommand);
    }

    public void addMessageReceiver(MessageReceiverEvent messageReceiverInterface) {
        MessageReceiver messageReceiver = new MessageReceiver(messageReceiverInterface, messageReceivers.size());
        messageReceiverInterface.configure(messageReceiver);
        messageReceivers.put(messageReceivers.size(), messageReceiver);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(slashCommands.values().stream().map(SlashCommand::build).toList())
                .addCommands(userCommands.values().stream().map(UserCommand::build).toList())
                .addCommands(messageCommands.values().stream().map(MessageCommand::build).toList())
                .queue();
    }

    private void sendError(Runnable runnable, Response response) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Error: ", e);
            response.setTemplate("500")
                    .send();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Runnable runnable = () -> {
            String idButton = event.getButton().getId();
            if (idButton != null) {
                String[] split = idButton.split("_");
                String command = split[0];
                if (command.contains("message"))
                    messageReceivers.get(Integer.parseInt(command.replaceAll("message", ""))).onButtonClicked(event, split[1]).send();
                else if (command.contains("usercontext"))
                    userCommands.get(command.replaceAll("usercontext", "")).onButtonClicked(event,split[1]).send();
                else if (command.contains("messagecontext"))
                    messageCommands.get(command.replaceAll("messagecontext", "")).onButtonClicked(event,split[1]).send();
                else
                    slashCommands.get(command).onButtonClicked(event, split[1]).send();
            } else {
                ResponseButton responseButton = new ResponseButton(event);
                responseButton.setTemplate("404").setVariable("message", "Button");
                responseButton.send();
            }
        };
        sendError(runnable, new ResponseButton(event));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Runnable runnable = () -> {
            if (!event.getAuthor().isBot())
                messageReceivers.values().stream().map(m -> m.messageReceived(event)).filter(Objects::nonNull).forEach(Response::send);
        };
        sendError(runnable, new ResponseMessage(event, -1));
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand command = slashCommands.get(event.getName());
        Runnable runnable = () -> command.execute(event).send();
        sendError(runnable, new ResponseCommand(event, command.isSendThinking(), command.isEphemeral()));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        slashCommands.get(event.getName()).onAutoComplete(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserCommand userCommand = userCommands.get(event.getName());
        Runnable runnable = () -> userCommand.execute(event).send();
        sendError(runnable, new ResponseCommand(event, userCommand.isSendThinking(), userCommand.isEphemeral()));
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        MessageCommand messageCommand = messageCommands.get(event.getName());
        Runnable runnable = () -> messageCommand.execute(event).send();
        sendError(runnable, new ResponseCommand(event, messageCommand.isSendThinking(), messageCommand.isEphemeral()));
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

    }
}
