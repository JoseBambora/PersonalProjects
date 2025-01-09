import aux.GenericTests;
import cases.usercommands.HelloCommandUser;
import cases.usercommands.HelloCommandUser2;
import mocks.MockUserCommand;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestUserCommands {

    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void addCommands() {
        configuration.addCommand(new HelloCommandUser());
        configuration.addCommand(new HelloCommandUser2());
    }

    @Test
    public void testCommand() {
        MockUserCommand mockUserCommand = new MockUserCommand("Hello", "teste", "teste", configuration);
        mockUserCommand.execute();
        GenericTests.testMessage(mockUserCommand.getResultMessage(true), "usercontext",true, mockUserCommand.getResultButtons(true), true, mockUserCommand.getResultFiles(true), false, mockUserCommand.getResultEmbed(true), false, "Hello", "SimpleMessage", Map.of("name", "teste"));
    }

    @Test
    public void testCommandNoThinking() {
        MockUserCommand mockUserCommand = new MockUserCommand("Hello2", "teste", "teste", configuration);
        mockUserCommand.execute();
        GenericTests.testMessage(mockUserCommand.getResultMessage(false), "usercontext",true, mockUserCommand.getResultButtons(false), true, mockUserCommand.getResultFiles(false), false, mockUserCommand.getResultEmbed(false), false, "Hello2", "SimpleMessage", Map.of("name", "teste"));
    }
}
