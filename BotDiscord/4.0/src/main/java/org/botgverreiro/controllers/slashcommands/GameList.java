package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GameStatus;
import org.botgverreiro.utils.ListUtils;

import java.util.Map;

public class GameList implements SlashEvent {
    private final int numberGames = 15;
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString optionNumber = new OptionString("estado","filtrar por estado",true)
                .addChoice(GameStatus.TO_OPEN.toString(), GameStatus.TO_OPEN.name())
                .addChoice(GameStatus.OPEN.toString(), GameStatus.OPEN.name())
                .addChoice(GameStatus.CLOSE.toString(), GameStatus.CLOSE.name())
                .addChoice(GameStatus.FINISHED.toString(), GameStatus.FINISHED.name());
        slashCommand.setName("game-list")
                .setDescription("Últimos " + numberGames + " jogos do bot para um certo estado.")
                .addOption(optionNumber);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        String statusName = (String) map.get("estado");
        GameStatus gameStatus = GameStatus.valueOf(statusName);
        Settings.commitTransaction(c -> Game.selectGamesByStatus(c,gameStatus.getStatus()))
                .thenApply(g -> responseCommand.setTemplate("games/GamesList")
                        .setVariable("finished", gameStatus.equals(GameStatus.TO_OPEN))
                        .setVariable("status", gameStatus.toString())
                        .setVariable("games", ListUtils.subList(g, numberGames)))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
