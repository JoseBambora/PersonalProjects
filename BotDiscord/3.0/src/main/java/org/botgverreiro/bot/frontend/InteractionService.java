package org.botgverreiro.bot.frontend;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.botgverreiro.bot.interactions.Interaction;
import org.botgverreiro.bot.threads.MyLocks;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles interactions. Each interaction has a lifetime period.
 * If the user does not interact with an interaction for 60 seconds, it is cleaned up.
 * When the user requests to see the same interaction twice, only the latest one is considered.
 * Interactions are only used for <b>/top</b> and <b>/delete</b> slash commands.
 *
 * @author José Bambora
 * @version 1.0
 * @see org.botgverreiro.bot.interactions.InteractionTop
 * @see org.botgverreiro.bot.interactions.InteractionDelete
 * @see org.botgverreiro.bot.interactions.Interaction
 */
public class InteractionService {
    private final Map<String, Interaction> messages = new HashMap<>();

    /**
     * Add a new interaction to this class.
     * It is important to handle concurrency problems, since this method can be called from two threads at the same time.
     *
     * @param event   Event command.
     * @param content Interaction to create.
     * @see MessageSender
     */
    public void addAndSendMessage(SlashCommandInteractionEvent event, Interaction content) {
        MyLocks.getInstance().lockWrite("interactions");
        this.messages.put(content.getUser(), content);
        this.clean();
        MyLocks.getInstance().unlockWrite("interactions");
        MessageSender.sendMessage(event, content.getInitialMessage(), content.getButtons(), message -> content.setId(message.getId()));
    }

    /**
     * Clean inactive interactions.
     */
    private void clean() {
        LocalDateTime now = LocalDateTime.now();
        List<String> toRemove = this.messages.values().stream().filter(p -> p.isInactive(now, 60)).map(Interaction::getUser).toList();
        toRemove.forEach(messages::remove);
    }

    /**
     * This method is called when a button of an interactions is clicked.
     *
     * @param event Button interaction event.
     * @see MessageSender
     * @see org.botgverreiro.bot.interactions.InteractionTop
     * @see org.botgverreiro.bot.interactions.InteractionDelete
     * @see org.botgverreiro.bot.interactions.Interaction
     */
    public void newInteraction(ButtonInteractionEvent event) {
        String messageId = event.getMessageId();
        String mention = event.getUser().getAsMention();
        if (!messages.containsKey(mention))
            MessageSender.sendResultAction(event, "Sessão expirada. Escreva novamente o comando.");
        else if (!messages.get(mention).getId().equals(messageId))
            MessageSender.sendResultAction(event, "Já escreveu novamente o comando " + messages.get(mention).getCommand());
        else
            MessageSender.sendMessage(event, messages.get(mention).clickedButtonMessage(event), messages.get(mention).getButtons());
    }
}
