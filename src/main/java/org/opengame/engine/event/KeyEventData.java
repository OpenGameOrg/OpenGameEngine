package org.opengame.engine.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Data provided with KEY_PRESSED event
 */
@RequiredArgsConstructor
@Getter
public class KeyEventData {
    private final int keyCode;
    private final boolean isPressed;
}