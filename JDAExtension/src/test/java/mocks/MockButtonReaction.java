package mocks;

import aux.GenericTests;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MockButtonReaction extends MockResults {
    private final Button button;
    private final ButtonInteractionEvent event;
    private final MessageEditCallbackAction messageEditCallbackAction;
    private final User user;
    private final String mention;
    private final Configuration configuration;

    private void configureUser() {
        when(user.getId()).thenReturn(mention);
        when(user.getAsMention()).thenReturn(mention);
    }
    private void configureButton(String id) {
        when(button.getId()).thenReturn(id);
    }
    private void configureEvent() {
        when(event.getMessageId()).thenReturn("");
        when(event.getUser()).thenReturn(user);
        when(event.getButton()).thenReturn(button);
        when(event.editMessage(anyString())).thenReturn(messageEditCallbackAction);
    }
    private void configureMessageEdit() {
        when(messageEditCallbackAction.setActionRow(anyList())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setComponents(anyList())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setEmbeds(any(MessageEmbed.class))).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setFiles(anyList())).thenReturn(messageEditCallbackAction);
    }

    public MockButtonReaction(String id, String mention, Configuration configuration) {
        event = mock(ButtonInteractionEvent.class);
        user = mock(User.class);
        messageEditCallbackAction = mock(MessageEditCallbackAction.class);
        button = mock(Button.class);
        this.mention = mention;
        this.configuration = configuration;
        configureButton(id);
        configureEvent();
        configureUser();
        configureMessageEdit();
    }

    public void execute() {
        this.configuration.onButtonInteraction(event);
    }

    // public Emoji getEmoji() {
    //     return getEmoji(c -> verify(message, atMost(1)).addReaction(c.capture()));
    // }

    public String getResultMessage() {
        return getResultMessage(c -> verify(event, atMost(1)).editMessage(c.capture()));
    }

    public List<Button> getResultButtons() {
        return getResultButtons(c -> verify(messageEditCallbackAction, atMost(1)).setActionRow(c.capture()));
    }

    public MessageEmbed getResultEmbed() {
        return getResultEmbed(c -> verify(messageEditCallbackAction, atMost(1)).setEmbeds(c.capture()));
    }

    public List<FileUpload> getResultFiles() {
        return getResultFiles(c -> verify(messageEditCallbackAction, atMost(1)).setFiles(c.capture()));
    }

    public String getUser() {
        return mention;
    }
}
