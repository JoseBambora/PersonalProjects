package org.botgverreiro.controllers.services;

import com.github.josebambora.generic.OnReadyEvent;
import com.github.josebambora.responses.ResponseTextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GamesOpenedCache;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class GameOpen implements OnReadyEvent {
    private TextChannel textChannel;

    private GameOpen() {}

    private void openGameSend(List<Game> games){
        List<String> gamesStr = games.stream().map(Game::toString).toList();
        ResponseTextChannel responseTextChannel = new ResponseTextChannel(textChannel);
        responseTextChannel.setTemplate("GamesOpen")
                .setVariable("games",gamesStr)
                .send();
    }
    public void call() {
         LocalDate today = LocalDate.now();
         Settings.commitTransaction(Game::getGamesNotOpened)
                 .thenApply(Collection::stream)
                 .thenApply(l -> l.filter(g -> g.getDateTime().toLocalDate().equals(today)))
                 .thenApply(Stream::toList)
                 .thenAccept(games -> {
                     MainService.getInstance().addServiceScheduled(() -> GameClose.getInstance().call(),games.stream().map(Game::getStartTime).toList());
                     openGameSend(games);
                     GamesOpenedCache.getInstance().addOpenGames(games);
                 })
                 .exceptionally(ExceptionsHandler::storeException);;
    }
    @Override
    public void onCall(ReadyEvent readyEvent) {
        textChannel = readyEvent.getJDA().getTextChannelById(System.getenv("CHANNEL_PREDICTIONS"));
    }

    private static GameOpen gameOpen;
    public static GameOpen getInstance() {
        if(gameOpen == null)
            gameOpen = new GameOpen();
        return gameOpen;
    }
}
