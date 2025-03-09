package org.botgverreiro;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.botgverreiro.controllers.messagereceivers.Bet;
import org.botgverreiro.controllers.services.GameClose;
import org.botgverreiro.controllers.services.GameOpen;
import org.botgverreiro.controllers.services.MainService;
import org.botgverreiro.controllers.slashcommands.GameAdd;
import org.botgverreiro.controllers.slashcommands.GameDel;
import org.botgverreiro.controllers.slashcommands.Help;
import com.github.josebambora.configuration.Configuration;

import java.util.EnumSet;

public class Main {

    private static void setUpService(Configuration configuration) {
        MainService mainService = MainService.getInstance();
        GameClose gameClose = GameClose.getInstance();
        GameOpen gameOpen = GameOpen.getInstance();
        mainService.addServiceDaily(gameOpen::call,18);
        configuration.addReadyEvent(gameOpen);
    }
    private static void startBot() {
        Configuration configuration = new Configuration();
        configuration.addCommand(new Help());
        configuration.addCommand(new GameAdd());
        configuration.addCommand(new GameDel());
        configuration.addMessageReceiver(new Bet());
        setUpService(configuration);


        JDABuilder.createLight(System.getenv("TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }

    public static void main(String[] args) {
        startBot();
    }
}