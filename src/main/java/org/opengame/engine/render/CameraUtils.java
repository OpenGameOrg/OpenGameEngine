package org.opengame.engine.render;

import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static org.lwjgl.bgfx.BGFX.bgfx_get_caps;

public class CameraUtils {

    public static void lookAt(Vector3f at, Vector3f eye, Matrix4x3f dest) {
        dest.lookAtLH(eye.x, eye.y, eye.z, at.x, at.y, at.z, 0.0f, 1.0f, 0.0f);
    }

    public static void perspective(float fov, int width, int height, float near, float far, Matrix4f dest) {
        float fovRadians = fov * (float) Math.PI / 180.0f;
        float aspect = width / (float) height;
        var zZeroToOne = !Objects.requireNonNull(bgfx_get_caps()).homogeneousDepth();
        dest.setPerspectiveLH(fovRadians, aspect, near, far, zZeroToOne);
    }
}
