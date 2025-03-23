package org.botgverreiro.controllers.autocompleters;

import com.github.josebambora.responses.ResponseAutoComplete;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.Cache;
import org.botgverreiro.utils.GameStatus;

public class GameList {

    private static final Cache<String, Game> cacheGames1 = new Cache<>(s -> Settings.commitTransaction(c -> Game.selectGames(c, s, GameStatus.TO_OPEN.getStatus())));;
    private static final Cache<String, Game> cacheGames2 = new Cache<>(s -> Settings.commitTransaction(c -> Game.selectGames(c, s)));;

    public static void gameListNotOpened(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheGames1.get(input)
                .thenAccept(l -> l.forEach(g -> responseAutoComplete.addChoice(g.toChoice())))
                .thenAccept(_ -> responseAutoComplete.send());
    }

    public static void gameList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheGames2.get(input)
                .thenAccept(l -> l.forEach(g -> responseAutoComplete.addChoice(g.toChoice())))
                .thenAccept(_ -> responseAutoComplete.send());
    }
}
