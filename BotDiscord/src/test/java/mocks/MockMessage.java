package mocks;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.botgverreiro.bot.listeners.MessageReceiveListener;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockMessage {
    private final MessageReceivedEvent event;
    private final User user;
    private final MessageChannelUnion channel;
    private final Message message;
    private final RestAction<Void> restAction;
    private final MessageReceiveListener messageReceiveListener;

    public MockMessage(String messageContent, String mentionUser, String userName, MessageReceiveListener messageReceiveListener) {
        event = mock(MessageReceivedEvent.class);
        user = mock(User.class);
        channel = mock(MessageChannelUnion.class);
        message = mock(Message.class);
        restAction = mock(RestAction.class);
        when(user.getAsMention()).thenReturn(mentionUser);
        when(user.getName()).thenReturn(userName);
        when(user.getId()).thenReturn(mentionUser);
        when(event.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(event.getChannel()).thenReturn(channel);
        when(channel.getId()).thenReturn(System.getenv("CHANNEL_BETS_TEST"));
        when(event.getMessage()).thenReturn(message);
        when(message.addReaction(any())).thenReturn(restAction);
        when(message.getContentDisplay()).thenReturn(messageContent);
        this.messageReceiveListener = messageReceiveListener;
    }

    public void executeCommandWait() {
        messageReceiveListener.onMessageReceived(event);
        messageReceiveListener.waitFinish();
    }

    public void executeCommandNoWait() {
        messageReceiveListener.onMessageReceived(event);
    }

    public Emoji getEmoji() {
        ArgumentCaptor<Emoji> captor = ArgumentCaptor.forClass(Emoji.class);
        verify(message).addReaction(captor.capture());
        return captor.getValue();
    }
}
