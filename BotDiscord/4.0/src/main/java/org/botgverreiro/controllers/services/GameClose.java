package org.botgverreiro.controllers.services;

import com.github.josebambora.generic.OnReadyEvent;
import com.github.josebambora.responses.ResponseTextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GamesOpenedCache;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class GameClose implements OnReadyEvent {
    private TextChannel textChannel;


    private GameClose() {}

    private void closeGamesSend(List<Game> games){
        List<String> gamesStr = games.stream().map(Game::toString).toList();
        ResponseTextChannel responseTextChannel = new ResponseTextChannel(textChannel);
        responseTextChannel.setTemplate("GamesClose")
                .setVariable("games",gamesStr)
                .send();
    }
    public void call() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesFinished = GamesOpenedCache.getInstance().getOpenGames()
                .stream()
                .filter(g -> g.getFinishTime().isBefore(now))
                .toList();
        Settings.commitTransaction(c -> Game.closeGames(c,gamesFinished))
                .thenAccept(_ -> {
                    GamesOpenedCache.getInstance().removeOpenGames(gamesFinished);
                    closeGamesSend(gamesFinished);
                })
                .exceptionally(ExceptionsHandler::storeException);
    }
    @Override
    public void onCall(ReadyEvent readyEvent) {
        textChannel = readyEvent.getJDA().getTextChannelById(System.getenv("CHANNEL_PREDICTIONS"));
    }

    private static GameClose closeService;
    public static GameClose getInstance() {
        if(closeService == null)
            closeService = new GameClose();
        return closeService;
    }
}
