package org.botgverreiro.controllers.autocompleters;

import com.github.josebambora.responses.ResponseAutoComplete;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.botgverreiro.models.Season;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.Cache;

public class SeasonList {

    private static final Cache<Integer, Season> cacheSeason = new Cache<>(s -> Settings.commitTransaction(c -> Season.selectSimilarSeasons(c, s)));

    public static void seasonList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        if(!input.isEmpty()) {
            try {
                int season = Integer.parseInt(input);
                cacheSeason.get(season)
                        .thenApply(l -> responseAutoComplete.addChoice(l.stream().map(Season::toChoice).toList()))
                        .thenAccept(ResponseAutoComplete::send);
            } catch (NumberFormatException e) {
                responseAutoComplete.send();
            }
        }
        else
            Settings.commitTransaction(Season::selectAllSeasons)
                    .thenApply(l -> responseAutoComplete.addChoice(l.stream().map(Season::toChoice).toList()))
                    .thenAccept(ResponseAutoComplete::send);
    }
}
