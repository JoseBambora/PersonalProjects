package org.botgverreiro.controllers.services;

import com.github.josebambora.generic.OnReadyEvent;
import com.github.josebambora.responses.ResponseTextChannel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GameStatus;
import org.botgverreiro.utils.GamesOpenedCache;
import org.botgverreiro.utils.PermissionsManager;

import java.time.LocalDateTime;
import java.util.List;

public class GameClose implements OnReadyEvent {
    private static GameClose closeService;
    private TextChannel textChannel;

    private GameClose() {
    }

    public static GameClose getInstance() {
        if (closeService == null)
            closeService = new GameClose();
        return closeService;
    }

    private void closeGamesSend(List<Game> games) {
        List<String> gamesStr = games.stream().map(Game::toString).toList();
        ResponseTextChannel responseTextChannel = new ResponseTextChannel(textChannel);
        responseTextChannel.setTemplate("GamesClose")
                .setVariable("games", gamesStr)
                .send();
    }

    public void call() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesFinished = GamesOpenedCache.getInstance().getOpenGames()
                .stream()
                .filter(g -> g.getFinishTime().isBefore(now))
                .toList();
        Settings.commitTransaction(c -> Game.updateStatus(c, gamesFinished, GameStatus.CLOSE.getStatus()))
                .thenAccept(_ -> {
                    MainService.getInstance().addServiceScheduled(() -> GetResultsWeb.getInstance().call(), gamesFinished.stream().map(Game::getFinishTime).toList());
                    GamesOpenedCache.getInstance().removeOpenGames(gamesFinished);
                    closeGamesSend(gamesFinished);
                })
                .thenAccept(_ -> {
                    if(GamesOpenedCache.getInstance().getOpenGames().isEmpty()) {
                        PermissionsManager.close(textChannel);
                    }
                })
                .exceptionally(ExceptionsHandler::storeException);
    }

    @Override
    public void onCall(ReadyEvent readyEvent) {
        textChannel = readyEvent.getJDA().getTextChannelById(System.getenv("CHANNEL_PREDICTIONS"));
    }
}
