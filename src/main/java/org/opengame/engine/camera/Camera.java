package org.opengame.engine.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.opengame.engine.Engine;
import org.opengame.engine.render.CameraUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.bgfx.BGFX.bgfx_set_view_transform;

/**
 * Base camera class
 */
public class Camera {

    private final Matrix4x3f view = new Matrix4x3f();
    private final FloatBuffer viewBuffer;
    private final Matrix4f projection = new Matrix4f();
    private final FloatBuffer projectionBuffer;

    public Camera() {
        viewBuffer = MemoryUtil.memAllocFloat(16);
        projectionBuffer = MemoryUtil.memAllocFloat(16);
    }

    public void setViewProjection() {
        CameraUtils.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, -35.0f), view);
        CameraUtils.perspective(60, Engine.getScreenWidth(), Engine.getScreenHeight(),
                0.1f, 100.0f, projection);

        bgfx_set_view_transform(0, view.get4x4(viewBuffer), projection.get(projectionBuffer));
    }

    public void dispose() {
        MemoryUtil.memFree(viewBuffer);
        MemoryUtil.memFree(projectionBuffer);
    }
}
