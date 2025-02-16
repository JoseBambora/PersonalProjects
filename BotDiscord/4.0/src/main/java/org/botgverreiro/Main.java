package org.botgverreiro;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.botgverreiro.controllers.slashcommands.GameAdd;
import org.botgverreiro.controllers.slashcommands.Help;
import org.jdaextension.configuration.Configuration;

import java.util.EnumSet;

public class Main {

    private static void startBot() {
        Configuration configuration = new Configuration();
        configuration.addCommand(new Help());
        configuration.addCommand(new GameAdd());
        JDABuilder.createLight(System.getenv("TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }
    public static void main(String[] args) {
        startBot();
    }
}