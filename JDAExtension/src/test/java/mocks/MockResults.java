package mocks;

import aux.GenericTests;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.Consumer;

public abstract class MockResults {
    protected Emoji getEmoji(Consumer<ArgumentCaptor<Emoji>> consumer) {
        ArgumentCaptor<Emoji> captor = ArgumentCaptor.forClass(Emoji.class);
        consumer.accept(captor);
        return GenericTests.getValueSingle(captor);
    }

    protected String getResultMessage(Consumer<ArgumentCaptor<String>> consumer) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        consumer.accept(captor);
        return GenericTests.getValueSingle(captor);
    }


    protected List<Button> getResultButtons(Consumer<ArgumentCaptor<List<Button>>> consumer) {
        ArgumentCaptor<List<Button>> captor = ArgumentCaptor.forClass(List.class);
        consumer.accept(captor);
        return GenericTests.getValueCollection(captor);
    }


    protected MessageEmbed getResultEmbed(Consumer<ArgumentCaptor<MessageEmbed>> consumer) {
        ArgumentCaptor<MessageEmbed> captor = ArgumentCaptor.forClass(MessageEmbed.class);
        consumer.accept(captor);
        return GenericTests.getValueSingle(captor);
    }

    protected List<FileUpload> getResultFiles(Consumer<ArgumentCaptor<List<FileUpload>>> consumer) {
        ArgumentCaptor<List<FileUpload>> captor = ArgumentCaptor.forClass(List.class);
        consumer.accept(captor);
        return GenericTests.getValueCollection(captor);
    }

    protected Modal getResultModal(Consumer<ArgumentCaptor<Modal>> consumer) {
        ArgumentCaptor<Modal> captor = ArgumentCaptor.forClass(Modal.class);
        consumer.accept(captor);
        return GenericTests.getValueSingle(captor);

    }
}
