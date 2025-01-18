import aux.GenericTests;
import cases.messages.SimpleMessage;
import cases.messages.SimpleMessageReactions;
import mocks.MockMessage;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMessage {
    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void configure() {
        configuration.addMessageReceiver(new SimpleMessage());
        configuration.addMessageReceiver(new SimpleMessageReactions());
    }

    @Test
    public void testMessage() {
        MockMessage mockMessageCorrect = new MockMessage("Hello Bot", "teste", "teste", configuration);
        mockMessageCorrect.execute();
        GenericTests.testMessage(mockMessageCorrect.getResultMessage(), "", true, mockMessageCorrect.getResultButtons(), true, mockMessageCorrect.getResultFiles(), false, mockMessageCorrect.getResultEmbed(), false, mockMessageCorrect.getEmojis(), new ArrayList<>(), "message_0", "SimpleMessage", Map.of("name", "teste"));
    }

    @Test
    public void testMessageNoAnswer() {
        MockMessage mockMessageNoAnswer = new MockMessage("Hello Bot", "teste2", "teste2", configuration);
        mockMessageNoAnswer.execute();
        GenericTests.testMessage(mockMessageNoAnswer.getResultMessage(), "", false, mockMessageNoAnswer.getResultButtons(), false, mockMessageNoAnswer.getResultFiles(), false, mockMessageNoAnswer.getResultEmbed(), false, mockMessageNoAnswer.getEmojis(), new ArrayList<>(), "message_0", null, Map.of());
    }


    @Test
    public void testMessageReaction() {
        MockMessage mockMessageReactions = new MockMessage("Hello Bot", "teste3", "teste3", configuration);
        mockMessageReactions.execute();
        GenericTests.testMessage(mockMessageReactions.getResultMessage(), "", false, mockMessageReactions.getResultButtons(), false, mockMessageReactions.getResultFiles(), false, mockMessageReactions.getResultEmbed(), false, mockMessageReactions.getEmojis(), List.of(Emoji.fromUnicode("✅")), "message_0", null, Map.of());
    }
}
