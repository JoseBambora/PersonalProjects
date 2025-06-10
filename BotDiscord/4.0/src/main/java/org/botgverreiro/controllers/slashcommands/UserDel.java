package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Settings;
import org.botgverreiro.models.User;

import java.util.Map;

public class UserDel implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("user-del")
                .setDescription("Eliminar os dados do utilizador")
                .setSendThinking()
                .setEphemeral();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Settings.commitTransaction(c -> User.deleteUser(c,slashCommandInteractionEvent.getUser().getId()))
                .thenApply(n -> n == 1 ? responseCommand.setTemplate("seasons/UserDel") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send);
    }
}
