package org.jdaextension.reponses;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ResponseMessage extends Response {

    public ResponseMessage(String filename) {
        super(filename);
    }

    @Override
    public ResponseMessage setVariable(String name, Object value) {
        return (ResponseMessage) super.setVariable(name, value);
    }

    public void send(SlashCommandInteractionEvent event, boolean sentThinking) {
        this.build(event.getName());
        if(sentThinking) {
            if(this.buttons.isEmpty()) {
                event.getHook()
                        .sendMessage(this.message.toString())
                        .queue();
            }
            else {
                event.getHook()
                        .sendMessage(this.message.toString())
                        .setActionRow(this.buttons)
                        .queue();
            }
        }
        else {
            if(this.buttons.isEmpty()) {
                event.reply(this.message.toString()).queue();
            }
            else {
                event.reply(this.message.toString())
                        .setActionRow(this.buttons)
                        .queue();
            }
        }
    }
}
