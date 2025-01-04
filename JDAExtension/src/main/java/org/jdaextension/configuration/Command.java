package org.jdaextension.configuration;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Command<T> extends ButtonBehaviour<T> {
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

    protected Response execute(CommandInteraction event) {
        if (permissions.isEmpty() || (event.getMember() != null && event.getMember().hasPermission(permissions))) {
            if (isSendThinking())
                event.deferReply(isEphemeral()).queue();
            return executeCommand(event);
        } else {
            return new ResponseCommand(event, false, false)
                    .setTemplate("403")
                    .setVariable("message", "You do not have access to this command");
        }
    }

    protected abstract CommandData build();

    protected abstract Response executeCommand(CommandInteraction event);
}
