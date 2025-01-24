package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public abstract class ResponseMessage<T> extends Response<T> {
    protected final int id;

    protected ResponseMessage(int id) {
        this.id = id;
    }

    protected void sendMessage(MessageCreateAction mca) {
        mca = !this.embedBuilder.isEmpty() ? mca.setEmbeds(embedBuilder.build()) : mca;
        mca = buttons.isEmpty() ? mca : mca.setActionRow(buttons);
        mca = mca.setFiles(this.files);
        this.sendMCA(mca);
    }

    protected void send(Message message) {
        boolean hasFile = this.build("message_" + id);
        this.sendReactions(message);
        if (hasFile) {
            MessageCreateAction mca = message.reply(this.message.toString());
            sendMessage(mca);
        }
    }
}
