package mocks;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMessageReceived extends MockMessage {
    private final MessageReceivedEvent event;
    private final Configuration configuration;

    public MockMessageReceived(String messageContent, String mentionUser, String userName, Configuration configuration) {
        super(messageContent, mentionUser, userName);
        event = mock(MessageReceivedEvent.class);
        this.configuration = configuration;
        configureEvent();
    }

    private void configureEvent() {
        when(event.getAuthor()).thenReturn(user);
        when(event.getChannel()).thenReturn(channel);
        when(channel.getId()).thenReturn(System.getenv("CHANNEL_BETS_TEST"));
        when(event.getMessage()).thenReturn(message);
    }

    @Override
    public void execute() {
        configuration.onMessageReceived(event);
    }
}
