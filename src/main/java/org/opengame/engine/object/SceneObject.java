package org.opengame.engine.object;

import lombok.extern.java.Log;

/**
 * Something that can be added to the scene
 */
@Log
public class SceneObject implements Renderable, DynamicObject {
    @Override
    public void frame(float time, float frameTimeMs) {
        // override in children
    }

    @Override
    public void update(float time, float tickTimeMs) {
        // override in children
    }
}
