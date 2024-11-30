package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ResponseSlashCommand extends Response{
    private final SlashCommandInteractionEvent event;
    private final boolean sendThinking;
    public ResponseSlashCommand(SlashCommandInteractionEvent event, boolean sendThinking) {
        this.event = event;
        this.sendThinking = sendThinking;
    }

    @Override
    public void send() {
        this.build(event.getName());
        if(sendThinking) {
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
