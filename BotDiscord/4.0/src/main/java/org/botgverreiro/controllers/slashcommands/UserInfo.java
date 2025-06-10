package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Settings;
import org.botgverreiro.models.User;

import java.util.Map;

public class UserInfo implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("user-info")
                .setDescription("Visualizar as estatísticas de um utilizador")
                .setEphemeral();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Settings.commitTransaction(c -> User.selectUserStats(c,slashCommandInteractionEvent.getUser().getId()))
                .thenApply(u -> {
                    u.ifPresent(user -> responseCommand.setVariable("name", user.getUserId())
                            .setVariable("points", user.getUserPoints())
                            .setVariable("predictions", user.getUserPredictions()));
                    return responseCommand.setVariable("userExists",u.isPresent());
                })
                .thenApply(r -> r.setTemplate("users/UserInfo"))
                .thenAccept(ResponseCommand::send);
    }
}
