package mocks;

import aux.GenericTests;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockMessage extends MockResults {
    private final MessageReceivedEvent event;
    private final User user;
    private final MessageChannelUnion channel;
    private final Message message;
    private final RestAction<Void> restAction;
    private final MessageCreateAction messageCreateAction;
    private final Configuration configuration;

    private void configureUser(String mentionUser, String userName) {
        when(user.getAsMention()).thenReturn(mentionUser);
        when(user.getName()).thenReturn(userName);
        when(user.getId()).thenReturn(mentionUser);
        when(user.isBot()).thenReturn(false);
    }

    private void configureEvent() {
        when(event.getAuthor()).thenReturn(user);
        when(event.getChannel()).thenReturn(channel);
        when(channel.getId()).thenReturn(System.getenv("CHANNEL_BETS_TEST"));
        when(event.getMessage()).thenReturn(message);
    }

    private void configureMessage(String messageContent) {
        when(message.reply(anyString())).thenReturn(messageCreateAction);
        when(message.addReaction(any())).thenReturn(restAction);
        when(message.getContentDisplay()).thenReturn(messageContent);
        when(messageCreateAction.setFiles(anyList())).thenReturn(messageCreateAction);
        when(messageCreateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(messageCreateAction);
        when(messageCreateAction.setActionRow(anyList())).thenReturn(messageCreateAction);
    }

    public MockMessage(String messageContent, String mentionUser, String userName, Configuration configuration) {
        event = mock(MessageReceivedEvent.class);
        user = mock(User.class);
        channel = mock(MessageChannelUnion.class);
        message = mock(Message.class);
        restAction = mock(RestAction.class);
        messageCreateAction = mock(MessageCreateAction.class);
        configureMessage(messageContent);
        configureEvent();
        configureUser(mentionUser,userName);
        this.configuration = configuration;
    }

    public void execute() {
        configuration.onMessageReceived(event);
    }

    public Emoji getEmoji() {
        return getEmoji(c -> verify(message, atMost(1)).addReaction(c.capture()));
    }

    public String getResultMessage() {
        return getResultMessage(c -> verify(message, atMost(1)).reply(c.capture()));
    }

    public List<Button> getResultButtons() {
        return getResultButtons(c -> verify(messageCreateAction, atMost(1)).setActionRow(c.capture()));
    }

    public MessageEmbed getResultEmbed() {
        return getResultEmbed(c -> verify(messageCreateAction, atMost(1)).setEmbeds(c.capture()));
    }

    public List<FileUpload> getResultFiles() {
        return getResultFiles(c -> verify(messageCreateAction, atMost(1)).setFiles(c.capture()));
    }

}
