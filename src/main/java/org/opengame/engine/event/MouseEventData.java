package org.opengame.engine.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MouseEventData extends EventData {
    private final double xPos;
    private final double yPos;
}