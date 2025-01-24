package org.jdaextension.generic;

import net.dv8tion.jda.api.events.session.ShutdownEvent;

public interface ShutdownInterface {

    default void onShutDown(ShutdownEvent shutdownEvent) {

    }
}
