package org.opengame.engine.scene;

import lombok.Getter;
import lombok.Setter;
import org.opengame.engine.camera.Camera;
import org.opengame.engine.render.Mesh;

import java.util.Vector;

/**
 * Scene
 */
@Getter
@Setter
public class Scene {
    private String name;
    private final Vector<Mesh> meshes;
    private final Camera camera;

    public Scene() {
        meshes = new Vector<>();
        camera = new Camera();
        name = "TestScene";

        camera.setViewProjection();
    }

    public void add(Mesh mesh) {
        meshes.add(mesh);
    }

    public void render(float time, float frameTime) {
        meshes.forEach((mesh) -> mesh.frame(time, frameTime));
    }
}
