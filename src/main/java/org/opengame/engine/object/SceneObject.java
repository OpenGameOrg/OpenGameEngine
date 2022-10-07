package org.opengame.engine.object;

/**
 * Something that can be added to the scene
 */
public class SceneObject implements DynamicObject {
    @Override
    public void frame(float time, float frameTimeMs) {
        // override in children
    }
}
