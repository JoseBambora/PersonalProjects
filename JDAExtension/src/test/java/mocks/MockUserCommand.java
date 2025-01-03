package mocks;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.Configuration;

import static org.mockito.Mockito.*;

public class MockUserCommand extends MockCommand {
    private final UserContextInteractionEvent event;

    public MockUserCommand(String commandName, String mentionUser, String userName, Configuration configuration) {
        super(mentionUser, userName, configuration, false);
        event = mock(UserContextInteractionEvent.class);
        configureEvent(commandName);
        super.setCommand(event);
    }

    private void configureEvent(String commandName) {
        when(event.getUser()).thenReturn(getUser());
        when(event.getTarget()).thenReturn(getUser());
        when(event.getName()).thenReturn(commandName);
        when(event.deferReply()).thenReturn(getReplyCallbackAction());
        when(event.deferReply(anyBoolean())).thenReturn(getReplyCallbackAction());
        when(event.reply(anyString())).thenReturn(getReplyCallbackAction());
        when(event.getHook()).thenReturn(getInteractionHook());
    }

    public void execute() {
        getConfiguration().onUserContextInteraction(event);
    }
}
