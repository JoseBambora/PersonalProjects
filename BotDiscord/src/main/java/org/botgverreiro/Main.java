package org.botgverreiro;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.botgverreiro.bot.frontend.BetCommands;
import org.botgverreiro.bot.listeners.MessageReceiveListener;
import org.botgverreiro.bot.listeners.ReminderService;
import org.botgverreiro.facade.Facade;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        Facade facade = new Facade(false);
        BetCommands betCommands = new BetCommands(facade);
        ReminderService reminderService = new ReminderService(facade);
        MessageReceiveListener messageReceiveListener = new MessageReceiveListener(betCommands);
        JDABuilder.createLight(System.getenv("TOKEN_TEST"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(messageReceiveListener)
                .addEventListeners(reminderService)
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down scheduler...");
            reminderService.onStop();
            messageReceiveListener.waitFinish();
            messageReceiveListener.stopThreads();
        }));
    }
}