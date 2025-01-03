package mocks;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jdaextension.configuration.Configuration;

import java.util.Map;

import static org.mockito.Mockito.*;

public class MockSlashCommand extends MockCommand {
    private final SlashCommandInteractionEvent event;

    public MockSlashCommand(String slashCommandName, String mentionUser, String userName, Configuration configuration, Map<String, Object> arguments, boolean mod) {
        super(mentionUser, userName, configuration, mod);
        event = mock(SlashCommandInteractionEvent.class);
        configEvent(slashCommandName, arguments);
        super.setCommand(event);

    }

    private void configEvent(String slashCommandName, Map<String, Object> arguments) {
        when(event.getName()).thenReturn(slashCommandName);
        when(event.getUser()).thenReturn(getUser());
        when(event.getMember()).thenReturn(getMember());
        when(event.reply(anyString())).thenReturn(getReplyCallbackAction());
        when(event.deferReply()).thenReturn(getReplyCallbackAction());
        when(event.deferReply(anyBoolean())).thenReturn(getReplyCallbackAction());
        when(event.getHook()).thenReturn(getInteractionHook());
        when(event.getChannel()).thenReturn(getChannel());


        if (arguments != null) {
            arguments.forEach((k, v) ->
                    when(event.getOption(k)).thenAnswer(_ -> {
                        OptionMapping mapping = mock(OptionMapping.class);
                        if (v instanceof String)
                            when(mapping.getAsString()).thenReturn((String) v);
                        else if (v instanceof Integer)
                            when(mapping.getAsInt()).thenReturn((Integer) v);
                        else if (v instanceof Double)
                            when(mapping.getAsDouble()).thenReturn((Double) v);
                        return mapping;
                    }));
        }
    }

    public void execute() {
        getConfiguration().onSlashCommandInteraction(event);
    }
}
