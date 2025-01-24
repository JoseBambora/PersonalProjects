package mocks;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class MockMessage extends MockResults {
    protected final User user;
    protected final MessageChannelUnion channel;
    protected final Message message;
    protected final RestAction<Void> restAction;
    protected final MessageCreateAction messageCreateAction;

    public MockMessage(String messageContent, String mentionUser, String userName) {
        user = mock(User.class);
        channel = mock(MessageChannelUnion.class);
        message = mock(Message.class);
        restAction = mock(RestAction.class);
        messageCreateAction = mock(MessageCreateAction.class);
        configureMessage(messageContent);
        configureUser(mentionUser, userName);
    }

    private void configureUser(String mentionUser, String userName) {
        when(user.getAsMention()).thenReturn(mentionUser);
        when(user.getName()).thenReturn(userName);
        when(user.getId()).thenReturn(mentionUser);
        when(user.isBot()).thenReturn(false);
    }


    private void configureMessage(String messageContent) {
        when(message.reply(anyString())).thenReturn(messageCreateAction);
        when(message.addReaction(any())).thenReturn(restAction);
        when(message.getContentDisplay()).thenReturn(messageContent);
        when(messageCreateAction.setFiles(anyList())).thenReturn(messageCreateAction);
        when(messageCreateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(messageCreateAction);
        when(messageCreateAction.setActionRow(anyList())).thenReturn(messageCreateAction);
    }

    public abstract void execute();

    public List<Emoji> getEmojis() {
        ArgumentCaptor<Emoji> captor = ArgumentCaptor.forClass(Emoji.class);
        verify(message, atLeast(0)).addReaction(captor.capture());
        return captor.getAllValues();
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
