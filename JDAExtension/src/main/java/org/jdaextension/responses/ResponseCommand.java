package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ResponseCommand extends Response {
    private final CommandInteraction event;
    private final boolean sendThinking;
    private final boolean ephemeral;

    public ResponseCommand(CommandInteraction event, boolean sendThinking, boolean ephemeral) {
        this.event = event;
        this.sendThinking = sendThinking;
        this.ephemeral = ephemeral;
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
        if (hasFile) {
            if (sendThinking) {
                setButtons(setEmbed(event.getHook().editOriginal(message.toString()))).setFiles(this.files).queue();
            } else {
                setButtons(replyMessageEmbed().setEphemeral(ephemeral)).setFiles(this.files).queue();
            }
        }
    }
}
