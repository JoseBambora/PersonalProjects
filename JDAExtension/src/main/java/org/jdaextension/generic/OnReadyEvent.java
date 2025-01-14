package org.jdaextension.generic;

import net.dv8tion.jda.api.events.session.ReadyEvent;

public abstract class OnReadyEvent {

    public abstract void onCall(ReadyEvent event);
}
