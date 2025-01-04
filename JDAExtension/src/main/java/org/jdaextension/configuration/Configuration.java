package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.interfaces.MessageCommandInterface;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.interfaces.UserCommandInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseCommand;
import org.jdaextension.responses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;


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

    public void addCommand(SlashCommandInterface slashCommandClass) {
        SlashCommand slashCommand = slashCommandClass.configure();
        slashCommand.setController(slashCommandClass);
        slashCommands.put(slashCommand.getName(), slashCommand);
    }

    public void addCommand(UserCommandInterface userCommandClass) {
        UserCommand userCommand = userCommandClass.configure();
        userCommand.setController(userCommandClass);
        userCommands.put(userCommand.getName(), userCommand);
    }

    public void addCommand(MessageCommandInterface messageCommandClass) {
        MessageCommand messageCommand = messageCommandClass.configure();
        messageCommand.setController(messageCommandClass);
        messageCommands.put(messageCommand.getName(), messageCommand);
    }

    public void addMessageReceiver(MessageReceiverInterface messageReceiverInterface) {
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
                    messageReceivers.get(Integer.parseInt(command.replaceAll("message", ""))).onButtonClick(event, split[1]).send();
                else
                    slashCommands.get(command).onButtonClick(event, split[1]).send();
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
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        slashCommands.get(event.getName()).onAutoComplete(event);
    }

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        UserCommand userCommand = userCommands.get(event.getName());
        Runnable runnable = () -> userCommand.execute(event).send();
        sendError(runnable, new ResponseCommand(event, userCommand.isSendThinking(), userCommand.isEphemeral()));
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        MessageCommand messageCommand = messageCommands.get(event.getName());
        Runnable runnable = () -> messageCommand.execute(event).send();
        sendError(runnable, new ResponseCommand(event, messageCommand.isSendThinking(), messageCommand.isEphemeral()));
    }
}
