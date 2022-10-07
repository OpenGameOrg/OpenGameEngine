package org.opengame.engine.scene;

import lombok.Getter;
import lombok.Setter;
import org.opengame.engine.camera.Camera;
import org.opengame.engine.camera.FlyingCamera;
import org.opengame.engine.object.SceneObject;

import java.util.Vector;

/**
 * Scene
 */
@Getter
@Setter
public class Scene {
    private String name;
    private final Vector<SceneObject> objects;
    private Camera camera;

    public Scene() {
        objects = new Vector<>();
        setCamera(FlyingCamera.createDefault());
        name = "TestScene";

        camera.setViewProjection();
    }

    public void add(SceneObject mesh) {
        objects.add(mesh);
    }
    public void add(Model model) {
        objects.addAll(model.getMeshes());
    }

    public void render(float time, float frameTime) {
        objects.forEach((mesh) -> mesh.frame(time, frameTime));
    }

    public void setCamera(Camera camera) {
        if (this.camera != null) {
            objects.remove(camera);
        }
        this.camera = camera;
        objects.add(camera);
    }

    public String getStats() {
        var vertexCount = objects.parallelStream().mapToInt((object) -> {
            if (object instanceof Mesh) {
                var mesh = (Mesh) object;
                return mesh.getVertexCount();
            }
            return 0;
        }).sum();
        var indexCount = objects.parallelStream().mapToInt((object) -> {
            if (object instanceof Mesh) {
                var mesh = (Mesh) object;
                return mesh.getIndexCount();
            }
            return 0;
        }).sum();
        return "[meshes: " + objects.size() + "; vertices: " + vertexCount + "; indices: " + indexCount + "]";
    }
}
