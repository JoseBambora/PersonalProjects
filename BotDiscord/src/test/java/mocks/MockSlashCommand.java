package mocks;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.botgverreiro.bot.listeners.MessageReceiveListener;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockSlashCommand {
    private final User user;
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final ReplyCallbackAction replyCallbackAction;
    private final InteractionHook interactionHook;
    private final MessageChannelUnion channel;
    private final MessageCreateAction messageCreateAction;
    private final TextChannel textChannel;
    private final WebhookMessageEditAction<Message> messageEditAction;
    private final MessageReceiveListener messageReceiveListener;

    public MockSlashCommand(String slashCommandName, String mentionUser, String userName, MessageReceiveListener messageReceiveListener, Map<String, Object> arguments, boolean mod) {
        user = mock(User.class);
        event = mock(SlashCommandInteractionEvent.class);
        replyCallbackAction = mock(ReplyCallbackAction.class);
        interactionHook = mock(InteractionHook.class);
        messageEditAction = mock(WebhookMessageEditAction.class);
        channel = mock(MessageChannelUnion.class);
        textChannel = mock(TextChannel.class);
        messageCreateAction = mock(MessageCreateAction.class);
        member = mock(Member.class);

        when(user.getAsMention()).thenReturn(mentionUser);
        when(user.getName()).thenReturn(userName);
        when(user.getId()).thenReturn(mentionUser);
        when(user.getAvatarUrl()).thenReturn("https://www.google.com/url?sa=i&url=https%3A%2F%2Ffoundation.mozilla.org%2Fen%2Fprivacynotincluded%2Fdiscord%2F&psig=AOvVaw3zfI6ZfB7RuOlBhgkZUgjR&ust=1719938792093000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLDzqrelhocDFQAAAAAdAAAAABAE");
        when(event.getName()).thenReturn(slashCommandName);
        when(event.getUser()).thenReturn(user);
        when(event.getMember()).thenReturn(member);
        when(member.hasPermission((Permission) any())).thenReturn(mod);
        when(event.deferReply()).thenReturn(replyCallbackAction);
        if (arguments != null) {
            arguments.forEach((k, v) ->
                    when(event.getOption(k)).thenAnswer(_ -> {
                        OptionMapping mapping = mock(OptionMapping.class);
                        if (v instanceof String)
                            when(mapping.getAsString()).thenReturn((String) v);
                        else if (v instanceof Integer)
                            when(mapping.getAsInt()).thenReturn((Integer) v);
                        return mapping;
                    }));
        }
        when(replyCallbackAction.setEphemeral(true)).thenReturn(replyCallbackAction);
        when(event.getHook()).thenReturn(interactionHook);
        when(interactionHook.editOriginal((String) any())).thenReturn(messageEditAction);
        when(interactionHook.editOriginalEmbeds((MessageEmbed) any())).thenReturn(messageEditAction);
        when(messageEditAction.setActionRow(anyList())).thenReturn(messageEditAction);

        when(event.getChannel()).thenReturn(channel);
        when(channel.asTextChannel()).thenReturn(textChannel);
        when(textChannel.sendMessage(anyString())).thenReturn(messageCreateAction);
        this.messageReceiveListener = messageReceiveListener;
    }

    public void executeCommandWait() {
        messageReceiveListener.onSlashCommandInteraction(event);
        messageReceiveListener.waitFinish();
    }

    public void executeCommandNoWait() {
        messageReceiveListener.onSlashCommandInteraction(event);
    }

    public String getResultMessage() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(interactionHook).editOriginal(captor.capture());
        return captor.getValue();
    }

    public String getMessageSent() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(textChannel).sendMessage(captor.capture());
        return captor.getValue();
    }

    public List<Button> getButtons() {
        ArgumentCaptor<List<Button>> captor = ArgumentCaptor.forClass(List.class);
        verify(messageEditAction).setActionRow(captor.capture());
        return captor.getValue();
    }

    public MessageEmbed getResultEmbed() {
        ArgumentCaptor<MessageEmbed> captor = ArgumentCaptor.forClass(MessageEmbed.class);
        verify(interactionHook).editOriginalEmbeds(captor.capture());
        return captor.getValue();
    }

    public User getUser() {
        return user;
    }
}
