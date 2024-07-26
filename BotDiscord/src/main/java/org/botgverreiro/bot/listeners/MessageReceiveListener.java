package org.botgverreiro.bot.listeners;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.botgverreiro.ParserInfo;
import org.botgverreiro.bot.frontend.BetCommands;
import org.botgverreiro.bot.frontend.MessageSender;
import org.botgverreiro.bot.threads.TaskManager;
import org.botgverreiro.bot.utils.RateLimiter;
import org.botgverreiro.bot.utils.Templates;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles messages and commands.
 * It is important to note that this just uses methods from BetCommand class, and we are using multithreading here.
 *
 * @author JoséBambora
 * @version 1.0
 * @see BetCommands
 * @see TaskManager
 */
public class MessageReceiveListener extends ListenerAdapter {
    private final BetCommands betCommands;
    private final TaskManager taskManager = new TaskManager();

    public MessageReceiveListener(BetCommands betCommands) {
        this.betCommands = betCommands;
    }

    private boolean correctTextChannel(@NotNull MessageReceivedEvent event) {
        String id = event.getChannel().getId();
        return !id.equals(ParserInfo.getInstance().getValueString("channelBets")) &&
                !id.equals(ParserInfo.getInstance().getValueString("channelBetsTest"));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!(event.getAuthor().isBot() || RateLimiter.isRateLimited(event.getAuthor().getId()) || correctTextChannel(event)))
            taskManager.addTask(() -> betCommands.receiveMessage(event));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        MessageSender.sendThinking(event);
        if (betCommands.hasCommand(event))
            MessageSender.sendMessage(event, Templates.messageUnkownCommand());
        else if (betCommands.hasPermission(event))
            MessageSender.sendMessage(event, Templates.messageNoPermissions());
        else if (RateLimiter.isRateLimited(event.getUser().getId()))
            MessageSender.sendMessage(event, Templates.messageRateLimit());
        else
            taskManager.addTask(() -> betCommands.execCommand(event));
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(betCommands.getCommandsMods())
                .addCommands(betCommands.getCommandsUser())
                .queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        taskManager.addTask(() -> betCommands.clickedButton(event));
    }

    /**
     * Just for tests.
     */
    public void waitFinish() {
        taskManager.waitTask();
    }

    /**
     * Just for tests.
     */
    public void stopThreads() {
        taskManager.stop();
    }
}