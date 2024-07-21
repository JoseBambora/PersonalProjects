package mocks;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import org.botgverreiro.bot.listeners.MessageReceiveListener;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MockButtonReaction {
    private final ButtonInteractionEvent event;
    private final MessageEditCallbackAction messageEditCallbackAction;
    private final MessageReceiveListener messageReceiveListener;
    private final User user;
    private final String mention;

    public MockButtonReaction(String id, String mention, MessageReceiveListener messageReceiveListener) {
        event = mock(ButtonInteractionEvent.class);
        user = mock(User.class);
        messageEditCallbackAction = mock(MessageEditCallbackAction.class);
        when(event.getMessageId()).thenReturn("");
        when(event.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(mention);
        when(user.getAsMention()).thenReturn(mention);
        this.mention = mention;
        when(event.getComponentId()).thenReturn(id);
        when(event.editMessage(anyString())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setActionRow(anyList())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setComponents(anyList())).thenReturn(messageEditCallbackAction);
        this.messageReceiveListener = messageReceiveListener;
    }

    public void sendMessage() {
        this.messageReceiveListener.onButtonInteraction(event);
    }

    public String getMessageSent() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(event).editMessage(captor.capture());
        return captor.getValue();
    }

    public List<Button> getButtons() {
        ArgumentCaptor<List<Button>> captor = ArgumentCaptor.forClass(List.class);
        verify(messageEditCallbackAction).setActionRow(captor.capture());
        return captor.getValue();
    }

    public List<LayoutComponent> getButtons2() {
        ArgumentCaptor<List<LayoutComponent>> captor = ArgumentCaptor.forClass(List.class);
        verify(messageEditCallbackAction).setComponents(captor.capture());
        return captor.getValue();
    }

    public String getUser() {
        return mention;
    }
}
