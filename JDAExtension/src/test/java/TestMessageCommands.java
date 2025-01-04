import aux.GenericTests;
import cases.messagecommands.HelloCommandMessage;
import cases.messagecommands.HelloCommandMessage2;
import mocks.MockMessageCommand;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestMessageCommands {

    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void addCommands() {
        configuration.addCommand(new HelloCommandMessage());
        configuration.addCommand(new HelloCommandMessage2());
    }

    @Test
    public void testCommand() {
        MockMessageCommand mockMessageCommand = new MockMessageCommand("Hello", "teste", "teste", configuration);
        mockMessageCommand.execute();
        GenericTests.testMessage(mockMessageCommand.getResultMessage(true), true, mockMessageCommand.getResultButtons(true), true, mockMessageCommand.getResultFiles(true), false, mockMessageCommand.getResultEmbed(true), false, "Hello", "SimpleMessage", Map.of("name", "teste"));
    }


    @Test
    public void testCommandNoThinking() {
        MockMessageCommand mockMessageCommand = new MockMessageCommand("Hello2", "teste", "teste", configuration);
        mockMessageCommand.execute();
        GenericTests.testMessage(mockMessageCommand.getResultMessage(false), true, mockMessageCommand.getResultButtons(false), true, mockMessageCommand.getResultFiles(false), false, mockMessageCommand.getResultEmbed(false), false, "Hello2", "SimpleMessage", Map.of("name", "teste"));
    }
}
