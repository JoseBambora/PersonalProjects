import aux.GenericTests;
import cases.messages.SimpleMessage;
import mocks.MockMessage;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestMessage {
    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void configure() {
        configuration.addMessageReceiver(new SimpleMessage());
    }

    @Test
    public void testMessage() {
        MockMessage mockMessageCorrect = new MockMessage("Hello Bot", "teste", "teste", configuration);
        mockMessageCorrect.execute();
        GenericTests.testMessage(mockMessageCorrect.getResultMessage(), "", true, mockMessageCorrect.getResultButtons(), true, mockMessageCorrect.getResultFiles(), false, mockMessageCorrect.getResultEmbed(), false, "message_0", "SimpleMessage", Map.of("name", "teste"));
    }

    @Test
    public void testMessageNoAnswer() {
        MockMessage mockMessageNoAnswer = new MockMessage("Hello Bot", "teste2", "teste2", configuration);
        mockMessageNoAnswer.execute();
        GenericTests.testMessage(mockMessageNoAnswer.getResultMessage(), "", false, mockMessageNoAnswer.getResultButtons(), false, mockMessageNoAnswer.getResultFiles(), false, mockMessageNoAnswer.getResultEmbed(), false, "message_0", null, Map.of());
    }
}
