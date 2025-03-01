package org.botgverreiro.controllers.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.Cache;
import org.botgverreiro.utils.LimitList;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseAutoComplete;
import org.jdaextension.responses.ResponseCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class GameDel implements SlashEvent {

    private final Cache<String, Game> cacheGames = new Cache<>(this::fetchGames);

    private CompletionStage<List<Game>> fetchGames(String input) {
        return Settings.commitTransaction(c -> Game.getGames(c, input))
                .thenApply(l -> LimitList.subList(l, 25));
    }

    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionGame = new OptionNumber("jogo", "Jogo a remover", true, Number.INTEGER)
                .setAutoComplete(this::gameList);
        slashCommand.setName("game_del")
                .setDescription("Remover um jogo do calendário")
                .setSendThinking()
                .setEphemeral()
                .addOptions(optionGame)
                .addPermission(Permission.KICK_MEMBERS);
    }

    private void gameList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheGames.get(input)
                .thenAccept(l -> l.forEach(g -> responseAutoComplete.addChoice(g.toChoice())))
                .thenAccept(_ -> responseAutoComplete.send());
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer gameId = (Integer) map.get("jogo");
        Settings.commitTransaction(c -> Game.deleteGame(c, gameId))
                .thenApply(r -> r == 1 ? responseCommand.setTemplate("Success").setVariable("op", "Remover Jogo.") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send);
    }
}
