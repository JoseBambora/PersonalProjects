package mocks;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMessageUpdate extends MockMessage {
    private final MessageUpdateEvent event;
    private final Configuration configuration;

    public MockMessageUpdate(String messageContent, String mentionUser, String userName, Configuration configuration) {
        super(messageContent, mentionUser, userName);
        event = mock(MessageUpdateEvent.class);
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
        configuration.onMessageUpdate(event);
    }
}
