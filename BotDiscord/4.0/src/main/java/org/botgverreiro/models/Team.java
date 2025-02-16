package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Teams;
import org.jooq.DSLContext;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class Team {
    @Column(name = "TEAM_NAME")
    private String teamName;

    @Override
    public String toString() {
        return "Team(" + teamName + ")";
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */
    /**
     * Get all the available teams whose names contains a certain string.
     * @param name Team name
     * @return A list containing all the teams that are similar to a certain name.
     */
    public static CompletionStage<List<Team>> getSimilarTeams(DSLContext context,String name) {
        return context.selectFrom(Teams.TEAMS)
                .where(Teams.TEAMS.TEAM_NAME.contains(name))
                .fetchAsync()
                .thenApply(r -> r.into(Team.class));
    }

    /**
     * Inserts a new team.
     * @param team Team name to insert.
     * @return 1 if success, 0 otherwise.
     */
    public static CompletionStage<Integer> insertTeam(DSLContext context,String team) {
        return context
                .insertInto(Teams.TEAMS)
                .set(Teams.TEAMS.TEAM_NAME, team)
                .onConflictDoNothing()
                .executeAsync();
    }

    public String getTeamName() {
        return teamName;
    }
}
