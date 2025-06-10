package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.SeasonList;
import org.botgverreiro.models.Mode;
import org.botgverreiro.models.Season;
import org.botgverreiro.models.Settings;
import org.botgverreiro.models.User;
import org.botgverreiro.utils.ListUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class UserLB implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionNumber = new OptionNumber("temporada","Temporada a consultar", false, Number.INTEGER)
                .setAutoComplete(SeasonList::seasonList);
        OptionString optionMode = new OptionString("modo", "Modalidade do jogo", false);
        Settings.commitTransactionNoResult(c -> Mode.selectAllModesSync(c).forEach(m -> optionMode.addChoice(m.toString(), m.toString())));

        slashCommand.setName("user-lb")
                .setDescription("Tabela classificativa")
                .addOption(optionNumber)
                .addOption(optionMode);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer season = (Integer) map.get("temporada");
        String mode = (String) map.get("modo");
        CompletionStage<List<User>> query = season == null ?
                Settings.commitTransaction(Season::selectLastSeason)
                        .thenCompose(s -> Settings.commitTransaction(c -> User.selectClassificationSeason(c,s.getSeasonId(), mode == null ? "Futebol" : mode)))
                :
                Settings.commitTransaction(c -> User.selectClassificationSeason(c,season, mode == null ? "Futebol" : mode));
        query
                .thenApply(l -> responseCommand.setVariable("users", ListUtils.subList(l, 15)))
                .thenApply(r -> r.setTemplate("users/UserLB"))
                .thenAccept(ResponseCommand::send);

    }
}
