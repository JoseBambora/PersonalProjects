package org.botgverreiro.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class PermissionsManager {
    private static List<Role> getRoles(TextChannel textChannel) {
        return textChannel.getGuild()
                .getRoles()
                .stream()
                .filter(r -> !r.hasPermission(Permission.KICK_MEMBERS))
                .toList();

    }
    public static void close(TextChannel textChannel) {
        getRoles(textChannel)
                .forEach(r ->
                        textChannel.upsertPermissionOverride(r)
                                .deny(Permission.MESSAGE_SEND)
                                .queue());
    }
    public static void open(TextChannel textChannel) {
        getRoles(textChannel)
                .forEach(r ->
                        textChannel.upsertPermissionOverride(r)
                                .grant(Permission.MESSAGE_SEND)
                                .queue());
    }
}
