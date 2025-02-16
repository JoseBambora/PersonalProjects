package org.jdaextension.configuration;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;
import org.jdaextension.responses.ResponseModal;

import java.util.*;

public abstract class Command<T> extends ButtonReceiver {
    protected final List<Permission> permissions;
    protected String name;
    private boolean sendThinking;
    private boolean ephemeral;

    protected Command() {
        this.name = "";
        permissions = new ArrayList<>();
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public T addPermission(Permission permission) {
        permissions.add(permission);
        return (T) this;
    }

    public T addPermissions(Permission... permissions) {
        return addPermissions(Arrays.asList(permissions));
    }


    public T addPermissions(Collection<Permission> permissions) {
        this.permissions.addAll(permissions);
        return (T) this;
    }

    public T setEphemeral() {
        ephemeral = true;
        return (T) this;
    }

    public T setSendThinking() {
        this.sendThinking = true;
        return (T) this;
    }


    public boolean isSendThinking() {
        return sendThinking;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public String getName() {
        return name;
    }

    protected void execute(CommandInteraction event) {
        if (permissions.isEmpty() || (event.getMember() != null && event.getMember().hasPermission(permissions))) {
            if (isSendThinking())
                event.deferReply(isEphemeral()).queue();
            executeCommand(event);
        } else {
            new ResponseCommand(event, "", false, false)
                    .setTemplate("403")
                    .setVariable("message", "You do not have access to this command")
                    .send();
        }
    }

    protected abstract CommandData build();

    protected abstract void executeCommand(CommandInteraction event);

    public Response onModalInteraction(ModalInteractionEvent event, String id) {
        Map<String, String> fields = new HashMap<>();
        event.getValues().forEach(mm -> fields.put(mm.getId(), mm.getAsString()));
        ResponseModal responseButton = new ResponseModal(event, sendThinking, ephemeral);
        getController().onCall(event, id, fields, responseButton);
        return responseButton;
    }

    protected void onShutDown(ShutdownEvent shutdownEvent) {
        getController().onShutDown(shutdownEvent);
    }
}
