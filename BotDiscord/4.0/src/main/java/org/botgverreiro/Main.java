package org.botgverreiro;


import com.github.josebambora.configuration.Configuration;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.botgverreiro.controllers.messagereceivers.Bet;
import org.botgverreiro.controllers.services.*;
import org.botgverreiro.controllers.slashcommands.*;

import java.util.EnumSet;

public class Main {

    private static void setUpService(Configuration configuration) {
        MainService mainService = MainService.getInstance();
        GetGamesWeb getGamesWeb = GetGamesWeb.getInstance();
        GetResultsWeb getResultsWeb = GetResultsWeb.getInstance();
        GameClose gameClose = GameClose.getInstance();
        GameOpen gameOpen = GameOpen.getInstance();

        mainService.addServiceDaily(gameOpen::call, Integer.parseInt(System.getenv("RUN_DAILY_TIME")));
        mainService.addServiceDaily(getGamesWeb::call, Integer.parseInt(System.getenv("RUN_DAILY_TIME")));
        configuration.addReadyEvent(gameOpen);
        configuration.addReadyEvent(gameClose);
        configuration.addReadyEvent(getResultsWeb);
    }

    private static void startBot() {
        Configuration configuration = new Configuration();
        configuration.addCommand(new Help());
        configuration.addCommand(new GameAdd());
        configuration.addCommand(new GameDel());
        configuration.addCommand(new GameList());
        configuration.addCommand(new GameInfo());
        configuration.addMessageReceiver(new Bet());
        setUpService(configuration);


        JDABuilder.createLight(System.getenv("TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }

    public static void main(String[] args) {
        startBot();
        // GetResultsWeb.getInstance().call();
    }
}