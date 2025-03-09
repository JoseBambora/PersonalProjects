package org.botgverreiro.controllers.messagereceivers;

import com.github.josebambora.configuration.MessageReceiver;
import com.github.josebambora.generic.MessageEvent;
import com.github.josebambora.responses.Response;
import com.github.josebambora.responses.ResponseMessage;
import com.github.josebambora.responses.ResponseMessageReceiver;
import com.github.josebambora.responses.ResponseMessageUpdate;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Prediction;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.GamesOpenedCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bet implements MessageEvent {
    private final Pattern pattern;
    public Bet() {
        pattern = Pattern.compile("(\\d+) *[x\\-] *(\\d+)");
    }
    @Override
    public void configure(MessageReceiver messageReceiver) {
        messageReceiver
                .addToPipelineReceive((e,_) -> e.getChannel().getId().equals(System.getenv("CHANNEL_PREDICTIONS")))
                .addToPipelineUpdate((e,_) -> e.getChannel().getId().equals(System.getenv("CHANNEL_PREDICTIONS")))
                .setReceived(true)
                .setUpdates(true);
    }

    private List<Integer>[] readMessage(String message) {
        List<Integer> homeGoals = new ArrayList<>(3);
        List<Integer> awayGoals = new ArrayList<>(3);
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            homeGoals.add(Integer.parseInt(matcher.group(1)));
            awayGoals.add(Integer.parseInt(matcher.group(2)));
        }
        return new List[]{homeGoals,awayGoals};
    }

    private CompletionStage<Boolean> addPrediction(String userId, List<Integer>[] goals) {
        List<Integer> homeGoals = goals[0];
        List<Integer> awayGoals = goals[1];
        List<Integer> gamesOpened = GamesOpenedCache.getInstance().getOpenGamesIds();
        if (!gamesOpened.isEmpty() && !homeGoals.isEmpty() && !awayGoals.isEmpty() && gamesOpened.size() == homeGoals.size() && awayGoals.size() == homeGoals.size()) {
            return Settings.commitTransaction(c -> Prediction.insertPredictions(c,userId,gamesOpened,homeGoals,awayGoals))
                    .thenApply(n -> n > 0);
        }
        else
            return CompletableFuture.completedFuture(false);

    }
    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        String userId = event.getAuthor().getId();
        String message = event.getMessage().getContentDisplay();
        List<Integer>[] goals = readMessage(message);
        addPrediction(userId,goals)
                .thenApply(b -> b ? response.addEmoji("✅") : response.addEmoji("❌"))
                .thenAccept(ResponseMessageReceiver::send);
    }

    @Override
    public void onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) {
        String userId = event.getAuthor().getId();
        String message = event.getMessage().getContentDisplay();
        List<Integer>[] goals = readMessage(message);
        addPrediction(userId,goals)
                .thenApply(b -> b ? response.addEmoji("U+1F504") : response.addEmoji("❌"))
                .thenAccept(ResponseMessageUpdate::send);
    }
}
