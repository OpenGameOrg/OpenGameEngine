package org.opengame.engine.render;

/**
 * Something that can be rendered
 */
public interface Rendarable {
    void frame(float time, float frameTimeMs);
}
