package org.botgverreiro.bot.frontend;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is responsible for handling the following tasks:
 * <ul>
 *     <li>Processing user commands.</li>
 *     <li>Sending messages to a specific text channel.</li>
 *     <li>Processing button click interactions.</li>
 * </ul>
 *
 * @author José Bambora
 * @version 1.0
 */
public class MessageSender {
    /**
     * Before sending responses to commands, the bot send a message that it is processing the request, and this method
     * is responsible for that.
     *
     * @param event Event command.
     */
    public static void sendThinking(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
    }

    /**
     * This method sends an embed as a response to a slash command.
     *
     * @param event   Event command.
     * @param content Embed to send.
     */
    public static void sendEmbed(SlashCommandInteractionEvent event, EmbedBuilder content) {
        event.getHook()
                .editOriginalEmbeds(content.build())
                .queue();
    }

    /**
     * This method sends a normal String message as a response to a slash command.
     *
     * @param event   Event command.
     * @param message Message content to send.
     */
    public static void sendMessage(SlashCommandInteractionEvent event, String message) {
        event.getHook()
                .editOriginal(message)
                .queue();
    }

    /**
     * Add an emoji reaction to a message. This method is used when a user submits a prediction.
     *
     * @param event Message event.
     * @param emoji Emoji to react.
     */
    public static void sendEmoji(MessageReceivedEvent event, Emoji emoji) {
        event.getMessage().addReaction(emoji).queue();
    }

    /**
     * Sends a message after a button interaction is clicked.
     *
     * @param event   Event command.
     * @param message Message response.
     */
    public static void sendResultAction(ButtonInteractionEvent event, String message) {
        event.editMessage(message).queue();
    }

    /**
     * Sends a response to a slash command with buttons. This method is used to create user interaction
     * (<b>/delete</b> and <b>/top</b> commands).
     *
     * @param event      Event command.
     * @param content    Message response.
     * @param buttonList Buttons to add.
     * @param consumer   Consumer to be executed when the message is sent.
     * @see org.botgverreiro.bot.interactions.InteractionDelete
     * @see org.botgverreiro.bot.interactions.InteractionTop
     * @see InteractionService
     */
    public static void sendMessage(SlashCommandInteractionEvent event, String content, List<Button> buttonList, Consumer<Message> consumer) {
        if (buttonList.isEmpty())
            event.getHook()
                    .editOriginal(content)
                    .queue(consumer);
        else
            event.getHook()
                    .editOriginal(content)
                    .setActionRow(buttonList)
                    .queue(consumer);
    }

    /**
     * Sends a message after a button interaction is clicked.
     *
     * @param event      Event command.
     * @param message    Message response.
     * @param buttonList Buttons to add.
     * @see org.botgverreiro.bot.interactions.InteractionDelete
     * @see org.botgverreiro.bot.interactions.InteractionTop
     * @see InteractionService
     */
    public static void sendMessage(ButtonInteractionEvent event, String message, List<Button> buttonList) {
        if (buttonList.isEmpty())
            event.editMessage(message).setComponents(Collections.emptyList()).queue();
        else
            event.editMessage(message).setActionRow(buttonList).queue();
    }

    /**
     * Sends a message to a text channel.
     *
     * @param textChannel Text channel to send a message.
     * @param message     Message to send.
     */
    public static void sendMessage(TextChannel textChannel, String message) {
        textChannel.sendMessage(message).queue();
    }
}
