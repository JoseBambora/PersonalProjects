package org.jdaextension;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jdaextension.configuration.Configuration;
import org.jdaextension.examples.HelloCommand;
import org.jdaextension.examples.HelloCommand2;
import org.jdaextension.examples.HelloMessage;
import org.jdaextension.examples.SimpleCommand;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.addCommand(new HelloCommand());
        configuration.addCommand(new HelloCommand2());
        configuration.addCommand(new SimpleCommand());
        configuration.addMessageReceiver(new HelloMessage());
        JDABuilder.createLight(System.getenv("token"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }
}