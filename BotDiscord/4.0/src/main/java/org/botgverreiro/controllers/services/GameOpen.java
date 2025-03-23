package org.botgverreiro.controllers.services;

import com.github.josebambora.generic.OnReadyEvent;
import com.github.josebambora.responses.ResponseTextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GameStatus;
import org.botgverreiro.utils.GamesOpenedCache;
import org.botgverreiro.utils.PermissionsManager;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class GameOpen implements OnReadyEvent {
    private static GameOpen gameOpen;
    private TextChannel textChannel;

    private GameOpen() {
    }

    public static GameOpen getInstance() {
        if (gameOpen == null)
            gameOpen = new GameOpen();
        return gameOpen;
    }

    private void openGameSend(List<Game> games) {
        List<String> gamesStr = games.stream().map(Game::toString).toList();
        ResponseTextChannel responseTextChannel = new ResponseTextChannel(textChannel);
        responseTextChannel.setTemplate("GamesOpen")
                .setVariable("games", gamesStr)
                .send();
    }

    public void call() {
        LocalDate today = LocalDate.now();
        CompletionStage<List<Game>> gamesToOpen = Settings.commitTransaction(c -> Game.selectGamesByStatus(c,GameStatus.TO_OPEN.getStatus()))
                .thenApply(Collection::stream)
                .thenApply(l -> l.filter(g -> g.getDateTime().toLocalDate().equals(today)))
                .thenApply(Stream::toList);
        gamesToOpen.thenApply(l -> Settings.commitTransaction(c -> Game.updateStatus(c, l, GameStatus.OPEN.getStatus())));
        gamesToOpen.thenAccept(games -> {
            MainService.getInstance().addServiceScheduled(() -> GameClose.getInstance().call(), games.stream().map(Game::getStartTime).toList());
            openGameSend(games);
            GamesOpenedCache.getInstance().addOpenGames(games);
            PermissionsManager.open(textChannel);
        }).exceptionally(ExceptionsHandler::storeException);
    }

    @Override
    public void onCall(ReadyEvent readyEvent) {
        textChannel = readyEvent.getJDA().getTextChannelById(System.getenv("CHANNEL_PREDICTIONS"));
    }
}
