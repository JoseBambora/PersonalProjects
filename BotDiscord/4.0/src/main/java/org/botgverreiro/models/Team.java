package org.botgverreiro.models;

import jakarta.persistence.Column;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.botgverreiro.tables.Teams;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class Team {
    @Column(name = "TEAM_NAME")
    private String teamName;

    public Team() {
    }

    public Team(String teamName) {
        this.teamName = teamName;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    /* =================== Inserts =================== */

    /**
     * Inserts a new team.
     *
     * @param context Database context.
     * @param team    Team name to insert.
     * @return 1 if success, 0 otherwise.
     */
    public static CompletionStage<Integer> insertTeam(DSLContext context, Team team) {
        return insertTeams(context, Collections.singletonList(team));
    }

    /**
     * Inserts new teams.
     *
     * @param context Database context.
     * @param teams   Teams name to insert.
     * @return size of the list teams if success, 0 otherwise.
     */
    public static CompletionStage<Integer> insertTeams(DSLContext context, List<Team> teams) {
        return context
                .insertInto(Teams.TEAMS)
                .set(teams.stream().map(t -> context.newRecord(Teams.TEAMS.TEAM_NAME).value1(t.teamName)).toList())
                .onConflictDoNothing()
                .executeAsync();
    }

    /* =================== Updates =================== */

    /* =================== Selects =================== */


    /**
     * Get all the available teams whose names contains a certain string.
     *
     * @param context Database context.
     * @param name    Team name.
     * @return A list containing all the teams that are similar to a certain name.
     */
    public static CompletionStage<List<Team>> selectSimilarTeams(DSLContext context, String name) {
        return context.selectFrom(Teams.TEAMS)
                .where(Teams.TEAMS.TEAM_NAME.contains(name))
                .fetchAsync()
                .thenApply(r -> r.into(Team.class));
    }

    /* =================== Deletes =================== */

    @Override
    public String toString() {
        return "Team(" + teamName + ")";
    }

    public Command.Choice toChoice() {
        return new Command.Choice(this.teamName, this.teamName);
    }

    public String getTeamName() {
        return teamName;
    }
}
