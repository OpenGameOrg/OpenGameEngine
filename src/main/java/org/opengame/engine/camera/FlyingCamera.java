package org.opengame.engine.camera;

import lombok.Getter;
import lombok.Setter;
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
    @Setter
    @Getter
    private float flySpeed;
    private boolean isChangingDirection;
    private double lastMouseX = -1;
    private double lastMouseY = -1;
    private float currentStrafeSpeed = 0;
    private float currentForwardSpeed = 0;
    private float currentUpSpeed = 0;

    private float boostMultiplier = 10f;

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
            var angle = (float) deltaX * mouseSensitivity;
            rotateLeftRight(angle);
            getRotation().add(angle, 0, 0);
        }
        if (deltaY != 0) {
            var angle = (float) deltaY * mouseSensitivity;
            rotateUpDown((float) deltaY * mouseSensitivity);
            getRotation().add(0, angle, 0);
        }
        setViewProjection();

        lastMouseX = eventData.getXPos();
        lastMouseY = eventData.getYPos();

        //Engine.setCursorPos(windowCenterPos);
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
        if (key == GLFW_KEY_LEFT_SHIFT) {
            currentUpSpeed = eventData.isPressed() ? currentUpSpeed * boostMultiplier : currentUpSpeed / boostMultiplier;
            currentStrafeSpeed = eventData.isPressed() ? currentStrafeSpeed * boostMultiplier : currentStrafeSpeed / boostMultiplier;
            currentForwardSpeed = eventData.isPressed() ? currentForwardSpeed * boostMultiplier : currentForwardSpeed / boostMultiplier;
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
    public void update(float time, float tickTime) {
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
