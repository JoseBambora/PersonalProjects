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
        boolean hasFile = this.build(event.getName());
        if(hasFile) {
            if (sendThinking) {
                if (this.buttons.isEmpty()) {
                    event.getHook()
                            .sendMessage(this.message.toString())
                            .setFiles(this.files)
                            .queue();
                } else {
                    event.getHook()
                            .sendMessage(this.message.toString())
                            .setActionRow(this.buttons)
                            .setFiles(this.files)
                            .queue();
                }
            } else {
                if (this.buttons.isEmpty()) {
                    event.reply(this.message.toString()).setFiles(this.files).queue();
                } else {
                    event.reply(this.message.toString())
                            .setActionRow(this.buttons)
                            .setFiles(this.files)
                            .queue();
                }
            }
        }
    }
}
