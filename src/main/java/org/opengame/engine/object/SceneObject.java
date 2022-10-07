package org.opengame.engine.object;

/**
 * Something that can be added to the scene
 */
public class SceneObject implements Renderable, DynamicObject {
    @Override
    public void frame(float time, float frameTimeMs) {
        // override in children
    }

    @Override
    public void update() {
        // override in children
    }
}
