package org.jdaextension;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jdaextension.cases.messages.SimpleMessage;
import org.jdaextension.cases.slashcommands.SimpleCommand;
import org.jdaextension.cases.slashcommands.SimpleCommandMod;
import org.jdaextension.cases.slashcommands.SimpleCommandOptions;
import org.jdaextension.configuration.Configuration;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        // configuration.addCommand(new HelloCommand());
        // configuration.addCommand(new HelloCommand2());
        configuration.addCommand(new SimpleCommand());
        // configuration.addCommand(new HelloCommandUser());
        // configuration.addCommand(new HelloCommandMessage());
        // configuration.addMessageReceiver(new HelloMessage());
        configuration.addCommand(new SimpleCommandOptions());
        configuration.addCommand(new SimpleCommandMod());
        configuration.addMessageReceiver(new SimpleMessage());
        JDABuilder.createLight(System.getenv("token"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(configuration)
                .build();
    }
}