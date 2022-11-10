package org.opengame.engine.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MouseEventData {
    private final double xPos;
    private final double yPos;
}