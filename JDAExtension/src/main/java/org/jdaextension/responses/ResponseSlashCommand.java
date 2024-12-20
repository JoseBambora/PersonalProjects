package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ResponseSlashCommand extends Response{
    private final SlashCommandInteractionEvent event;
    private final boolean sendThinking;
    public ResponseSlashCommand(SlashCommandInteractionEvent event, boolean sendThinking) {
        this.event = event;
        this.sendThinking = sendThinking;
    }

    private ReplyCallbackAction replyMessageEmbed() {
        return setEmbed(event.reply(message.toString()));
    }

    private ReplyCallbackAction setButtons(ReplyCallbackAction r) {
        return buttons.isEmpty() ? r : r.setActionRow(buttons);
    }

    private WebhookMessageEditAction<Message> setButtons(WebhookMessageEditAction<Message> r) {
        return buttons.isEmpty() ? r : r.setActionRow(buttons);
    }

    private WebhookMessageCreateAction<Message> setButtons(WebhookMessageCreateAction<Message> r) {
        return buttons.isEmpty() ? r : r.setActionRow(buttons);
    }


    @Override
    public void send() {
        boolean hasFile = this.build(event.getName());
        if(hasFile) {
            if (sendThinking) {
                setButtons(setEmbed(event.getHook().editOriginal(message.toString()))).setFiles(this.files).queue();
            } else {
                setButtons(replyMessageEmbed()).setFiles(this.files).queue();
            }
        }
    }
}
