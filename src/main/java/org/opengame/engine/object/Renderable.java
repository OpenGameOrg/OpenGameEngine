package org.opengame.engine.object;

/**
 * Something that can be rendered
 */
public interface Renderable {
    void frame(float time, float frameTimeMs);
}