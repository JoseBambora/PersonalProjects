package mocks;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.Configuration;

import static org.mockito.Mockito.*;

public class MockMessageCommand extends MockCommand {
    private final MessageContextInteractionEvent event;

    public MockMessageCommand(String commandName, String mentionUser, String userName, Configuration configuration) {
        super(mentionUser, userName, configuration, false);
        event = mock(MessageContextInteractionEvent.class);
        configureEvent(commandName);
        super.setCommand(event);
    }

    private void configureEvent(String commandName) {
        when(event.getUser()).thenReturn(getUser());
        when(event.getName()).thenReturn(commandName);
        when(event.deferReply()).thenReturn(getReplyCallbackAction());
        when(event.deferReply(anyBoolean())).thenReturn(getReplyCallbackAction());
        when(event.reply(anyString())).thenReturn(getReplyCallbackAction());
        when(event.getHook()).thenReturn(getInteractionHook());
    }

    public void execute() {
        getConfiguration().onMessageContextInteraction(event);
    }
}
