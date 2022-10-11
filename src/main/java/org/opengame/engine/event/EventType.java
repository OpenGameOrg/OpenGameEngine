package org.opengame.engine.event;

public enum EventType {
    KEY_PRESSED(KeyEventData.class),
    KEY_RELEASED(KeyEventData.class),
    MOUSE_BUTTON_EVENT(KeyEventData.class),
    MOUSE_MOVED(MouseEventData.class);

    private final Class<? extends EventData> eventDataClass;

    EventType(Class<? extends EventData> eventDataClass) {
        this.eventDataClass = eventDataClass;
    }
}