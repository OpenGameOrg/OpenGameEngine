package org.opengame.engine.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Vertex struct for meshes
 */
@RequiredArgsConstructor
@Getter
public class PosColorVertex {
    private final float x;
    private final float y;
    private final float z;
    private final int abgr;
}
