package org.botgverreiro;


import org.botgverreiro.models.*;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Settings.commitTransaction(c -> {
            Mode.insertMode(c,"Futebol").thenAccept(t -> System.out.println("Mode " + t));
            Season.newSeason(c).thenAccept(t -> System.out.println("Season " + t));
            Team.insertTeam(c,"Vitória SC").thenAccept(t -> System.out.println("Team " + t));
            return 0;
        });
        Thread.sleep(5000);
        Settings.commitTransaction(c -> {
            User.updatePoints(c, List.of("Olá","Olá 2"),2425,"Futebol",2,2);
            return 0;
        });
        Thread.sleep(5000);
        Settings.commitTransaction(c -> {
            Mode.getAllModes(c).thenAccept(System.out::println);
            Season.getAllSeason(c).thenAccept(System.out::println);
            Team.getSimilarTeams(c,"").thenAccept(System.out::println);
            User.getClassificationSeason(c,1).thenAccept(System.out::println);
            return 0;
        });
        Thread.sleep(5000);
    }
}