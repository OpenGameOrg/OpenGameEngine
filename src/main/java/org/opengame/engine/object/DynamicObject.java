package org.opengame.engine.object;

/**
 * Something that can be updated in some way
 */
public interface DynamicObject {
    void frame(float time, float frameTimeMs);
}