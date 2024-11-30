package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Configuration extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private final Map<String,SlashCommand> commands;
    private final Map<Integer,MessageReceiver> messageReceivers;

    public Configuration() {
        commands = new HashMap<>();
        messageReceivers = new HashMap<>();
    }
    public void addCommand(SlashCommandInterface slashCommandClass) {
        SlashCommand slashCommand = slashCommandClass.configure();
        slashCommand.setController(slashCommandClass);
        commands.put(slashCommand.getName(),slashCommand);
    }

    public void addMessageReceiver(MessageReceiverInterface messageReceiverInterface) {
        MessageReceiver messageReceiver = messageReceiverInterface.configure();
        messageReceiver.setController(messageReceiverInterface);
        messageReceiver.setId(messageReceivers.size());
        messageReceivers.put(messageReceivers.size(),messageReceiver);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commands.values().stream().map(SlashCommand::build).toList())
                .queue();
    }

    private void sendError(Runnable runnable, Response response) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            log.error("Error: ", e);
            response.setTemplate("500")
                    .setVariable("message", e.getMessage() + ". Please contact the developer.")
                    .send();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Runnable runnable = () -> commands.get(event.getName()).execute(event).send();
        sendError(runnable,  new ResponseSlashCommand(event,false));
    }


    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Runnable runnable = () -> {
            String idButton = event.getButton().getId();
            if(idButton != null) {
                String[] split = idButton.split("_");
                String command = split[0];
                if (command.contains("message"))
                    messageReceivers.get(Integer.parseInt(command.replaceAll("message", ""))).onButtonClick(event, split[1]).send();
                else
                    commands.get(command).onButtonClick(event, split[1]).send();
            }
            else {
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
        sendError(runnable, new ResponseMessage(event,-1));
    }
}
