package org.opengame.engine.camera;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.opengame.engine.Engine;
import org.opengame.engine.object.MaterialObject;
import org.opengame.engine.render.CameraUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.bgfx.BGFX.bgfx_set_view_transform;

/**
 * Base camera class
 */
@Log
public class Camera extends MaterialObject {

    private final Matrix4x3f view = new Matrix4x3f();
    private final FloatBuffer viewBuffer;
    private final Matrix4f projection = new Matrix4f();
    private final FloatBuffer projectionBuffer;
    protected Vector3f direction;
    protected Vector3f right;
    protected Vector3f left;
    protected Vector3f up;

    private float nearPlane = 0.1f;
    private float farPlane = 100f;

    @Setter
    @Getter
    private boolean debugMode = false;


    public Camera() {
        viewBuffer = MemoryUtil.memAllocFloat(16);
        projectionBuffer = MemoryUtil.memAllocFloat(16);
        direction = new Vector3f(0, 0, -1);
        right = new Vector3f(1, 0, 0);
        left = new Vector3f(1, 0, 0);
        up = new Vector3f(0, 1, 0);

        setPerspective(35, Engine.getScreenWidth(), Engine.getScreenHeight(),
                nearPlane, nearPlane);
    }

    public void setPerspective(float fov, int screenWidth, int screenHeight, float nearPlane, float farPlane) {
        CameraUtils.perspective(fov, screenWidth, screenHeight, nearPlane, farPlane, projection);
        setViewProjection();
    }

    public void setViewProjection() {
        view.identity();
        var eye = new Vector3f(getPosition());
        eye.add(direction);
        CameraUtils.lookAt(getPosition(), eye, view);

        bgfx_set_view_transform(0, view.get4x4(viewBuffer), projection.get(projectionBuffer));

        if (debugMode) {
            log.info("Current pos: " + getPosition());
            log.info("Current rotation: " + getRotation());
        }
    }

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);

        setViewProjection();
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.setRotation(rotation);

        rotateLeftRight(rotation.x);
        rotateUpDown(rotation.y);

        setViewProjection();
    }


    protected void rotateUpDown(float delta) {
        var normalizedDirection = direction.normalize();
        var directionNoY = new Vector3f(normalizedDirection.x, 0, normalizedDirection.z).normalize();

        var currentAngleDegrees = Math.toDegrees(Math.acos(directionNoY.dot(direction)));
        if (normalizedDirection.y < 0.0f) {
            currentAngleDegrees = -currentAngleDegrees;
        }

        var newAngleDegrees = currentAngleDegrees + delta;

        if (newAngleDegrees < -85.0f || newAngleDegrees > 85.0f) return;

        var rotationAxis = new Vector3f(normalizedDirection).cross(up).normalize();
        var rotationMatrix = new Matrix4f().rotate((float) Math.toRadians(delta), rotationAxis);

        var rotatedDirection = new Vector4f(normalizedDirection, 0).mul(rotationMatrix).normalize();
        direction = new Vector3f(rotatedDirection.x, rotatedDirection.y, rotatedDirection.z);
    }

    protected void rotateLeftRight(float delta) {
        var normalizedDirection = direction.normalize();
        var rotationMatrix = new Matrix4f().rotate((float) Math.toRadians(delta), up);
        var rotatedDirection = new Vector4f(normalizedDirection, 0).mul(rotationMatrix);
        direction = new Vector3f(rotatedDirection.x, rotatedDirection.y, rotatedDirection.z);

        var rotatedRight = new Vector4f(right.normalize(), 0).mul(rotationMatrix);
        right = new Vector3f(rotatedRight.x, rotatedRight.y, rotatedRight.z);
    }

    public void moveForward(float offset) {
        getPosition().add(direction.x * offset, direction.y * offset, direction.z * offset);
        setViewProjection();
    }

    public void moveRight(float offset) {
        getPosition().add(right.x * offset, right.y * offset, right.z * offset);
        setViewProjection();
    }

    public void moveUp(float offset) {
        getPosition().add(up.x * offset, up.y * offset, up.z * offset);
        setViewProjection();
    }

    public void dispose() {
        MemoryUtil.memFree(viewBuffer);
        MemoryUtil.memFree(projectionBuffer);
    }
}
