package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.SeasonList;
import org.botgverreiro.models.Season;
import org.botgverreiro.models.Settings;

import java.util.Map;

public class SeasonDel implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionNumber = new OptionNumber("temporada","Temporada a remover", true, Number.INTEGER)
                .setAutoComplete(SeasonList::seasonList);
        slashCommand
                .setName("season-del")
                .setDescription("Remover uma temporada")
                .setEphemeral()
                .setSendThinking()
                .addOption(optionNumber);
    }



    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer seasonId = (Integer) map.get("temporada");
        Settings.commitTransaction(c -> Season.deleteSeason(c,seasonId))
                .thenApply(n -> responseCommand.setTemplate("seasons/SeasonDel").setVariable("seasonExists",n == 1))
                .thenAccept(ResponseCommand::send);
    }
}
