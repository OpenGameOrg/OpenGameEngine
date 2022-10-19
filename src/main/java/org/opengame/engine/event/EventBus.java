package org.opengame.engine.event;

import lombok.extern.java.Log;

import java.util.*;
import java.util.function.Consumer;

/**
 * Connect event producers and consumers
 */
@Log
public enum EventBus {
    INSTANCE;

    private final Map<EventType, List<Consumer<Object>>> eventListeners;

    EventBus() {
        eventListeners = new HashMap<>();
    }

    public static void subscribeToEvent(EventType eventType, Consumer<Object> consumer) {
        log.info("Subscribed to " + eventType);

        if (!INSTANCE.eventListeners.containsKey(eventType)) {
            INSTANCE.eventListeners.put(eventType, new ArrayList<>());
        }

        INSTANCE.eventListeners.get(eventType).add(consumer);
    }

    public static void broadcastEvent(EventType eventType, Object data) {
        //log.warning("Event broadcast " + eventType);

        if (!INSTANCE.eventListeners.containsKey(eventType)) return;

        INSTANCE.eventListeners.get(eventType).forEach((consumer) -> consumer.accept(data));
    }
}
