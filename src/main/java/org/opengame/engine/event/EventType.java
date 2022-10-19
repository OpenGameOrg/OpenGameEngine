package org.opengame.engine.event;

import org.opengame.engine.object.SceneObject;

public enum EventType {
    KEY_PRESSED(KeyEventData.class),
    KEY_RELEASED(KeyEventData.class),
    MOUSE_BUTTON_EVENT(KeyEventData.class),
    MOUSE_MOVED(MouseEventData.class),

    // Scene
    OBJECT_ADDED_TO_SCENE(SceneObject .class);

    private final Class<?> eventDataClass;

    EventType(Class<?> eventDataClass) {
        this.eventDataClass = eventDataClass;
    }
}