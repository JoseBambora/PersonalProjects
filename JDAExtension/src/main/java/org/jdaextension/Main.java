package org.jdaextension;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jdaextension.configuration.Configuration;
import org.jdaextension.examples.HelloCommand;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.addCommand(new HelloCommand());
        JDABuilder.createLight(System.getenv("token"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }
}