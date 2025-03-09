package com.github.josebambora.responses;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ResponseTextChannel extends Response<ResponseTextChannel> {
    private TextChannel textChannel;
    public ResponseTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
    @Override
    public void send() {
        this.build("");
        this.textChannel.sendMessage(this.message).queue();
    }
}
