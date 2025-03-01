package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.session.ReadyEvent;

public interface OnReadyEvent {
    void onCall(ReadyEvent event);
}
