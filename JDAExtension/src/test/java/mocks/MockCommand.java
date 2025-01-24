package mocks;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public abstract class MockCommand extends MockResults {
    private final ModalCallbackAction modalCallbackAction;
    private final User user;
    private final Member member;
    private final ReplyCallbackAction replyCallbackAction;
    private final InteractionHook interactionHook;
    private final MessageChannelUnion channel;
    private final MessageCreateAction messageCreateAction;
    private final TextChannel textChannel;
    private final WebhookMessageEditAction<Message> messageEditAction;
    private final Configuration configuration;
    private ModalInteractionEvent modalInteractionEvent;
    private CommandInteraction event;

    public MockCommand(String mentionUser, String userName, Configuration configuration, boolean mod) {

        user = mock(User.class);
        modalCallbackAction = mock(ModalCallbackAction.class);
        replyCallbackAction = mock(ReplyCallbackAction.class);
        interactionHook = mock(InteractionHook.class);
        messageEditAction = mock(WebhookMessageEditAction.class);
        channel = mock(MessageChannelUnion.class);
        textChannel = mock(TextChannel.class);
        messageCreateAction = mock(MessageCreateAction.class);
        member = mock(Member.class);
        this.configuration = configuration;
        configUser(mentionUser, userName);
        configMember(mod);
        configReply();
        configInteraction();
        configMessageEdit();
        configChannel();

    }

    private void configUser(String mentionUser, String userName) {
        when(user.getAsMention()).thenReturn(mentionUser);
        when(user.getName()).thenReturn(userName);
        when(user.getId()).thenReturn(mentionUser);
        when(user.getAvatarUrl()).thenReturn("https://www.google.com/url?sa=i&url=https%3A%2F%2Ffoundation.mozilla.org%2Fen%2Fprivacynotincluded%2Fdiscord%2F&psig=AOvVaw3zfI6ZfB7RuOlBhgkZUgjR&ust=1719938792093000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLDzqrelhocDFQAAAAAdAAAAABAE");
    }

    private void configMember(boolean mod) {
        when(member.hasPermission((Permission) any())).thenReturn(mod);
        when(member.hasPermission(anyList())).thenReturn(mod);
    }

    private void configReply() {
        when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
        when(replyCallbackAction.setActionRow(anyList())).thenReturn(replyCallbackAction);
        when(replyCallbackAction.setFiles(anyCollection())).thenReturn(replyCallbackAction);
        when(replyCallbackAction.setEmbeds(any(MessageEmbed.class))).thenReturn(replyCallbackAction);
    }

    private void configInteraction() {
        when(interactionHook.editOriginal((String) any())).thenReturn(messageEditAction);
        when(interactionHook.editOriginalEmbeds((MessageEmbed) any())).thenReturn(messageEditAction);
    }

    private void configMessageEdit() {
        when(messageEditAction.setActionRow(anyList())).thenReturn(messageEditAction);
        when(messageEditAction.setFiles(anyList())).thenReturn(messageEditAction);
    }

    private void configChannel() {
        when(channel.asTextChannel()).thenReturn(textChannel);
        when(textChannel.sendMessage(anyString())).thenReturn(messageCreateAction);
    }

    public abstract void execute();

    public String getResultMessage(boolean sendThinking) {
        Consumer<ArgumentCaptor<String>> consumer;
        if (sendThinking)
            consumer = c -> verify(interactionHook, atMost(1)).editOriginal(c.capture());
        else
            consumer = c -> verify(event, atMost(1)).reply(c.capture());
        return getResultMessage(consumer);
    }

    public List<Button> getResultButtons(boolean sendThinking) {
        Consumer<ArgumentCaptor<List<Button>>> consumer;
        if (sendThinking)
            consumer = c -> verify(messageEditAction, atMost(1)).setActionRow(c.capture());
        else
            consumer = c -> verify(replyCallbackAction, atMost(1)).setActionRow(c.capture());
        return getResultButtons(consumer);
    }

    public MessageEmbed getResultEmbed(boolean sendThinking) {
        Consumer<ArgumentCaptor<MessageEmbed>> consumer;
        if (sendThinking)
            consumer = c -> verify(messageEditAction, atMost(1)).setEmbeds(c.capture());
        else
            consumer = c -> verify(replyCallbackAction, atMost(1)).setEmbeds(c.capture());
        return getResultEmbed(consumer);
    }

    public List<FileUpload> getResultFiles(boolean sendThinking) {
        Consumer<ArgumentCaptor<List<FileUpload>>> consumer;
        if (sendThinking)
            consumer = c -> verify(messageEditAction, atMost(1)).setFiles(c.capture());
        else
            consumer = c -> verify(replyCallbackAction, atMost(1)).setFiles(c.capture());
        return getResultFiles(consumer);
    }

    public Modal getResultModal(boolean sendThinking) {
        Consumer<ArgumentCaptor<Modal>> consumer;
        if (sendThinking)
            consumer = c -> verify(event, atMost(1)).replyModal(c.capture());
        else
            consumer = c -> verify(event, atMost(1)).replyModal(c.capture());
        return getResultModal(consumer);
    }

    public String getResultModalMessage(boolean sendThinking) {
        Consumer<ArgumentCaptor<String>> consumer;
        if (sendThinking)
            consumer = c -> verify(modalInteractionEvent, atMost(1)).reply(c.capture());
        else
            consumer = c -> verify(modalInteractionEvent, atMost(1)).reply(c.capture());
        return getResultMessage(consumer);
    }

    protected User getUser() {
        return user;
    }

    public String getCommand() {
        return event.getName();
    }

    protected void setCommand(CommandInteraction event) {
        this.event = event;
        when(this.event.replyModal(any())).thenReturn(modalCallbackAction);
    }

    protected void setCommand(ModalInteractionEvent event) {
        modalInteractionEvent = event;
        when(modalInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected ReplyCallbackAction getReplyCallbackAction() {
        return replyCallbackAction;
    }

    protected Member getMember() {
        return member;
    }

    protected InteractionHook getInteractionHook() {
        return interactionHook;
    }

    protected MessageChannelUnion getChannel() {
        return channel;
    }
}
