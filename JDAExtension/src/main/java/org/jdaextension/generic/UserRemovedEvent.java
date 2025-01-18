package org.jdaextension.generic;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public interface UserRemovedEvent {
    void onCall(GuildMemberRemoveEvent event, String userID, String username);
}
