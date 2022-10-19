package org.opengame.engine.scene;

import lombok.Getter;
import org.joml.Vector3f;
import org.opengame.engine.object.MaterialObject;
import org.opengame.engine.object.Renderable;

import java.io.IOException;
import java.util.List;

public class Model extends MaterialObject {
    private String model;
    @Getter
    private List<Mesh> meshes;

    private Vector3f scale = new Vector3f(1, 1, 1);

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    @Override
    public void setRotation(Vector3f setRotation) {
        super.setRotation(setRotation);
        meshes.forEach((mesh) -> mesh.setRotation(setRotation));
    }
    public void setPosition(Vector3f position) {
        super.setPosition(position);
        meshes.forEach((mesh) -> mesh.setPosition(position));
    }

    public void setTexture(String textureName) {
        meshes.forEach(mesh -> {
            try {
                mesh.setTexture(textureName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setScale(Vector3f scale) {
        super.setScale(scale);
        this.scale = scale;
        meshes.forEach(mesh -> mesh.setScale(scale));
    }

    @Override
    public void frame(float time, float frameTimeMs) {
        meshes.forEach((mesh) -> mesh.frame(time, frameTimeMs));
    }
}
