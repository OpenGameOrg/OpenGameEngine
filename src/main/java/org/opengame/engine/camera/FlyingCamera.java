package org.opengame.engine.camera;

import lombok.extern.java.Log;
import org.joml.*;
import org.opengame.engine.Engine;
import org.opengame.engine.event.EventBus;
import org.opengame.engine.event.EventType;
import org.opengame.engine.event.KeyEventData;
import org.opengame.engine.event.MouseEventData;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

@Log
public class FlyingCamera extends Camera {

    private static final float DEFAULT_FLY_SPEED = 0.05f;

    private final Vector2f windowCenterPos;

    private Vector2f viewportSize;
    private float flySpeed;
    private boolean isChangingDirection;
    private double lastMouseX = -1;
    private double lastMouseY = -1;
    private float currentStrafeSpeed = 0;
    private float currentForwardSpeed = 0;
    private float currentUpSpeed = 0;

    private float startTime;
    private float mouseSensitivity = 0.15f;

    private FlyingCamera() {
        super();

        viewportSize = new Vector2f(Engine.getScreenWidth(), Engine.getScreenHeight());
        windowCenterPos = viewportSize.div(2);

        //position = new Vector3f(0, 5, -10);
        //setViewProjection();

        EventBus.subscribeToEvent(EventType.KEY_PRESSED, (eventData) -> processKeyPressedEvent((KeyEventData) eventData));
        EventBus.subscribeToEvent(EventType.MOUSE_BUTTON_EVENT, (eventData) -> processMousePressedEvent((KeyEventData) eventData));
        EventBus.subscribeToEvent(EventType.MOUSE_MOVED, (eventData) -> processMouseMovedEvent((MouseEventData) eventData));
    }

    public static FlyingCamera createDefault() {
        var defaultCam = new FlyingCamera();
        defaultCam.flySpeed = DEFAULT_FLY_SPEED;

        return defaultCam;
    }
    private void processMouseMovedEvent(MouseEventData eventData) {
        if (!isChangingDirection) return;

        if (lastMouseX == -1 || lastMouseY == -1) {
            lastMouseX = eventData.getXPos();
            lastMouseY = eventData.getYPos();
        }
        var deltaX = lastMouseX - eventData.getXPos();
        var deltaY = lastMouseY - eventData.getYPos();

        if (deltaX != 0) {
            rotateLeftRight((float) deltaX * mouseSensitivity);
        }
        if (deltaY != 0) {
            rotateUpDown((float) deltaY * mouseSensitivity);
        }
        setViewProjection();

        lastMouseX = eventData.getXPos();
        lastMouseY = eventData.getYPos();

        //Engine.setCursorPos(windowCenterPos);
    }

    private void rotateUpDown(float delta) {
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

    private void rotateLeftRight(float delta) {
        var normalizedDirection = direction.normalize();
        var rotationMatrix = new Matrix4f().rotate((float) Math.toRadians(delta), up);
        var rotatedDirection = new Vector4f(normalizedDirection, 0).mul(rotationMatrix);
        direction = new Vector3f(rotatedDirection.x, rotatedDirection.y, rotatedDirection.z);

        var rotatedRight = new Vector4f(right.normalize(), 0).mul(rotationMatrix);
        right = new Vector3f(rotatedRight.x, rotatedRight.y, rotatedRight.z);
    }

    private void processKeyPressedEvent(KeyEventData eventData) {
        var key = eventData.getKeyCode();

        var isPressedMultiplier = eventData.isPressed() ? 1.0 : -1.0;

        if (key == GLFW_KEY_RIGHT || key == GLFW_KEY_D) {
            currentStrafeSpeed += flySpeed * isPressedMultiplier;
        }
        if (key == GLFW_KEY_LEFT || key == GLFW_KEY_A) {
            currentStrafeSpeed -= flySpeed * isPressedMultiplier;
        }
        if (key == GLFW_KEY_UP || key == GLFW_KEY_W) {
            currentForwardSpeed -= flySpeed * isPressedMultiplier;
        }
        if (key == GLFW_KEY_DOWN || key == GLFW_KEY_S) {
            currentForwardSpeed += flySpeed * isPressedMultiplier;
        }
        if (key == GLFW_KEY_SPACE) {
            currentUpSpeed += flySpeed * isPressedMultiplier;
        }

        if (key == GLFW_MOUSE_BUTTON_RIGHT) {
            isChangingDirection = eventData.isPressed();
        }
    }

    private void processMousePressedEvent(KeyEventData eventData) {
        if (eventData.getKeyCode() != GLFW_MOUSE_BUTTON_RIGHT) return;

        lastMouseX = -1;
        lastMouseY = -1;

        isChangingDirection = eventData.isPressed();
    }

    @Override
    public void frame(float time, float frameTimeMs) {
        if (currentStrafeSpeed != 0) {
            moveRight(currentStrafeSpeed);
        }
        if (currentForwardSpeed != 0) {
            moveForward(currentForwardSpeed);
        }
        if (currentUpSpeed != 0) {
            moveUp(currentUpSpeed);
        }
    }
}
