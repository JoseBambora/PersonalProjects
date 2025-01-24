package org.jdaextension.generic;


import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;

public interface UsernameUpdateEvent extends ShutdownInterface {
    void onCall(UserUpdateNameEvent event, String userID, String oldName, String newName);
}
