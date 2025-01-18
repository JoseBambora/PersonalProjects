package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdaextension.generic.*;
import org.jdaextension.responses.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Configuration extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private final Map<String, SlashCommand> slashCommands;
    private final Map<String, UserCommand> userCommands;
    private final Map<String, MessageCommand> messageCommands;
    private final Map<Integer, MessageReceiver> messageReceivers;
    private final List<OnReadyEvent> readyEvents;
    private final List<UsernameUpdateEvent> usernameUpdateEvents;
    private final List<UserRemovedEvent> userRemovedEvents;

    public Configuration() {
        slashCommands = new HashMap<>();
        userCommands = new HashMap<>();
        messageCommands = new HashMap<>();
        messageReceivers = new HashMap<>();
        readyEvents = new ArrayList<>();
        usernameUpdateEvents = new ArrayList<>();
        userRemovedEvents = new ArrayList<>();
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

    public void addMessageReceiver(MessageEvent messageReceiverInterface) {
        MessageReceiver messageReceiver = new MessageReceiver(messageReceiverInterface, messageReceivers.size());
        messageReceiverInterface.configure(messageReceiver);
        messageReceivers.put(messageReceivers.size(), messageReceiver);
    }

    public void addUsernameUpdate(UsernameUpdateEvent usernameUpdateInterface) {
        usernameUpdateEvents.add(usernameUpdateInterface);
    }

    public void addUserRemove(UserRemovedEvent userRemovedInterface) {
        userRemovedEvents.add(userRemovedInterface);
    }

    public void addReadyEvent(OnReadyEvent readyEvent) {
        readyEvents.add(readyEvent);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(slashCommands.values().stream().map(SlashCommand::build).toList())
                .addCommands(userCommands.values().stream().map(UserCommand::build).toList())
                .addCommands(messageCommands.values().stream().map(MessageCommand::build).toList())
                .queue();
    }

    private void sendError(Runnable runnable, Runnable error) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Error: ", e);
            error.run();
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
                    messageReceivers.get(Integer.parseInt(split[1])).onButtonClicked(event, split[2]).send();
                else if (command.contains("usercontext"))
                    userCommands.get(split[1]).onButtonClicked(event, split[2]).send();
                else if (command.contains("messagecontext"))
                    messageCommands.get(split[1]).onButtonClicked(event, split[2]).send();
                else
                    slashCommands.get(split[1]).onButtonClicked(event, split[2]).send();
            } else {
                ResponseButton responseButton = new ResponseButton(event);
                responseButton.setTemplate("404").setVariable("message", "Button");
                responseButton.send();
            }
        };
        sendError(runnable, () -> new ResponseButton(event).setTemplate("500").send());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            Runnable runnable = () -> messageReceivers.values().stream().map(m -> m.messageReceived(event)).filter(Objects::nonNull).forEach(Response::send);
            sendError(runnable, () -> new ResponseMessageReceiver(event, -1).setTemplate("500").send());
        }
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand command = slashCommands.get(event.getName());
        Runnable runnable = () -> command.execute(event).send();
        sendError(runnable, () -> new ResponseCommand(event, "command", command.isSendThinking(), command.isEphemeral()).setTemplate("500").send());
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        slashCommands.get(event.getName()).onAutoComplete(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserCommand userCommand = userCommands.get(event.getName());
        Runnable runnable = () -> userCommand.execute(event).send();
        sendError(runnable, () -> new ResponseCommand(event, "usercontext", userCommand.isSendThinking(), userCommand.isEphemeral()).setTemplate("500").send());
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        MessageCommand messageCommand = messageCommands.get(event.getName());
        Runnable runnable = () -> messageCommand.execute(event).send();
        sendError(runnable, () -> new ResponseCommand(event, "messagecontext", messageCommand.isSendThinking(), messageCommand.isEphemeral()).setTemplate("500").send());
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Runnable runnable = () -> {
            String idModal = event.getModalId();
            String[] split = idModal.split("_");
            String command = split[0];
            if (command.contains("usercontext"))
                userCommands.get(split[1]).onModalInteraction(event, split[1]).send();
            else if (command.contains("messagecontext"))
                messageCommands.get(split[1]).onModalInteraction(event, split[1]).send();
            else
                slashCommands.get(split[1]).onModalInteraction(event, split[1]).send();
        };
        sendError(runnable, () -> new ResponseModal(event, false, false).setTemplate("500").send());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        readyEvents.forEach(r -> r.onCall(event));
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        slashCommands.values().forEach(sl -> sl.onShutDown(event));
        messageCommands.values().forEach(mc -> mc.onShutDown(event));
        userCommands.values().forEach(uc -> uc.onShutDown(event));
        messageCommands.values().forEach(mc -> mc.onShutDown(event));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (!event.getAuthor().isBot()) {
            Runnable runnable = () -> messageReceivers.values().stream().map(m -> m.messageUpdated(event)).filter(Objects::nonNull).forEach(Response::send);
            sendError(runnable, () -> new ResponseMessageUpdate(event, -1).setTemplate("500").send());
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        messageReceivers.values().forEach(m -> m.messageDelete(event));
    }

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event) {
        usernameUpdateEvents.forEach(e -> e.onCall(event, event.getUser().getId(), event.getOldName(), event.getNewName()));
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        userRemovedEvents.forEach(e -> e.onCall(event, event.getUser().getId(), event.getUser().getName()));
    }
}
