package mocks;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MockModalResult extends MockCommand {
    private final ModalInteractionEvent event;
    private final MessageEditCallbackAction messageEditCallbackAction;
    private final List<ModalMapping> modalMapping;
    private final User user;
    private final String mention;
    private final Configuration configuration;

    public MockModalResult(String id, String mention, Configuration configuration, Map<String, String> answers) {
        super(id, mention, configuration, false);
        event = mock(ModalInteractionEvent.class);
        user = mock(User.class);
        modalMapping = mock(List.class);
        messageEditCallbackAction = mock(MessageEditCallbackAction.class);
        this.mention = mention;
        this.configuration = configuration;
        configureEvent(id, answers);
        configureUser();
        configureMessageEdit();
        setCommand(event);
    }

    private void configureUser() {
        when(user.getId()).thenReturn(mention);
        when(user.getAsMention()).thenReturn(mention);
    }

    private void configureEvent(String id, Map<String, String> answers) {
        when(event.getModalId()).thenReturn("command_" + id);
        when(event.getUser()).thenReturn(user);
        when(event.getValues()).thenReturn(modalMapping);
        when(event.editMessage(anyString())).thenReturn(messageEditCallbackAction);

        int i = 0;
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            ModalMapping modalMapping1 = mock(ModalMapping.class);
            when(modalMapping.get(i)).thenReturn(modalMapping1);
            when(modalMapping1.getId()).thenReturn(entry.getKey());
            when(modalMapping1.getAsString()).thenReturn(entry.getValue());
            i++;
        }

    }

    private void configureMessageEdit() {
        when(messageEditCallbackAction.setActionRow(anyList())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setComponents(anyList())).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setEmbeds(any(MessageEmbed.class))).thenReturn(messageEditCallbackAction);
        when(messageEditCallbackAction.setFiles(anyList())).thenReturn(messageEditCallbackAction);
    }

    public void execute() {
        this.configuration.onModalInteraction(event);
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
}
