package org.opengame.engine.scene;

import lombok.Getter;
import org.joml.Vector3f;
import org.opengame.engine.object.DynamicObject;

import java.util.List;

public class Model implements DynamicObject {
    private String model;
    @Getter
    private List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public void setRotation(Vector3f setRotation) {
        meshes.forEach((mesh) -> mesh.setRotation(setRotation));
    }
    public void setPosition(Vector3f position) {
        meshes.forEach((mesh) -> mesh.setPosition(position));
    }

    @Override
    public void frame(float time, float frameTimeMs) {
        meshes.forEach((mesh) -> mesh.frame(time, frameTimeMs));
    }
}