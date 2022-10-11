package org.opengame.engine.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.opengame.engine.Engine;
import org.opengame.engine.object.SceneObject;
import org.opengame.engine.render.CameraUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.bgfx.BGFX.bgfx_set_view_transform;

/**
 * Base camera class
 */
public class Camera extends SceneObject {

    private final Matrix4x3f view = new Matrix4x3f();
    private final FloatBuffer viewBuffer;
    private final Matrix4f projection = new Matrix4f();
    private final FloatBuffer projectionBuffer;
    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f direction;
    protected Vector3f right;
    protected Vector3f left;
    protected Vector3f up;


    public Camera() {
        viewBuffer = MemoryUtil.memAllocFloat(16);
        projectionBuffer = MemoryUtil.memAllocFloat(16);
        position = new Vector3f();
        rotation = new Vector3f();
        direction = new Vector3f(0, 0, -1);
        right = new Vector3f(1, 0, 0);
        left = new Vector3f(1, 0, 0);
        up = new Vector3f(0, 1, 0);

        CameraUtils.perspective(35, Engine.getScreenWidth(), Engine.getScreenHeight(),
                0.1f, 100.0f, projection);
    }

    public void setViewProjection() {
        view.identity();
        var eye = new Vector3f(position);
        eye.add(direction);
        CameraUtils.lookAt(position, eye, view);

        bgfx_set_view_transform(0, view.get4x4(viewBuffer), projection.get(projectionBuffer));
    }

    public void moveForward(float offset) {
        position.add(direction.x * offset, direction.y * offset, direction.z * offset);
        setViewProjection();
    }

    public void moveRight(float offset) {
        position.add(right.x * offset, right.y * offset, right.z * offset);
        setViewProjection();
    }

    public void moveUp(float offset) {
        position.add(up.x * offset, up.y * offset, up.z * offset);
        setViewProjection();
    }

    public void dispose() {
        MemoryUtil.memFree(viewBuffer);
        MemoryUtil.memFree(projectionBuffer);
    }
}
