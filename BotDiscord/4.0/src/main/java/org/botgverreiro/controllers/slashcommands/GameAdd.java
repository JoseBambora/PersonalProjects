package org.botgverreiro.controllers.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.models.Team;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class GameAdd implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionMonth = new OptionNumber("mes","Mês do jogo", true, Number.INTEGER);
        OptionNumber optionDay = new OptionNumber("dia","Dia do jogo", true, Number.INTEGER);
        OptionNumber optionHour = new OptionNumber("hora","Hora do jogo", true, Number.INTEGER);
        OptionNumber optionMinute = new OptionNumber("minutos","Minutos do jogo", true, Number.INTEGER);
        OptionString optionTeam = new OptionString("adversario", "Adversário", true)
                .setAutoComplete(this::teamsList);

        slashCommand.setName("game_add")
                .setDescription("Calendarizar um jogo")
                .setSendThinking()
                .addOptions(optionMonth,optionDay,optionHour,optionMinute,optionTeam)
                .addPermission(Permission.KICK_MEMBERS);
    }

    private Map<String,String> teamsList(CommandAutoCompleteInteractionEvent event, String input) {
        CompletionStage<List<Team>> teams = Settings.commitTransaction(c -> Team.getSimilarTeams(c,input));
        teams.thenAccept(l -> {
            List<Team> send = l.size() > 25 ? l.subList(0,25) : l;
            Map<String,String> result = new HashMap<>();
            send.forEach(t -> result.put(t.getTeamName(),t.getTeamName()));
        });
        return new HashMap<>();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        String team = (String) map.get("adversario");
        int season = (Integer) map.get("season");
        String mode = (String) map.get("modo");
        int field = (Integer) map.get("field");
        int month = (Integer) map.get("mes");
        int day = (Integer) map.get("dia");
        int hours = (Integer) map.get("hora");
        int minutes = (Integer) map.get("minuto");
        Settings.commitTransaction(c -> Team.insertTeam(c,team)
                        .thenCompose(res -> Game.insertGame(c, season, mode, field,month,day,hours,minutes, team)))
                .thenAccept(
                res -> {
                    if(res == 1)
                        responseCommand.setTemplate("success").setVariable("op", "Adicionar Jogo.");
                    else
                        responseCommand.setTemplate("500");
                    responseCommand.send();
                }
        );
    }
}
