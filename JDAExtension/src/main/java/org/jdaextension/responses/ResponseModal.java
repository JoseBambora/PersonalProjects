package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ResponseModal extends Response {
    private final ModalInteractionEvent event;
    private final boolean sendThinking;
    private final boolean ephemeral;

    public ResponseModal(ModalInteractionEvent event, boolean sendThinking, boolean ephemeral) {
        this.event = event;
        this.sendThinking = sendThinking;
        this.ephemeral = ephemeral;
    }

    @Override
    public void send() {
        boolean hasFile = this.build(event.getModalId());
        if (hasFile) {
            if (sendThinking) {
                WebhookMessageEditAction<Message> wmea = event.getHook().editOriginal(message.toString());
                wmea = !this.embedBuilder.isEmpty() ? wmea.setEmbeds(embedBuilder.build()) : wmea;
                wmea = buttons.isEmpty() ? wmea : wmea.setActionRow(buttons);
                wmea = wmea.setFiles(this.files);
                this.sendWA(wmea);
            } else {
                ReplyCallbackAction rca = event.reply(message.toString()).setEphemeral(ephemeral);
                rca = !this.embedBuilder.isEmpty() ? rca.setEmbeds(embedBuilder.build()) : rca;
                rca = buttons.isEmpty() ? rca : rca.setActionRow(buttons);
                rca = rca.setFiles(this.files);
                this.sendIH(rca);
            }
        }
    }
}
