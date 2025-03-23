package org.botgverreiro.controllers.autocompleters;

import com.github.josebambora.responses.ResponseAutoComplete;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.botgverreiro.models.Settings;
import org.botgverreiro.models.Team;
import org.botgverreiro.utils.Cache;

public class TeamList {
    private static final Cache<String, Team> cacheTeams = new Cache<>(s -> Settings.commitTransaction(c -> Team.selectSimilarTeams(c, s)));

    public static void teamsList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheTeams.get(input)
                .thenApply(l -> responseAutoComplete.addChoice(l.stream().map(Team::toChoice).toList()))
                .thenAccept(ResponseAutoComplete::send);
    }
}
